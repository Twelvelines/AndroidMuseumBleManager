package com.androidbeaconedmuseum;

/**
 * Created by toy on 15/06/2017.
 */

public class Artwork {
    private String addr, name;
    private int imageSrc = -1;
    private int textSrc = -1;

    public Artwork(String name, String addr, int textSrc) {
        this.name = name;
        this.addr = addr;
        this.textSrc = textSrc;
    }

    public Artwork(String name, String addr, int textSrc, int imageSrc) {
        this(name, addr, textSrc);
        this.imageSrc = imageSrc;
    }

    public String getAddr() {
        return addr;
    }

    public String getName() {
        return name;
    }

    public int getImageSrc() {
        return imageSrc;
    }

    public int getTextSrc() {
        return textSrc;
    }
}
