package com.uet.android.mouspad.Model.ViewModel;

import android.content.Context;

import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.User;
import java.util.ArrayList;

public class LibraryStoryModel {
    private ArrayList<Story> mStories;
    private ArrayList<User> mUsers;
    private ArrayList<LibraryChapterModel> mLibraryChapterModels;
    private ArrayList<LibraryItem> mLibraryItems;
    private Context mContext;

    public LibraryStoryModel(Context context){
        mStories = new ArrayList<>();
        mUsers = new ArrayList<>();
        mLibraryChapterModels = new ArrayList<>();
        mLibraryItems = new ArrayList<>();
        this.mContext = context;
    }

    public ArrayList<Story> getStories() {
        return mStories;
    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public  ArrayList<LibraryChapterModel> getLibraryChapterModel() {
        return mLibraryChapterModels;
    }

    public void setStories(ArrayList<Story> mStories) {
        this.mStories = mStories;
    }

    public void setUsers(ArrayList<User> mUsers) {
        this.mUsers = mUsers;
    }

    public void setLibraryChapterModels( ArrayList<LibraryChapterModel> mLibraryChapterModel) {
        this.mLibraryChapterModels = mLibraryChapterModel;
    }

    public ArrayList<LibraryItem> getLibraryItems() {
        return mLibraryItems;
    }

    public void setLibraryItems(ArrayList<LibraryItem> mLibraryItems) {
        this.mLibraryItems = mLibraryItems;
    }
}
