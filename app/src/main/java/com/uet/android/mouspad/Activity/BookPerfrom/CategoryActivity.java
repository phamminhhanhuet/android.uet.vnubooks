package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.uet.android.mouspad.Activity.SimpleFragmentActivity;
import com.uet.android.mouspad.Fragment.Home.CategoryFragment;
import com.uet.android.mouspad.Utils.Constants;

public class CategoryActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String mCategory = getIntent().getStringExtra(CategoryFragment.CATEGORY_PARAM);
        return CategoryFragment.newInstance(mCategory);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}