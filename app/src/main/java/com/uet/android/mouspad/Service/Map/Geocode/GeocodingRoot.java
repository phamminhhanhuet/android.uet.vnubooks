package com.uet.android.mouspad.Service.Map.Geocode;

import java.util.List;

public class GeocodingRoot {
    private List<Result> results ;

    public List<Result> getResults() {
        return results;
    }

    public GeocodingRoot setResults(List<Result> results) {
        this.results = results;
        return this;
    }
}
