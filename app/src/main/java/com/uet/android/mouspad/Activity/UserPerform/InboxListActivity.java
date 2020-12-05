package com.uet.android.mouspad.Activity.UserPerform;

import androidx.fragment.app.Fragment;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Inbox.InboxListFragment;

public class InboxListActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return InboxListFragment.newInstance();
    }
}