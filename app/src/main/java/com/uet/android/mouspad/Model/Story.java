package com.uet.android.mouspad.Model;

import android.util.Log;

import com.uet.android.mouspad.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Story implements Serializable {
    private String story_id;
    private String user_id;
    private String title;
    private String description;
    private String cover;
    private String genre;
    private String status;
    private String format;
    private Boolean published;

    public Story(){
    }

    public Story(String story_id, String user_id, String title, String description, String cover, String genre, String status, String format, Boolean published) {
        this.story_id = story_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.cover = cover;
        this.genre = genre;
        this.status = status;
        this.format =format;
        this.published = published;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public JSONObject toJSON() {
        try{
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_STORY_ID, story_id);
            json.put(Constants.JSON_STORY_OWNER_ID, user_id);
            json.put(Constants.JSON_STORY_TITLE, title);
            json.put(Constants.JSON_STORY_DESCRIPTION, description);
            json.put(Constants.JSON_STORY_COVER, cover);
            json.put(Constants.JSON_STORY_GENRE, genre);
            json.put(Constants.JSON_STORY_STATUS, status);
            json.put(Constants.JSON_STORY_PUBLISHED, published);
            json.put(Constants.JSON_STORY_FORMAT, format);
            return json;
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
        return null;
    }

    public Story(JSONObject json)  {
        try {
            story_id = json.getString(Constants.JSON_STORY_ID);
            user_id = json.getString(Constants.JSON_STORY_OWNER_ID);
            title = json.getString(Constants.JSON_STORY_TITLE);
            description = json.getString(Constants.JSON_STORY_DESCRIPTION);
            cover = json.getString(Constants.JSON_STORY_COVER);
            genre = json.getString(Constants.JSON_STORY_GENRE);
            status = json.getString(Constants.JSON_STORY_STATUS);
            published = json.getBoolean(Constants.JSON_STORY_PUBLISHED);
            format = json.getString(Constants.JSON_STORY_FORMAT);
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
    }
}
