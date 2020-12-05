package com.uet.android.mouspad.Model;

import java.util.Date;

public class InboxList {
    private User contact_user;
    private Date timestamp;

    public InboxList() {
    }

    public InboxList(User contact_user, Date timestamp) {
        this.contact_user = contact_user;
        this.timestamp = timestamp;
    }

    public User getContact_user() {
        return contact_user;
    }

    public void setContact_user(User contact_user) {
        this.contact_user = contact_user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
