package com.uet.android.mouspad.Service.YoutubeApi;

public class Id {
    public String kind;
    public String channelId;
    public String videoId;

    public String getKind() {
        return kind;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getVideoId() {
        return videoId;
    }

    public Id setVideoId(String videoId) {
        this.videoId = videoId;
        return this;
    }
}
