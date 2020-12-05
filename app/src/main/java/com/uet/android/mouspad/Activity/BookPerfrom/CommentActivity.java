package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.StoryInterface.CommentFragment;
import com.uet.android.mouspad.Utils.Constants;

public class CommentActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String story_id = getIntent().getStringExtra(Constants.STORY_INDEX);
        String story_title = getIntent().getStringExtra(Constants.STORY_TITLE);
        String chapter_id = getIntent().getStringExtra(Constants.STORY_CHAPTER_INDEX);
        return CommentFragment.newInstance(story_id, story_title,  chapter_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}