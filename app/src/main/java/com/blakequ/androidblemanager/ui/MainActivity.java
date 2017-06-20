package com.blakequ.androidblemanager.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blakequ.androidblemanager.BuildConfig;
import com.blakequ.androidblemanager.R;
import com.blakequ.androidblemanager.adapter.FragmentPageAdapter;
import com.blakequ.androidblemanager.containers.BluetoothLeDeviceStore;
import com.blakequ.androidblemanager.event.UpdateEvent;
import com.blakequ.androidblemanager.ui.scan.ScanFragment;
import com.blakequ.androidblemanager.utils.BluetoothUtils;
import com.blakequ.androidblemanager.utils.Constants;
import com.blakequ.androidblemanager.utils.IntentUtils;
import com.blakequ.androidblemanager.utils.LocationUtils;
import com.blakequ.androidblemanager.utils.PreferencesUtils;
import com.blakequ.androidblemanager.widget.MyAlertDialog;
import com.blakequ.androidblemanager.widget.ScrollViewPager;
import com.blakequ.bluetooth_manager_lib.BleManager;
import com.blakequ.bluetooth_manager_lib.BleParamsOptions;
import com.blakequ.bluetooth_manager_lib.connect.ConnectConfig;
import com.blakequ.bluetooth_manager_lib.scan.BluetoothScanManager;
import com.blakequ.bluetooth_manager_lib.scan.ScanOverListener;
import com.blakequ.bluetooth_manager_lib.scan.bluetoothcompat.ScanCallbackCompat;
import com.blakequ.bluetooth_manager_lib.scan.bluetoothcompat.ScanResultCompat;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends ToolbarActivity {

    private BluetoothLeDeviceStore mDeviceStore;
    private BluetoothUtils mBluetoothUtils;
    private BluetoothScanManager scanManager;
    private String filterName;
    private int filterRssi;
    private boolean filterSwitch;
    private String[] permissionList = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        BleManager.setBleParamsOptions(new BleParamsOptions.Builder()
                .setBackgroundBetweenScanPeriod(5 * 60 * 1000)
                .setBackgroundScanPeriod(10000)
                .setForegroundBetweenScanPeriod(5000)
                .setForegroundScanPeriod(10000)
                .setDebugMode(BuildConfig.DEBUG)
                .setMaxConnectDeviceNum(5)
                .setReconnectBaseSpaceTime(8000)
                .setReconnectMaxTimes(4)
                .setReconnectStrategy(ConnectConfig.RECONNECT_FIXED_TIME)
                .setReconnectedLineToExponentTimes(5)
                .build());

        initScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterName = PreferencesUtils.getString(this, Constants.FILTER_NAME, "");
        filterRssi = PreferencesUtils.getInt(this, Constants.FILTER_RSSI, -100);
        filterSwitch = PreferencesUtils.getBoolean(this, Constants.FILTER_SWITCH, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    public BluetoothLeDeviceStore getDeviceStore() {
        return mDeviceStore;
    }

    private void initScan() {
        mBluetoothUtils = new BluetoothUtils(this);
        mDeviceStore = new BluetoothLeDeviceStore();
        scanManager = BluetoothScanManager.getInstance(this);
//        scanManager.addScanFilterCompats(new ScanFilterCompat.Builder().setDeviceName("").build());
        scanManager.setScanOverListener(new ScanOverListener() {
            @Override
            public void onScanOver() {
                if (scanManager.isPauseScanning()) {
                    invalidateOptionsMenu();
                }
            }
        });
        scanManager.setScanCallbackCompat(new ScanCallbackCompat() {
            @Override
            public void onBatchScanResults(List<ScanResultCompat> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(final int errorCode) {
                super.onScanFailed(errorCode);
            }

            @Override
            public void onScanResult(int callbackType, ScanResultCompat result) {
                super.onScanResult(callbackType, result);
                Logger.i("scan device " + result.getLeDevice().getAddress() + " " + result.getScanRecord().getDeviceName());
                if (filterSwitch) {
                    if (filterRssi <= result.getRssi()) {
                        if (filterName == null || filterName.equals("")) {
                            mDeviceStore.addDevice(result.getLeDevice());
                        } else if (filterName.equals(result.getScanRecord().getDeviceName())) {
                            mDeviceStore.addDevice(result.getLeDevice());
                        }
                    }
                } else {
                    mDeviceStore.addDevice(result.getLeDevice());
                }
                EventBus.getDefault().post(new UpdateEvent(UpdateEvent.Type.SCAN_UPDATE));
            }
        });
    }

    public void startScan() {
        if (checkPermission()) {
            if (checkIsBleState()) {
                mDeviceStore.clear();
                EventBus.getDefault().post(new UpdateEvent(UpdateEvent.Type.SCAN_UPDATE));
//                scanManager.startCycleScan();
                scanManager.startScanNow();
                invalidateOptionsMenu();
            }
        }
    }

    public void stopScan() {
        if (scanManager.isScanning()) {
            scanManager.stopCycleScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 || requestCode == 12) {//请求位置信息
            if (LocationUtils.isGpsProviderEnabled(this)) {
                Toast.makeText(this, R.string.ble_location_is_open, Toast.LENGTH_LONG).show();
            } else {
                if (requestCode == 11) {
                    showReOpenLocationDialog();
                } else {
                    Toast.makeText(this, R.string.ble_location_not_open_notice, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (resultCode != Activity.RESULT_OK) {
//                checkIsBleState();
            } else {
                startScan();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(UpdateEvent event) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (!scanManager.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_filter).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_filter).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScan();
                break;
            case R.id.menu_stop:
                scanManager.stopCycleScan();
                break;
            case R.id.menu_filter:
                startActivity(new Intent(this, FilterActivity.class));
                break;
        }
        return true;
    }

    @Override
    public int provideContentViewId() {
        return R.layout.activity_main;
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            EventBus.getDefault().post(new UpdateEvent(UpdateEvent.Type.TAB_SWITCH, position));
            invalidateOptionsMenu();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private boolean checkIsBleState() {
        if (!mBluetoothUtils.isBluetoothLeSupported()) {
            showNotSupportDialog();
        } else if (!mBluetoothUtils.isBluetoothOn()) {
            showOpenBleDialog();
        } else {
            return true;
        }
        return false;
    }

    private void showNotSupportDialog() {
        MyAlertDialog.getDialog(this, R.string.ble_not_support, R.string.ble_exit_app,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private void showExitDialog() {
        MyAlertDialog.getDialog(this, R.string.exit_app, R.string.ble_exit_app, R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 检查无法扫描到的情况dialog
     */
    private void showCheckBleNotScanDialog() {
        if (Build.VERSION.SDK_INT >= 23) {
            MyAlertDialog.getDialog(this, R.string.ble_not_scan, R.string.ble_not_scan_bt1, R.string.cancel,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!LocationUtils.isGpsProviderEnabled(MainActivity.this)) {
                                showOpenLocationSettingDialog();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.ble_location_has_open, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            MyAlertDialog.getDialog(this, R.string.ble_not_scan1, R.string.cancel,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 是否打开ble
     */
    private void showOpenBleDialog() {
        MyAlertDialog.getDialog(this, R.string.ble_not_open, R.string.ble_open, R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 重新检查位置信息是否开启
     */
    private void showReOpenLocationDialog() {
        MyAlertDialog.getDialog(this, R.string.ble_location_not_open, R.string.ble_location_open, R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.startLocationSettings(MainActivity.this, 12);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 打开位置信息
     */
    private void showOpenLocationSettingDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.include_location_dialog, null);
        MyAlertDialog.getViewDialog(this, view, R.string.ble_location_open, R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.startLocationSettings(MainActivity.this, 11);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showReOpenLocationDialog();
                        dialog.dismiss();
                    }
                }, false).show();
    }


    /**
     * 检查权限
     *
     * @return
     */
    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = PermissionUtils.hasSelfPermissions(this, permissionList);
            MainActivityPermissionsDispatcher.showCheckPermissionStateWithCheck(this);
            if (!LocationUtils.isGpsProviderEnabled(this)) {
                return false;
            }
            return hasPermission;
        }
        return true;
    }


    //请求权限

    /**
     * 这个方法中写正常的逻辑（假设有该权限应该做的事）
     */
    @NeedsPermission({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showCheckPermissionState() {
        //检查是否开启位置信息（如果没有开启，则无法扫描到任何蓝牙设备在6.0）
        if (!LocationUtils.isGpsProviderEnabled(this)) {
            showOpenLocationSettingDialog();
        }
    }

    /**
     * 弹出权限同意窗口之前调用的提示窗口
     *
     * @param request
     */
    @OnShowRationale({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForPermissionState(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        MyAlertDialog.showRationaleDialog(this, R.string.permission_rationale, request);
    }

    /**
     * 提示窗口和权限同意窗口--被拒绝时调用
     */
    @OnPermissionDenied({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onPermissionStateDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
    }

    /**
     * 当完全拒绝了权限打开之后调用
     */
    @OnNeverAskAgain({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onPermissionNeverAskAgain() {
        MyAlertDialog.showOpenSettingDialog(this, R.string.open_setting_permission);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
