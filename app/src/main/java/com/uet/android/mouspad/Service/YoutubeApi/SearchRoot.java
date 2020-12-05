package com.uet.android.mouspad.Service.YoutubeApi;

import java.util.List;

public class SearchRoot {
    public String kind;
    public String etag;
    public String nextPageToken;
    public String regionCode;
    public PageInfo pageInfo;
    public List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public SearchRoot setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
