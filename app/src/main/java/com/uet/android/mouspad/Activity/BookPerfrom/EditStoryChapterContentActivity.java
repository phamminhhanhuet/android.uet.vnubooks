package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Write.EditStoryStudio.EditStoryChapterContentFragment;

import static com.uet.android.mouspad.Utils.Constants.STORY_CHAPTER_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class EditStoryChapterContentActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
//        int position = getIntent().getIntExtra(Constants.STORY_CHAPTER_INDEX, -1);
        String storyId = getIntent().getStringExtra(STORY_INDEX);
        String chapterId = getIntent().getStringExtra(STORY_CHAPTER_INDEX);
        return EditStoryChapterContentFragment.newInstance(storyId, chapterId);
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}