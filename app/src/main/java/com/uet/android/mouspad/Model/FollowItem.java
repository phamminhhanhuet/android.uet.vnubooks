package com.uet.android.mouspad.Model;

import java.io.Serializable;
import java.util.Date;

public class FollowItem implements Serializable {
    private String user_id;
    private Date timestamp;

    public FollowItem() {
    }

    public FollowItem(String user_id, Date timestamp) {
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
