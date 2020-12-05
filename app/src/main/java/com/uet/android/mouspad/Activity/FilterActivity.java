package com.uet.android.mouspad.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

public class FilterActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private CheckBox mCheckBook;
    private CheckBox mCheckAuthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setTheme(LayoutUtils.Constant.theme);
        MappingWidgets();
        initView();
        ActionToolbar();
    }
    private void MappingWidgets() {
        mToolbar = findViewById(R.id.toolbarFilter);
        mCheckBook = findViewById(R.id.checkStoryFilter);
        mCheckAuthor = findViewById(R.id.checkAuthorFilter);
    }

    private void initView() {
        mCheckBook.setChecked(false);
        mCheckAuthor.setChecked(false);
        mCheckBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mCheckBook.isChecked() == false){
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_BOOK);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        mCheckAuthor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent();
                intent.putExtra(Constants.SEARCH_FILTER, Constants.SEARCH_AUTHOR);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    private void ActionToolbar() {
       setSupportActionBar(mToolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.text_search);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
    }
}