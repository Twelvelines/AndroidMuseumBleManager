package com.androidbeaconedmuseum.location;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toy on 05/06/2017.
 */

public class BeaconDeviceLocationData {
    private static List<BeaconDeviceLocation> locations = new ArrayList<>();

    static {
        //TODO adding locations
        locations.add(new BeaconDeviceLocation(
                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
                "40:F3:85:90:63:9E",
                0, 0));
        locations.add(new BeaconDeviceLocation(
                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
                "40:F3:85:90:63:A1",
                1, 0));
        locations.add(new BeaconDeviceLocation(
                "fda50693-a4e2-4fb1-afcf-c6eb07647825", 10002, 34452,
                "40:F3:85:90:63:9F",
                0, -1));
        locations.add(new BeaconDeviceLocation("40:F3:85:90:63:94", 1, 1));
        locations.add(new BeaconDeviceLocation("40:F3:85:90:63:99", -1, 1));
        locations.add(new BeaconDeviceLocation("40:F3:85:90:63:A0", 1, -1));
    }

    public static boolean isRecognisedBeacon(IBeaconDevice beacon) {
        for (BeaconDeviceLocation aLocation : locations) {
            if (aLocation.getMacAddr().equals(beacon.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public static BeaconDeviceLocation getLocation(IBeaconDevice beacon) throws BeaconUnrecognisedException {
        for (BeaconDeviceLocation aLocation : locations) {
            if (aLocation.getMacAddr().equals(beacon.getAddress())) {
                return aLocation;
            }
        }
        // else it's not recognised
        throw new BeaconUnrecognisedException();
    }
}
