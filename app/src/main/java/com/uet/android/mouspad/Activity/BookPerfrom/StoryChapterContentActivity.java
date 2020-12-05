package com.uet.android.mouspad.Activity.BookPerfrom;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.StoryInterface.StoryChapterContentFragment;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class StoryChapterContentActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        int position = getIntent().getIntExtra(Constants.STORY_CHAPTER_INDEX, 0);
        String storyId = getIntent().getStringExtra(Constants.STORY_INDEX);
        String storyTitle = getIntent().getStringExtra(Constants.STORY_TITLE);
        ArrayList<String> titles = (ArrayList<String>) getIntent().getSerializableExtra(Constants.STORY_CHAPTER_TITLE);
        ArrayList<String> chapterIds = (ArrayList<String>) getIntent().getSerializableExtra(Constants.STORY_CHAPTER_LIST);
        String ownerId = getIntent().getStringExtra(Constants.OWNER_ID);
        return StoryChapterContentFragment.newInstance(position, storyId, storyTitle, ownerId, chapterIds, titles);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}