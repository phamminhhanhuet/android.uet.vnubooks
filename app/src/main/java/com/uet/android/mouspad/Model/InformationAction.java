package com.uet.android.mouspad.Model;


import java.util.Date;

public class InformationAction {
    private String action_image;
    private String action_title;
    private String action_description;
    private Date timestamp;

    public InformationAction(String action_image, String action_title, String action_description, Date timestamp) {
        this.action_image = action_image;
        this.action_title = action_title;
        this.action_description = action_description;
        this.timestamp = timestamp;
    }

    public InformationAction(){

    }

    public String getAction_image() {
        return action_image;
    }

    public void setAction_image(String action_image) {
        this.action_image = action_image;
    }

    public String getAction_title() {
        return action_title;
    }

    public void setAction_title(String action_title) {
        this.action_title = action_title;
    }

    public String getAction_description() {
        return action_description;
    }

    public void setAction_description(String action_description) {
        this.action_description = action_description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
