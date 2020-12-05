package com.uet.android.mouspad.Fragment.UserSetting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.HashMap;
import java.util.Map;

public class NotificationSettingFragment extends Fragment {

    private CheckBox mCheckBoxInbox, mCheckMessageBoard, mCheckBoxComment, mCheckboxLibrary, mCheckBoxNewFollower,  mCheckBoxVote, mCheckBoxFromFollowing;
    private Toolbar mToolbar;
    public NotificationSettingFragment() {
    }

    public static NotificationSettingFragment newInstance() {
        return new NotificationSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_notification_setting, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        return view;
    }
    @Override
    public void onDestroy() {
        NotificationAction notificationAction = NotificationAction.getInstance(getContext());
        Map<String, Object> map = new HashMap<>();
        map.put("inbox", notificationAction.isInboxNotification());
        map.put("comment", notificationAction.isCommentNotification());
        map.put("message_board", notificationAction.isMessageBoard());
        map.put("library", notificationAction.isLibraryNotifcation());
        map.put("vote", notificationAction.isVoteNotification());
        map.put("new_follower", notificationAction.isNewFollowerNotification());
        map.put("updates_from_following", notificationAction.isUpdateFromFollowingNotifi());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("notification_setups").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map);
        super.onDestroy();
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarNotificationSetting);
        mCheckBoxInbox = view.findViewById(R.id.checkInboxNotificationSetting);
        mCheckBoxComment = view.findViewById(R.id.checkNewCommentNotificationSetting);
        mCheckboxLibrary = view.findViewById(R.id.checkLibraryNotificationSetting);
        mCheckBoxNewFollower = view.findViewById(R.id.checkNewFollowerNotificationSetting);
        mCheckBoxVote = view.findViewById(R.id.checkVoteMyStoryNotificationSetting);
        mCheckBoxFromFollowing = view.findViewById(R.id.checkUpdateFollowingNotificationSetting);
        mCheckMessageBoard = view.findViewById(R.id.checkMessageBoardNotificationSetting);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_notification);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initView(View view) {

        //Log.d("Story Chapter Back", "Navigate back here");
        //ActionTaskLoader actionTaskLoader = new ActionTaskLoader(getContext(), NotificationAction.getInstance(getContext()));
        //actionTaskLoader.loadInBackground();
        //Log.d("Service", " A Service Action is call from a Context which contains a ServiceTaskLoader");
        // Intent intent = new Intent(getContext(), ImplementService.class);
        //getContext().startService(intent);
        final NotificationAction notificationAction = NotificationAction.getInstance(getContext());
        mCheckBoxInbox.setChecked(notificationAction.isInboxNotification());
        mCheckBoxInbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isInboxNotification()){
                    notificationAction.setInboxNotification(false);
                } else {
                    notificationAction.setInboxNotification(true);
                }
            }
        });
        mCheckMessageBoard.setChecked(notificationAction.isMessageBoard());
        mCheckMessageBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isMessageBoard()){
                    notificationAction.setMessageBoard(false);
                } else {
                    notificationAction.setMessageBoard(true);
                }
            }
        });
        mCheckBoxComment.setChecked(notificationAction.isCommentNotification());
        mCheckBoxComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isCommentNotification()){
                    notificationAction.setCommentNotification(false);
                } else {
                    notificationAction.setCommentNotification(true);
                }
            }
        });
        mCheckboxLibrary.setChecked(notificationAction.isLibraryNotifcation());
        mCheckboxLibrary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isLibraryNotifcation()){
                    notificationAction.setLibraryNotifcation(false);
                } else {
                    notificationAction.setLibraryNotifcation(true);
                }
            }
        });
        mCheckBoxNewFollower.setChecked(notificationAction.isNewFollowerNotification());
        mCheckBoxNewFollower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isNewFollowerNotification()){
                    notificationAction.setNewFollowerNotification(false);
                } else {
                    notificationAction.setNewFollowerNotification(true);
                }
            }
        });
        mCheckBoxVote.setChecked(notificationAction.isNotifition());
        mCheckBoxVote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isVoteNotification()){
                    notificationAction.setVoteNotification(false);
                } else {
                    notificationAction.setVoteNotification(true);
                }
            }
        });
        mCheckBoxFromFollowing.setChecked(notificationAction.isUpdateFromFollowingNotifi());
        mCheckBoxFromFollowing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notificationAction.isUpdateFromFollowingNotifi()){
                    notificationAction.setUpdateFromFollowingNotifi(false);
                } else {
                    notificationAction.setUpdateFromFollowingNotifi(true);
                }
            }
        });
    }
}