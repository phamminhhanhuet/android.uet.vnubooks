package com.uet.android.mouspad.Fragment.Home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.uet.android.mouspad.Adapter.PagerAdapter;
import com.uet.android.mouspad.Fragment.HomeFragment;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;


public class UpdatesFragment extends Fragment {

    private Toolbar mToolbar, mToolbarBottom;
    private ImageButton mButtonOption;

    private ViewPager mViewPager;
    private PagerAdapter mPaperAdapter;
    private TabLayout mTabLayout;

    public UpdatesFragment() {
    }

    public static UpdatesFragment newInstance() {
        return new UpdatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        HomeFragment.mTabLayout.setBackgroundColor(LayoutUtils.Constant.color);
        View view = inflater.inflate(R.layout.fragment_updates, container, false);
        MappingWidgets(view);
        ActionToolbar();
        if(ConnectionUtils.isLoginValid){
            initData();
            initView(view);
            LinearLayout linearLayout = view.findViewById(R.id.blankText);
            linearLayout.setVisibility(View.GONE);
        } else {
            LinearLayout linearLayout = view.findViewById(R.id.blankText);
            linearLayout.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void MappingWidgets(View view) {
        mViewPager = view.findViewById(R.id.viewPagerUpdates);
        mPaperAdapter = new PagerAdapter(getContext(), getChildFragmentManager(), Constants.PAGER_ADAPTER_UPDATES_REQUEST);
        mViewPager.setAdapter(mPaperAdapter);
        mTabLayout = view.findViewById(R.id.tabLayoutUpdates);

        mToolbar = view.findViewById(R.id.toolbarUpdates);
        mButtonOption = view.findViewById(R.id.btnOptionUpdates);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setTitle(R.string.text_update);
        mButtonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
    private void initData(){
    }

    private void initView(View view) {
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        mTabLayout.setTabTextColors(getResources().getColor(android.R.color.darker_gray), LayoutUtils.Constant.colorPrimary);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}