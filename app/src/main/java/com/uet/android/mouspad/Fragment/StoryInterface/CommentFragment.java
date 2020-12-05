package com.uet.android.mouspad.Fragment.StoryInterface;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Model.Comment;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Client;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentFragment extends Fragment {

    private Toolbar mToolbar;
    private ImageView mImageAvatar;
    private EditText mEditComment;
    private ImageButton mBtnSendComment;
    private RecyclerView mRecylerView;

    private ArrayList<InformationAction> mInformationActions;
    private InformationActionAdapter mInformationActionAdapter;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private User mUser;

    private String user_id;
    private String chapter_id;
    private String story_id;
    private String story_title;

    private APIService mApiService;


    public CommentFragment() {
    }

    public static CommentFragment newInstance(String storyId , String storyTitle, String chapterId) {
        Bundle args = new Bundle();
        args.putString(Constants.STORY_CHAPTER_INDEX, chapterId);
        args.putString(Constants.STORY_INDEX, storyId);
        args.putString(Constants.STORY_TITLE, storyTitle);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        if(ConnectionUtils.isLoginValid){
            user_id = mFirebaseAuth.getCurrentUser().getUid();
            mApiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        }
        story_id = getArguments().getString(Constants.STORY_INDEX);
        chapter_id = getArguments().getString(Constants.STORY_CHAPTER_INDEX);
        story_title = getArguments().getString(Constants.STORY_TITLE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_comment, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        initData();
        return view;
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarComment);
        mImageAvatar = view.findViewById(R.id.imgUserComment);
        mEditComment = view.findViewById(R.id.editComment);
        mBtnSendComment = view.findViewById(R.id.btnSendComment);
        mRecylerView = view.findViewById(R.id.recyclerComment);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Comments");
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initData() {
        if(ConnectionUtils.isLoginValid){
            mFirebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mUser = task.getResult().toObject(User.class);
                    Uri avatarUri = Uri.parse(mUser.getAvatar());
                    Picasso.get()
                            .load(avatarUri)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(mImageAvatar);
                }
            });
        }


        Query query =  mFirebaseFirestore.collection("comments/" + chapter_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (true) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String commentId = document.getId();
                                final Comment comment = document.toObject(Comment.class);
                                mFirebaseFirestore.collection("users").document(comment.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        User user = task.getResult().toObject(User.class);
                                        InformationAction informationAction = new InformationAction( comment.getImage(), user.getAccount(), comment.getMessage(), comment.getTimestamp());
                                        mInformationActions.add(informationAction);
                                        mInformationActionAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } else {
                        }
                    }
                });
    }

    private void initView(View view){
        if(ConnectionUtils.isLoginValid){
            mBtnSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean valid = WidgetsUtils.validateEditText(mEditComment, "Your comment is empty!");
                    if(valid) {
                        setCommentContentFirebase();
                        mEditComment.setText("");
                        updateInformationAction();
                        mFirebaseFirestore.collection("tokens").document(mUser.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final Token receiverToken = task.getResult().toObject(Token.class);
                                if(receiverToken != null){
                                    mFirebaseFirestore.collection("notification_setups").document(mUser.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                NotificationSetup setup = task.getResult().toObject(NotificationSetup.class);
                                                if(setup.isComment()){
                                                    FirebaseInstanceId.getInstance().getInstanceId()
                                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                    String token = task.getResult().getToken();
                                                                    NotificationAction notificationAction = NotificationAction.getInstance(getContext());
                                                                    notificationAction.sendNotification(receiverToken.getToken(), token, Constants.NOTIFICATION_COMMENT);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
            mEditComment.setEnabled(false);
            mEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
            });
            mBtnSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
            });
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecylerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        mRecylerView.addItemDecoration(dividerItemDecoration);
        mInformationActions = new ArrayList<>();
        mInformationActionAdapter = new InformationActionAdapter(mInformationActions,getContext(), this);
        mInformationActionAdapter.setRequestCode(Constants.COMMENT_REQUEST_CODE);
        mRecylerView.setAdapter(mInformationActionAdapter);
    }

    private void setCommentContentFirebase(){
        final String message = mEditComment.getText().toString();
        final FieldValue timestamp = FieldValue.serverTimestamp();
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("user_id", user_id);
        map.put("image", mUser.getAvatar().toString());
        map.put("timestamp",timestamp );

        mFirebaseFirestore.collection("comments/" + chapter_id + "/contain")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        InformationAction informationAction = new InformationAction( mUser.getAvatar(), mUser.getAccount(), message, new Date(System.currentTimeMillis()));
                        mInformationActions.add(informationAction);
                        mInformationActionAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });
    }

    private void queryAllComments(){
        Query query =  mFirebaseFirestore.collection("comments/" + chapter_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (true) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String commentId = document.getId();
                                Log.d("CommentGet", commentId);
                                Comment comment = document.toObject(Comment.class);
                                InformationAction informationAction = new InformationAction( comment.getImage(), "", comment.getMessage(), comment.getTimestamp());
                                mInformationActions.add(0,informationAction);
                                mInformationActionAdapter.notifyDataSetChanged();
                            }
                        } else {
                        }
                    }
                });
    }

    private void updateInformationAction(){
        Map<String,Object>map = new HashMap<>();
        map.put("action_image", mUser.getAvatar());
        map.put("action_title", mUser.getAccount());
        map.put("action_description", "Commented on " + story_title);
        map.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").add(map);
    }
}