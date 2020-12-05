package com.uet.android.mouspad.Activity.UserPerform;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.UserFragment;
import com.uet.android.mouspad.Utils.Constants;

public class UserActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String userId = getIntent().getStringExtra(Constants.USER_ID);
        return UserFragment.newInstance(userId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
