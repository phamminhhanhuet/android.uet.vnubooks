package com.uet.android.mouspad.Model;

import android.util.Log;

import com.uet.android.mouspad.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private String user_id;
    private String fullname;
    private String account;
    private String description;
    private String avatar ;
    private String background;
    private String email;
    private String gender;
    private Date birthday;

    public  User(){

    }

    public User(String user_id, String fullname, String account, String description, String avatar, String background, String email, String gender, Date birthday) {
        this.user_id = user_id;
        this.fullname = fullname;
        this.account = account;
        this.description = description;
        this.avatar = avatar;
        this.background = background;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public JSONObject toJSON() {
        try{
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_USER_ID, user_id);
            json.put(Constants.JSON_USER_FULLNAME, fullname);
            json.put(Constants.JSON_USER_ACCOUNT, account);
            json.put(Constants.JSON_USER_DESCRIPTION, description);
            json.put(Constants.JSON_USER_AVARTAR, avatar);
            json.put(Constants.JSON_USER_BACKGROUND, background);
            json.put(Constants.JSON_USER_EMAIL, email);
            json.put(Constants.JSON_USER_BIRTHDAY, birthday.getTime());
            json.put(Constants.JSON_USER_GENDER, gender);
            return json;
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
        return null;
    }

    public User(JSONObject json)  {
        try {
            user_id = json.getString(Constants.JSON_USER_ID);
            fullname = json.getString(Constants.JSON_USER_FULLNAME);
            account = json.getString(Constants.JSON_USER_ACCOUNT);
            description = json.getString(Constants.JSON_USER_DESCRIPTION);
            avatar = json.getString(Constants.JSON_USER_AVARTAR);
            background = json.getString(Constants.JSON_USER_BACKGROUND);
            email = json.getString(Constants.JSON_USER_EMAIL);
            gender = json.getString(Constants.JSON_USER_GENDER);
            birthday = new Date(json.getLong(Constants.JSON_USER_BIRTHDAY));
        } catch (JSONException e){
            Log.d("Exception JSON", e.getMessage());
        }
    }
}
