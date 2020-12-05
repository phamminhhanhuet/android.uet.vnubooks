package com.uet.android.mouspad.Model;

import java.util.Date;

public class StoryUserItem {


    private String story_id;
    private Date timestamp;
    private boolean published;

    public StoryUserItem() {
    }

    public StoryUserItem(String story_id, Date timestamp, boolean published) {
        this.story_id = story_id;
        this.timestamp = timestamp;
        this.published = published;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
