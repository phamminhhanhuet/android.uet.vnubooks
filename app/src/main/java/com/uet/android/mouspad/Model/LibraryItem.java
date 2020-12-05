package com.uet.android.mouspad.Model;

import android.util.Log;

import com.uet.android.mouspad.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LibraryItem {
    private String story_id;
    private String owner_id;
    private Date timestamp;
    private int status;
    private boolean downloaded;

    public LibraryItem(){
    }

    public LibraryItem(String story_id, String owner_id, Date timestamp, int status, boolean downloaded) {
        this.story_id = story_id;
        this.owner_id = owner_id;
        this.timestamp = timestamp;
        this.status = status;
        this.downloaded = downloaded;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public JSONObject toJSON() {
        try{
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_LIBRARY_ITEM_STORY_ID, story_id);
            json.put(Constants.JSON_LIBRARY_ITEM_OWNER_ID, owner_id);
        //    json.put(Constants.JSON_LIBRARY_ITEM_TIMESTAMP, timestamp.getTime());
            json.put(Constants.JSON_LIBRARY_ITEM_STATUS, status);
            json.put(Constants.JSON_LIBRARY_ITEM_DOWNLOADED, downloaded);
            return json;
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
        return null;
    }

    public LibraryItem(JSONObject json)  {
        try {
            story_id = json.getString(Constants.JSON_LIBRARY_ITEM_STORY_ID);
            owner_id = json.getString(Constants.JSON_LIBRARY_ITEM_OWNER_ID);
           // timestamp = new Date(json.getLong(Constants.JSON_LIBRARY_ITEM_TIMESTAMP));
            status = json.getInt(Constants.JSON_LIBRARY_ITEM_STATUS);
            downloaded = json.getBoolean(Constants.JSON_LIBRARY_ITEM_DOWNLOADED);
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
    }
}
