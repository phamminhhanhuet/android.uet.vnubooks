package com.uet.android.mouspad.Fragment.StoryInterface;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryChapterContentActivity;
import com.uet.android.mouspad.Activity.UserPerform.UserActivity;
import com.uet.android.mouspad.Adapter.StoryAdapter;
import com.uet.android.mouspad.Adapter.StoryDetailAdapter;
import com.uet.android.mouspad.Adapter.TagsAdapter;
import com.uet.android.mouspad.Ebook.EbookDatabase;
import com.uet.android.mouspad.App;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.Ebook.PDFViewerActivity;
import com.uet.android.mouspad.Model.Ebook.PDFUrl;
import com.uet.android.mouspad.Model.ReadingListIndex;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.Tag;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Model.ViewModel.ContentModel;
import com.uet.android.mouspad.Model.ViewModel.ListModel;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.WidgetsUtils;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryDetailFragment extends Fragment implements DiscreteScrollView.ScrollStateChangeListener<StoryDetailAdapter.ViewHolder>,DiscreteScrollView.OnItemChangedListener<StoryDetailAdapter.ViewHolder>,
        View.OnClickListener {

    private LinearLayout mLinearLayout;
    private Toolbar mToolbar;
    private int mPosition ;
    private ArrayList<Story> mStories;
    private StoryDetailAdapter mStoryDetailAdapter;

    private RecyclerView mRecyclerStoryRecommend;
    private DiscreteScrollView mDiscreteStoryDetail;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String user_id;
    private String story_id;

    private ImageView mImgCoverTransparent, mImgAvatarUser;
    private TextView mTxtTitleStory, mTxtDescriptionStory, mTxtUserName;
    private Button mButtonReadStory;
    private CardView mCardViewChapters;
    private TextView mTextTotalChapters, mTextStatus, mTextLocation;
    private RecyclerView mRecyclerTags;

    private EbookDatabase mEbookDatabase;
    private ListModel mListModel;
    private int mCurrentPosition;

    private LinearLayout mCustomBottomSheet;
    private CoordinatorLayout mContainBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mHeaderLayout;
    private ImageView mHeaderImage;
    private EditText mEditBottomSheet;

    private List<ReadingListIndex> mReadingListIndices;

    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    private StoryDetailFragment() {
    }

    public static StoryDetailFragment newInstance(int position, ArrayList<Story> stories) {
        Bundle args = new Bundle();
        args.putInt(Constants.STORY_INDEX, position);
        args.putSerializable(Constants.STORY_LIST, stories);
        StoryDetailFragment fragment = new StoryDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(Constants.STORY_INDEX);
        mStories = (ArrayList<Story>) getArguments().getSerializable(Constants.STORY_LIST);
        setHasOptionsMenu(true);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view  = inflater.inflate(R.layout.fragment_story_detail, container, false);
        MappingWidgets(view);
        ActionToolBar();
        initView(view);
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(ConnectionUtils.isConnectingInternet){
            mToolbar.inflateMenu(R.menu.story_detail_menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_to_library:
                if(ConnectionUtils.isLoginValid){
                    addStoryToLibrary(mCurrentPosition);
                } else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_add_to_reading_list:
                if(ConnectionUtils.isLoginValid){
                    performBottomSheet();
                } else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
               // addStoryToReadingList("");
                return true;
            case R.id.action_create_new_list:
                if(ConnectionUtils.isLoginValid){
                    inflateDataToBlankEditText(getContext());
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share_story:
                if(ConnectionUtils.isLoginValid){
                    shareContentViaSocial();
                } else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_story_report:
                if(ConnectionUtils.isLoginValid){
                    Toast.makeText(getContext(), "report", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    private void shareContentViaSocial(){
        String titleParse = mStories.get(mCurrentPosition).getTitle().replace(" ", "+");
        String descriptionParse =  mStories.get(mCurrentPosition).getDescription().replace(" ", "+");
        String dynamicCustom = "https://mouspad.team/?" +
                "link=https://mouspad.team/" + /*link*/
                titleParse + "&st_id=" + story_id + "&stc_id=" + "chapter_id" +
                "&apn=" + /*getPackageName()*/
                "com.uet.android.mouspad" +
                "&st=" + /*titleSocial*/
                titleParse +
                "&sd=" + /*description*/
                descriptionParse +
                "&utm_source=" + /*source*/
                "AndroidApp";
        Uri uri = Uri.parse("https://mouspad.team/" + titleParse + "&st_id=" + story_id + "&stc_id=" + "chapter_id");
        String path = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://mouspad.team")
                .setLink(uri)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build()) //com.melardev.tutorialsfirebase
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share this App").setDescription("blabla").build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource("AndroidApp").build())
                .buildDynamicLink().getUri().toString();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, path);
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sendIntent, null));
    }

    private void performBottomSheet() {
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mHeaderImage.setRotation(slideOffset * 180);
            }
        });

        for(int i = 1 ; i < mCustomBottomSheet.getChildCount() -1; i ++){
            final String storyid = mStories.get(mStoryDetailAdapter.getItemId()).getStory_id();
            if(i ==1){
                final CheckBox checkBox = (CheckBox) mCustomBottomSheet.getChildAt(i);
                checkBox.setVisibility(View.VISIBLE);
                mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(storyid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(!checkBox.isChecked()){
                                    mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(storyid).delete();
                                    mFirebaseFirestore.collection("follows/" + storyid + "/contain").document(user_id).delete();
                                } else {
                                    addStoryToLibrary(mCurrentPosition);
                                }
                                mContainBottomSheet.setVisibility(View.GONE);
                                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                } else {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    mContainBottomSheet.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
            if(i >1 && i<= mReadingListIndices.size() + 1 ){
                final CheckBox checkBox = (CheckBox) mCustomBottomSheet.getChildAt(i);
                checkBox.setVisibility(View.VISIBLE);
                final String listId = mReadingListIndices.get(i-2).getList_id();
                String titleList = mReadingListIndices.get(i-2).getTitle();
                checkBox.setText(titleList);
                mFirebaseFirestore.collection("reading_lists/" + user_id + "/contain/" + listId + "/contain").document(storyid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            checkBox.setChecked(true);
                        }else {
                            checkBox.setChecked(false);
                        }
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(!checkBox.isChecked()){
                                    mFirebaseFirestore.collection("reading_lists/" + user_id + "/contain/" + listId + "/contain").document(storyid).delete();
                                    mFirebaseFirestore.collection("follows/" + storyid + "/contain").document(user_id).delete();
                                } else {
                                    addStoryToReadingList(listId);
                                }
                                mContainBottomSheet.setVisibility(View.GONE);
                                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                } else {
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    mContainBottomSheet.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
            if(i == mCustomBottomSheet.getChildCount() - 2){
                mContainBottomSheet.setVisibility(View.VISIBLE);
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                mHeaderLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            mContainBottomSheet.setVisibility(View.GONE);
                        }

                    }
                });
            }
        }
    }

    private List<User> mUsers = new ArrayList<>();
    private List<ContentModel> contentModelList = new ArrayList<>();
    private int currentPosOfQuery = -1;
    private boolean isloaded = false;

    private void initData() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        if(ConnectionUtils.isLoginValid){
            user_id = mFirebaseAuth.getCurrentUser().getUid();
            mEbookDatabase = App.getDB(getContext());
        }
        mStoryDetailAdapter = new StoryDetailAdapter(mStories, getActivity());

        mListModel = new ListModel();
        for( int i = 0 ;i < mStories.size(); i ++){
            currentPosOfQuery = i;

            Story story = mStories.get(i);
            final String user_id = story.getUser_id();
            final ContentModel contentModel = new ContentModel();
            mFirebaseFirestore.collection("users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            mUsers.add(user);
//                            if(currentPosOfQuery == mStories.size() -1){
//                                mListModel.setUsers(mUsers);
//                            }
                            if(mUsers.size() == mStories.size()){
                                mListModel.setUsers(mUsers);
                            }
                        }
                    }
                });
            if(ConnectionUtils.isConnectingInternet){
                mFirebaseFirestore.collection("locations").document(story.getStory_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String place = task.getResult().get("description").toString();
                            if(place== null && place.equals("")){
                                place = "";
                            }
                            contentModel.setLocation(place);
                        }
                    }
                });
            } else {
                contentModel.setLocation("");
            }
            final List<String> mTags = new ArrayList<>();
            getTagsOfAStory(story.getStory_id(), mTags, contentModel);

            List<StoryChapter> storyChapters = new ArrayList<>();
            getChaptersOfAStory(story.getStory_id(), storyChapters, contentModel);

            if(currentPosOfQuery == mStories.size() -1) {
                mListModel.setContentModels(contentModelList);
            }
        }

        if(ConnectionUtils.isLoginValid){
            mReadingListIndices = new ArrayList<>();
            Query query = mFirebaseFirestore.collection("reading_list_index/" + user_id + "/contain").limit(100);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        ReadingListIndex index = documentSnapshot.toObject(ReadingListIndex.class);
                        mReadingListIndices.add(index);
                    }
                }
            });
        }
    }

    private void getTagsOfAStory(String story_id, final List<String> mTags, final  ContentModel contentModel){
        if(ConnectionUtils.isConnectingInternet){
            Query queryTag = mFirebaseFirestore.collection("story_tags/" + story_id + "/contain").limit(100);
            queryTag.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        Tag tag = documentSnapshot.toObject(Tag.class);
                        mTags.add(tag.getTitle());
                        contentModel.setTagList(mTags);
                    }
                }
            });
        } else {
            contentModel.setTagList(mTags);
        }

    }

    private void getChaptersOfAStory(String story_id, final List<StoryChapter> storyChapters, final ContentModel contentModel){
        Query query = mFirebaseFirestore.collection("chapters/" +story_id + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
                    storyChapters.add(chapter);
                    contentModel.setChapterList(storyChapters);
                }
                contentModelList.add(contentModel);
                if(currentPosOfQuery == mStories.size() - 1){
                    mListModel.setContentModels(contentModelList);
                    isloaded = true;
                    updateCurrentItemView(mPosition);
                }
            }
        });
    }

    private void MappingWidgets(View view) {
        mLinearLayout = view.findViewById(R.id.linearLayoutDetailStory);
        mToolbar = view.findViewById(R.id.toolbarStoryDetail);
        mDiscreteStoryDetail = view.findViewById(R.id.discreteScrollStoryDetail);
        mImgCoverTransparent = view.findViewById(R.id.imgCoverTransparentStoryDetail);
        mImgAvatarUser = view.findViewById(R.id.imgAvatarItemStoryDetail);
        mTxtTitleStory = view.findViewById(R.id.textNameItemStoryDetail);
        mTxtDescriptionStory = view.findViewById(R.id.txtDesItemStoryDetail);
        mTxtUserName = view.findViewById(R.id.txtUserItemStoryDetail);
        mButtonReadStory = view.findViewById(R.id.btnReadItemStoryDetail);
        mCardViewChapters = view.findViewById(R.id.cardviewChaptersStoryDetail);
        mTextTotalChapters = view.findViewById(R.id.cardviewTotalChapterStoryDetail);
        mTextStatus = view.findViewById(R.id.cardviewStatusChapterStoryDetail);
        mTextLocation = view.findViewById(R.id.txtLocationStoryDetail);
        mRecyclerTags = view.findViewById(R.id.recyclerViewTagsStoryDetail);
        mRecyclerStoryRecommend = view.findViewById(R.id.recyclerViewRecommendStoryDetail);


        mCustomBottomSheet = view.findViewById(R.id.custom_bottom_sheet);
        mContainBottomSheet = view.findViewById(R.id.containBottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);
        mHeaderLayout = view.findViewById(R.id.header_layout);
        mHeaderImage = view.findViewById(R.id.header_arrow);
        mEditBottomSheet = view.findViewById(R.id.editBottomSheet);
    }

    private void ActionToolBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initView(View view){
        mDiscreteStoryDetail.setOrientation(DSVOrientation.HORIZONTAL);
        mDiscreteStoryDetail.addScrollStateChangeListener(this);
        mDiscreteStoryDetail.addOnItemChangedListener(this);
        mDiscreteStoryDetail.setAdapter(mStoryDetailAdapter);
        mDiscreteStoryDetail.scrollToPosition(mPosition);
        mDiscreteStoryDetail.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
    }

    private String title;
    private Boolean status;

    private void addStoryToLibrary(int mPosition){
        Story story = mStories.get(mPosition);
        story_id = story.getStory_id();
        String owner_id =story.getUser_id();
        String format = story.getFormat();
        int readingStatus = 0;
        title = story.getTitle();
        mEbookDatabase.setReadmode(Constants.READING_MODE_LIBRARY);
        if(format.equals(Constants.FORMAT_DEFAULT_APP)){
            status = false;
            mFirebaseFirestore.collection("users").document(owner_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                User user = task.getResult().toObject(User.class);
                                mEbookDatabase.addBookDf(story_id, title, user.getAccount(), System.currentTimeMillis());
                                Log.d("LibrarayFragmnet ad", user.getAccount());
                            }
                        }
                    });
        } else if (format.equals(Constants.FORMAT_PDF)){
            status = true;
            readingStatus = 100;
            checkFileExistInDatabase(story_id, owner_id,"pdf");
        } else if(format.equals(Constants.FORMAT_EPUB)){
            status = true;
            readingStatus =100;
            checkFileExistInDatabase(story_id, owner_id, "epub");
        }
        Map<String, Object> library = new HashMap<>();
        library.put("story_id", story_id);
        library.put("owner_id", owner_id);
        library.put("downloaded", status);
        library.put("status",readingStatus);
        library.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(story_id).set(library).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "Saved successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String mes = e.getMessage();
                Toast.makeText(getContext(), mes, Toast.LENGTH_SHORT).show();
            }
        });


        Map<String, Object>map = new HashMap<>();
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("user_id", user_id);
        mFirebaseFirestore.collection("follows/" + story_id + "/contain").document(user_id).set(map);
    }

    private void addStoryToReadingList(String listId){
        story_id = mStories.get(mStoryDetailAdapter.getItemId()).getStory_id();
        String owner_id = mStories.get(mStoryDetailAdapter.getItemId()).getUser_id();
        Map<String, Object> list = new HashMap<>();
        list.put("story_id", story_id);
        list.put("owner_id", owner_id);
        list.put("list_id", listId);
        list.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("reading_lists/" + user_id + "/contain/" + listId + "/contain").document(story_id).set(list);

        Map<String, Object>map = new HashMap<>();
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("user_id", user_id);
        mFirebaseFirestore.collection("follows/" + story_id + "/contain").document(user_id).set(map);
    }

    private void checkFileExistInDatabase(String story_id, String owner_id, String format){
        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getActivity().getPackageName() + "/files";
        fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
        fileDirectory = fullDirectory + "/" + story_id + "." + format;
        int result = DataUtils.checkFileExists(fullDirectory, story_id+ "." + format);
        if(result == Constants.FILE_EXISTS) {
            Toast.makeText(getContext(), "File is already downloaded", Toast.LENGTH_SHORT).show();
            mFirebaseFirestore.collection("users").document(owner_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                User user = task.getResult().toObject(User.class);
                                addBook(fileDirectory, user.getAccount(), true, System.currentTimeMillis());
                                Log.d("LibrarayFragmnet ad", user.getAccount());
                            }
                        }
                    });
        } else if(result == Constants.FILE_DOES_NOT_EXIST){
            findFileOnFirebase(story_id, format);
        }
    }

    private String fullDirectory ;
    private String fileDirectory ;

    private void findFileOnFirebase(final String storyId, final String format){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("story_pdfs").document(storyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    PDFUrl pdfStory = task.getResult().toObject(PDFUrl.class);
                    String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
                    String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getActivity().getPackageName() + "/files";
                    fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + storyId;
                    fileDirectory = fullDirectory + "/" + storyId + "." + format;
                    downloadFileFromFirebase(storyId, "." + format, storyId,  pdfStory.getUrl());
                }
            }
        });
    }

    private void downloadFileFromFirebase( String filename ,String fileExtension, String directory, String url) {
        DownloadManager downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getActivity().getApplicationContext(), directory, filename + fileExtension);
        downloadManager.enqueue(request);
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            mFirebaseFirestore.collection("users").document(user_id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                User user = task.getResult().toObject(User.class);
                                                addBook(fileDirectory, user.getAccount(), true, System.currentTimeMillis());
                                            }
                                        }
                                    });
                        } else {
                            int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                        }
                    }
                }
            }
        }
    };

    private boolean addBook(String filename,String author,  boolean showToastWarnings, long dateadded) {
        try {
            Log.d("LibrarayFragmnet 1", author);
            if (mEbookDatabase.containsBook(filename)) {
                if (showToastWarnings) {
                    Toast.makeText(getContext(), getString(R.string.already_added, new File(filename).getName()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            mEbookDatabase.addBook(fileDirectory, title, author, dateadded);
        } catch (Exception e) {
            Log.e("BookList", "File: " + filename  + ", " + e.getMessage(), e);
        }
        return false;
    }

    private void inflateDataToBlankEditText(Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View blankFragment = layoutInflater.inflate(R.layout.menu_reading_list, null);

        alertDialog.setView(blankFragment);
        final  AlertDialog dialogParam = alertDialog.create();

        final EditText mEditNewReadingList = blankFragment.findViewById(R.id.editNewReadingList);
        ImageButton mBtnNewReadingList =blankFragment.findViewById(R.id.btnConfirmNewReadingList);
        dialogParam.show();

        mBtnNewReadingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = WidgetsUtils.validateEditText(mEditNewReadingList, "List's title is empty!");
                if(isValid){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("list_id", "");
                    map.put("title", mEditNewReadingList.getText().toString());
                    mFirebaseFirestore.collection("reading_list_index/" + user_id + "/contain").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                String listId = task.getResult().getId();
                                HashMap<String, Object> reMap = new HashMap<>();
                                reMap.put("list_id", listId);
                                reMap.put("title", mEditNewReadingList.getText().toString());
                                mFirebaseFirestore.collection("reading_list_index/" + user_id + "/contain").document(listId).set(reMap);
                                addStoryToReadingList(listId);
                                if(dialogParam.isShowing()){
                                    mEditNewReadingList.setText("");
                                    dialogParam.hide();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) { }
    @Override
    public void onCurrentItemChanged(@Nullable StoryDetailAdapter.ViewHolder viewHolder, final int adapterPosition) {
      updateCurrentItemView(adapterPosition);
    }

    private void updateCurrentItemView(final int adapterPosition) {
        if(ConnectionUtils.isConnectingInternet){
            requestSearch(mStories.get(adapterPosition).getUser_id());
        }
        mCurrentPosition = adapterPosition;
      //  Toast.makeText(getContext(), String.valueOf(adapterPosition), Toast.LENGTH_SHORT).show();
        Uri coverUri = Uri.parse(mStories.get(adapterPosition).getCover());
        Picasso.get()
                .load(coverUri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(mImgCoverTransparent);

        if(isloaded == true && mListModel.getContentModels().size() == mStories.size()){
            //User user = mUsers.get(adapterPosition);
            mFirebaseFirestore.collection("users").document(mStories.get(adapterPosition).getUser_id()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                 User user = task.getResult().toObject(User.class);
                            Uri avatarUri = Uri.parse(user.getAvatar());
                            final String user_id = user.getUser_id();
                            Picasso.get()
                                    .load(avatarUri)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(mImgAvatarUser);

                            mImgAvatarUser.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), UserActivity.class);
                                    intent.putExtra(Constants.USER_ID, user_id);
                                    startActivity(intent);
                                }
                            });

                            mTxtUserName.setText(user.getAccount());
                            mTxtUserName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), UserActivity.class);
                                    intent.putExtra(Constants.USER_ID, user_id);
                                    startActivity(intent);
                                }
                            });
                        }
                    });


            mTxtTitleStory.setText(mStories.get(adapterPosition).getTitle());
            mTxtDescriptionStory.setText(mStories.get(adapterPosition).getDescription());
            String place = mListModel.getContentModels().get(adapterPosition).getLocation();
            if(place!= null && !place.equals("")){
                mTextLocation.setText(place);
            } else {
                mTextLocation.setVisibility(View.GONE);
            }
            TagsAdapter tagsAdapter = new TagsAdapter(mListModel.getContentModels().get(adapterPosition).getTagList(), getContext());
            if(!mListModel.getContentModels().get(adapterPosition).getTagList().isEmpty()){
                mRecyclerTags.setVisibility(View.VISIBLE);
                mRecyclerTags.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager_tags = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                mRecyclerTags.setLayoutManager(linearLayoutManager_tags);
                mRecyclerTags.setAdapter(tagsAdapter);
            }else {
                mRecyclerTags.setVisibility(View.GONE);
            }

            final ArrayList<String> mTitles = new ArrayList<>();
            final ArrayList<String> mChapterIds = new ArrayList<>();
            String format = mStories.get(adapterPosition).getFormat();
            if(format.equals(Constants.FORMAT_DEFAULT_APP)){
                Query query = mFirebaseFirestore.collection("chapters/" +mStories.get(adapterPosition).getStory_id() + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
                query.get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                            StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
                            mChapterIds.add(chapter.getChapter_id());
                            mTitles.add(chapter.getTitle());
                        }
                        if(mTitles.size() == task.getResult().size() && mChapterIds.size() == task.getResult().size()){
                            mCardViewChapters.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ListView listView = LayoutUtils.inflateListViewDataDialogIntoLayout(getContext(), mTitles,  R.string.text_chapters);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l_id) {
                                            Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
                                            intent.putExtra(Constants.STORY_CHAPTER_INDEX,pos);
                                            intent.putExtra(Constants.STORY_INDEX, mStories.get(adapterPosition).getStory_id());
                                            intent.putExtra(Constants.STORY_TITLE, mStories.get(adapterPosition).getTitle());
                                            intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
                                            intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
                                            Log.d("Chaptertitle 1", mTitles.size() +"");
                                            intent.putExtra(Constants.OWNER_ID, user_id);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });

                            mButtonReadStory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
                                    intent.putExtra(Constants.STORY_CHAPTER_INDEX,0);
                                    intent.putExtra(Constants.STORY_INDEX, mStories.get(adapterPosition).getStory_id());
                                    intent.putExtra(Constants.STORY_TITLE, mStories.get(adapterPosition).getTitle());
                                    intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
                                    intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
                                    Log.d("Chaptertitle 1", mTitles.size() +"");
                                    intent.putExtra(Constants.OWNER_ID, user_id);
                                    startActivity(intent);
                                }
                            });
                        }
                        mTextTotalChapters.setText(task.getResult().size() + " parts");
                    }
                });
            } else if(format.equals(Constants.FORMAT_PDF)){
                mTextTotalChapters.setText("1 part pdf");
                mButtonReadStory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPDFContent(mStories.get(adapterPosition).getStory_id(), mStories.get(adapterPosition).getTitle(), mStories.get(adapterPosition).getUser_id());
                    }
                });
                mCardViewChapters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPDFContent(mStories.get(adapterPosition).getStory_id(), mStories.get(adapterPosition).getTitle(), mStories.get(adapterPosition).getUser_id());
                    }
                });
            } else if(format.equals(Constants.FORMAT_EPUB)){
                mTextTotalChapters.setText("1 part ebook");
                mButtonReadStory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getEPUBContent(mStories.get(adapterPosition).getStory_id(), mStories.get(adapterPosition).getTitle(),  mStories.get(adapterPosition).getUser_id());
                    }
                });
                mCardViewChapters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getEPUBContent(mStories.get(adapterPosition).getStory_id(), mStories.get(adapterPosition).getTitle(),  mStories.get(adapterPosition).getUser_id());
                    }
                });
            }
            mTextStatus.setText(mStories.get(adapterPosition).getStatus());
        }
    }

    private void requestSearch(String searchableAttribute){
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_story");
        com.algolia.search.saas.Query querySearch = new com.algolia.search.saas.Query(searchableAttribute)
                .setAttributesToRetrieve("storyInfo")
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
                        String infor = object.getString("storyInfo");

                        JSONObject info = hits.getJSONObject(i).getJSONObject("_highlightResult");
                        JSONObject title = info.getJSONObject("storyTitle");
                        JSONObject id = info.getJSONObject("instanceId");
                        JSONObject cover = info.getJSONObject("storyCover");
                        JSONObject ownerId = info.getJSONObject("ownerId");
                        JSONObject genre = info.getJSONObject("storyGenre");
                        JSONObject status = info.getJSONObject("storyStatus");
                        JSONObject description = info.getJSONObject("storyDes");
                        // JSONObject published = info.getJSONObject("storyPublish");
                        JSONObject format = info.getJSONObject("storyFormat");

                        String titleString = title.getString("value").replace("<em>", "").replace("</em>", "");
                        String idString = id.getString("value").replace("<em>", "").replace("</em>", "");
                        String coverString = cover.getString("value").replace("<em>", "").replace("</em>", "");;
                        String ownerIdString = ownerId.getString("value").replace("<em>", "").replace("</em>", "");;
                        String genreString = genre.getString("value").replace("<em>", "").replace("</em>", "");;
                        String statusString = status.getString("value").replace("<em>", "").replace("</em>", "");;
                        String descriptionString = description.getString("value").replace("<em>", "").replace("</em>", "");;
                        //boolean publishedString = published.getBoolean("value");
                        String formatString = format.getString("value").replace("<em>", "").replace("</em>", "");;
                        Log.d("SearchAl s", titleString);
                        Log.d("SearchAl s", idString);
                        Log.d("SearchAl s", formatString);

                        Story story = new Story(idString, ownerIdString, titleString, descriptionString, coverString, genreString, statusString, formatString, true);
                        stories.add(story);
                        if(stories.size() == 10)break;
                        Log.d("SearchAl s", "" + stories.size());
                    }

                    StoryAdapter storyAdapter = new StoryAdapter( stories, getContext());
                    Log.d("SearchAl s", "" + storyAdapter.getItemCount());
                    mRecyclerStoryRecommend.setHasFixedSize(true);
                    LayoutUtils.LinearLayoutManagerWithSmoothScroller linearLayoutManager2 = new LayoutUtils.LinearLayoutManagerWithSmoothScroller(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    mRecyclerStoryRecommend.setLayoutManager(linearLayoutManager2);
                    mRecyclerStoryRecommend.setAdapter(storyAdapter);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void getPDFContent(final String story_id, final String story_title, final String ower_id) {
        if(ConnectionUtils.isConnectingInternet){
            mFirebaseFirestore.collection("story_pdfs").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        PDFUrl pdfStory = task.getResult().toObject(PDFUrl.class);
                        String url = pdfStory.getUrl();
                        Intent intent = new Intent(getContext(), PDFViewerActivity.class);
                        intent.putExtra(Constants.STORY_PDF_URL,url);
                        intent.putExtra(Constants.FORMAT_PDF, "");
                        intent.putExtra(Constants.STORY_INDEX, story_id);
                        intent.putExtra(Constants.STORY_TITLE,story_title );
                        intent.putExtra(Constants.USER_ID, ower_id);
                        startActivity(intent);
                    }
                }
            });
        } else {
            String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
            String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getContext().getPackageName() + "/files";
            String fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
            int result = DataUtils.checkFileExists(fullDirectory, story_id+ ".pdf" );
            if(result == Constants.FILE_EXISTS){
                Intent intent = new Intent(getContext(), PDFViewerActivity.class);
                intent.putExtra(Constants.STORY_PDF_URL, "");
                intent.putExtra(Constants.FORMAT_PDF, fullDirectory);
                intent.putExtra(Constants.STORY_INDEX, story_id);
                intent.putExtra(Constants.STORY_TITLE,story_title );
                intent.putExtra(Constants.USER_ID, ower_id);
                startActivity(intent);
            } else if(result == Constants.FILE_DOES_NOT_EXIST) {
                Toast.makeText(getContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getEPUBContent(String story_id, final String story_title,String owner_id){
        Intent intent = new Intent(getContext(), EpubListActivity.class);
        intent.putExtra(Constants.FORMAT_EPUB, Constants.FORMAT_EPUB);
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.USER_ID,owner_id );
        intent.putExtra(Constants.STORY_TITLE, story_title);
        intent.putExtra(Constants.READING_MODE, Constants.READING_MODE_DEFAULT);
        startActivity(intent);
    }

    @Override
    public void onScrollStart(@NonNull StoryDetailAdapter.ViewHolder currentItemHolder, int adapterPosition) {
    }

    @Override
    public void onScrollEnd(@NonNull StoryDetailAdapter.ViewHolder currentItemHolder, final int adapterPosition) {
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable StoryDetailAdapter.ViewHolder currentHolder, @Nullable StoryDetailAdapter.ViewHolder newCurrent) {
    }
}