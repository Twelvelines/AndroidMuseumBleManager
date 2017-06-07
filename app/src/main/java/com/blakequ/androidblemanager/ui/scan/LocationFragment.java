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
import com.blakequ.bluetooth_manager_lib.util.BluetoothUtils;
import com.toy.example.BeaconDeviceLocationData;
import com.toy.example.MainDeviceLocation;

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

public class LocationFragment extends Fragment {
    @Bind(R.id.tvBluetoothLe)
    protected TextView mTvBluetoothLeStatus;
    @Bind(R.id.tvBluetoothStatus)
    protected TextView mTvBluetoothStatus;
    @Bind(R.id.tvBluetoothFilter)
    protected TextView mTvBluetoothFilter;
    @Bind(R.id.tvItemCount)
    protected TextView mTvItemCount;
    @Bind(R.id.LocationView)
    protected TextView mLocationView;
    @Bind(android.R.id.list)
    protected ListView mList;
    @Bind(android.R.id.empty)
    protected View mEmpty;
    private View rootView;
    private DeviceListAdapter mLeDeviceListAdapter;
    private BluetoothUtils mBluetoothUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // mBluetoothUtils = new BluetoothUtils(getActivity());
        mLeDeviceListAdapter = new DeviceListAdapter(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        // final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

       /* if (mIsBluetoothOn) {
            mTvBluetoothStatus.setText(R.string.on);
        } else {
            mTvBluetoothStatus.setText(R.string.off);
        }

        if (mIsBluetoothLePresent) {
            mTvBluetoothLeStatus.setText(R.string.supported);
        } else {
            mTvBluetoothLeStatus.setText(R.string.not_supported);
        }

        String filterName = PreferencesUtils.getString(getContext(), Constants.FILTER_NAME, "");
        int filterRssi = PreferencesUtils.getInt(getContext(), Constants.FILTER_RSSI, -100);
        boolean filterSwitch = PreferencesUtils.getBoolean(getContext(), Constants.FILTER_SWITCH, false);
        if (filterSwitch){
            if (filterName != null && filterName.length() > 0){
                mTvBluetoothFilter.setText("FilterName:"+filterName+" ,FilterRssi:"+filterRssi);
            }else {
                mTvBluetoothFilter.setText("FilterRssi:"+filterRssi);
            }
        }else {
            mTvBluetoothFilter.setText(R.string.off);
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefresh(UpdateEvent event){
        switch (event.getType()){
            case SCAN_UPDATE:
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null){
                    BluetoothLeDeviceStore store = activity.getDeviceStore();
                    if (store != null){
                        mLeDeviceListAdapter.refreshData(store.getDeviceList());
                        // updating user location after refreshing
                        List<IBeaconDevice> beaconList = filterDevices(store);
                        if (beaconList.size() > 2) {
                            MainDeviceLocation.locate(beaconList.get(0), beaconList.get(1), beaconList.get(2));
                            mLocationView.setText("Location:\n" +
                                    "Longitude: " + MainDeviceLocation.getLongitude() + "\n" +
                                    "Latitude: " + MainDeviceLocation.getLatitude() + "\n");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.app_location, null);
        /*ButterKnife.bind(this, rootView);
        mList.setEmptyView(mEmpty);
        mList.setOnItemClickListener(this);
        mList.setAdapter(mLeDeviceListAdapter);
        updateItemCount(0);*/
        return v;
    }

    public List<IBeaconDevice> filterDevices(BluetoothLeDeviceStore bds) {
        final List<BluetoothLeDevice> bleDevices = bds.getDeviceList();
        List<IBeaconDevice> iBeacons = new ArrayList<>();
        for(final BluetoothLeDevice device : bleDevices){
            boolean isIBeacon = BeaconUtils.getBeaconType(device) == BeaconType.IBEACON;
            if(isIBeacon){
                iBeacons.add(device.getIBeaconDevice());
            }
        }
        return iBeacons;
    }
}