package com.uet.android.mouspad.Service.Map.Geocode;

public class Result {
    public Geometry geometry ;

    public Geometry getGeometry() {
        return geometry;
    }

    public Result setGeometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }
}
