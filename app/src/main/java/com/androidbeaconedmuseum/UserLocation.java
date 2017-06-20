package com.androidbeaconedmuseum;

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
            double x_1 = BeaconLocationData.getLocation(selected.get(0)).getLatitude();
            double y_1 = BeaconLocationData.getLocation(selected.get(0)).getLongitude();
            double x_2 = BeaconLocationData.getLocation(selected.get(1)).getLatitude();
            double y_2 = BeaconLocationData.getLocation(selected.get(1)).getLongitude();
            double x_3 = BeaconLocationData.getLocation(selected.get(2)).getLatitude();
            double y_3 = BeaconLocationData.getLocation(selected.get(2)).getLongitude();
            // algorithm part
            double k_1 = 2 * (x_2 - x_1);
            double k_2 = 2 * (y_2 - y_1);
            double b_1 = Math.pow(d_1, 2) - Math.pow(d_2, 2) + Math.pow(x_2, 2) - Math.pow(x_1, 2) + Math.pow(y_2, 2) - Math.pow(y_1, 2);
            double k_3 = 2 * (x_3 - x_1);
            double k_4 = 2 * (y_3 - y_1);
            double b_2 = Math.pow(d_1, 2) - Math.pow(d_3, 2) + Math.pow(x_3, 2) - Math.pow(x_1, 2) + Math.pow(y_3, 2) - Math.pow(y_1, 2);
            double x, y;
            if (k_2 == 0) {
                x = b_1 / k_1;
                y = (b_2 - k_3 * x) / k_4;
            } else if (k_4 == 0) {
                x = b_2 / k_3;
                y = (b_1 - k_1 * x) / k_2;
            } else {
                x = (k_2 * b_2 - k_4 * b_1) / (k_3 * k_2 - k_4 * k_1);
                y = (b_1 - k_1 * x) / k_2;
            }

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
            if (BeaconLocationData.isRecognisedBeacon(aBeacon)) {
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
