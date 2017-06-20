package com.androidbeaconedmuseum;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toy on 05/06/2017.
 */

public class BeaconLocationData {
    private static List<BeaconLocation> locations = new ArrayList<>();

    static {
        // adding locations
        locations.add(new BeaconLocation("40:F3:85:90:63:9C", 0, 0));
        locations.add(new BeaconLocation("40:F3:85:90:63:A0", -1, 0));
        locations.add(new BeaconLocation("40:F3:85:90:63:99", 0, -1));
//        locations.add(new BeaconLocation(
//                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
//                "40:F3:85:90:63:9E",
//                1, 0));
//        locations.add(new BeaconLocation(
//                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
//                "40:F3:85:90:63:A1",
//                0, 1));
//        locations.add(new BeaconLocation(
//                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
//                "40:F3:85:90:63:9F",
//                1, 1));
//        locations.add(new BeaconLocation("40:F3:85:90:63:94", 1, 1));
//        locations.add(new BeaconLocation("40:F3:85:90:63:99", -1, 1));
//        locations.add(new BeaconLocation("40:F3:85:90:63:A0", 1, -1));
    }

    public static boolean isRecognisedBeacon(IBeaconDevice beacon) {
        for (BeaconLocation aLocation : locations) {
            if (aLocation.getMacAddr().equals(beacon.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public static BeaconLocation getLocation(IBeaconDevice beacon) throws BeaconUnrecognisedException {
        for (BeaconLocation aLocation : locations) {
            if (aLocation.getMacAddr().equals(beacon.getAddress())) {
                return aLocation;
            }
        }
        // else it's not recognised
        throw new BeaconUnrecognisedException();
    }
}
