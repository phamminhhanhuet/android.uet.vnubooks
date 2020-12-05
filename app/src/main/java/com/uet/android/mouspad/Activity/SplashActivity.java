package com.uet.android.mouspad.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Fragment.SplashFragment;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.StoryDatabase;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.uet.android.mouspad.Utils.ConnectionUtils.isLoginValid;

public class SplashActivity extends SimpleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SplashFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        checkInternetConnection();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CUSTOMIZE_THEME, MODE_PRIVATE);
        int color = sharedPreferences.getInt("color", 0xffFF9800);
        int theme = sharedPreferences.getInt("theme", R.style.AppThemeNoActionBar);
        LayoutUtils.Constant.color = color;
        LayoutUtils.Constant.theme = theme;
        LayoutUtils.Method.setColorTheme();
//
//        ArrayList<String> userIds = new ArrayList<>();
//        userIds.add("4baNCKDqsyYNnCqghZVBreW17Qn1");
//        userIds.add("NVTcEADew4SsX46QFRRErMeoY9M2");
//        userIds.add("ayUFZqhPJpajoFce7Pw3vD8xqZ12");
//        userIds.add("2wsFngyY2ZRjwOA45rSLqkPMo3z2");
//        userIds.add("tIFNXSTypeUzNI38jM4B3QhRGNh2");
//        userIds.add("u9lwcUN18wMA2kQ8zjRtrhJRN123");
//        userIds.add("QC5LDaRLPuaJuajMUTQ4V648YRS2");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("hachan0102@gmail.com");
//        emails.add("17020721@vnu.edu.vn");
//        emails.add("kimlan2807@gmail.com");
//        emails.add("phamminhhanhuetk62@gmail.com");
//        emails.add("phamminhhanhuetk62@gmail");
//        emails.add("hoangminhtu623@gmail.com");
//        emails.add("phamvuanhquan_t64@hus.edu.vn");
//        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        Query query = firebaseFirestore.collection("stories").limit(100);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                    final Story story = documentSnapshot.toObject(Story.class);
//                    if(!story.getUser_id().equals("")){
//                        HashMap<String, Object> map4 = new HashMap<>();
//                        map4.put("story_id", story.getStory_id());
//                        map4.put("timestamp", FieldValue.serverTimestamp());
//                        map4.put("published", story.getPublished());
//                        firebaseFirestore.collection("story_user/" + story.getUser_id() + "/contain").document(story.getStory_id()).set(map4);
//                    }
//                    if(story.getFormat().equals(Constants.FORMAT_DEFAULT_APP)){
//                        Query chapters = firebaseFirestore.collection("chapters/" + story.getStory_id() +"/contain").limit(100);
//                        chapters.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                for(QueryDocumentSnapshot snapshot : task.getResult()){
//                                    Log.d("chapterssss", snapshot.getId());
//                                    Log.d("chapterssss ss", story.getStory_id());
//
//                                    StoryChapter storyChapter = snapshot.toObject(StoryChapter.class);
//                                    HashMap<String, Object> objectHashMap = new HashMap<>();
//                                    objectHashMap.put("chapter_id", storyChapter.getChapter_id());
//                                    objectHashMap.put("content", storyChapter.getContent());
//
//                                    firebaseFirestore.collection("chapter_content").document(storyChapter.getChapter_id()).set(objectHashMap);
//                                }
//                            }
//                        });
//                    }
//
//                }
//            }
//        });

//        for(int i = 0 ; i <userIds.size() ; i ++){
//
//            String userInfo = emails.get(i);
//            int endIndex = userInfo.indexOf("@");
//            String fullname = userInfo.substring(0, endIndex).toUpperCase();
//
//            Map<String, Object> user = new HashMap<>();
//            user.put("user_id", userIds.get(i));
//            user.put("fullname",fullname);
//            user.put("account", userInfo.substring(0, endIndex));
//            user.put("description", "This user has no description yet");
//            user.put("gender", "Do not want to tell");
//            user.put("email", emails.get(i));
//            user.put("birthday", FieldValue.serverTimestamp());
//            user.put("avatar", "https://firebasestorage.googleapis.com/v0/b/mouspad-66c48.appspot.com/o/image_avatars%2Favatar.png?alt=media&token=bb219cb4-d46b-4a24-aa60-92d15eab5ffb");
//            user.put("background", "https://firebasestorage.googleapis.com/v0/b/mouspad-66c48.appspot.com/o/image_avatars%2Fitem_add_media_background.png?alt=media&token=affad1a9-c69f-4b02-9980-29c21e8ede3f");
//
//            firebaseFirestore.collection("users").document(userIds.get(i))
//                    .set(user);
//        }

//        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        DatabaseReference mDatabase;
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                StoryDatabase comment = dataSnapshot.getValue(StoryDatabase.class);
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("story_id", comment.getStory_id());
//                map.put("chapter_id", comment.getChapter_id());
//                map.put("audio", "");
//                map.put("youtube", "");
//                map.put("cover", "");
//                map.put("title", comment.getTitle());
//                map.put("published", comment.isPublished());
//                map.put("content", comment.getContent());
//                map.put("timestamp", FieldValue.serverTimestamp());
//                firebaseFirestore.collection("chapters/" + comment.getStory_id()+ "/contain").document(comment.getChapter_id()).set(map);
//                Log.d("Somethinghere", comment.getChapter_id());
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                StoryDatabase newComment = dataSnapshot.getValue(StoryDatabase.class);
//                Log.d("Somethinghere", newComment.getChapter_id());
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        };
//        mDatabase.addChildEventListener(childEventListener);
    }

    private void checkInternetConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        ConnectionUtils.isConnectingInternet = isConnected;

        SharedPreferences loginPres = getApplicationContext().getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPres.edit();
        isLoginValid = loginPres.getBoolean("isLogin", false);
    }
}