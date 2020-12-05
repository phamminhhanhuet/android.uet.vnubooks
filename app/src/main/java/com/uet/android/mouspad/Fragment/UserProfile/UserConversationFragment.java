package com.uet.android.mouspad.Fragment.UserProfile;

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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.Fragment.UserFragment;
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

public class UserConversationFragment extends Fragment {

    private EditText mEditComment;
    private ImageButton mBtnSendComment;
    private RecyclerView mRecylerView;

    private ArrayList<InformationAction> mInformationActions;
    private InformationActionAdapter mInformationActionAdapter;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private User mUser;
    private User currentUser;

    private String user_id;
    private String chapter_id;
    private String story_id;
    private String story_title;

    private APIService mApiService;
    private boolean isCurrentUser;
    public UserConversationFragment() {
    }

    public static UserConversationFragment newInstance(String userId) {
        UserConversationFragment fragment = new UserConversationFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        user_id = getArguments().getString(Constants.USER_ID);
        if(ConnectionUtils.isLoginValid){
            isCurrentUser = user_id.equals(mFirebaseAuth.getCurrentUser().getUid());
        } else {
            isCurrentUser = false;
        }
        mApiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_conversation, container, false);
        MappingWidgets(view);
        initView(view);
        initData();
        return view;
    }

    private void MappingWidgets(View view) {
        mEditComment = view.findViewById(R.id.editConversation);
        if(isCurrentUser){
            mEditComment.setHint("Post something to your followers.");
        } else {
            mEditComment.setHint("Post something to this author.");
        }
        mBtnSendComment = view.findViewById(R.id.btnSendConversation);
        mRecylerView = view.findViewById(R.id.recyclerConversation);
    }


    private void initData() {
        if(ConnectionUtils.isLoginValid){
            mFirebaseFirestore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    currentUser = task.getResult().toObject(User.class);
                }
            });
        }
        mFirebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mUser = task.getResult().toObject(User.class);
            }
        });
        Query query =  mFirebaseFirestore.collection("comments/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
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
                    boolean valid = WidgetsUtils.validateEditText(mEditComment, "Your post is empty!");
                    if(valid && isCurrentUser ==false) {
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
                                                                    notificationAction.sendNotification(receiverToken.getToken(), token, Constants.NOTIFICATION_MESSAGE_BOARD);
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
            mBtnSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
            });
            mEditComment.setEnabled(false);
            mEditComment.setOnClickListener(new View.OnClickListener() {
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
        map.put("image", currentUser.getAvatar());
        map.put("timestamp",timestamp );

        mFirebaseFirestore.collection("comments/" + user_id + "/contain")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        InformationAction informationAction = new InformationAction( currentUser.getAvatar(), currentUser.getAccount(), message, new Date(System.currentTimeMillis()));
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


    private void updateInformationAction(){
        Map<String,Object>map = new HashMap<>();
        map.put("action_image", currentUser.getAvatar());
        map.put("action_title", currentUser.getAccount());
        map.put("action_description", "posted on board: " );
        map.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").add(map);
    }
}