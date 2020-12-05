package com.uet.android.mouspad.Activity.BookPerfrom;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.StoryInterface.StoryDetailFragment;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class StoryDetailActivity extends SimpleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int position = getIntent().getIntExtra(Constants.STORY_INDEX, 0);
        ArrayList<Story> storyList = (ArrayList<Story>) getIntent().getSerializableExtra(Constants.STORY_LIST);
        return StoryDetailFragment.newInstance(position,storyList );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}