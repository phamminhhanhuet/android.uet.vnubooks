package com.uet.android.mouspad.Model;

public class ReadingListIndex {
    String list_id;
    String title;

    public ReadingListIndex() {
    }

    public ReadingListIndex(String list_id, String title) {
        this.list_id = list_id;
        this.title = title;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
