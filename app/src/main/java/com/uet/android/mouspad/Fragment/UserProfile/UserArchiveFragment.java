package com.uet.android.mouspad.Fragment.UserProfile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uet.android.mouspad.Activity.UserPerform.ReadingListActivity;
import com.uet.android.mouspad.Adapter.StoryAdapter;
import com.uet.android.mouspad.Model.ReadingListIndex;
import com.uet.android.mouspad.Model.ReadingListItem;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserArchiveFragment extends Fragment {
    ArrayList<Integer> mContentName ;

    private String mUserId;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    private Map<String,Object> mReadingMap;
    private List<ReadingListIndex> mReadingListIndices;
    private boolean isCurrentUser ;

    private View mView;
    public UserArchiveFragment() {
    }

    public static UserArchiveFragment newInstance(String userId) {
        UserArchiveFragment fragment = new UserArchiveFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_ID,userId );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mUserId = getArguments().getString(Constants.USER_ID);
        if(ConnectionUtils.isLoginValid){
            isCurrentUser = mUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else {
            isCurrentUser = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_archive, container, false);
        MappingWidgets(view);
        initView(view);
        initData(view);
        mView = view;
        return view;
    }

    private void MappingWidgets(View view) {
    }

    private void initData(final View view){
        Query query = mFirebaseFirestore.collection("reading_list_index/" + mUserId + "/contain").limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    ReadingListIndex index = documentSnapshot.toObject(ReadingListIndex.class);
                    mReadingListIndices.add(index);
                    Log.d("REAdinglist i", index.getList_id());
                }
                initReadingList(view);
            }
        });
    }

    private void initReadingList(final View view){
        for(final ReadingListIndex index : mReadingListIndices){
            final String list_id = index.getList_id();
            Log.d("REAdinglist ", list_id);

            final ArrayList<ReadingListItem> listItems = new ArrayList<>();
            Query readingList = mFirebaseFirestore.collection("reading_lists/" + mUserId+ "/contain/" + list_id + "/contain" ).orderBy("timestamp", Query.Direction.DESCENDING);
            readingList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot documentSnapshot :task.getResult()){
                        ReadingListItem readingListItem = documentSnapshot.toObject(ReadingListItem.class);
                        listItems.add(readingListItem);
                    }
                    mReadingMap.put(index.getList_id(), listItems);
                    Log.d("REAdinglist s" , "" + listItems.size());
                    if(mReadingMap.size() == mReadingListIndices.size()){
                        mappingRecyclerView(view);
                    }
                }
            });
        }
    }

    private void mappingRecyclerView(View view){
        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutReadingList);
        if(mReadingListIndices.size() == 0){
            linearLayout.setVisibility(View.GONE);
        }
        for(int i = 0 ; i <linearLayout.getChildCount(); i ++){
            ConstraintLayout view1 = (ConstraintLayout) linearLayout.getChildAt(i);
            if(i >=1 && i <= mReadingListIndices.size()){
                final TextView textView = (TextView) view1.getChildAt(0);
                final String id = mReadingListIndices.get(i-1).getList_id();

                ArrayList<ReadingListItem> readingListItems = (ArrayList<ReadingListItem>) mReadingMap.get(mReadingListIndices.get(i -1).getList_id());
                ArrayList<Story> storyList = new ArrayList<>();

                textView.setText(mReadingListIndices.get(i -1).getTitle());

                TextView txtTotal = (TextView) view1.getChildAt(1);
                //txtTotal.setText(storyList.size() + " stories");

                RecyclerView recyclerView = (RecyclerView) view1.getChildAt(2);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                StoryAdapter storyAdapter = new StoryAdapter(storyList, getActivity());
                storyAdapter.setTitleVisible(false);
                recyclerView.setAdapter(storyAdapter);
                if(!readingListItems.isEmpty())   getStoryFromList(readingListItems, storyAdapter, storyList, textView, txtTotal);
            } else if(i == mReadingListIndices.size() +1){
                view1.setVisibility(View.VISIBLE);
                view1.getChildAt(0).setVisibility(View.GONE);
                view1.getChildAt(1).setVisibility(View.GONE);
            } else {
                view1.setVisibility(View.GONE);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView(View view) {
        mContentName = new ArrayList<>();
        mContentName.add(R.string.text_story_by);
        mContentName.add(R.string.text_reading_list);

        mReadingListIndices = new ArrayList<>();
        mReadingMap = new HashMap<>();
    }
    private void getStoryFromList(final ArrayList<ReadingListItem>readingListItems, final StoryAdapter storyAdapter, final ArrayList<Story> list, final TextView textView, final TextView txtTotal){
        for(final ReadingListItem item : readingListItems){
            String story_id = item.getStory_id();
            String owner_id = item.getOwner_id();
            mFirebaseFirestore.collection("stories/").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Story story = task.getResult().toObject(Story.class);
                            list.add(story);
                            storyAdapter.notifyDataSetChanged();
                            if(list.size() == readingListItems.size()){
                                if(isCurrentUser){
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getContext(), ReadingListActivity.class);
                                            intent.putExtra(Constants.READING_LIST_INDEX,item.getList_id());
                                            intent.putExtra(Constants.READING_LIST_TITLE, textView.getText());
                                            intent.putExtra(Constants.READING_LIST_DATA, list);
                                            startActivityForResult(intent, Constants.READING_MODE_MOUSPAD);
                                        }
                                    });
                                }
                                txtTotal.setText(list.size() + " stories");
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.READING_MODE_MOUSPAD && data!= null) {
            String listId = data.getStringExtra(Constants.READING_LIST_INDEX);
            String titleNew = data.getStringExtra(Constants.READING_LIST_TITLE);
            ArrayList<Story> stories = (ArrayList<Story>) data.getSerializableExtra(Constants.READING_LIST_DATA);
            if(titleNew.equals("delete")){
                for(ReadingListIndex index: mReadingListIndices){
                    if(index.getList_id().equals(listId)){
                        mReadingListIndices.remove(index);
                    }
                }
                mReadingMap.remove(listId);
            } else {
                editView(mView, listId, titleNew, stories);
            }
        }
    }

    private void editView(View view, String listId, String title, ArrayList<Story> newListStories) {
        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutReadingList);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ConstraintLayout view1 = (ConstraintLayout) linearLayout.getChildAt(i);
            if (i >= 1 && i <= mReadingListIndices.size()) {
                if (listId.equals(mReadingListIndices.get(i-1).getList_id())) {
                    final TextView textView = (TextView) view1.getChildAt(0);
                    textView.setText(title);
                    ArrayList<ReadingListItem> readingListItems = (ArrayList<ReadingListItem>) mReadingMap.get(mReadingListIndices.get(i - 1).getList_id());
                    if (readingListItems.size() == newListStories.size()) {
                        break;
                    } else {
                        RecyclerView recyclerView = (RecyclerView) view1.getChildAt(1);
                        StoryAdapter storyAdapter = new StoryAdapter(newListStories, getActivity());
                        recyclerView.setAdapter(storyAdapter);
                    }
                }

            }
        }
    }
}