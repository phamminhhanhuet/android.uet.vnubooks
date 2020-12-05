package com.uet.android.mouspad.Activity.UserSetting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Home.Updates.NotificationFragment;
import com.uet.android.mouspad.Fragment.UserSetting.NotificationSettingFragment;

public class NotificationSettingActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NotificationSettingFragment.newInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}