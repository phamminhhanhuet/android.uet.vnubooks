package com.uet.android.mouspad.Model;

import java.util.Date;

public class ReadingListItem {
    private String story_id;
    private String owner_id;
    private String list_id;
    private Date timestamp;

    public ReadingListItem(){

    }

    public ReadingListItem(String story_id, String owner_id, String list_id,  Date timestamp) {
        this.story_id = story_id;
        this.owner_id = owner_id;
        this.list_id = list_id;
        this.timestamp = timestamp;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }
}
