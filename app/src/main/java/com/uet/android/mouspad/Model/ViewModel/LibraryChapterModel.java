package com.uet.android.mouspad.Model.ViewModel;

import com.uet.android.mouspad.Model.StoryChapter;

import java.util.ArrayList;

public class LibraryChapterModel {
    private ArrayList<StoryChapter> mStoryChapters;

    public ArrayList<StoryChapter> getStoryChapters() {
        return mStoryChapters;
    }

    private  LibraryChapterModel (Builder builder){
        this.mStoryChapters = builder.mStoryChapters;
    }

    public static class Builder {
        private ArrayList<StoryChapter> mStoryChapters;

        public Builder setStoryChapters(ArrayList<StoryChapter> mStoryChapters) {
            this.mStoryChapters = mStoryChapters;
            return this;
        }

        public LibraryChapterModel build(){
            return new LibraryChapterModel(this);
        }
    }
}
