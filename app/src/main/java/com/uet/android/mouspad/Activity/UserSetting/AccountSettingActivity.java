package com.uet.android.mouspad.Activity.UserSetting;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.UserSetting.AccountSettingFragment;
import com.uet.android.mouspad.Utils.Constants;

public class AccountSettingActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String mUserId = getIntent().getStringExtra(Constants.USER_ID);
        return AccountSettingFragment.newInstance(mUserId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}