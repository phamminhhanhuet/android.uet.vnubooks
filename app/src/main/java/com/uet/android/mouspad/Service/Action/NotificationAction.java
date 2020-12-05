package com.uet.android.mouspad.Service.Action;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.Notifications.Client;
import com.uet.android.mouspad.Service.Notifications.Data;
import com.uet.android.mouspad.Service.Notifications.MyResponse;
import com.uet.android.mouspad.Service.Notifications.Sender;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Service.ServiceAction;
import com.uet.android.mouspad.Utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationAction extends ServiceAction {
    private boolean isNotifition = true;
    private boolean isInboxNotification = true;
    private boolean isCommentNotification = true;
    private boolean isLibraryNotifcation = true;
    private boolean isNewFollowerNotification = true;
    private boolean isVoteNotification = true;
    private boolean isUpdateFromFollowingNotifi = true;
    private boolean isMessageBoard = true;
    private static NotificationAction sNotificationAction;

    private APIService mApiService;
    private Context mContext;
    private String mUserId;

    public NotificationAction(Context mContext, int mServiceRequestCode) {
        super(mContext, mServiceRequestCode);
        this.mContext = mContext;
        mApiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static NotificationAction getInstance(Context context){
        if(sNotificationAction == null){
            sNotificationAction = new NotificationAction(context, 0);
        }
        return sNotificationAction;
    }

    public Notification builder(final Context context){
        createNotificationChannel(context);
        if(isNotifition  == true){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("My notification")
                            .setContentText("Much longer text that cannot fit one line...")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Much longer text that cannot fit one line..."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    synchronized (mBuilder){
                    }
                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, mBuilder.build());
                }
            }, 3000);
        }
        return null;
    }

    private void createNotificationChannel (Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = "this is the new Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(final String receiver, final String username, final int messageCode){
        FirebaseFirestore.getInstance().collection("users").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User currentUser = task.getResult().toObject(User.class);
                boolean isNotification = false;
                String titleMessage = "";
                switch (messageCode){
                    case Constants.NOTIFICATION_INBOX:
                        if(isInboxNotification() == false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "New inbox.";
                        }
                        break;
                    case Constants.NOTIFICATION_COMMENT:
                        if(isCommentNotification() == false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "New comment on your story!";
                        }
                        break;
                    case Constants.NOTIFICATION_LIBRARY:
                        if(isLibraryNotifcation() ==false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "Your story is shared!";
                        }
                        break;
                    case Constants.NOTIFICATION_VOTE:
                        if(isVoteNotification() ==false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "New vote on your story.";
                        }
                        break;
                    case Constants.NOTIFICATION_NEW_FOLLOWER:
                        if(isNewFollowerNotification() ==false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "Meet your new follower!";
                        }
                        break;
                    case Constants.NOTIFICATION_UPDATES_FROM_FOLLOWING:
                        if(isUpdateFromFollowingNotifi()==false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "New updates from your favor!";
                        }
                        break;
                    case Constants.NOTIFICATION_MESSAGE_BOARD:
                        if(isMessageBoard()==false){
                            isNotification = false;
                        } else {
                            isNotification = true;
                            titleMessage = "New message on your board!";
                        }
                        break;
                }

                if(isNotification){
                    Toast.makeText(mContext, "Notification Service is on!", Toast.LENGTH_LONG).show();
                    Token token = new Token(receiver);
                    Data data = new Data(mUserId, R.mipmap.ic_launcher,titleMessage , currentUser.getAccount(),
                            mUserId);
                    Sender sender = new Sender(data, token.getToken());

                    mApiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                } else {
                    Toast.makeText(mContext, "Notification Service is off!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendNotificationWithServiceMode(final String receiver, final String username, final int messageCode) {
        FirebaseFirestore.getInstance().collection("users").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User currentUser = task.getResult().toObject(User.class);
                String titleMessage = "";
                switch (messageCode){
                    case Constants.NOTIFICATION_INBOX:
                        titleMessage = "New inbox.";
                        break;
                    case Constants.NOTIFICATION_COMMENT:
                        titleMessage = "New comment on your story.";
                        break;
                    case Constants.NOTIFICATION_LIBRARY:
                        titleMessage = "Your story is shared!";
                        break;
                    case Constants.NOTIFICATION_VOTE:
                        titleMessage = "New vote on your story.";
                        break;
                    case Constants.NOTIFICATION_NEW_FOLLOWER:
                        titleMessage = "Meet your new follower!";
                        break;
                    case Constants.NOTIFICATION_UPDATES_FROM_FOLLOWING:
                        titleMessage = "New updates from your favor!";
                        break;
                    case Constants.NOTIFICATION_MESSAGE_BOARD:
                        titleMessage = "New message on your board!";
                        break;
                }
                Toast.makeText(mContext, "Notification Service is on!", Toast.LENGTH_LONG).show();
                Token token = new Token(receiver);
                Data data = new Data(mUserId, R.mipmap.ic_launcher, titleMessage, currentUser.getAccount(), mUserId);
                Sender sender = new Sender(data, token.getToken());

                mApiService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200){
                                    if (response.body().success != 1){
                                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
            }
        });

    }

    public boolean isNotifition() {
        return isNotifition;
    }

    public boolean isInboxNotification() {
        return isInboxNotification;
    }

    public boolean isCommentNotification() {
        return isCommentNotification;
    }

    public boolean isLibraryNotifcation() {
        return isLibraryNotifcation;
    }

    public boolean isNewFollowerNotification() {
        return isNewFollowerNotification;
    }

    public boolean isVoteNotification() {
        return isVoteNotification;
    }

    public boolean isUpdateFromFollowingNotifi() {
        return isUpdateFromFollowingNotifi;
    }

    public boolean isMessageBoard() {
        return isMessageBoard;
    }

    public void setInboxNotification(boolean inboxNotification) {
        isInboxNotification = inboxNotification;
    }

    public void setCommentNotification(boolean commentNotification) {
        isCommentNotification = commentNotification;
    }

    public void setLibraryNotifcation(boolean libraryNotifcation) {
        isLibraryNotifcation = libraryNotifcation;
    }

    public void setNewFollowerNotification(boolean newFollowerNotification) {
        isNewFollowerNotification = newFollowerNotification;
    }

    public void setVoteNotification(boolean voteNotification) {
        isVoteNotification = voteNotification;
    }

    public void setUpdateFromFollowingNotifi(boolean updateFromFollowingNotifi) {
        isUpdateFromFollowingNotifi = updateFromFollowingNotifi;
    }

    public void setMessageBoard(boolean messageBoard) {
        isMessageBoard = messageBoard;
    }
}
