package com.uet.android.mouspad.Activity.UserPerform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Home.UpdatesFragment;

public class UpdatesActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return UpdatesFragment.newInstance();
    }
}