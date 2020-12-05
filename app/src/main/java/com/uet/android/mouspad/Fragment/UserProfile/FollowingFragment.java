package com.uet.android.mouspad.Fragment.UserProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class FollowingFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String user_id;

    private ArrayList<InformationAction> mInformationActions;
    private ArrayList<FollowItem> mFollowings;
    private InformationActionAdapter mInformationActionAdapter;
    private ArrayList<String> mUserIds = new ArrayList<>();

    private RecyclerView mRecyclerView;

    public FollowingFragment() {
    }

    public static FollowingFragment newInstance(ArrayList<FollowItem> followItems) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_FOLLOWING, followItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFollowings = (ArrayList<FollowItem>) getArguments().getSerializable(Constants.USER_FOLLOWING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        MappingWidgets(view);
        initView(view);
        initData();
        return view;
    }

    private void MappingWidgets(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerFollowing);

    }

    private void initData()  {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mFirebaseAuth.getCurrentUser().getUid();
        if(mFollowings.size() > 0){
            for(final FollowItem item: mFollowings){
                mUserIds.add(item.getUser_id());
                mFirebaseFirestore.collection("users").document(item.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        InformationAction action = new InformationAction(user.getAvatar(), user.getAccount(), user.getDescription(), item.getTimestamp());
                        mInformationActions.add(action);
                        mInformationActionAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }


    private void initView(View view) {
        mInformationActions = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mInformationActionAdapter = new InformationActionAdapter( mInformationActions, getContext(),this);
        mInformationActionAdapter.setRequestCode(Constants.GALLERY_REQUEST_CODE_FOR_USER);
        mInformationActionAdapter.setUserId(mUserIds);
        mRecyclerView.setAdapter(mInformationActionAdapter);
    }
}