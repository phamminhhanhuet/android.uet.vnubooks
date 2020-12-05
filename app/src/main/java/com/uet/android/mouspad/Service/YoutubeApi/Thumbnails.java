package com.uet.android.mouspad.Service.YoutubeApi;

public class Thumbnails {
    public Default dfault;
    public Medium medium;
    public High high;

    public Medium getMedium() {
        return medium;
    }

    public Thumbnails setMedium(Medium medium) {
        this.medium = medium;
        return this;
    }
}
