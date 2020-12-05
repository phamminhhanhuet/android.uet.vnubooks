package com.uet.android.mouspad.Fragment.Home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.uet.android.mouspad.Adapter.SearchTopicAdapter;
import com.uet.android.mouspad.Adapter.StoryAdapter;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {


    public static final String CATEGORY_PARAM = "CATEGORY_PARAM";

    private String mCategory;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_PARAM, category);
        fragment.setArguments(args);
        Log.d("CategoryD", category);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(CATEGORY_PARAM);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView();
        return view;
    }

    private void ActionToolbar(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mCategory);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarCategory);
        mRecyclerView = view.findViewById(R.id.recyclerCategory);
    }

    private void initView(){
        if(mCategory.equals("Classic Books")){
            requestGenreSearch("Classics");
        } else {
            requestGenreSearch("Biograph");
        }
    }

    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    private void requestGenreSearch(String searchableAttribute){
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_story");

        Query querySearch = new Query(searchableAttribute)
                .setAttributesToRetrieve("storyGenre")
                .setHitsPerPage(50);
        index.searchAsync(querySearch, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    JSONArray hits = jsonObject.getJSONArray("hits");
                    Log.d("SearchAl js", jsonObject.toString());

                    ArrayList<Story> stories = new ArrayList<>();

                    for(int i = 0;i < hits.length(); i ++){
                        JSONObject object = hits.getJSONObject(i);
                        String infor = object.getString("storyGenre");

                        JSONObject info = hits.getJSONObject(i).getJSONObject("_highlightResult");
                        JSONObject title = info.getJSONObject("storyTitle");
                        JSONObject id = info.getJSONObject("instanceId");
                        JSONObject cover = info.getJSONObject("storyCover");
                        JSONObject ownerId = info.getJSONObject("ownerId");
                        JSONObject genre = info.getJSONObject("storyGenre");
                        JSONObject status = info.getJSONObject("storyStatus");
                        JSONObject description = info.getJSONObject("storyDes");
                        // JSONObject published = info.getJSONObject("storyPublish");
                        JSONObject format = info.getJSONObject("storyFormat");

                        String titleString = title.getString("value").replace("<em>", "").replace("</em>", "");
                        String idString = id.getString("value");
                        String coverString = cover.getString("value");
                        String ownerIdString = ownerId.getString("value");
                        String genreString = genre.getString("value").replace("<em>", "").replace("</em>", "");
                        String statusString = status.getString("value").replace("<em>", "").replace("</em>", "");
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");
                        //boolean publishedString = published.getBoolean("value");
                        String formatString = format.getString("value");
                        Log.d("SearchAl s", titleString);
                        Log.d("SearchAl s", idString);

                        Story story = new Story(idString, ownerIdString, titleString, descriptionString, coverString, genreString, statusString, formatString, true);
                        stories.add(story);
                    }

                   // StoryAdapter storyAdapter = new StoryAdapter( stories, getContext());
                    SearchTopicAdapter searchTopicAdapter = new SearchTopicAdapter(stories, getContext());
                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setAdapter(searchTopicAdapter);
                    if(mRecyclerView.getVisibility() == View.GONE || mRecyclerView.getVisibility()== View.INVISIBLE){
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    Log.d("SearchAl s", "" + searchTopicAdapter.getItemCount());

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}