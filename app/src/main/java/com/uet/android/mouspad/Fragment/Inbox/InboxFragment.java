package com.uet.android.mouspad.Fragment.Inbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.uet.android.mouspad.Adapter.InboxAdapter;
import com.uet.android.mouspad.Model.Inbox;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Client;
import com.uet.android.mouspad.Service.Notifications.Data;
import com.uet.android.mouspad.Service.Notifications.MyResponse;
import com.uet.android.mouspad.Service.Notifications.Sender;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class InboxFragment extends Fragment  {

    private CircleImageView mImageProfile;

    private User mUser;
    private User mReceiver;
    private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirebaseFirestore;
    FirebaseAuth mFirebaseAuth;

    private ValueEventListener eventListener;

    private ImageButton mButtonSend;
    private EditText mEditMessage;
    private Toolbar mToolbar;

    private InboxAdapter mInboxAdapter;
    private ArrayList<Inbox> mInboxs;

    private RecyclerView mRecyclerView;

    private Intent intent;


    private String receiverUserid;
    private String currentUserId;

    private APIService mApiService;

    private boolean notify = false;

    public InboxFragment() {
    }

    public static InboxFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_ID, userId);
        InboxFragment fragment = new InboxFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiverUserid = getArguments().getString(Constants.USER_ID);
        //receiverUserid = "uT2bgDXxugPgjahuhKUc394FGMr2";

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mApiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_inbox, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initData(view);
        return view;
    }

    @Override
    public void onDestroy() {
        setInboxListInformation();
        super.onDestroy();
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarInbox);
        mImageProfile = view.findViewById(R.id.imgProfileInbox);
        mButtonSend = view.findViewById(R.id.btnSendInbox);
        mEditMessage = view.findViewById(R.id.editTextInbox);
        mRecyclerView = view.findViewById(R.id.recyclerInbox);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    private void initData(final View view) {
        mInboxs = new ArrayList<>();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        mFirebaseFirestore.collection("users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mUser = task.getResult().toObject(User.class);
            }
        });

        mFirebaseFirestore.collection("users").document(receiverUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mReceiver = task.getResult().toObject(User.class);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mReceiver.getAccount());
                if (mReceiver.getAvatar().equals("default")){
                    mImageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(mReceiver.getAvatar()).into(mImageProfile);
                }
                initView(view);
                queryAllInboxs();
            }
        });
    }

    private void queryAllInboxs(){
        Query query =  mFirebaseFirestore.collection("inboxs/"  +currentUserId + "/contain/" + receiverUserid + "/contain").orderBy("timestamp", Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String inboxId = document.getId();
                            Inbox inbox = document.toObject(Inbox.class);
                            mInboxs.add(inbox);
                            mInboxAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initView(View view) {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mInboxAdapter = new InboxAdapter(getContext(), mInboxs, mReceiver.getAvatar());
        mRecyclerView.setAdapter(mInboxAdapter);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = WidgetsUtils.validateEditText(mEditMessage, "Your message is empty!");
                if(valid) {
                    setMessageContentInformation();
                    mEditMessage.setText("");
                    mFirebaseFirestore.collection("tokens").document(receiverUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final Token receiverToken = task.getResult().toObject(Token.class);
                            if(receiverToken != null){
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                String token = task.getResult().getToken();
                                                sendNotification(receiverToken.getToken(), token, "Inbox");
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });

    }

    private void setMessageContentInformation(){
        final String message = mEditMessage.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("sender", currentUserId);
        map.put("receiver", receiverUserid);
        map.put("timestamp", FieldValue.serverTimestamp());

        mFirebaseFirestore.collection("inboxs/" + currentUserId + "/contain/" + receiverUserid + "/contain")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Inbox inbox = task.getResult().toObject(Inbox.class);
                                mInboxs.add(inbox);
                                mInboxAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });

        mFirebaseFirestore.collection("inboxs/" + receiverUserid +"/contain/" + currentUserId + "/contain")
                .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });
    }

    private void setInboxListInformation(){
        Map<String,Object> map = new HashMap<>();
        map.put("contact_user", mReceiver);
        map.put("timestamp", FieldValue.serverTimestamp());

        mFirebaseFirestore.collection("inbox_lists/" + currentUserId + "/contain").document(receiverUserid)
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message){
        final NotificationAction notificationAction = NotificationAction.getInstance(getContext());
        if(true){
            Toast.makeText(getContext(), "Notification Service is on!", Toast.LENGTH_LONG).show();
            Token token = new Token(receiver);
            Data data = new Data(mUser.getUser_id(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                    mUser.getUser_id());
            Sender sender = new Sender(data, token.getToken());

            mApiService.sendNotification(sender)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if (response.body().success != 1){
                                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Notification Service is off!", Toast.LENGTH_LONG).show();

        }

    }


    private void currentUser(String userid){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }



}