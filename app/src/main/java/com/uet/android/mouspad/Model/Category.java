package com.uet.android.mouspad.Model;

import java.util.Date;

public class Category {
    private String category_title;
    private Date timestamp;
    public Category(){
    }

    public Category(String category_title, Date timestamp) {
        this.category_title = category_title;
        this.timestamp = timestamp;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
