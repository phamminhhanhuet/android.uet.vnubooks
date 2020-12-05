package com.uet.android.mouspad.Model.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.uet.android.mouspad.Model.Category;
import com.uet.android.mouspad.Model.CurrentReadItem;
import com.uet.android.mouspad.Model.Ebook.PDFUrl;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.StoryUserItem;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;

    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

   // private String user_id = mFirebaseAuth.getCurrentUser().getUid();

    private Query storyRef = mFirebaseFirestore.collection("stories/").limit(100);
    private Query categoryRef = mFirebaseFirestore.collection("categories/").orderBy("timestamp", Query.Direction.ASCENDING).limit(30);

    //category
    private List<CategoryModel> mCategoryModels;
    ArrayList<Story> currReading ;
    ArrayList<Story> mostFav ;
    ArrayList<Story> recently ;
    ArrayList<Story>completed ;
    ArrayList<Story> follow ;

    //library
    private ArrayList<LibraryItem> mLibraryItems;
    private LibraryStoryModel mLibraryStoryModel;
    private ArrayList<Story> mStoriesLibrary;

    //tokens
    private ArrayList<Token> mTokens;
    private List<Boolean> mFlNotification;
    private List<User> mUsersFl;
    private TokenFlViewModel mTokenFlViewModel;
    private String user_id = "m24EaTpNHJYpqIQHOM4su1zSiQG2";

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public String getUserId (){
        if(mFirebaseAuth.getCurrentUser() != null){
            return mFirebaseAuth.getCurrentUser().getUid();
        } else return null;
    }

    public interface OnFirestoreTaskComplete {
        void categoryListDataAdded(List<CategoryModel> categoryModels);
        void libraryDataAdded(LibraryStoryModel libraryStoryModel);
        void tokenDataAdded(List<Token> tokenModel, List<Boolean> flNotification, List<User> userFls);
        void onError(Exception e);
    }

    private void storeStoryListModel(String list_id, String title){
        Map<String, Object> storyList = new HashMap<>();
        storyList.put("list_id", list_id);
        storyList.put("title", title);
        storyList.put("story_id", "");
        mFirebaseFirestore.collection("story_list/" )
                .add(storyList)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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

    //category
    public void getStoryCategoryData() {
        getCurrReadingCategoryData();
    }

    private void getCurrReadingCategoryData(){
        currReading = new ArrayList<>();
        if(ConnectionUtils.isLoginValid){
            mFirebaseFirestore.collection("current_read").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        CurrentReadItem item = task.getResult().toObject(CurrentReadItem.class);
                        String story_id = item.getStory_id();
                        mFirebaseFirestore.collection("stories").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Story story = task.getResult().toObject(Story.class);
                                currReading.add(story);
                                Log.d("firebaserepo", "size r" + currReading.size());
                                getMostFavCategoryData();
                            }
                        });
                    }
                }
            });
        } else {
            Query query = mFirebaseFirestore.collection("story_pdfs").limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String storyId = documentSnapshot.getId();
                        mFirebaseFirestore.collection("stories").document(storyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Story story = task.getResult().toObject(Story.class);
                                currReading.add(story);
                                Log.d("firebaserepo", "size r" + currReading.size());
                                getMostFavCategoryData();
                            }
                        });
                    }
                }
            });
        }
    }

    private void getMostFavCategoryData(){
        mostFav  = new ArrayList<>();
        storyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    if(i % 2 == 0 && i < 20){
                        Story story = documentSnapshot.toObject(Story.class);
                        mostFav.add(story);
                    }
                    i ++;
                }
                getRecentlyCategoryData();
                Log.d("firebaserepo", "size m " +  mostFav.size() );
            }
        });
    }

    private void getRecentlyCategoryData(){
        recently = new ArrayList<>();
        storyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    if(i < 10){
                        Story story = documentSnapshot.toObject(Story.class);
                        recently.add(story);
                        i ++;
                    } else if(i == 10) break;
                }
                getCompletedCatagoryData();
                Log.d("firebaserepo", "size r" +  recently.size() );
            }
        });
    }

    private void getCompletedCatagoryData(){
        completed = new ArrayList<>();
        storyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Story story = documentSnapshot.toObject(Story.class);
                    if(i <10 && story.getStatus().equals("completed")){
                        completed.add(story);
                        i ++;
                    } else if(i == 10) break;
                }
                getFollowCatagoryData();
                Log.d("firebaserepo", "size c" +  completed.size() );
            }
        });
    }

    private void getFollowCatagoryData(){
        follow = new ArrayList<>();
        if(ConnectionUtils.isLoginValid){
            final Query query = mFirebaseFirestore.collection("follows/" + user_id + "/contain").orderBy("timestamp").limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        final FollowItem followItem = documentSnapshot.toObject(FollowItem.class);
                        Query storyQuery = mFirebaseFirestore.collection("story_user/" + followItem.getUser_id() + "/contain").limit(10);
                        storyQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<QuerySnapshot> querySnapshotTask) {
                                for(QueryDocumentSnapshot queryDocumentSnapshot: querySnapshotTask.getResult()){
                                    StoryUserItem storyUserItem = queryDocumentSnapshot.toObject(StoryUserItem.class);
                                    Log.d("firebaserepo", "size f " + storyUserItem.getStory_id());
                                    mFirebaseFirestore.collection("stories").document(storyUserItem.getStory_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Story story = task.getResult().toObject(Story.class);
                                            follow.add(story);
                                            Log.d("firebaserepo", "size f" + follow.size());
                                            if(follow.size() == querySnapshotTask.getResult().size()){
                                                getCategories();
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
            Story story = recently.get(0);
            Query query = mFirebaseFirestore.collection("story_user/" + story.getUser_id() + "/contain").limit(10);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<QuerySnapshot> allStory) {
                    for(QueryDocumentSnapshot documentSnapshot: allStory.getResult()){
                        StoryUserItem item = documentSnapshot.toObject(StoryUserItem.class);
                        String storyId = item.getStory_id();
                        mFirebaseFirestore.collection("stories").document(storyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Story result = task.getResult().toObject(Story.class);
                                follow.add(result);

                                if(follow.size() == allStory.getResult().size()){
                                    getCategories();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void getCategories (){
        mCategoryModels = new ArrayList<>();
        categoryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int i = 0;
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String category_id = documentSnapshot.getId();
                        String category_title = documentSnapshot.toObject(Category.class).getCategory_title();
                        CategoryModel categoryModel;
                        categoryModel = new CategoryModel(category_id, category_title, new ArrayList<Story>());
                        switch (i){
                            case 0:
                                categoryModel = new CategoryModel(category_id, category_title, mostFav);
                                break;
                            case 1:
                                if(ConnectionUtils.isLoginValid){
                                    categoryModel = new CategoryModel(category_id, category_title, currReading);
                                } else {
                                    categoryModel = new CategoryModel(category_id, "Feature ebook this week!", currReading);
                                }
                                break;
                            case 2:
                                categoryModel = new CategoryModel(category_id, category_title, recently);
                                break;
                            case 3:
                                if(ConnectionUtils.isLoginValid){
                                    categoryModel = new CategoryModel(category_id, category_title, follow);
                                } else {
                                    categoryModel = new CategoryModel(category_id, "From incredible author", follow);
                                }
                                break;
                            case 4:
                                categoryModel = new CategoryModel(category_id, category_title, completed);
                                break;
                        }
                        mCategoryModels.add(categoryModel);
                        i ++;
                    }
                    onFirestoreTaskComplete.categoryListDataAdded(mCategoryModels);
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }
        });
    }

    //library

    public void getStoryLibraryData(){
        mLibraryItems = new ArrayList<>();
        mLibraryStoryModel = new LibraryStoryModel(null);
        mStoriesLibrary = new ArrayList<>();

        Query query = mFirebaseFirestore.collection("libraries/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    LibraryItem libraryItem = documentSnapshot.toObject(LibraryItem.class);
                    mLibraryItems.add(libraryItem);
                    mLibraryStoryModel.getLibraryItems().add(libraryItem);
                    final String story_id = libraryItem.getStory_id();
                    final String owner_id = libraryItem.getOwner_id();

                    mFirebaseFirestore.collection("stories/").document(story_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Story story = task.getResult().toObject(Story.class);
                                    mStoriesLibrary.add(story);
                                    mLibraryStoryModel.getStories().add(story);
                                    getOwnerOfLibraryData(owner_id);
                                    ArrayList<StoryChapter> storyChapters = new ArrayList<>();
                                    if(story.getFormat().equals(Constants.FORMAT_DEFAULT_APP)){
                                        getChaptersOfAStory(story_id, storyChapters);
                                    } else {
                                        StoryChapter storyChapter = new StoryChapter("idnull", "idstorynull", "titlenull", "contentnull", "", "", "", new Date(), true);
                                        storyChapters.add(storyChapter);
                                        LibraryChapterModel libraryChapterModel = new LibraryChapterModel.Builder().setStoryChapters(storyChapters).build();
                                        mLibraryStoryModel.getLibraryChapterModel().add(libraryChapterModel);
                                    }

                                }
                            });
                }
                onFirestoreTaskComplete.libraryDataAdded(mLibraryStoryModel);
            }
        });
    }

    private void getOwnerOfLibraryData(String owner_id){
        mFirebaseFirestore.collection("users").document(owner_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            mLibraryStoryModel.getUsers().add(user);
                        }
                    }
                });
    }

    private void getChaptersOfAStory(String story_id, final ArrayList<StoryChapter> storyChapters){
        Query query = mFirebaseFirestore.collection("chapters/" +story_id + "/contain").orderBy("timestamp",  Query.Direction.DESCENDING);
        query.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
                    if(chapter.isPublished()){
                        storyChapters.add(chapter);
                    }
                }
                LibraryChapterModel libraryChapterModel = new LibraryChapterModel.Builder().setStoryChapters(storyChapters).build();
                mLibraryStoryModel.getLibraryChapterModel().add(libraryChapterModel);
            }
        });
    }

    //tokens
    public void getTokensFollowing(String user_id){
        mTokens = new ArrayList<>();
        mFlNotification = new ArrayList<>();
        mUsersFl = new ArrayList<>();
        Query query = mFirebaseFirestore.collection("follow/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    FollowItem followItem = documentSnapshot.toObject(FollowItem.class);
                    String user_id = followItem.getUser_id();
                    mFirebaseFirestore.collection("notification_setups").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                NotificationSetup setup = task.getResult().toObject(NotificationSetup.class);
                                mFlNotification.add(setup.isUpdates_from_following());
                            }
                        }
                    });
                    mFirebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                User user = task.getResult().toObject(User.class);
                                mUsersFl.add(user);
                            }
                        }
                    });

                    mFirebaseFirestore.collection("tokens").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Token token = task.getResult().toObject(Token.class);
                            mTokens.add(token);
                        }
                    });
                }
            }
        });
    }
}

