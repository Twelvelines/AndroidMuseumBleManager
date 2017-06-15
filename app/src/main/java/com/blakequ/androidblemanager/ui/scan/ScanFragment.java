package com.blakequ.androidblemanager.ui.scan;

import com.androidbeaconedmuseum.ArtworkNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeaconedmuseum.Artwork;
import com.androidbeaconedmuseum.ArtworkData;
import com.androidbeaconedmuseum.LocationView;
import com.androidbeaconedmuseum.ScrollingActivity;
import com.blakequ.androidblemanager.R;
import com.blakequ.androidblemanager.adapter.DeviceListAdapter;
import com.blakequ.androidblemanager.containers.BluetoothLeDeviceStore;
import com.blakequ.androidblemanager.event.UpdateEvent;
import com.blakequ.androidblemanager.ui.MainActivity;
import com.blakequ.androidblemanager.utils.BluetoothUtils;
import com.blakequ.bluetooth_manager_lib.device.BeaconType;
import com.blakequ.bluetooth_manager_lib.device.BeaconUtils;
import com.blakequ.bluetooth_manager_lib.device.BluetoothLeDevice;
import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;
import com.androidbeaconedmuseum.BeaconDeviceLocation;
import com.androidbeaconedmuseum.BeaconDeviceLocationData;
import com.androidbeaconedmuseum.BeaconUnrecognisedException;
import com.androidbeaconedmuseum.UserLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright (C) BlakeQu All Rights Reserved <blakequ@gmail.com>
 * <p/>
 * Licensed under the blakequ.com License, Version 1.0 (the "License");
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * author  : quhao <blakequ@gmail.com> <br>
 * date     : 2016/8/23 19:20 <br>
 * last modify author : <br>
 * version : 1.0 <br>
 * description:
 */
public class ScanFragment extends Fragment {
    @Bind(R.id.location_board)
    protected LocationView locationView;
    @Bind(R.id.locationText)
    protected TextView locationText;
    private View rootView;
    private DeviceListAdapter mLeDeviceListAdapter;
    private BluetoothUtils mBluetoothUtils;
    boolean mIsBluetoothOn;
    boolean mIsBluetoothLePresent;

    public static final String ARTWORK_MESSAGE_TEXT = "com.androidbeaconedmuseum.AMT";
    public static final String ARTWORK_MESSAGE_IMAGE = "com.androidbeaconedmuseum.AMI";
    public static final String ARTWORK_MESSAGE_NAME = "com.androidbeaconedmuseum.AMN";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBluetoothUtils = new BluetoothUtils(getActivity());
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
        mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefresh(UpdateEvent event) {
        switch (event.getType()) {
            case SCAN_UPDATE:
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    BluetoothLeDeviceStore store = activity.getDeviceStore();
                    if (store != null) {
                        mLeDeviceListAdapter.refreshData(store.getDeviceList());
                        updateMapping(store.getDeviceList());
                    }
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.app_location, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void updateMapping(List<BluetoothLeDevice> deviceList) {
        List<IBeaconDevice> beaconList = filterDevices(deviceList);
        List<BeaconDeviceLocation> locations = new ArrayList<>();
        for (IBeaconDevice aBeacon : beaconList) {
            try {
                locations.add(BeaconDeviceLocationData.getLocation(aBeacon));
            } catch (BeaconUnrecognisedException bue) {
            }
        }
        if (locations.size() > 2) {
            // updating location mapping
            UserLocation.locate(beaconList);
            locationView.setDeviceLocations(locations);
            locationView.invalidate();
            locationText.setText("User location:\n" +
                    "Latitude: " + UserLocation.getLatitude() + "\n" +
                    "Longitude: " + UserLocation.getLongitude() + "\n");
        }
        for (IBeaconDevice aBeacon : beaconList) {
            if (aBeacon.getAccuracy() < 1.5) {
                examineArtwork(aBeacon.getAddress());
            }
        }
    }

    private List<IBeaconDevice> filterDevices(List<BluetoothLeDevice> bleDevices) {
        List<IBeaconDevice> iBeacons = new ArrayList<>();
        for (final BluetoothLeDevice device : bleDevices) {
            if (
                    BeaconUtils.getBeaconType(device) == BeaconType.IBEACON &&
                    BeaconDeviceLocationData.isRecognisedBeacon(device.getIBeaconDevice())
                    ) {
                iBeacons.add(device.getIBeaconDevice());
            }
        }
        return iBeacons;
    }

    private void examineArtwork(String beaconMacAddr) {
        try {
            Artwork art = ArtworkData.getArtwork(beaconMacAddr);
            Intent intent = new Intent(getContext(), ScrollingActivity.class);
            intent.putExtra(ARTWORK_MESSAGE_NAME, art.getName());
            intent.putExtra(ARTWORK_MESSAGE_TEXT, art.getTextSrc());
            intent.putExtra(ARTWORK_MESSAGE_IMAGE, art.getImageSrc());
            startActivity(intent);
        } catch (ArtworkNotFoundException ae) {
        }
    }
}
