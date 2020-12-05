package com.uet.android.mouspad.Activity.UserSetting;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.UserSetting.UserSettingFragment;
import com.uet.android.mouspad.Utils.Constants;

public class UserSettingActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String mUserId = getIntent().getStringExtra(Constants.USER_ID);
        return UserSettingFragment.newInstance(mUserId);
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