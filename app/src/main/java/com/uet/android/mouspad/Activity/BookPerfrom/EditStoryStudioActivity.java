package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Write.EditStoryStudio.EditStoryStudioFragment;
import com.uet.android.mouspad.Utils.Constants;

public class EditStoryStudioActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String story_id = getIntent().getStringExtra(Constants.STORY_INDEX);
        return EditStoryStudioFragment.newInstance(story_id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}