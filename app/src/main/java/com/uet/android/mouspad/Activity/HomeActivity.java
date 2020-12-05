package com.uet.android.mouspad.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.Fragment.HomeFragment;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Action.ActionTaskLoader;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.ImplementService;
import com.uet.android.mouspad.Utils.Constants;

import static com.uet.android.mouspad.Utils.ConnectionUtils.isLoginValid;

public class HomeActivity extends SimpleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return HomeFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences loginPres = getApplicationContext().getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
        isLoginValid = loginPres.getBoolean("isLogin", false);
        isLoginValid = getIntent().getBooleanExtra(Constants.LOGIN_STATE, false);
        Toast.makeText(getApplicationContext(), isLoginValid + "", Toast.LENGTH_SHORT).show();
        if(isLoginValid){
            setUpNotificationServiceMode();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        if(isLoginValid){
            ActionTaskLoader actionTaskLoader = new ActionTaskLoader(getApplicationContext(), NotificationAction.getInstance(getApplicationContext()));
            actionTaskLoader.loadInBackground();
            Intent intent = new Intent(HomeActivity.this, ImplementService.class);
            startService(intent);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpNotificationServiceMode(){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("notification_setups").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    NotificationSetup setup = task.getResult().toObject(NotificationSetup.class);
                    NotificationAction notificationAction = NotificationAction.getInstance(getApplicationContext());
                    notificationAction.setInboxNotification(setup.isInbox());
                    notificationAction.setCommentNotification(setup.isComment());
                    notificationAction.setLibraryNotifcation(setup.isLibrary());
                    notificationAction.setVoteNotification(setup.isVote());
                    notificationAction.setNewFollowerNotification(setup.isNew_follower());
                    notificationAction.setUpdateFromFollowingNotifi(setup.isUpdates_from_following());
                    notificationAction.setMessageBoard(setup.isMessage_board());
                }
            }
        });
    }

}