package com.uet.android.mouspad.Model.ViewModel;

import com.uet.android.mouspad.Model.User;

import java.util.List;

public class ListModel {
    private List<User> users;
    private List<ContentModel> contentModels;

    public ListModel() {
    }

    public ListModel(List<User> users,  List<ContentModel> contentModelss) {
        this.users = users;
        this.contentModels = contentModels;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    public List<ContentModel> getContentModels() {
        return contentModels;
    }

    public void setContentModels(List<ContentModel> contentModels) {
        this.contentModels = contentModels;
    }

}
