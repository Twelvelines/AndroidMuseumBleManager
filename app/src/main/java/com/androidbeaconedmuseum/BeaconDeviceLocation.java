package com.androidbeaconedmuseum;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

/**
 * Created by toy on 05/06/2017.
 */

public class BeaconDeviceLocation {
    private double latitude, longitude;
    private int major, minor;
    private String uuid, macAddr;

    public BeaconDeviceLocation(String macAddr, double latitude, double longitude) {
        this.macAddr = macAddr;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BeaconDeviceLocation(String uuid, int major, int minor, String macAddr, double latitude, double longitude) {
        this(macAddr, longitude, latitude);
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public String getUuid() {
        return uuid;
    }
}
