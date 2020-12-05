package com.uet.android.mouspad.Activity.UserPerform;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Inbox.InboxFragment;
import com.uet.android.mouspad.Utils.Constants;

public class InboxActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String user_id = getIntent().getStringExtra(Constants.USER_ID);
        return InboxFragment.newInstance(user_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}