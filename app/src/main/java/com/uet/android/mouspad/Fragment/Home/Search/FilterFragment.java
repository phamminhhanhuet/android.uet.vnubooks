package com.uet.android.mouspad.Fragment.Home.Search;

import android.app.Activity;
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
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;



public class FilterFragment extends Fragment {
    private Toolbar mToolbar;
    private CheckBox mCheckBook;
    private CheckBox mCheckAuthor;

    public FilterFragment() {
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        getActivity().setTheme(LayoutUtils.Constant.theme);

        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        return view;
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarFilter);
        mCheckBook = view.findViewById(R.id.checkStoryFilter);
        mCheckAuthor = view.findViewById(R.id.checkAuthorFilter);
    }

    private void initView(View view) {
//        mCheckBook.setChecked(false);
//        mCheckAuthor.setChecked(false);
        mCheckBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent data = new Intent();
                data.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_BOOK);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
//        mCheckBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    final Intent data = new Intent();
//                    data.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_BOOK);
//                    getActivity().setResult(Activity.RESULT_OK, data);
//                    getActivity().finish();
//            }
//        });
        mCheckAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent data = new Intent();
                data.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_AUTHOR);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        });
//        mCheckAuthor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                final Intent data = new Intent();
//                data.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_AUTHOR);
//                getActivity().setResult(Activity.RESULT_OK, data);
//                getActivity().finish();
//            }
//        });

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