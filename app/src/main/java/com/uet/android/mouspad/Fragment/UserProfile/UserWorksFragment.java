package com.uet.android.mouspad.Fragment.UserProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uet.android.mouspad.Adapter.UserWorkAdapter;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class UserWorksFragment extends Fragment {

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    private ArrayList<Story> mStoriesComposed;
    private UserWorkAdapter userWorkAdapter;
    private String mUserId;
    private RecyclerView mRecyclerView;
    private boolean isCurrentUser = false;
    public UserWorksFragment() {
    }


    public static UserWorksFragment newInstance(String userId) {
        UserWorksFragment fragment = new UserWorksFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mUserId =getArguments().getString(Constants.USER_ID);
        if(ConnectionUtils.isLoginValid){
            if(mUserId.equals(mFirebaseAuth.getCurrentUser().getUid())){
                isCurrentUser = true;
            }
        } else {
            isCurrentUser = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_works, container, false);
        initView(view);
        initData();
        return view;
    }
    private void initView(View view) {
        mStoriesComposed = new ArrayList<>();
        userWorkAdapter = new UserWorkAdapter(mStoriesComposed, getActivity());
        userWorkAdapter.setUserId(mUserId);
        mRecyclerView = view.findViewById(R.id.recyclerWorksUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(userWorkAdapter);
    }
    private void initData() {
        Query query = mFirebaseFirestore.collection("stories/" );
        query.get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Story story = documentSnapshot.toObject(Story.class);
                    if(story.getUser_id().equals(mUserId)){
                        if(isCurrentUser){
                            mStoriesComposed.add(story);
                            userWorkAdapter.notifyDataSetChanged();
                        }else {
                            if(story.getPublished()){
                                mStoriesComposed.add(story);
                                userWorkAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }
}