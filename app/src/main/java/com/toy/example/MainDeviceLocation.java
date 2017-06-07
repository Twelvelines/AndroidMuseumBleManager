package com.toy.example;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

/**
 * Created by toy on 04/06/2017.
 */

public class MainDeviceLocation {
    private static double longitude;
    private static double latitude;

    public static void locate(IBeaconDevice iBeaconOne, IBeaconDevice iBeaconTwo, IBeaconDevice iBeaconThree) {
        try {
            double d_1 = iBeaconOne.getAccuracy();
            double d_2 = iBeaconTwo.getAccuracy();
            double d_3 = iBeaconThree.getAccuracy();
            double x_1 = BeaconDeviceLocationData.getLocation(iBeaconOne).getLatitude();
            double y_1 = BeaconDeviceLocationData.getLocation(iBeaconOne).getLongitude();
            double x_2 = BeaconDeviceLocationData.getLocation(iBeaconTwo).getLatitude();
            double y_2 = BeaconDeviceLocationData.getLocation(iBeaconTwo).getLongitude();
            double x_3 = BeaconDeviceLocationData.getLocation(iBeaconThree).getLatitude();
            double y_3 = BeaconDeviceLocationData.getLocation(iBeaconThree).getLongitude();
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

    public static double getLongitude() {
        return longitude;
    }

    public static double getLatitude() {
        return latitude;
    }
}
