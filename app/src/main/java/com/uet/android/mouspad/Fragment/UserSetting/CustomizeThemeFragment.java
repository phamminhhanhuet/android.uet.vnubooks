package com.uet.android.mouspad.Fragment.UserSetting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uet.android.mouspad.Activity.Authorize.LoginActivity;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CustomizeThemeFragment extends Fragment {

    private SharedPreferences mSharedPreferences ;
    private SharedPreferences.Editor mEditor;
    private Button mButton;
    private LayoutUtils.Method mMethod;
    private Toolbar mToolbar;

    public CustomizeThemeFragment() {
    }

    public static CustomizeThemeFragment newInstance() {
       return new CustomizeThemeFragment();
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
        View view = inflater.inflate(R.layout.fragment_customize_theme, container, false);
        MappingWidgets(view);
        initData();
        ActionToolbar();
        initView(view);
        return view;
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarCustomizeTheme);
        mButton = view.findViewById(R.id.btnCustomizeTheme);
    }

    private void initData() {
        mSharedPreferences = getActivity().getSharedPreferences(Constants.CUSTOMIZE_THEME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_customize_theme);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initView(View view) {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker colorPicker  = new ColorPicker(getActivity());
                colorPicker.setColors(0xffF44336, 0xffE9163,0xff9C27B0,0xff673AB7,0xff3F51B5, 0xff03A9F4, 0xff4CAF50,0xffFF9800, 0xff9E9E9E, 0xff795548)
                        .setColumns(5);
                colorPicker.setRoundColorButton(true);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position,int color) {
                        LayoutUtils.Constant.color = color;
                        mMethod.setColorTheme();
                        mEditor.putInt("color",color);
                        mEditor.putInt("theme", LayoutUtils.Constant.theme);
                        mEditor.apply();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancel(){
                        // put code
                    }
                });
                colorPicker.show();
            }
        });
    }

}