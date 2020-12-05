package com.uet.android.mouspad.Fragment.Home.Updates;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String user_id;

    private ArrayList<InformationAction> mInformationActions;
    private InformationActionAdapter mInformationActionAdapter;

    private RecyclerView mRecyclerView;

    private boolean isFirstLoad = true;
    private DocumentSnapshot mLastVisibleDocument =null;


    public NotificationFragment() {
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        MappingWidgets(view);
        initView(view);
        initData();
        return view;
    }

    private void MappingWidgets(View view) {
         mRecyclerView = view.findViewById(R.id.recyclerViewNotification);
    }

    private void initData()  {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mFirebaseAuth.getCurrentUser().getUid();
        Query query = mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING).limit(12);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    InformationAction informationAction = documentSnapshot.toObject(InformationAction.class);
                    if(isFirstLoad){
                        mInformationActions.add(informationAction);
                    } else {
                        mInformationActions.add(0, informationAction);
                    }
                    mInformationActionAdapter.notifyDataSetChanged();
                    i ++;
                    if (i == task.getResult().size() -1){
                        if(isFirstLoad)
                            mLastVisibleDocument = documentSnapshot;
                    }
                }
                isFirstLoad = false;
            }
        });

        if(isFirstLoad == false){
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachBottom = !recyclerView.canScrollVertically(1);
                    if(reachBottom){
                        Toast.makeText(getContext(),"Reach!", Toast.LENGTH_SHORT).show();
                        loadMoreQuery();
                    }
                }
            });
        }
    }

    private void loadMoreQuery(){
        Query nextQuery = mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(mLastVisibleDocument).limit(12);
        nextQuery.get().addOnCompleteListener(getActivity(),new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(!task.getResult().isEmpty()){
                        int i =0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            InformationAction informationAction = documentSnapshot.toObject(InformationAction.class);
                            mInformationActions.add(informationAction);
                            mInformationActionAdapter.notifyDataSetChanged();
                            i ++;
                            if (i == task.getResult().size() -1){
                                mLastVisibleDocument = documentSnapshot;
                            }
                        }

                    }
                } else {
                }
            }
        });
    }

    private void initView(View view) {
        mInformationActions = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mInformationActionAdapter = new InformationActionAdapter( mInformationActions, getContext(),this);
        mRecyclerView.setAdapter(mInformationActionAdapter);
    }

}