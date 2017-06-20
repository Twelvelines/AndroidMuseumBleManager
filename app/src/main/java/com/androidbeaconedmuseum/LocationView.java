package com.androidbeaconedmuseum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blakequ.bluetooth_manager_lib.device.BeaconType;
import com.blakequ.bluetooth_manager_lib.device.BeaconUtils;
import com.blakequ.bluetooth_manager_lib.device.BluetoothLeDevice;
import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DorisLiu on 6/6/17.
 */

public class LocationView extends View {
    private Paint cPaint, tPaint;
    private List<BeaconLocation> locations = new ArrayList<>();
    private List<IBeaconDevice> beacons = new ArrayList<>();

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        cPaint = new Paint();
        cPaint.setColor(Color.BLACK);       // 设置画笔颜色
        cPaint.setStyle(Paint.Style.FILL);  // 设置画笔模式为填充
        cPaint.setStrokeWidth(10f);         // 设置画笔宽度为10px

        tPaint = new Paint();
        tPaint.setColor(Color.BLACK);
        tPaint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        for (BeaconLocation aDeviceLocation : locations) {
            float deviceLatitude = (float) (aDeviceLocation.getLatitude() - UserLocation.getLatitude()) * 80;
            float deviceLongitude = (float) (aDeviceLocation.getLongitude() - UserLocation.getLongitude()) * 80;
            canvas.drawCircle(deviceLatitude, deviceLongitude, 20, cPaint);           // draw the beacons
            double dist = beacons.get(locations.indexOf(aDeviceLocation)).getAccuracy();
            if (dist < 3) {
                canvas.drawText("I'm here!", deviceLongitude/8*7, deviceLatitude, tPaint);
            }
        }
    }

    // called by higher component that detects and informs the beacons nearby
    public void updateViewParams(List<BluetoothLeDevice> allDevices) {
        List<BeaconLocation> locations = new ArrayList<>();
        List<IBeaconDevice> beacons = filterDevices(allDevices, locations);
        if (locations.size() > 2) {
            // updating location mapping parameters
            this.beacons = beacons;
            this.locations = locations;
            UserLocation.locate(beacons);
        }
        // updating the view
        invalidate();
    }

    private List<IBeaconDevice> filterDevices(List<BluetoothLeDevice> allDevices, List<BeaconLocation> locations) {
        List<IBeaconDevice> filtered = new ArrayList<>();
        for (final BluetoothLeDevice device : allDevices) {
            if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON &&
                    device.getIBeaconDevice().getAccuracy() < 8) {
                try {
                    locations.add(BeaconLocationData.getLocation(device.getIBeaconDevice()));
                    filtered.add(device.getIBeaconDevice());
                } catch (BeaconUnrecognisedException bue) {
                }
            }
        }
        return filtered;
    }

    public List<IBeaconDevice> getFilteredBeacons() {
        return beacons;
    }
}
