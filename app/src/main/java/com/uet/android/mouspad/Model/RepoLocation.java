package com.uet.android.mouspad.Model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class RepoLocation implements Serializable {
    private GeoPoint contain;
    private String description;

    public RepoLocation() {
    }

    public RepoLocation(GeoPoint contain, String description) {
        this.contain = contain;
        this.description = description;
    }

    public GeoPoint getContain() {
        return contain;
    }

    public void setContain(GeoPoint contain) {
        this.contain = contain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
