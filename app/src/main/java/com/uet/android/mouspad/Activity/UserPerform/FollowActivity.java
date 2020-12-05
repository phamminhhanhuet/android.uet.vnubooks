package com.uet.android.mouspad.Activity.UserPerform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uet.android.mouspad.Adapter.PagerAdapter;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPaperAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private FirebaseAuth mFirebaseAuth;

    private ArrayList<FollowItem> mFollowings ;
    private ArrayList<FollowItem> mFollowers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        LayoutUtils.changeStatusBarBackgroundColor(this);
        MappdingWidgets();
        ActionToolbar();
        initData();
        initView();
    }

    private void MappdingWidgets() {
        mViewPager = findViewById(R.id.viewPagerFollow);
        mPaperAdapter = new PagerAdapter(getApplicationContext(), getSupportFragmentManager(), Constants.PAGER_ADAPTER_FOLLOW_REQUEST);
        mViewPager.setAdapter(mPaperAdapter);
        mTabLayout = findViewById(R.id.tabLayoutFollow);
        mToolbar = findViewById(R.id.toolbarFollow);
    }

    private void ActionToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.text_follow));
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFollowings = (ArrayList<FollowItem>) getIntent().getSerializableExtra(Constants.USER_FOLLOWING);
        mFollowers = (ArrayList<FollowItem>) getIntent().getSerializableExtra(Constants.USER_FOLLOWERS);
        mPaperAdapter.setFollowers(mFollowers);
        mPaperAdapter.setFollowings(mFollowings);
    }

    private void initView() {
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        mTabLayout.setTabTextColors(android.R.color.darker_gray, LayoutUtils.Constant.colorPrimary);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}