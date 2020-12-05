package com.uet.android.mouspad.Activity.UserPerform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Adapter.StoryAdapter;
import com.uet.android.mouspad.Adapter.UserWorkAdapter;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Retrofit;

public class ReadingListActivity extends AppCompatActivity {

    public static boolean isChoosen = false;
    private Toolbar mToolbar;
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private String list_id ;
    private String title;
    private ArrayList<Story> mStories;
    private ArrayList<String> mIds ;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_list);
        MappingWidgets();
        ActionToolbar();
        initView();
    }


    private void MappingWidgets() {
        mToolbar = findViewById(R.id.toolbarReadingList);
        mEditText = findViewById(R.id.editReadingList);
        mRecyclerView = findViewById(R.id.recyclerReadingList);
    }
    private void ActionToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setTitle(R.string.text_edit);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final Intent data = new Intent();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("list_id", list_id);
        map.put("title", mEditText.getText().toString());
        FirebaseFirestore.getInstance().collection("reading_list_index/" + mUserId + "/contain").document(list_id).set(map);
        data.putExtra(Constants.READING_LIST_INDEX, list_id);
        data.putExtra(Constants.READING_LIST_TITLE, mEditText.getText().toString());
        data.putExtra(Constants.READING_LIST_DATA, mStories);
        setResult(Activity.RESULT_OK, data);
        finish();
        super.onBackPressed();
    }

    private UserWorkAdapter mUserWorkAdapter ;
    private void initView() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        list_id = getIntent().getStringExtra(Constants.READING_LIST_INDEX);
        title = getIntent().getStringExtra(Constants.READING_LIST_TITLE);
        mStories = (ArrayList<Story>) getIntent().getSerializableExtra(Constants.READING_LIST_DATA);
        mIds = (ArrayList<String>) getIntent().getSerializableExtra(Constants.READING_LIST_IDS);

        mEditText.setText(title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mUserWorkAdapter = new UserWorkAdapter(mStories, this);
        mUserWorkAdapter.setReadingList(true, list_id);
        mRecyclerView.setAdapter(mUserWorkAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.reading_list_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_delete_reading_list){
            FirebaseFirestore.getInstance().collection("reading_list_index").document(list_id).delete();
            FirebaseFirestore.getInstance().collection("reading_lists/" + mUserId + "/contain").document(list_id).delete();
            setResult();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setResult(){
        final Intent data = new Intent();
        data.putExtra(Constants.READING_LIST_INDEX, list_id);
        data.putExtra(Constants.READING_LIST_TITLE, "delete");
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}