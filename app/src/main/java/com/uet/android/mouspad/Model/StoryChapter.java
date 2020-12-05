package com.uet.android.mouspad.Model;

import android.util.Log;

import com.uet.android.mouspad.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class StoryChapter implements Serializable {
    private String chapter_id;
    private String story_id;
    private String title;
    private String content;
    private String cover;
    private String audio;
    private String youtube;
    private Date timestamp;
    private boolean published;
    public StoryChapter(){
    }

    public StoryChapter(String chapter_id, String story_id, String title, String content, String cover, String audio, String youtube, Date timestamp, boolean published) {
        this.chapter_id = chapter_id;
        this.story_id = story_id;
        this.title = title;
        this.content = content;
        this.cover = cover;
        this.audio = audio;
        this.youtube = youtube;
        this.timestamp = timestamp;
        this.published = published;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public JSONObject toJSON() {
        try{
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_CHAPTER_ID, chapter_id);
            json.put(Constants.JSON_CHAPTER_STORY_ID, story_id);
            json.put(Constants.JSON_CHAPTER_TITLE, title);
            json.put(Constants.JSON_CHAPTER_CONTENT, content);
            json.put(Constants.JSON_CHAPTER_COVER, cover);
            json.put(Constants.JSON_CHAPTER_AUDIO, audio);
            json.put(Constants.JSON_CHAPTER_YOUTUBE, youtube);
            json.put(Constants.JSON_CHAPTER_TIMESTAMP, timestamp.getTime());
            json.put(Constants.JSON_CHAPTER_PUBLISHED, published);
            return json;
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
        return null;
    }

    public StoryChapter(JSONObject json)  {
        try {
            chapter_id = json.getString(Constants.JSON_CHAPTER_ID);
            story_id = json.getString(Constants.JSON_CHAPTER_STORY_ID);
            title = json.getString(Constants.JSON_CHAPTER_TITLE);
            content = json.getString(Constants.JSON_CHAPTER_CONTENT);
            cover = json.getString(Constants.JSON_CHAPTER_COVER);
            audio = json.getString(Constants.JSON_CHAPTER_AUDIO);
            youtube = json.getString(Constants.JSON_CHAPTER_YOUTUBE);
            timestamp = new Date(json.getLong(Constants.JSON_CHAPTER_TIMESTAMP));
            published = json.getBoolean(Constants.JSON_CHAPTER_PUBLISHED);
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
    }
}
