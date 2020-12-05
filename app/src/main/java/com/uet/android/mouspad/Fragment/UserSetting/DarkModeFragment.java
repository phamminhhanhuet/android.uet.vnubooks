package com.uet.android.mouspad.Fragment.UserSetting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.uet.android.mouspad.Activity.HomeActivity;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;

public class DarkModeFragment extends Fragment {
    private Toolbar mToolbar;
    private CheckBox mDarkOn;
    private CheckBox mDarkOff;
    public DarkModeFragment() {

    }

    public static DarkModeFragment newInstance() {
        return new DarkModeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_dark_mode, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        return view;
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarDarkModeSetting);
        mDarkOn = view.findViewById(R.id.checkOnDarkMode);
        mDarkOff = view.findViewById(R.id.checkOffDarkMode);
    }

    private void initView(View view) {
        mDarkOn.setChecked(false);
        mDarkOff.setChecked(false);
        mDarkOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mDarkOn.isChecked() == false){
                    Toast.makeText(getContext(), "dark", Toast.LENGTH_SHORT).show();
                    LayoutUtils.Constant.theme = R.style.AppThemeDark;
                    LayoutUtils.Constant.colorPrimaryDark = 0xff795548;
                    LayoutUtils.Constant.color = 0xff5F6AA8;
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        mDarkOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            }
        });

    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_dark_mode);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

}