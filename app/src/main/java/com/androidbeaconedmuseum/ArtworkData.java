package com.androidbeaconedmuseum;

import com.blakequ.androidblemanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toy on 15/06/2017.
 */

public class ArtworkData {
    private static List<Artwork> artworks = new ArrayList<>();

    static {
        // putting arkwork info here
        artworks.add(new Artwork("Demo", "40:F3:85:90:63:94", R.string.artwork_demo));
        artworks.add(new Artwork("Lab Demo", "40:F3:85:90:63:A1", R.string.artwork_labdemo, R.drawable.artwork_labdemo));
    }

    public static Artwork getArtwork(String addr) throws ArtworkNotFoundException {
        for (Artwork aWork : artworks) {
            if (addr.equals(aWork.getAddr())) {
                return aWork;
            }
        }
        throw new ArtworkNotFoundException();
    }
}
