package com.uet.android.mouspad.Fragment.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BlankTextActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.CategoryActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryChapterContentActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryDetailActivity;
import com.uet.android.mouspad.Activity.UserPerform.UserActivity;
import com.uet.android.mouspad.Adapter.SearchTopicAdapter;
import com.uet.android.mouspad.Adapter.StoryAdapter;
import com.uet.android.mouspad.Fragment.HomeFragment;
import com.uet.android.mouspad.Model.Tag;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Model.ViewModel.CategoryModel;
import com.uet.android.mouspad.Model.ViewModel.CategoryViewModel;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static android.content.Context.MODE_PRIVATE;
import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_LIST;

public class HomeInterfaceFragment extends Fragment implements
        DiscreteScrollView.ScrollStateChangeListener<StoryAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<StoryAdapter.ViewHolder>,
        View.OnClickListener{
    private Toolbar mToolbar;
    private ImageView mButtonAvatar;
    private ImageView mImgCoverBlur;
    private ImageView mImgMore;
    private TextView mTextTitleRecent;
    private TextView mTextContentRecent;
    private Button mTextStatusRecent;
    private ArrayList<String> mTopicNames ;
    private ArrayList<Story> stories;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private CategoryViewModel categoryViewModel;
    private List<CategoryModel> categoryModelList;


    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    public HomeInterfaceFragment() {

    }

    public static HomeInterfaceFragment newInstance() {
       return new HomeInterfaceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
//        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
//        final Index index = client.getIndex("firebase_story");
//        final Index indexUser = client.getIndex("firebase_user");
//        Query queryStory = mFirebaseFirestore.collection("stories").limit(100);
//        queryStory.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                    final Story story = documentSnapshot.toObject(Story.class);
//                    if(story.getPublished()){
//                        final ArrayList<String> mTags = new ArrayList<>();
//                        Query queryTag = mFirebaseFirestore.collection("story_tags/" + story.getStory_id() + "/contain").limit(100);
//                        queryTag.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                                    Tag tag = documentSnapshot.toObject(Tag.class);
//                                    mTags.add(tag.getTitle());
//                                }
//                                String allTitle = story.getTitle() + " " + story.getGenre() + " ";
//                                for(int i = 0; i < mTags.size(); i ++){
//                                    allTitle += mTags.get(i) + " ";
//                                }
//
//                                try {
//                                    JSONObject jsonObjects = new JSONObject().put("storyInfo", allTitle).
//                                            put("storyTitle", story.getTitle())
//                                            .put("instanceId", story.getStory_id())
//                                            .put("storyCover", story.getCover())
//                                            .put("ownerId", story.getUser_id())
//                                            .put("storyGenre", story.getGenre())
//                                            .put("storyStatus", story.getStatus())
//                                            .put("storyDes", story.getDescription())
//                                            .put("storyPublish", story.getPublished())
//                                            .put("storyFormat", story.getFormat());
//                                    index.addObjectAsync(jsonObjects, story.getStory_id(), null);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//
//                }
//            }
//        });
//
//        Query queryUser = mFirebaseFirestore.collection("users").limit(100);
//        queryUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                    User user = documentSnapshot.toObject(User.class);
//                    try {
//                        JSONObject jsonObjects = new JSONObject().put("userInfo", user.getFullname() + " " + user.getAccount()).
//                                put("userAccount", user.getAccount())
//                                .put("userFullname", user.getFullname())
//                                .put("instanceId", user.getUser_id())
//                                .put("userAvatar", user.getAvatar())
//                                .put("userBackground", user.getBackground())
//                                .put("userDes", user.getDescription())
//                                .put("userMail", user.getEmail())
//                                .put("userGender", user.getGender())
//                                .put("userBirthday",user.getBirthday());
//                        indexUser.addObjectAsync(jsonObjects, user.getUser_id(), null);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        Query query = mFirebaseFirestore.collection("stories").limit(100);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                int count = 0;
//                ArrayList<String> mTagsId = new ArrayList<>();
//                mTagsId.add("1xECML8kQRbpBJPdxSQ0");
//                mTagsId.add("9fzDa6kfuzd0GCfPL9sY");
//                mTagsId.add("AUPh6hGiGVhvCEKVCiIb");
//                mTagsId.add("FWLwfU8ugPFNljjaChDA");
//                mTagsId.add("eHxVowhuaSZEonhNXfbs");
//                mTagsId.add("r4CcCL2G2F53InJgKh4w");
//                mTagsId.add("rAsm9gpzkizPF1bCZwA7");
//                mTagsId.add("tfqLN7P53qIlpxz8jJrB");
//                for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                    Story story = documentSnapshot.toObject(Story.class);
//                    HashMap<String, Object> story_user = new HashMap<>();
//                    story_user.put("story_id", story.getStory_id());
//                    story_user.put("timestamp", FieldValue.serverTimestamp());
//                    story_user.put("published", story.getPublished());
//                    mFirebaseFirestore.collection("story_user/" + story.getUser_id() + "/contain").document(story.getStory_id()).set(story_user);
//
//                    ArrayList<String> mTags = new ArrayList<>();
//                    if(count % 4 == 0){
//                        mTags.add(story.getGenre());
//                        mTags.add(story.getFormat());
//                        mTags.add(String.valueOf(story.getPublished()));
//                    } else if(count % 4 ==1){
//                        mTags.add(story.getGenre());
//                        mTags.add(story.getFormat());
//                    } else if(count % 4 ==2){
//                        mTags.add(story.getGenre());
//                        mTags.add(story.getFormat());
//                        mTags.add(String.valueOf(story.getPublished()));
//                        int index = story.getTitle().indexOf(" ");
//                        String con = story.getTitle().substring(0, index);
//                        mTags.add(String.valueOf(con));
//                        mTags.add(String.valueOf(story.getPublished()));
//                    } else if(count % 4 ==3){
//                        mTags.add(story.getFormat());
//                    }
//                    count ++;
//                    if(!mTags.isEmpty()){
//                        for(int i = 0 ; i < mTags.size() ; i ++){
//                            HashMap<String, Object> tags = new HashMap<>();
//                            tags.put("title",mTags.get(i));
//                            mFirebaseFirestore.collection("story_tags/" + story.getStory_id() + "/contain").document(mTagsId.get(i)).set(tags);
//                        }
//                    }
//                }
//            }
//        });
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(LayoutUtils.Constant.theme);
        HomeFragment.mTabLayout.setBackgroundColor(LayoutUtils.Constant.color);
        View view= inflater.inflate(R.layout.fragment_home_interface, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initData(view);
        if(ConnectionUtils.isLoginValid){
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
        proccessDynamicLink();
        return view;
    }

    private void proccessDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("DeepLinkjk", deepLink.toString());

                        }else if(getActivity().getIntent().getData() != null){
                            deepLink = getActivity().getIntent().getData();
                            Log.d("DeepLinkjk", deepLink.toString());
                        }
                        if(deepLink != null){
                            String deepString = deepLink.toString();
                            String story_id = deepString.substring(deepString.indexOf("st_id=") + 5, deepString.indexOf("stc_id=") -1);
                            String chapter_id = deepString.substring(deepString.indexOf("stc_id=") +6);
                            Log.d("Deeplink", story_id);
                            Log.d("Deeplink", chapter_id);
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseFirestore = FirebaseFirestore.getInstance();
                           // initDynamicLinkDataForChapter(story_id, chapter_id);
                            requestSearch(story_id);
                        }
                    }
                })
                .addOnFailureListener(getActivity(),new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ShareLink", "getDynamicLink:onFailure", e);
                    }
                });
    }


    private void requestSearch(String searchableAttribute){
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_story");

        com.algolia.search.saas.Query querySearch = new com.algolia.search.saas.Query(searchableAttribute)
                .setAttributesToRetrieve("instanceId")
                .setHitsPerPage(50);
        index.searchAsync(querySearch, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    JSONArray hits = jsonObject.getJSONArray("hits");
                    Log.d("SearchAl js", jsonObject.toString());

                    ArrayList<Story> stories = new ArrayList<>();

                    for(int i = 0;i < hits.length(); i ++){
                        JSONObject object = hits.getJSONObject(i);
                        String infor = object.getString("instanceId");
                        JSONObject info = hits.getJSONObject(i).getJSONObject("_highlightResult");
                        Log.d("SearchAl jshh", object.toString());
                        Log.d("SearchAl jshh", infor.toString());

                        JSONObject title = info.getJSONObject("storyTitle");
                        JSONObject id = info.getJSONObject("instanceId");
                        JSONObject cover = info.getJSONObject("storyCover");
                        JSONObject ownerId = info.getJSONObject("ownerId");
                        JSONObject genre = info.getJSONObject("storyGenre");
                        JSONObject status = info.getJSONObject("storyStatus");
                        JSONObject description = info.getJSONObject("storyDes");
                        JSONObject format = info.getJSONObject("storyFormat");

                        String titleString = title.getString("value").replace("<em>", "").replace("</em>", "");
                        String idString = id.getString("value").replace("<em>", "").replace("</em>", "");
                        String coverString = cover.getString("value");
                        String ownerIdString = ownerId.getString("value");
                        String genreString = genre.getString("value").replace("<em>", "").replace("</em>", "");
                        String statusString = status.getString("value");
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");
                        String formatString = format.getString("value");

                        Story story = new Story(idString, ownerIdString, titleString, descriptionString, coverString, genreString, statusString, formatString, true);
                        stories.add(story);
                        Intent intent = new Intent(getContext(), StoryDetailActivity.class);
                        intent.putExtra(STORY_INDEX, 0);
                        intent.putExtra(STORY_LIST, stories);
                        startActivity(intent);
                        break;
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initDynamicLinkDataForChapter(final String story_id, final String chapter_id){
        final ArrayList<StoryChapter> storyChapters = new ArrayList<>();
        final String storyId = Uri.parse(story_id).toString();
        final String chapterId = Uri.parse(chapter_id).toString();
        Query query = mFirebaseFirestore.collection("stories").orderBy("format");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Story story = documentSnapshot.toObject(Story.class);
                    if(story.getStory_id().equals(story_id)){
                        final String owner_id = story.getUser_id();
                        final String title = story.getTitle();
                        if(story != null){
                            Query query = mFirebaseFirestore.collection("chapters/" + story.getStory_id() + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int run = 0;
                                    int positionInList = 0;
                                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
                                        storyChapters.add(chapter);
                                        if(chapter.getChapter_id().equals(chapterId)){
                                            positionInList = storyChapters.indexOf(chapter);
                                        }
                                        run ++;
                                        if(run == task.getResult().size()){
                                            Intent intent = new Intent(getActivity(), StoryChapterContentActivity.class);
                                            intent.putExtra(Constants.STORY_CHAPTER_INDEX,positionInList);
                                            intent.putExtra(Constants.STORY_INDEX, story_id);
                                            intent.putExtra(Constants.STORY_TITLE, title);
                                         //   intent.putExtra(Constants.STORY_CHAPTER_LIST, storyChapters);
                                            intent.putExtra(Constants.OWNER_ID, owner_id);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
//        mFirebaseFirestore.collection("stories/").document("7tQ5KRcF0DQTgL4NWqqk").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    Story story = task.getResult().toObject(Story.class);
//                    final String owner_id = story.getUser_id();
//                    final String title = story.getTitle();
//                    if(story != null){
//                        Query query = mFirebaseFirestore.collection("chapters/" + story.getStory_id() + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
//                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                int run = 0;
//                                int positionInList = 0;
//                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                                    StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
//                                    storyChapters.add(chapter);
//                                    if(chapter.getChapter_id().equals(chapterId)){
//                                        positionInList = storyChapters.indexOf(chapter);
//                                    }
//                                    run ++;
//                                    if(run == task.getResult().size()){
//                                        Intent intent = new Intent(getActivity(), StoryChapterContentActivity.class);
//                                        intent.putExtra(Constants.STORY_CHAPTER_INDEX,positionInList);
//                                        intent.putExtra(Constants.STORY_INDEX, story_id);
//                                        intent.putExtra(Constants.STORY_TITLE, title);
//                                        intent.putExtra(Constants.STORY_CHAPTER_LIST, storyChapters);
//                                        intent.putExtra(Constants.OWNER_ID, owner_id);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }


    private void updateToken(String token){
        Map<String, Object> map = new HashMap<>();
        Token objectToken = new Token(token);
        map.put("token", token);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("tokens/").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(map);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString(Constants.CURRENT_USER, userid);
        editor.apply();
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarHome);
        mButtonAvatar = view.findViewById(R.id.btnAvatarHome);
        mImgCoverBlur = view.findViewById(R.id.imgBlurCoverHome);
        mImgMore = view.findViewById(R.id.imgMre);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        if(ConnectionUtils.isLoginValid){
            final String userId = mFirebaseAuth.getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    Picasso.get()
                            .load(user.getAvatar())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(mButtonAvatar);
                    mButtonAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), UserActivity.class);
                            intent.putExtra(Constants.USER_ID, userId);
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            mButtonAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BlankTextActivity.class);
                    intent.putExtra(Constants.READING_MODE, getString(R.string.text_profile));
                    startActivity(intent);
                }
            });
        }
    }

    private void initData(final View view) {
        mTopicNames = new ArrayList<>();
        categoryModelList = new ArrayList<>();

        categoryViewModel = new ViewModelProvider(getActivity()).get(CategoryViewModel.class);
        categoryViewModel.getCategoryData().observe(getViewLifecycleOwner(), new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> list) {
                List<CategoryModel> categoryModelList1 = list;

                int i = 0;
                for(CategoryModel categoryModel: categoryModelList1){
                    String topic = categoryModel.getCategory_title();
                    mTopicNames.add(topic);
                    categoryModelList.add(categoryModel);
                    if(i < 5){
                        categoryModelList.add(i,categoryModel);
                    } else {
                        i = 0;
                    }
                    i ++;
                }
                stories = categoryModelList.get(0).getStories();
                initView(view);
            }
        });

    }


    private void initView(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutHome);

        TextView txtContinueReading, txtRecently, txtStoryby, txtCompleted;
        txtContinueReading = view.findViewById(R.id.txtContinueReading);
        txtRecently = view.findViewById(R.id.txtRecently);
        txtStoryby = view.findViewById(R.id.txtStoryBy);
        txtCompleted = view.findViewById(R.id.txtCompleted);

        txtContinueReading.setText(mTopicNames.get(1));
        txtRecently.setText(mTopicNames.get(2));
        txtStoryby.setText(mTopicNames.get(3));
        txtCompleted.setText(mTopicNames.get(4));

        DiscreteScrollView scrollView1 = view.findViewById(R.id.discreteScroll_1);
        StoryAdapter storyAdapter1 = new StoryAdapter(categoryModelList.get(0).getStories(), getActivity());
        scrollView1.setAdapter(storyAdapter1);
        scrollView1.setSlideOnFling(true);
        scrollView1.addOnItemChangedListener(this);
        scrollView1.addScrollStateChangeListener(this);
        scrollView1.scrollToPosition(2);
        scrollView1.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());

        CardView cardViewCurrRead = view.findViewById(R.id.layoutCurrReadHome);
        TextView tvTitle = cardViewCurrRead.findViewById(R.id.titleItemCurrRead);
        TextView tvDes = cardViewCurrRead.findViewById(R.id.contentItemCurrRead);
       // tvDes.setText(categoryModelList.get(1).getStories().get(0).getTitle());
        String storyTitle = categoryModelList.get(1).getStories().get(0).getTitle();
        String storyDes = categoryModelList.get(1).getStories().get(0).getDescription();

        if(storyTitle.length() > 20){
            String descrip = storyTitle.substring(0, 17);
            tvDes.setText(descrip + "...");
        } else {
            tvDes.setText(categoryModelList.get(1).getStories().get(0).getTitle());
        }

        if(storyDes.length() > 40){
            String descrip = categoryModelList.get(1).getStories().get(0).getDescription().substring(0, 37);
            tvTitle.setText(descrip + "...");
        } else {
            tvTitle.setText(categoryModelList.get(1).getStories().get(0).getDescription());
        }

        cardViewCurrRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StoryDetailActivity.class);
                intent.putExtra(STORY_INDEX, 0);
                intent.putExtra(STORY_LIST, categoryModelList.get(1).getStories());
                startActivity(intent);
            }
        });

        mTextTitleRecent = view.findViewById(R.id.titleListHome);
        mTextContentRecent = view.findViewById(R.id.contentListHome);
        mTextStatusRecent = view.findViewById(R.id.textStatusListHome);

        DiscreteScrollView scrollView3 = view.findViewById(R.id.discreteScroll_3);
        StoryAdapter storyAdapter3 = new StoryAdapter(categoryModelList.get(2).getStories(), getActivity());
        storyAdapter3.setTitleVisible(false);
        scrollView3.setAdapter(storyAdapter3);
        scrollView3.setSlideOnFling(true);
        scrollView3.addOnItemChangedListener(recentlyOnItemChanged);
        scrollView3.scrollToPosition(2);
        scrollView3.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.7f).setMaxScale(0.9f).build());


        RecyclerView scrollView4 = view.findViewById(R.id.discreteScroll_4);
        StoryAdapter storyAdapter4 = new StoryAdapter(categoryModelList.get(3).getStories(), getActivity());
        scrollView4.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        scrollView4.setLayoutManager(layoutManager);
        scrollView4.setAdapter(storyAdapter4);

        RecyclerView scrollView5 = view.findViewById(R.id.discreteScroll_5);
        StoryAdapter storyAdapter5 = new StoryAdapter(categoryModelList.get(4).getStories(), getActivity());
        scrollView5.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        scrollView5.setLayoutManager(linearLayoutManager);
        scrollView5.setAdapter(storyAdapter5);

        CardView cardViewClassic = view.findViewById(R.id.categoryClassic);
        CardView cardViewBio = view.findViewById(R.id.categoryBio);
        cardViewClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra(CategoryFragment.CATEGORY_PARAM, "Classic Books");
                getActivity().startActivity(intent);
            }
        });
        cardViewBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra(CategoryFragment.CATEGORY_PARAM, "Biography Books");
                getActivity().startActivity(intent);
            }
        });
    }



    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable StoryAdapter.ViewHolder viewHolder, int adapterPosition) {
        Story story = stories.get(adapterPosition);
        Picasso.get()
                .load(story.getCover())
                .transform(new BlurTransformation(getContext(), 25, 1))
                .error(R.drawable.default_avatar)
                .into(mImgCoverBlur);
    }

    @Override
    public void onScrollStart(@NonNull StoryAdapter.ViewHolder currentItemHolder, int adapterPosition) {
    }

    @Override
    public void onScrollEnd(@NonNull StoryAdapter.ViewHolder currentItemHolder, int adapterPosition) {
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable StoryAdapter.ViewHolder currentHolder, @Nullable StoryAdapter.ViewHolder newCurrent) {
    }

    DiscreteScrollView.OnItemChangedListener<StoryAdapter.ViewHolder> recentlyOnItemChanged = new DiscreteScrollView.OnItemChangedListener<StoryAdapter.ViewHolder>() {
        @Override
        public void onCurrentItemChanged(@Nullable StoryAdapter.ViewHolder viewHolder, final int adapterPosition) {
            ArrayList<Story > stories = categoryModelList.get(2).getStories();
            mTextTitleRecent.setText(stories.get(adapterPosition).getTitle());
            mTextStatusRecent.setText(stories.get(adapterPosition).getStatus());
            mImgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
                    intent.putExtra(STORY_INDEX, adapterPosition);
                    intent.putExtra(STORY_LIST, categoryModelList.get(2).getStories());
                    startActivity(intent);
                }
            });
            String storyDes = stories.get(adapterPosition).getDescription();
            if(storyDes.length() > 60){
                String descrip = storyDes.substring(0, 57);
                mTextContentRecent.setText(descrip + "...");
            } else {
                mTextContentRecent.setText(categoryModelList.get(1).getStories().get(0).getDescription());
            }
        }
    };
}