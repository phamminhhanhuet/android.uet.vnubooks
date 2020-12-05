package com.uet.android.mouspad.Fragment.Home;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.uet.android.mouspad.Activity.FilterActivity;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Adapter.SearchTopicAdapter;
import com.uet.android.mouspad.Fragment.HomeFragment;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;


public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener{
    private Toolbar mToolbar ;
    private TextView mTextFilter;
    private TextView mTextReset;
    private RecyclerView mRecyclerView;

    private LinearLayout mLayoutResult ;
    private LinearLayout mDashboardGenre;

    private SearchView mSearchView;


    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    private boolean searchingStory = true;
    private boolean isSearch = false;
    private static final int REQUEST_FILTER_SEARCH = 012;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new  SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        HomeFragment.mTabLayout.setBackgroundColor(LayoutUtils.Constant.color);
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        checkSearchPermission(true);
//        CollectionReference collectionReference = mFirebaseFirestore.collection("story_user");
//        Map<String,Object> mapOne = new HashMap<>();
//        mapOne.put("storyTitle", "Title 1");
//        Map<String,Object> mapTwo = new HashMap<>();
//        mapTwo.put("storyTitle", "Title 2");
//        Map<String,Object> mapThree = new HashMap<>();
//        mapThree.put("storyTitle", "Title 3");
//        WriteBatch writeBatch = mFirebaseFirestore.batch();
//        writeBatch.set(collectionReference.document(), mapOne);
//        writeBatch.set(collectionReference.document(), mapTwo);
//        writeBatch.set(collectionReference.document(), mapThree);
//        writeBatch.commit();
//
//

//        List<JSONObject> array = new ArrayList<>();
//        array.add(new JSONObject(mapOne));
//        array.add(new JSONObject(mapTwo));
//        array.add(new JSONObject(mapThree));
//        index.addObjectsAsync(new JSONArray(array), null);

//        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    List<String> list = new ArrayList<>();
//                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                        Log.d("SearchAl", documentSnapshot.getId() + "==>" + documentSnapshot.getData());
//                        list.add(documentSnapshot.getString("storyTitle"));
//                    }
//                    ArrayAdapter<String> adapter= new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list);
//                    listView.setAdapter(adapter);
//                } else {
//                    Log.d("SearchAl", "Get Error");
//                }
//            }
//        });
//
//        List<String> newList = new ArrayList<>();
//        newList.add("Title 1");
//        newList.add("Title 2");
//        newList.add("Title 3");
//        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,newList);
//        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarSearch);
        mSearchView = view.findViewById(R.id.searchViewHome);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        mTextFilter = view.findViewById(R.id.filterSearchHome);
        mTextFilter.setVisibility(View.INVISIBLE);
        mTextReset = view.findViewById(R.id.txtSearchReset);
        mRecyclerView = view.findViewById(R.id.recyclerViewSearchTopic);

        mLayoutResult = view.findViewById(R.id.layoutResultSearch);
        mDashboardGenre = view.findViewById(R.id.dashboardGenre);
        mLayoutResult.setVisibility(View.GONE);
        mDashboardGenre.setVisibility(View.VISIBLE);
    }

    private void ActionToolbar(){
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setTitle(R.string.text_search);
    }

    CardView cardViewAction, cardViewClassic, cardViewComic, cardViewHis,  cardViewLiter, cardViewRomance, cardViewScience, cardViewReligion,
        cardViewPoet, cardViewDiary, cardViewBio, cardViewEssay, cardViewText, cardViewPolitic;

    private void initView(View view) {
        mTextFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, REQUEST_FILTER_SEARCH);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mLayoutResult.getVisibility() == View.GONE){
                    mLayoutResult.setVisibility(View.VISIBLE);
                }
                mDashboardGenre.setVisibility(View.GONE);
                if(mTextFilter.getVisibility() == View.INVISIBLE){
                    mTextFilter.setVisibility(View.VISIBLE);
                    if(searchingStory){
                       // Toast.makeText(getContext(), "Yayyy", Toast.LENGTH_SHORT).show();
                        requestSearch(query);
                    } else {
                   //     Toast.makeText(getContext(), "UUUUyy", Toast.LENGTH_SHORT).show();
                        requestUserSearch(query);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        cardViewAction = view.findViewById(R.id.dashboardAction);
        cardViewBio = view.findViewById(R.id.dashboardBio);
        cardViewClassic = view.findViewById(R.id.dashboardClassic);
        cardViewComic = view.findViewById(R.id.dashboardComic);
        cardViewDiary = view.findViewById(R.id.dashboardDiary);
        cardViewEssay = view.findViewById(R.id.dashboardEssay);
        cardViewHis = view.findViewById(R.id.dashboardHistory);
        cardViewLiter = view.findViewById(R.id.dashboardLiter);
        cardViewPoet = view.findViewById(R.id.dashboardPoetry);
        cardViewPolitic = view.findViewById(R.id.dashboardPolitic);
        cardViewReligion = view.findViewById(R.id.dashboardReligion);
        cardViewRomance = view.findViewById(R.id.dashboardRomance);
        cardViewScience = view.findViewById(R.id.dashboardScience);
        cardViewText = view.findViewById(R.id.dashboardTextBook);


        cardViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_action));
            }
        });

        cardViewBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_bio));
            }
        });
        cardViewClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_classic));
            }
        });
        cardViewComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_comic));
            }
        });
        cardViewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_dia));
            }
        });
        cardViewEssay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_doc));
            }
        });
        cardViewHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_his));
            }
        });
        cardViewLiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_lit));
            }
        });
        cardViewPoet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_poet));
            }
        });
        cardViewPolitic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_politic));
            }
        });
        cardViewReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_rel));
            }
        });
        cardViewRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_rom));
            }
        });
        cardViewScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_sci));
            }
        });
        cardViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGenreSearch(getString(R.string.genre_text));
            }
        });

        mTextReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSearch && mDashboardGenre.getVisibility() == View.GONE && mLayoutResult.getVisibility() == View.VISIBLE){
                    mDashboardGenre.setVisibility(View.VISIBLE);
                    mLayoutResult.setVisibility(View.GONE);
                    isSearch = false;
                    mTextReset.setText(getString(R.string.text_search));
                    if(!searchingStory){
                        searchingStory = true;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILTER_SEARCH) {
            if(resultCode == Activity.RESULT_OK) {
                final String result = data.getStringExtra(Constants.SEARCH_FILTER);
                if(result.equals(Constants.SEARCH_BOOK)){
                    searchingStory = true;
                } else if(result.equals(Constants.SEARCH_AUTHOR)){
                    searchingStory = false;
                }
                if(searchingStory){
                    requestSearch(mSearchView.getQuery().toString());
                } else {
                    requestUserSearch(mSearchView.getQuery().toString());
                }
                Toast.makeText(getContext(), "Result: " + result, Toast.LENGTH_LONG).show();
            } else {
            }
        }
    }

    private boolean checkSearchPermission(boolean yay) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.GLOBAL_SEARCH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.GLOBAL_SEARCH},
                    yay? REQUEST_READ_EXTERNAL_STORAGE : REQUEST_READ_EXTERNAL_STORAGE_NOYAY);
            return false;
        }
        return true;
    }

    private static final int REQUEST_READ_EXTERNAL_STORAGE_NOYAY = 4333;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 4334;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean yay = true;
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_NOYAY:
                yay = false;
            case REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (yay) Toast.makeText(getContext(), "Yay", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Boo", Toast.LENGTH_LONG).show();
                }

        }
    }

    private void requestSearch(String searchableAttribute){
        isSearch = true;
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_story");

        Query querySearch = new Query(searchableAttribute)
                .setAttributesToRetrieve("storyInfo")
                .setHitsPerPage(50);
        index.searchAsync(querySearch, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    JSONArray hits = jsonObject.getJSONArray("hits");
                    Log.d("SearchAl js", jsonObject.toString());

                    ArrayList<Story> stories = new ArrayList<>();
                    SearchTopicAdapter searchTopicAdapter = new SearchTopicAdapter(stories, getContext());
                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setAdapter(searchTopicAdapter);
                    if(mRecyclerView.getVisibility() == View.GONE || mRecyclerView.getVisibility()== View.INVISIBLE){
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    Log.d("SearchAl s", "" + searchTopicAdapter.getItemCount());
                    mTextReset.setText(getString(R.string.text_reload));
                    for(int i = 0;i < hits.length(); i ++){
                        JSONObject object = hits.getJSONObject(i);
                        String infor = object.getString("storyInfo");

                        JSONObject info = hits.getJSONObject(i).getJSONObject("_highlightResult");
                        JSONObject title = info.getJSONObject("storyTitle");
                        JSONObject id = info.getJSONObject("instanceId");
                        JSONObject cover = info.getJSONObject("storyCover");
                        JSONObject ownerId = info.getJSONObject("ownerId");
                        JSONObject genre = info.getJSONObject("storyGenre");
                        JSONObject status = info.getJSONObject("storyStatus");
                        JSONObject description = info.getJSONObject("storyDes");
                        JSONObject format = info.getJSONObject("storyFormat");

                        String titleString = title.getString("value").replace("<em>", "").replace("</em>", "");
                        String idString = id.getString("value").replace("<em>", "").replace("</em>", "");;
                        String coverString = cover.getString("value");;
                        String ownerIdString = ownerId.getString("value").replace("<em>", "").replace("</em>", "");;
                        String genreString = genre.getString("value").replace("<em>", "").replace("</em>", "");
                        String statusString = status.getString("value").replace("<em>", "").replace("</em>", "");
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");
                        String formatString = format.getString("value").replace("<em>", "").replace("</em>", "");;
                        Log.d("SearchAl s", titleString);
                        Log.d("SearchAl s", idString);
                        Log.d("SearchAl s", formatString);

                        Story story = new Story(idString, ownerIdString, titleString, descriptionString, coverString, genreString, statusString, formatString, true);
                        stories.add(story);
                        Log.d("SearchAl siz", "" + stories.size());
                        searchTopicAdapter.notifyDataSetChanged();
                    }
                    Log.d("SearchAl s", "end" + "" + stories.size());


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void requestUserSearch(String searchableAttribute){
        isSearch = true;
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_user");
        Query querySearch = new Query(searchableAttribute)
                .setAttributesToRetrieve("userInfo")
                .setHitsPerPage(50);
        index.searchAsync(querySearch, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    JSONArray hits = jsonObject.getJSONArray("hits");
                    Log.d("SearchAl", hits.toString());
                    ArrayList<InformationAction> mInformationActions = new ArrayList<>();
                    ArrayList<String> mUserIds = new ArrayList<>();
                    for(int i = 0;i < hits.length(); i ++){
                        Log.d("SearchAlU", jsonObject.toString());

                        JSONObject object = hits.getJSONObject(i);
                        String infor = object.getString("userInfo");

                        JSONObject info = hits.getJSONObject(i).getJSONObject("_highlightResult");
                        Log.d("SearchAl ddds", "" + info.toString());

                        JSONObject id = info.getJSONObject("instanceId");
                        Log.d("SearchAl ddds", "" + id.toString());

                        JSONObject account = info.getJSONObject("userAccount");
                        Log.d("SearchAl ddds", "" + account.toString());

                        JSONObject fullname = info.getJSONObject("userFullname");
                        Log.d("SearchAl ddds", "" + fullname.toString());

                        JSONObject avatar = info.getJSONObject("userAvatar");
                        Log.d("SearchAl ddds", "" + avatar.toString());

                        JSONObject background = info.getJSONObject("userBackground");
                        Log.d("SearchAl ddds", "" + background.toString());

                        JSONObject description = info.getJSONObject("userDes");
                        Log.d("SearchAl ddds", "" + description.toString());

                        String idString = id.getString("value").replace("<em>", "").replace("</em>", "");;
                        String accountString = account.getString("value").replace("<em>", "").replace("</em>", "");
                        String fullnameIdString = fullname.getString("value");
                        String avatarString = avatar.getString("value").replace("<em>", "").replace("</em>", "");;
                        String backgroundString = background.getString("value");
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");

                        mUserIds.add(idString);
                        InformationAction informationAction = new InformationAction(avatarString, accountString, descriptionString, new Date(System.currentTimeMillis()));
                        mInformationActions.add(informationAction);
                    }

                    InformationActionAdapter mInformationActionAdapter = new InformationActionAdapter( mInformationActions, getContext(),null);
                    mInformationActionAdapter.setRequestCode(Constants.GALLERY_REQUEST_CODE_FOR_USER);
                    mInformationActionAdapter.setUserId(mUserIds);
                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    if(mRecyclerView.getVisibility() == View.GONE || mRecyclerView.getVisibility()== View.INVISIBLE){
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    Log.d("SearchAl ddds", "" + mInformationActions.size());

                    mRecyclerView.setAdapter(mInformationActionAdapter);
                    mTextReset.setText(getString(R.string.text_reload));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void requestGenreSearch(String searchableAttribute){
        isSearch = true;
        mDashboardGenre.setVisibility(View.GONE);
        mLayoutResult.setVisibility(View.VISIBLE);
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
                        String idString = id.getString("value").replace("<em>", "").replace("</em>", "");;
                        String coverString = cover.getString("value");;
                        String ownerIdString = ownerId.getString("value").replace("<em>", "").replace("</em>", "");;
                        String genreString = genre.getString("value").replace("<em>", "").replace("</em>", "");
                        String statusString = status.getString("value").replace("<em>", "").replace("</em>", "");
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");
                        //boolean publishedString = published.getBoolean("value");
                        String formatString = format.getString("value").replace("<em>", "").replace("</em>", "");;
                        Log.d("SearchAl s", titleString);
                        Log.d("SearchAl s", idString);
                        Log.d("SearchAl s", formatString);

                        Story story = new Story(idString, ownerIdString, titleString, descriptionString, coverString, genreString, statusString, formatString, true);
                        stories.add(story);
                        Log.d("SearchAl s", "" + stories.size());
                    }

                    SearchTopicAdapter searchTopicAdapter = new SearchTopicAdapter(stories, getContext());
                    mRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setAdapter(searchTopicAdapter);
                    if(mRecyclerView.getVisibility() == View.GONE || mRecyclerView.getVisibility()== View.INVISIBLE){
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    Log.d("SearchAl s", "" + searchTopicAdapter.getItemCount());
                    mTextReset.setText(getString(R.string.text_reload));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        if(mTextFilter.getVisibility() == View.INVISIBLE){
            mTextFilter.setVisibility(View.VISIBLE);
            if(searchingStory){
                requestSearch(query);
            } else {
                requestUserSearch(query);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}