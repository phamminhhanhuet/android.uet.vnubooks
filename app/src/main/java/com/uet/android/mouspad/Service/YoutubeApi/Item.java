package com.uet.android.mouspad.Service.YoutubeApi;

public class Item {
    public String kind;
    public String etag;
    public Id id;
    public Snippet snippet;

    public Id getId() {
        return id;
    }

    public Item setId(Id id) {
        this.id = id;
        return this;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public Item setSnippet(Snippet snippet) {
        this.snippet = snippet;
        return this;
    }
}
