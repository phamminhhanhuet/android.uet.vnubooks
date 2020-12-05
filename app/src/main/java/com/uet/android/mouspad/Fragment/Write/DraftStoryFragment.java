package com.uet.android.mouspad.Fragment.Write;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.uet.android.mouspad.Adapter.WriteStudioAdapter;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryUserItem;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;

public class DraftStoryFragment extends Fragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;

    private String mUserId;

    private ArrayList<Story> mStories;
    private WriteStudioAdapter mWriteStudioAdapter;

    public DraftStoryFragment() {
    }

    public static DraftStoryFragment newInstance() {
        return new DraftStoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view= inflater.inflate(R.layout.fragment_draft_story, container, false);
        initView(view);
        initData();
        Log.d("darafgjl", "new  2");
        //     initModelView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        mWriteStudioAdapter.startListening();
    }

    @Override
    public void onStop() {
//        mWriteStudioAdapter.stopListening();
        super.onStop();
    }

    private void initModelView(View view){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mUserId = mFirebaseAuth.getCurrentUser().getUid();


        Query queryStory = mFirebaseFirestore.collection("stories/").limit(10);
        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(queryStory, Story.class)
                .build();
//        mWriteStudioAdapter = new com.uet.android.mouspad.Adapter.FirebaseUI.WriteStudioAdapter(ebook_menu);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerDraftWriteStudio);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
//        recyclerView.setAdapter(mWriteStudioAdapter);
    }

    private void initData() {
        Log.d("darafgjl", "new 3");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mUserId = mFirebaseAuth.getCurrentUser().getUid();
        Log.d("darafgjl", "new dra" + mUserId);


        Query query = mFirebaseFirestore.collection("story_user/" + mUserId + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    StoryUserItem item = documentSnapshot.toObject(StoryUserItem.class);
                    if(!item.isPublished()){
                        mFirebaseFirestore.collection("stories").document(item.getStory_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Story story = task.getResult().toObject(Story.class);
                                if(!story.getPublished()){
                                    mStories.add(story);
                                    mWriteStudioAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void initView(View view) {
        Log.d("darafgjl", "new 4");

        mStories = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerDraftWriteStudio);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mWriteStudioAdapter = new WriteStudioAdapter(mStories,getContext());
        recyclerView.setAdapter(mWriteStudioAdapter);
    }
}
