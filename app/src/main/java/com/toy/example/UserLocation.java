package com.toy.example;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toy on 04/06/2017.
 */

public class UserLocation {
    private static double longitude;
    private static double latitude;

    public static void locate(List<IBeaconDevice> selected) {
        /*// initialising a known beacon list of three
        List<IBeaconDevice> selected = selectBeacons(beacons);
        if (selected.size() < 3) {
            // abandoning the attempt to locate the user
            return;
        }*/
        double d_1 = selected.get(0).getAccuracy();
        double d_2 = selected.get(1).getAccuracy();
        double d_3 = selected.get(2).getAccuracy();
        try {
            double x_1 = BeaconDeviceLocationData.getLocation(selected.get(0)).getLatitude();
            double y_1 = BeaconDeviceLocationData.getLocation(selected.get(0)).getLongitude();
            double x_2 = BeaconDeviceLocationData.getLocation(selected.get(1)).getLatitude();
            double y_2 = BeaconDeviceLocationData.getLocation(selected.get(1)).getLongitude();
            double x_3 = BeaconDeviceLocationData.getLocation(selected.get(2)).getLatitude();
            double y_3 = BeaconDeviceLocationData.getLocation(selected.get(2)).getLongitude();
            // algorithm part
            double k_1 = -1 * (x_2 - x_1) / (y_2 - y_1);
            double k_2 = -1 * (x_3 - x_2) / (y_3 - y_2);
            double b_1 = (Math.pow(x_2, 2) - Math.pow(x_1, 2) + Math.pow(d_1, 2) - Math.pow(d_2, 2)) / (2 * (y_2 - y_1)) + (y_1 + y_2) / 2;
            double b_2 = (Math.pow(x_3, 2) - Math.pow(x_2, 2) + Math.pow(d_2, 2) - Math.pow(d_3, 2)) / (2 * (y_3 - y_2)) + (y_2 + y_3) / 2;
            double x = (b_2 - b_1) / (k_1 - k_2);
            double y = k_1 * (b_2 - b_1) / (k_1 - k_2) + b_1;

            latitude = x;
            longitude = y;
        } catch (BeaconUnrecognisedException be) {
        }

    }

    private static List<IBeaconDevice> selectBeacons(List<IBeaconDevice> beacons) {
        List<IBeaconDevice> selected = new ArrayList<>();
        for (IBeaconDevice aBeacon : beacons) {
            if (selected.size() > 2) {
                break;
            }
            if (BeaconDeviceLocationData.isRecognisedBeacon(aBeacon)) {
                selected.add(aBeacon);
            }
        }
        return selected;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static double getLatitude() {
        return latitude;
    }
}
