package com.uet.android.mouspad.Model.ViewModel;

import com.uet.android.mouspad.Model.Story;

import java.util.ArrayList;

public class CategoryModel {
    private String category_title;
    private String category_id;
    private ArrayList<Story> stories;

    public CategoryModel(){
    }

    public CategoryModel(String category_id, String category_title, ArrayList<Story> stories) {
        this.category_title = category_title;
        this.category_id = category_id;
        this.stories = stories;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }
}

