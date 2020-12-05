package com.uet.android.mouspad.Service.YoutubeApi;

import java.util.Date;

public class Snippet {
    public Date publishedAt;
    public String channelId;
    public String title;
    public String description;
    public Thumbnails thumbnails;
    public String channelTitle;
    public String liveBroadcastContent;
    public Date publishTime;

    public Date getPublishedAt() {
        return publishedAt;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getLiveBroadcastContent() {
        return liveBroadcastContent;
    }

    public Date getPublishTime() {
        return publishTime;
    }
}
