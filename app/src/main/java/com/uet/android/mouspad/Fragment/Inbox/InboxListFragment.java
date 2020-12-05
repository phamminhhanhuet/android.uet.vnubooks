package com.uet.android.mouspad.Fragment.Inbox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Adapter.InboxListAdapter;
import com.uet.android.mouspad.Model.InboxList;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;

import java.util.ArrayList;

public class InboxListFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private InboxListAdapter mInboxListAdapter;
    private ArrayList<User> mUsersList;

    private FirebaseUser mUser;

    private FirebaseFirestore mFirebaseFirestore;

    private ArrayList<InboxList> mInboxList;

    public InboxListFragment() {
    }


    public static InboxListFragment newInstance() {
        InboxListFragment fragment = new InboxListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_inbox_list, container, false);

        MappingWidgets(view);
        initModelView(view);

        return view;
    }

    private void MappingWidgets(View view){
        mRecyclerView = view.findViewById(R.id.recyclerInboxList);
    }


    private void initModelView(View view) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mInboxList = new ArrayList<>();
        mUsersList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mInboxListAdapter = new InboxListAdapter(getContext(),mUsersList, true);
        mRecyclerView.setAdapter(mInboxListAdapter);

        queryAllInboxList();
    }

    private void queryAllInboxList() {
        Query query = mFirebaseFirestore.collection("inbox_lists/" + mUser.getUid() + "/contain").orderBy("timestamp", Query.Direction.DESCENDING).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    InboxList inboxList = documentSnapshot.toObject(InboxList.class);
                    mInboxList.add(inboxList);
                    mUsersList.add(inboxList.getContact_user());
                    mInboxListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}