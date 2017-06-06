package com.toy.example;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

/**
 * Created by toy on 05/06/2017.
 */

public class BeaconDeviceLocation {
    private double latitude, longitude;
    private String uuid, major, minor;

    public BeaconDeviceLocation(String uuid, String major, String minor, double longitude, double latitude) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
