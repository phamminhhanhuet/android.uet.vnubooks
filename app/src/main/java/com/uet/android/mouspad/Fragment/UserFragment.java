package com.uet.android.mouspad.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.UserPerform.FollowActivity;
import com.uet.android.mouspad.Activity.UserPerform.InboxActivity;
import com.uet.android.mouspad.Activity.UserSetting.AccountSettingActivity;
import com.uet.android.mouspad.Activity.UserSetting.UserSettingActivity;
import com.uet.android.mouspad.Adapter.PagerAdapter;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.Model.RepoLocation;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class UserFragment extends Fragment {

    private Toolbar mToolbar;
    private ImageView mImageAvatar, mImageBackground;
    private TextView mTxtName, mTxtAccountName, mTxtDescription, mTxtLocation, mTxtTotalWrite, mTxtTotalFollowing, mTxtTotalFollower;
    private Button mButtonFollow ,mButtonFollowing;
    private ImageView mBtnInbox;
    private ViewPager mViewPager;
    private PagerAdapter mPaperAdapter;
    private  TabLayout mTabLayout;

    private User mUser;
    private String mUserId;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    private boolean isCurrentUser = false;

    private UserFragment() {
    }


    public static UserFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_ID, userId);
        UserFragment fragment = new UserFragment();
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
            isCurrentUser = mUserId.equals(mFirebaseAuth.getCurrentUser().getUid());
        } else {
            isCurrentUser = false;
        }
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view= inflater.inflate(R.layout.fragment_user, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        initData(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.MAP_REQUEST_USER_CODE){

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(isCurrentUser == true){
            mToolbar.inflateMenu(R.menu.user_menu);
        } else {
            mToolbar.inflateMenu(R.menu.user_interact_menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_user_setting:
                Intent intent = new Intent(getContext(), UserSettingActivity.class);
                intent.putExtra(Constants.USER_ID, mUserId);
                startActivity(intent);
                return true;
            case R.id.action_inbox_user:
                if(ConnectionUtils.isLoginValid){
                    Intent inboxIntent = new Intent(getContext(), InboxActivity.class);
                    inboxIntent.putExtra(Constants.USER_ID, mUserId);
                    startActivity(inboxIntent);
                } else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return false;
        }
    }

    private void MappingWidgets (View view){
        mToolbar = view.findViewById(R.id.toolbarUser);
        mImageAvatar = view.findViewById(R.id.imgAvatarUser);
        mImageBackground = view.findViewById(R.id.imgBackgroundUser);
        mTxtName = view.findViewById(R.id.txtNameUser);
        mTxtAccountName = view.findViewById(R.id.txtAccountUser);
        mTxtDescription = view.findViewById(R.id.txtDescriptionUser);
        mTxtLocation = view.findViewById(R.id.txtLocationUser);
        mTxtTotalWrite = view.findViewById(R.id.txtTotalWriteUser);
        mTxtTotalFollowing = view.findViewById(R.id.txtTotalFollowingUser);
        mTxtTotalFollower = view.findViewById(R.id.txtTotalFollowerUser);
        mButtonFollow = view.findViewById(R.id.btnFollowUser);
        mBtnInbox = view.findViewById(R.id.btnInboxUser);
        mViewPager = view.findViewById(R.id.viewPagerUser);
        mPaperAdapter = new PagerAdapter(getContext(), getChildFragmentManager(), Constants.PAGER_ADAPTER_USER_REQUEST);
        mViewPager.setAdapter(mPaperAdapter);
        mPaperAdapter.setUserId(mUserId);
        mTabLayout = view.findViewById(R.id.tabLayoutUser);
    }
    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackground(getResources().getDrawable(R.drawable.transparent_total));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private String userID = "";
    private boolean isFollowing = false;
    private RepoLocation mRepoLocation;
    private void initData(final View view) {
        if(isCurrentUser){
            mButtonFollow.setText("Edit your profile");
            isFollowing = false;
        } else {
            if(ConnectionUtils.isLoginValid){
               userID =  mFirebaseAuth.getCurrentUser().getUid();
                mFirebaseFirestore.collection("follows/" + mUserId + "/contain").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            isFollowing = true;
                        }
                        if(isFollowing){
                            mButtonFollow.setVisibility(View.VISIBLE);
                            mButtonFollow.setText("Unfollow this user");
                            mButtonFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mFirebaseFirestore.collection("follows/" + mUserId + "/contain").document(userID).delete();
                                    mFirebaseFirestore.collection("followings/" + userID + "/contain").document(mUserId).delete();
                                    mButtonFollow.setText("Follow this user");
                                    isFollowing = false;
                                }
                            });
                        } else {
                            mButtonFollow.setVisibility(View.VISIBLE);
                            mButtonFollow.setText("Follow this user");
                            mButtonFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String, Object>map = new HashMap<>();
                                    map.put("timestamp", FieldValue.serverTimestamp());
                                    map.put("user_id", userID);
                                    mFirebaseFirestore.collection("follows/" + mUserId + "/contain").document(userID).set(map);
                                    Map<String, Object>hashMap = new HashMap<>();
                                    hashMap.put("timestamp", FieldValue.serverTimestamp());
                                    hashMap.put("user_id", mUserId);
                                    mFirebaseFirestore.collection("followings/" + userID + "/contain").document(mUserId).set(map);
                                    mButtonFollow.setText("Unfollow this user");
                                    isFollowing = true;
                                }
                            });
                        }
                    }
                });
            } else {
                mButtonFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        mFirebaseFirestore.collection("users").document(mUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            mUser = task.getResult().toObject(User.class);
                            mTxtName.setText(mUser.getFullname());
                            mTxtAccountName.setText("@" + mUser.getAccount());
                            if(mUser.getDescription() != null){
                                String description = mUser.getDescription();
                                if(description.length() > 100)
                                {
                                    String trim = description.substring(0, 97);
                                    mTxtDescription.setText(trim + "...");
                                }else {
                                    mTxtDescription.setText(mUser.getDescription());
                                }

                            }
                            Uri avatarUri = Uri.parse(mUser.getAvatar());
                            Picasso.get()
                                    .load(avatarUri)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(mImageAvatar);
                            Uri backgroundUri = Uri.parse(mUser.getBackground());
                            Picasso.get()
                                    .load(backgroundUri)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(mImageBackground);
                        }
                        if(ConnectionUtils.isLoginValid){
                            userID =  mFirebaseAuth.getCurrentUser().getUid();
                            if(mUserId.equals(userID)){
                                mButtonFollow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getContext(), AccountSettingActivity.class);
                                        intent.putExtra(Constants.USER_ID, mUserId);
                                        startActivity(intent);
                                    }
                                });
                            }
                            mBtnInbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent inboxIntent = new Intent(getContext(), InboxActivity.class);
                                    inboxIntent.putExtra(Constants.USER_ID, mUserId);
                                    startActivity(inboxIntent);
                                }
                            });
                        } else {
                            mBtnInbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });

        mFirebaseFirestore.collection("locations").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    String place = mRepoLocation.getDescription();
                    if(place!= null && !place.equals("")){
                        mTxtLocation.setText(place);
                    } else {
                        mTxtLocation.setText("This user's not have a location yet");
                        //mTxtLocation.setVisibility(View.GONE);
                    }
                }
            }
        });

        getTotalWorks();
        getTotalFollowers();
    }

    private void getTotalWorks(){
        Query query = mFirebaseFirestore.collection("story_user/" + mUserId + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int size = task.getResult().size();
                    mTxtTotalWrite.setText(size + "");
                } else {
                    mTxtTotalWrite.setText("0");
                }
            }
        });
    }

    private ArrayList<FollowItem> followings = new ArrayList<>();
    private ArrayList<FollowItem> followers = new ArrayList<>();

    private void getTotalFollowers (){
        Query query = mFirebaseFirestore.collection("follows/" + mUserId + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int size = task.getResult().size();
                    mTxtTotalFollower.setText(size + "");
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        FollowItem followItem = documentSnapshot.toObject(FollowItem.class);
                        followers.add(followItem);
                    }
                    getTotalFollowing();
                } else {
                    mTxtTotalFollower.setText("0");
                }
            }
        });
    }

    private void getTotalFollowing(){
        Query query = mFirebaseFirestore.collection("followings/" + mUserId + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int size = task.getResult().size();
                    mTxtTotalFollowing.setText(size + "");
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        FollowItem followItem = documentSnapshot.toObject(FollowItem.class);
                        followings.add(followItem);
                    }
                    mTxtTotalFollowing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), FollowActivity.class);
                            intent.putExtra(Constants.USER_FOLLOWING, followings);
                            intent.putExtra(Constants.USER_FOLLOWERS, followers);
                            startActivity(intent);
                        }
                    });

                    mTxtTotalFollower.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), FollowActivity.class);
                            intent.putExtra(Constants.USER_FOLLOWING, followings);
                            intent.putExtra(Constants.USER_FOLLOWERS, followers);
                            startActivity(intent);
                        }
                    });
                } else {
                    mTxtTotalFollowing.setText("0");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView(View view) {
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        mTabLayout.setTabTextColors( getResources().getColor(android.R.color.darker_gray), LayoutUtils.Constant.colorPrimary);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_BOTTOM);
    }
}