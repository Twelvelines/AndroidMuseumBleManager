package com.blakequ.androidblemanager.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blakequ.androidblemanager.R;
import com.blakequ.androidblemanager.adapter.DeviceListAdapter;
import com.blakequ.androidblemanager.containers.BluetoothLeDeviceStore;
import com.blakequ.androidblemanager.event.UpdateEvent;
import com.blakequ.androidblemanager.ui.MainActivity;
import com.blakequ.androidblemanager.utils.Constants;
import com.blakequ.androidblemanager.utils.PreferencesUtils;
import com.blakequ.bluetooth_manager_lib.device.BeaconType;
import com.blakequ.bluetooth_manager_lib.device.BeaconUtils;
import com.blakequ.bluetooth_manager_lib.device.BluetoothLeDevice;
import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;
import com.toy.example.BeaconDeviceLocationData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by DorisLiu on 5/30/17.
 */

public class LocationFragment extends Fragment implements AdapterView.OnItemClickListener{

    @Bind(R.id.tvBluetoothLe)
    protected TextView mTvBluetoothLeStatus;
    @Bind(R.id.tvBluetoothStatus)
    protected TextView mTvBluetoothStatus;
    @Bind(R.id.tvBluetoothFilter)
    protected TextView mTvBluetoothFilter;
    @Bind(R.id.tvItemCount)
    protected TextView mTvItemCount;
    @Bind(android.R.id.list)
    protected ListView mList;
    @Bind(android.R.id.empty)
    protected View mEmpty;
    private View rootView;

    private DeviceListAdapter mLeDeviceListAdapter;
    //private BluetoothUtils mBluetoothUtils;

    public BluetoothLeDeviceStore BDS;

    public List<BluetoothLeDevice> showLocation(){

        final List<BluetoothLeDevice> list = BDS.getDeviceList();
        List<BluetoothLeDevice> distances = new ArrayList<BluetoothLeDevice>();
        //int m = 0;
        for(final BluetoothLeDevice device: list){
            boolean isIBeacon = BeaconUtils.getBeaconType(device) == BeaconType.IBEACON;
            if(isIBeacon){
                distances.add(device);
            }
        }
        return distances;
    }

    public List<IbeaconStore> drawlocation() {

        List<BluetoothLeDevice> deviceList = showLocation();
        List<IbeaconStore> ibeaconStores = new ArrayList<IbeaconStore>();
        IbeaconStore IS;
        double d_1 = deviceList.get(1).getIBeaconDevice().getAccuracy();
        double d_2 = deviceList.get(2).getIBeaconDevice().getAccuracy();
        double d_3 = deviceList.get(3).getIBeaconDevice().getAccuracy();
        double x_1 = 0;
        double y_1 = 2;
        double x_2 = 1;
        double y_2 = 2;
        double x_3 = 1;
        double y_3 = 1;
        // algorithm part
        double k_1 = -1 * (x_2 - x_1) / (y_2 - y_1);
        double k_2 = -1 * (x_3 - x_2) / (y_3 - y_2);
        double b_1 = (Math.pow(x_2, 2) - Math.pow(x_1, 2) + Math.pow(d_1, 2) - Math.pow(d_2, 2)) / (2 * (y_2 - y_1)) + (y_1 + y_2) / 2;
        double b_2 = (Math.pow(x_3, 2) - Math.pow(x_2, 2) + Math.pow(d_2, 2) - Math.pow(d_3, 2)) / (2 * (y_3 - y_2)) + (y_2 + y_3) / 2;
        double x = (b_2 - b_1) / (k_1 - k_2);
        double y = k_1 * (b_2 - b_1) / (k_1 - k_2) + b_1;

        ibeaconStores.get(1).X=x_1-x;
        ibeaconStores.get(2).X=x_2-x;
        ibeaconStores.get(3).X=x_3-x;
        ibeaconStores.get(1).Y=y_1-y;
        ibeaconStores.get(2).Y=y_2-y;
        ibeaconStores.get(3).Y=y_3-y;

        return ibeaconStores;
    }






//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        // mBluetoothUtils = new BluetoothUtils(getActivity());
//        mLeDeviceListAdapter = new DeviceListAdapter(getActivity());
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
//        // final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
//
//       /* if (mIsBluetoothOn) {
//            mTvBluetoothStatus.setText(R.string.on);
//        } else {
//            mTvBluetoothStatus.setText(R.string.off);
//        }
//
//        if (mIsBluetoothLePresent) {
//            mTvBluetoothLeStatus.setText(R.string.supported);
//        } else {
//            mTvBluetoothLeStatus.setText(R.string.not_supported);
//        }*/
//
//        String filterName = PreferencesUtils.getString(getContext(), Constants.FILTER_NAME, "");
//        int filterRssi = PreferencesUtils.getInt(getContext(), Constants.FILTER_RSSI, -100);
//        boolean filterSwitch = PreferencesUtils.getBoolean(getContext(), Constants.FILTER_SWITCH, false);
//        if (filterSwitch){
//            if (filterName != null && filterName.length() > 0){
//                mTvBluetoothFilter.setText("FilterName:"+filterName+" ,FilterRssi:"+filterRssi);
//            }else {
//                mTvBluetoothFilter.setText("FilterRssi:"+filterRssi);
//            }
//        }else {
//            mTvBluetoothFilter.setText(R.string.off);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventRefresh(UpdateEvent event){
//        switch (event.getType()){
//            case SCAN_UPDATE:
//                MainActivity activity = (MainActivity) getActivity();
//                if (activity != null){
//                    BluetoothLeDeviceStore store = activity.getDeviceStore();
//                    if (store != null){
//                        mLeDeviceListAdapter.refreshData(store.getDeviceList());
//                        updateItemCount(mLeDeviceListAdapter.getCount());
//                    }
//                }
//                break;
//        }
//    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.app_location, null);
        /*ButterKnife.bind(this, rootView);
        mList.setEmptyView(mEmpty);
        mList.setOnItemClickListener(this);
        mList.setAdapter(mLeDeviceListAdapter);
        updateItemCount(0);*/
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothLeDevice device = (BluetoothLeDevice) mLeDeviceListAdapter.getItem(position);
        if (device == null) return;

        final Intent intent = new Intent(getActivity(), DeviceDetailsActivity.class);
        intent.putExtra(DeviceDetailsActivity.EXTRA_DEVICE, device);

        startActivity(intent);
    }

    private void updateItemCount(final int count) {
        mTvItemCount.setText(getString(R.string.formatter_item_count, String.valueOf(count)));
    }
}