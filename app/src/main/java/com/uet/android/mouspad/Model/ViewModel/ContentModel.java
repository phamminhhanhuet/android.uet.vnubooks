package com.uet.android.mouspad.Model.ViewModel;

import com.uet.android.mouspad.Model.StoryChapter;

import java.util.ArrayList;
import java.util.List;

public class ContentModel {
    private List<StoryChapter> chapterList = new ArrayList<>();
    private List<String> tagList = new ArrayList<>();
    private String location ="";

    public ContentModel() {
    }

    public ContentModel(List<StoryChapter> chapterList, List<String> tagList, String location) {
        this.chapterList = chapterList;
        this.tagList = tagList;
        this.location = location;
    }

    public List<StoryChapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<StoryChapter> chapterList) {
        this.chapterList = chapterList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
