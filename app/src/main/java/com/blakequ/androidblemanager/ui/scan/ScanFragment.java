package com.blakequ.androidblemanager.ui.scan;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeaconedmuseum.Artwork;
import com.androidbeaconedmuseum.ArtworkData;
import com.androidbeaconedmuseum.ArtworkNotFoundException;
import com.androidbeaconedmuseum.DirectionView;
import com.androidbeaconedmuseum.LocationView;
import com.androidbeaconedmuseum.ScrollingActivity;
import com.androidbeaconedmuseum.UserLocation;
import com.blakequ.androidblemanager.R;
import com.blakequ.androidblemanager.adapter.DeviceListAdapter;
import com.blakequ.androidblemanager.containers.BluetoothLeDeviceStore;
import com.blakequ.androidblemanager.event.UpdateEvent;
import com.blakequ.androidblemanager.ui.MainActivity;
import com.blakequ.androidblemanager.utils.BluetoothUtils;
import com.blakequ.bluetooth_manager_lib.device.BluetoothLeDevice;
import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
public class ScanFragment extends Fragment implements SensorEventListener {
    @Bind(R.id.location_board)
    protected LocationView locationView;
    @Bind(R.id.direction_view)
    protected DirectionView directionView;
    @Bind(R.id.next_scan_hint)
    protected TextView nextScanHint;
    private View rootView;
    private DeviceListAdapter mLeDeviceListAdapter;
    private BluetoothUtils mBluetoothUtils;
    boolean mIsBluetoothOn;
    boolean mIsBluetoothLePresent;

    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private long timestamp = 0;

    public static final String ARTWORK_MESSAGE_TEXT = "com.androidbeaconedmuseum.AMT";
    public static final String ARTWORK_MESSAGE_IMAGE = "com.androidbeaconedmuseum.AMI";
    public static final String ARTWORK_MESSAGE_NAME = "com.androidbeaconedmuseum.AMN";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBluetoothUtils = new BluetoothUtils(getActivity());
        mLeDeviceListAdapter = new DeviceListAdapter(getActivity());
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.app_location, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                        // updating for every 3 seconds
                        long currentTime = SystemClock.elapsedRealtime();
                        if (currentTime > timestamp + 3000) {
                            timestamp = currentTime;
                            nextScanHint.setText(R.string.scan_hint_3);
                            updateMapping(store.getDeviceList());
                        } else if (currentTime > timestamp + 1000 &&
                                currentTime < timestamp + 2000) {
                            nextScanHint.setText(R.string.scan_hint_2);
                        } else if (currentTime > timestamp + 2000) {
                            nextScanHint.setText(R.string.scan_hint_3);
                        }
                    }
                }
                break;
        }
    }

    private void updateMapping(List<BluetoothLeDevice> deviceList) {
        locationView.updateViewParams(deviceList);
        for (IBeaconDevice aBeacon : locationView.getFilteredBeacons()) {
            if (aBeacon.getAccuracy() < 1) {
                examineArtwork(aBeacon.getAddress());
            }
        }
    }

    private void examineArtwork(String beaconMacAddr) {
        try {
            Artwork art = ArtworkData.getArtwork(beaconMacAddr);
            Intent intent = new Intent(getContext(), ScrollingActivity.class);
            intent.putExtra(ARTWORK_MESSAGE_NAME, art.getName());
            intent.putExtra(ARTWORK_MESSAGE_TEXT, art.getTextSrc());
            intent.putExtra(ARTWORK_MESSAGE_IMAGE, art.getImageSrc());
            /*Activity activity = getActivity();
            if (activity.getClass().equals(MainActivity.class)) {
                ((MainActivity) activity).stopScan();
            }*/
            startActivity(intent);
        } catch (ArtworkNotFoundException ae) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
        directionView.updateAngle(mOrientationAngles[0]);
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }
}
