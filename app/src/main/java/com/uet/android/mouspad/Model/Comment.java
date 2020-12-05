package com.uet.android.mouspad.Model;

import java.util.Date;
public class Comment {
    private String message, user_id;
    private String image;
    private Date timestamp;

    public Comment(){

    }

    public Comment(String message, String user_id, String image, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

