package com.uet.android.mouspad.Model;

public class StoryDatabase {
    private String story_id;
    private String chapter_id;
    private boolean published;
    private String audio;
    private String youtube;
    private String cover;
    private String title;
    private String content;

    public StoryDatabase() {
    }

    public StoryDatabase(String story_id, String chapter_id, boolean published, String audio, String youtube, String cover, String title, String content) {
        this.story_id = story_id;
        this.chapter_id = chapter_id;
        this.published = published;
        this.audio = audio;
        this.youtube = youtube;
        this.cover = cover;
        this.title = title;
        this.content = content;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
