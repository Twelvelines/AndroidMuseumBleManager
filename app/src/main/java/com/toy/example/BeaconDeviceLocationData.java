package com.toy.example;

import com.blakequ.bluetooth_manager_lib.device.ibeacon.IBeaconDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toy on 05/06/2017.
 */

public static class BeaconDeviceLocationData {
    private static List<BeaconDeviceLocation> locations = new ArrayList<>();

    static {
        //TODO adding locations
        locations.add(new BeaconDeviceLocation("uuid", "major", "minor", -1, -1));
        locations.add(new BeaconDeviceLocation("uuid", "major", "minor", -1, -1));
        locations.add(new BeaconDeviceLocation("uuid", "major", "minor", -1, -1));
    }

    public static BeaconDeviceLocation getLocation(IBeaconDevice beacon) throws BeaconUnrecognisedException {
        for (BeaconDeviceLocation aLocation : locations) {
            if (aLocation.getUuid().equals(beacon.getUUID()) &&
                    aLocation.getMajor().equals(beacon.getMajor()) &&
                    aLocation.getMinor().equals(beacon.getMajor())) {
                return aLocation;
            }
        }
        // else it's not recognised
        throw new BeaconUnrecognisedException();
    }
}
