package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.fragment.app.Fragment;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.WriteStudioFragment;

public class WriteStudioActivity extends SimpleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new WriteStudioFragment();
    }
}
