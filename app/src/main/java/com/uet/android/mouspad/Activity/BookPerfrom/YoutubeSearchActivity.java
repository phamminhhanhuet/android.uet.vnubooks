package com.uet.android.mouspad.Activity.BookPerfrom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.uet.android.mouspad.Adapter.YoutubeSearchAdapter;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.YoutubeApi.Id;
import com.uet.android.mouspad.Service.YoutubeApi.Item;
import com.uet.android.mouspad.Service.YoutubeApi.Medium;
import com.uet.android.mouspad.Service.YoutubeApi.SearchRoot;
import com.uet.android.mouspad.Service.YoutubeApi.Snippet;
import com.uet.android.mouspad.Service.YoutubeApi.Thumbnails;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeSearchActivity extends AppCompatActivity {
    public static boolean isChoosen = false;
    private Toolbar mToolbar;
    private Retrofit mRetrofit;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private YoutubeSearchAdapter mYoutubeSearchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);
        MappingWidgets();
        ActionToolbar();
        initRetrofit();
        initView();
//        if(isChoosen) {
//            setChoosenResultBack();
//        }
    }

    private void ActionToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setTitle(R.string.text_youtube);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void MappingWidgets() {
        mToolbar = findViewById(R.id.toolbarYoutube);
        mSearchView = findViewById(R.id.searchViewYoutube);
        mRecyclerView = findViewById(R.id.recyclerViewYoutube);
    }

    private void initView(){
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), mSearchView.getQuery().toString() +"1", Toast.LENGTH_LONG).show();
                requestSearch();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void initRetrofit (){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.YOUTUBE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

   private void requestSearch(){
       APIService serviceAPI = mRetrofit.create(APIService.class);
       String keyword = mSearchView.getQuery().toString();
       Log.d("Youtube key", keyword);
       Call<SearchRoot> call = serviceAPI.getSearch(keyword);
       call.enqueue(new Callback<SearchRoot>() {
           @Override
           public void onResponse(Call<SearchRoot> call, Response<SearchRoot> response) {
               SearchRoot searchRoot = response.body();
               List<Item> items = searchRoot.getItems();

               ArrayList<String> list = new ArrayList<>();
               ArrayList<InformationAction> informationActionArrayList = new ArrayList<>();
               ArrayList<String> itemIds = new ArrayList<>();
               for(int i = 0; i < items.size(); i ++){
                   Snippet snippet = items.get(i).getSnippet();
                   Id id = items.get(i).getId();
                   Thumbnails thumbnails = snippet.getThumbnails();
                   Medium medium = thumbnails.getMedium();
                   InformationAction informationAction = new InformationAction(medium.getUrl(), snippet.getTitle(), snippet.getDescription(), snippet.getPublishTime());
                   informationActionArrayList.add(informationAction);
                   itemIds.add(id.getVideoId());
               }
               mYoutubeSearchAdapter = new YoutubeSearchAdapter(informationActionArrayList, YoutubeSearchActivity.this);
               mYoutubeSearchAdapter.setIds(itemIds);
               mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
               mRecyclerView.setAdapter(mYoutubeSearchAdapter);
           }
           @Override
           public void onFailure(Call<SearchRoot> call, Throwable throwable) {
           }
       });
   }

   public void setChoosenResultBack(){
           Intent data = new Intent();
           data.putExtra(Constants.YOUTUBE_RESULT_ID, mYoutubeSearchAdapter.getChoosenId());
           setResult(RESULT_OK, data);
           data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(data);
           finish();
   }
}