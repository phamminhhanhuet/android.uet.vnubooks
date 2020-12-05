
package com.uet.android.mouspad.Fragment.StoryInterface;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.CommentActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryChapterContentActivity;
import com.uet.android.mouspad.Model.ItemView;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Model.ViewModel.LibraryStoryModel;
import com.uet.android.mouspad.Model.ViewModel.LibraryViewModel;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.APIService;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Client;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.URLUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class StoryChapterContentFragment extends Fragment  {

    private TextView mTxtTitle, mTxtContent;
    private AreTextView areTextView;
    private Toolbar mToolbar, mToolbarBottom;
    private Button mButtonIncreaseTextSize, mButtonDecreaseTextSize;
    private ImageButton mButtonBackgroundWhite, mButtonBackgroundBlack, mButtonBackgroundAccent;

    private ImageView mImageAddMedia, mImageMediaBackground, mImagePressAudio, mImageMenuMedia;
    private TextView mTextAddMedia,mTextCurrentTimeAudio, mTextDurationAudio;
    private SeekBar mSeekBarAudio;
    private View mViewAudio, mYoutubeView;
    private YouTubePlayerView mYouTubePlayer;

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();

    private RelativeLayout mRelativeLayoutTextAttribute;
    private LinearLayout mLinearLayoutReadingContent;
    private ConstraintLayout mConstraintLayoutAudio;
    private int mPositionIndex ;
    private String mStoryIndex;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private User mUser;
    private String user_id;
    private String chapter_id;
    private String story_id;
    private String owner_id;
    private String story_title;
    private Story mStory;

    private String youtubeApi = "";
    private Uri coverUri = null;
    private Uri audioUri = null;

    private ArrayList<StoryChapter> mStoryChapters;
    private ArrayList<String> mChapterIds ;
    private ArrayList<String> mTitles;
    private StoryChapter mStoryChapter;
    private BottomNavigationView mNavigationChapterContent;

    private APIService mApiService;

    private CallbackManager mCallbackManager;
    private ShareDialog mShareDialog;

    public static String READ_MODE = "READ_MODE";
    private int read_mode = -1;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    private LibraryViewModel libraryViewModel;
    public StoryChapterContentFragment() {
    }

    public static StoryChapterContentFragment newInstance(int position, String storyId, String storyTitle,  String ownerId, ArrayList<String> chapterIds, ArrayList<String> titles) {
        Bundle args = new Bundle();
        args.putInt(Constants.STORY_CHAPTER_INDEX, position);
        args.putString(Constants.STORY_INDEX, storyId);
        args.putSerializable(Constants.STORY_CHAPTER_LIST, chapterIds);
        args.putSerializable(Constants.STORY_CHAPTER_TITLE, titles);

        args.putString(Constants.OWNER_ID, ownerId);
        args.putString(Constants.STORY_TITLE, storyTitle);
        StoryChapterContentFragment storyChapterContentFragment = new StoryChapterContentFragment();
        storyChapterContentFragment.setArguments(args);
        return storyChapterContentFragment;
    }

    public static StoryChapterContentFragment newInstance() {
        return new StoryChapterContentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mSharedPreferences = getActivity().getSharedPreferences(READ_MODE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        read_mode = mSharedPreferences.getInt("readmode", 0);

        mPositionIndex = getArguments().getInt(Constants.STORY_CHAPTER_INDEX,0);
        story_id = getArguments().getString(Constants.STORY_INDEX);
        story_title = getArguments().getString(Constants.STORY_TITLE);
        owner_id = getArguments().getString(Constants.OWNER_ID);
        mChapterIds = (ArrayList<String>) getArguments().getSerializable(Constants.STORY_CHAPTER_LIST);
        mTitles = (ArrayList<String>) getArguments().getSerializable(Constants.STORY_CHAPTER_TITLE);

        mStoryChapters = new ArrayList<>();

        if(ConnectionUtils.isLoginValid && ConnectionUtils.isConnectingInternet){
            user_id = mFirebaseAuth.getCurrentUser().getUid();
            mFirebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mUser = task.getResult().toObject(User.class);
                }
            });

            HashMap<String, Object> map = new HashMap<>();
            map.put("story_id", story_id);
            mFirebaseFirestore.collection("current_read").document(user_id).set(map);
        }

        initData();

        if(ConnectionUtils.isLoginValid && ConnectionUtils.isConnectingInternet){
            mApiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            ActivityUtils.createNotificationChannel(getContext());
            FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());
            mCallbackManager = CallbackManager.Factory.create();
            mShareDialog = new ShareDialog(this);
        }
        setHasOptionsMenu(true);
    }

    private View mView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        final View view =inflater.inflate(R.layout.fragment_story_chapter_content, container, false);
        mView = view;

        if(!mChapterIds.isEmpty() && ConnectionUtils.isConnectingInternet){
            chapter_id = mChapterIds.get(mPositionIndex);
            mFirebaseFirestore.collection("chapters/" + story_id + "/contain").document(chapter_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mStoryChapter = task.getResult().toObject(StoryChapter.class);

                    mFirebaseFirestore.collection("views").document(chapter_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String, Object> map = new HashMap<>();
                            if(task.getResult().exists()){
                                ItemView itemView = task.getResult().toObject(ItemView.class);
                                long viewNum = itemView.getTotal() +1;
                                map.put("total", viewNum);
                            }else {
                                map.put("total", 1);
                            }
                            mFirebaseFirestore.collection("views").document(chapter_id).set(map);
                        }
                    });

                    if(ConnectionUtils.isLoginValid){
                        mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    LibraryItem libraryItem = task.getResult().toObject(LibraryItem.class);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("downloaded", libraryItem.isDownloaded());
                                    map.put("owner_id", libraryItem.getOwner_id());
                                    map.put("story_id", libraryItem.getStory_id());
                                    map.put("timestamp", libraryItem.getTimestamp());
                                    int status = mPositionIndex / mTitles.size() * 100;
                                    map.put("status", status);
                                    mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(story_id).set(map);
                                }
                            }
                        });
                    }

                    if(mStoryChapter != null){
                        MappingWidgets(view);
                        ActionToolbar();
                        initView(view);
                    }
                }
            });
        } else {
            MappingWidgets(view);
            ActionToolbar();
            initView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mToolbar.inflateMenu(R.menu.story_chapter_content_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_reading_mode:
                setInvisibleSettingReadingMode();
                return  true;
            case R.id.action_story_chapter_list:
                ListView listView = null;
                listView = LayoutUtils.inflateListViewDataDialogIntoLayout(getContext(), mTitles, R.string.text_chapters);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l_id) {
                        Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
                        intent.putExtra(Constants.STORY_CHAPTER_INDEX, mPositionIndex);
                        intent.putExtra(Constants.STORY_INDEX, story_id);
                        intent.putExtra(Constants.STORY_TITLE, story_title);
                        intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
                        intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
                        intent.putExtra(Constants.OWNER_ID, owner_id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                        getActivity().finish();
                    }
                });
                return true;
            default:
                return false;
        }
    }

    private int countDownActivity = 0;
    private int countUpActivity = 0;

    private void MappingWidgets(View view) {
        areTextView = view.findViewById(R.id.areTextView);
        areTextView.setVisibility(View.VISIBLE);

        mTxtTitle = view.findViewById(R.id.txtTitleChapterContent);
        mTxtContent = view.findViewById(R.id.txtChapterContent);
        mTxtContent.setVisibility(View.GONE);
        mToolbar = view.findViewById(R.id.toolbarChapterContent);
        mButtonIncreaseTextSize = view.findViewById(R.id.btnTextSizeIncreaseChapterContent);
        mButtonDecreaseTextSize = view.findViewById(R.id.btnTextSizeDecreaseChapterContent);
        mButtonBackgroundWhite = view.findViewById(R.id.btnBackgroundWhiteChapterContent);
        mButtonBackgroundBlack = view.findViewById(R.id.btnBackgroundBlackChapterContent);
        mButtonBackgroundAccent = view.findViewById(R.id.btnBackgroundAccentChapterContent);

        mRelativeLayoutTextAttribute = view.findViewById(R.id.relativeLayoutTextAttrChapterContent);
        mLinearLayoutReadingContent = view.findViewById(R.id.linearLayoutReadingChapterContent);

        //navigation
        mNavigationChapterContent = view.findViewById(R.id.navigationChapterContent);
        mNavigationChapterContent.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(ConnectionUtils.isLoginValid  &&ConnectionUtils.isConnectingInternet){
            prepareNavigationItemMenu();
        }

        //media
        View viewAddMedia = mLinearLayoutReadingContent.getChildAt(0);
        mConstraintLayoutAudio = viewAddMedia.findViewById(R.id.constraintLayoutAudioAddMedia);
        mImageAddMedia = viewAddMedia.findViewById(R.id.imgItemAddMedia);
        mTextAddMedia = viewAddMedia.findViewById(R.id.textItemAddMedia);
        mImageMenuMedia = viewAddMedia.findViewById(R.id.menuAddMedia);
        mViewAudio = viewAddMedia.findViewById(R.id.viewBackgroundAudio);
        mImageMediaBackground = viewAddMedia.findViewById(R.id.imgItemAddMedia2);
        mImagePressAudio = viewAddMedia.findViewById(R.id.imgAudioPressAddMedia);
        mTextCurrentTimeAudio = viewAddMedia.findViewById(R.id.textCurrentTimeAddMedia);
        mTextDurationAudio = viewAddMedia.findViewById(R.id.textDurationAddMedia);
        mSeekBarAudio = viewAddMedia.findViewById(R.id.seekBarAudioAddMedia);
        mYoutubeView= view.findViewById(R.id.youtubeAddMedia);
        mYouTubePlayer = mYoutubeView.findViewById(R.id.item_youtube_view);

        mTextAddMedia.setVisibility(View.GONE);
        mImageMenuMedia.setVisibility(View.GONE);
        mSeekBarAudio.setMax(100);
        mSeekBarAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int playPosition = (mMediaPlayer.getDuration()/100) * seekBar.getProgress();
                mMediaPlayer.seekTo(playPosition);
                mTextDurationAudio.setText(milliSecondsToTimer(mMediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mImagePressAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer.isPlaying()){
                    mHandler.removeCallbacks(mUpdater);
                    mMediaPlayer.pause();
                    mImagePressAudio.setImageResource(R.drawable.ic_play_circle);

                } else {
                    mMediaPlayer.start();
                    mImagePressAudio.setImageResource(R.drawable.ic_pause_circle);
                    updateSeekbar();
                }
            }
        });


        final ScrollView scrollView = view.findViewById(R.id.scrollViewReadingChapterContent);
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onScrollChanged() {
                        if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                            if(mPositionIndex >= 0 && mPositionIndex < mChapterIds.size() -1){
                                if(countDownActivity == 5){
                                    countDownActivity = 0;
                                    onNewDownActivityFromThisContext(mPositionIndex +1 );
                                } else {
                                    countDownActivity ++;
                                }
                            }
                        } else if (scrollView.getScrollY() <= 0){
                            if(mPositionIndex > 0 && mPositionIndex < mChapterIds.size()){
                                if(countUpActivity == 5){
                                    countUpActivity = 0;
                                    onNewUpActivityFromThisContext(mPositionIndex -1);
                                } else {
                                    countUpActivity ++;
                                }
                            }
                        }
                    }
                });
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mTitles.get(mPositionIndex));
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mButtonDecreaseTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSizeReadingLayout(Constants.ACTION_DECREASE_TEXT_SIZE_CODE);
            }
        });

        mButtonIncreaseTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSizeReadingLayout(Constants.ACTION_INCREASE_TEXT_SIZE_CODE);
            }
        });

        mButtonBackgroundWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackgroundReadingLayout(R.color.colorThemeWhite);
            }
        });

        mButtonBackgroundBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackgroundReadingLayout(R.color.colorThemeBlack);
            }
        });
        mButtonBackgroundAccent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackgroundReadingLayout(R.color.colorAccent);
            }
        });

    }

    private LibraryStoryModel libraryStoryModel ;

    private void initData()  {
        if(ConnectionUtils.isConnectingInternet){
            mFirebaseFirestore.collection("stories/").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mStory = task.getResult().toObject(Story.class);
                }
            });
        } else {
            try {
                chapter_id = mChapterIds.get(mPositionIndex);
                libraryStoryModel = new LibraryStoryModel(getContext());
                libraryStoryModel = DataUtils.loadDataFromInternalStorage(getContext(),libraryStoryModel);
                ArrayList<Story> mStories = libraryStoryModel.getStories();
                for(int i = 0; i < mStories.size() ;i ++ ){
                    if(mStories.get(i).getStory_id().equals(story_id)){
                        mStory = mStories.get(i);
                        mStoryChapter = libraryStoryModel.getLibraryChapterModel().get(i).getStoryChapters().get(mPositionIndex);
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView(View view) {
        switch (read_mode){
            case 0:
                setBackgroundReadingLayout(R.color.colorThemeWhite);
                break;
            case 1:
                setBackgroundReadingLayout(R.color.colorThemeBlack);
                break;
            case 2:
                setBackgroundReadingLayout(R.color.colorAccent);
                break;
        }
        areTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mToolbar.getVisibility() == View.GONE && mNavigationChapterContent.getVisibility() == View.GONE){
                    mToolbar.setVisibility(View.VISIBLE);
                    mNavigationChapterContent.setVisibility(View.VISIBLE);
                } else {
                    mToolbar.setVisibility(View.GONE);
                    mNavigationChapterContent.setVisibility(View.GONE);
                }
            }
        });
        mRelativeLayoutTextAttribute.setVisibility(View.GONE);
        if(mPositionIndex >= 0 && mPositionIndex < mChapterIds.size()){
            mTxtTitle.setText(mTitles.get(mPositionIndex));
            String string = mStoryChapter.getContent();
//            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) Html.fromHtml(string, FROM_HTML_MODE_LEGACY, new URLUtils.URLImageGetter(getContext()),null );
      //      mTxtContent.setText(spannableStringBuilder);
            areTextView.fromHtml(string);

            coverUri = Uri.parse(mStoryChapter.getCover());
            audioUri = Uri.parse(mStoryChapter.getAudio());
            youtubeApi = mStoryChapter.getYoutube();

            if(!youtubeApi.isEmpty()){
                mYoutubeView.setVisibility(View.VISIBLE);
                mConstraintLayoutAudio.setVisibility(View.GONE);
                mImageAddMedia.setVisibility(View.GONE);
                mTextAddMedia.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Youtube exists", Toast.LENGTH_LONG).show();
                mYouTubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(youtubeApi, 0);
                    }
                });
            } else if(!audioUri.toString().isEmpty()){
                mConstraintLayoutAudio.setVisibility(View.VISIBLE);
                mImageAddMedia.setVisibility(View.GONE);
                mTextAddMedia.setVisibility(View.GONE);
                mYoutubeView.setVisibility(View.GONE);
                prepareMediaPlayer(audioUri);
                mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        mSeekBarAudio.setSecondaryProgress(i);
                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mSeekBarAudio.setProgress(0);
                        mImagePressAudio.setImageResource(R.drawable.ic_play_circle);
                        mTextCurrentTimeAudio.setText("00:00");
                        mTextDurationAudio.setText("00:00");
                        mediaPlayer.reset();
                        prepareMediaPlayer(audioUri);
                    }
                });

            }else if(coverUri!= null){
                mTextAddMedia.setVisibility(View.INVISIBLE);
                mConstraintLayoutAudio.setVisibility(View.GONE);
                mYoutubeView.setVisibility(View.GONE);
            }
            if(coverUri != null){
                Picasso.get()
                        .load(coverUri)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(mImageMediaBackground);
                Picasso.get()
                        .load(coverUri)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(mImageAddMedia);
            }
        }
    }

    private void setInvisibleSettingReadingMode(){
        if(mRelativeLayoutTextAttribute.getVisibility()== View.INVISIBLE || mRelativeLayoutTextAttribute.getVisibility() == View.GONE){
            mRelativeLayoutTextAttribute.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayoutTextAttribute.setVisibility(View.GONE);
        }
    }

    private void prepareMediaPlayer(Uri uri)  {
        try {
            mMediaPlayer.setDataSource(uri.toString());
            mMediaPlayer.prepare();
            mTextDurationAudio.setText(milliSecondsToTimer(mMediaPlayer.getDuration()));
        } catch (Exception e){
        }
    }

    private Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mMediaPlayer.getCurrentPosition();
            mTextCurrentTimeAudio.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekbar(){
        if(mMediaPlayer.isPlaying()){
            mSeekBarAudio.setProgress((int)(((float)mMediaPlayer.getCurrentPosition()/mMediaPlayer.getDuration()) *100));
            mHandler.postDelayed(mUpdater, 1000);
        }
    }

    private String milliSecondsToTimer(long miliseconds){
        String timeString = "";
        String sencondString;

        int hours = (int)( miliseconds/ (1000 * 60 * 60));
        int minutes = (int) (miliseconds %(1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((miliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if(hours >0){
            timeString = hours + ":";
        }
        if(seconds <10){
            sencondString = "0" + seconds;
        } else {
            sencondString = "" + seconds;
        }
        timeString = timeString + minutes + ":" +sencondString;
        return timeString;
    }

    private void setBackgroundReadingLayout(int colorId){
        mLinearLayoutReadingContent.setBackgroundColor(getResources().getColor(colorId));
        switch (colorId){
            case R.color.colorThemeWhite:
                mEditor.putInt("readmode", 0);
                mEditor.apply();
                areTextView.setTextColor(getResources().getColor(R.color.colorThemeBlack));
                break;
            case R.color.colorThemeBlack:
                mEditor.putInt("readmode", 1);
                mEditor.apply();
                areTextView.setTextColor(getResources().getColor(R.color.colorThemeWhite));
                break;
            case R.color.colorAccent:
                mEditor.putInt("readmode", 2);
                mEditor.apply();
                areTextView.setTextColor(getResources().getColor(R.color.colorThemeBlack));
                break;
        }
    }

    private int mCurrentTextSize = 18;
    private void setTextSizeReadingLayout (int actionId){
        if(actionId == Constants.ACTION_DECREASE_TEXT_SIZE_CODE){
            mCurrentTextSize --;
            areTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mCurrentTextSize);
        } else if(actionId == Constants.ACTION_INCREASE_TEXT_SIZE_CODE){
            mCurrentTextSize ++;
            areTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mCurrentTextSize);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onNewDownActivityFromThisContext(int mPositionIndex){
        Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
        intent.putExtra(Constants.STORY_CHAPTER_INDEX, mPositionIndex);
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.STORY_TITLE, story_title);
        intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
        intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
        intent.putExtra(Constants.OWNER_ID, owner_id);

        intent.putExtra("anim id in", R.anim.up_in);
        intent.putExtra("anim id out", R.anim.up_out);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.down_in, R.anim.down_out);
        getActivity().finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onNewUpActivityFromThisContext(int mPositionIndex){
        Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
        intent.putExtra(Constants.STORY_CHAPTER_INDEX, mPositionIndex );
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.STORY_TITLE, story_title);
        intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
        intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
        intent.putExtra(Constants.OWNER_ID, owner_id);

        intent.putExtra("anim id in", R.anim.down_in);
        intent.putExtra("anim id out", R.anim.down_out);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.up_in, R.anim.up_out);
        getActivity().finish();
    }

    private void setLikeContentFirebase(){
        mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + chapter_id + "/likes")
                .document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String, Object> likeMap = new HashMap<>();
                    likeMap.put("timestamp", FieldValue.serverTimestamp());
                    mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + chapter_id + "/likes")
                            .document(user_id)
                            .set(likeMap);

                    updateInformationAction();

                    mFirebaseFirestore.collection("tokens").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final Token receiverToken = task.getResult().toObject(Token.class);
                            if(receiverToken != null){
                                mFirebaseFirestore.collection("notification_setups").document(mUser.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()){
                                            NotificationSetup setup = task.getResult().toObject(NotificationSetup.class);
                                            if(setup.isVote()){
                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                String token = task.getResult().getToken();
                                                                NotificationAction notificationAction = NotificationAction.getInstance(getContext());
                                                                notificationAction.sendNotification(receiverToken.getToken(), token, Constants.NOTIFICATION_VOTE);
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });

                }else {
                    mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + chapter_id + "/likes")
                            .document(user_id)
                            .delete();
                    Toast.makeText(getContext(), "Unlike!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void shareContentViaSocial(){
        String titleParse = story_title.replace(" ", "+");
        String descriptionParse = mStory.getDescription().replace(" ", "+");
        String dynamicCustom = "https://mouspad.team/?" +
                "link=https://mouspad.team/" + /*link*/
                titleParse + "&st_id=" + story_id + "&stc_id=" + chapter_id +
                "&apn=" + /*getPackageName()*/
                "com.uet.android.mouspad" +
                "&st=" + /*titleSocial*/
                titleParse +
                "&sd=" + /*description*/
                descriptionParse +
                "&utm_source=" + /*source*/
                "AndroidApp";
        Uri uri = Uri.parse("https://mouspad.team/" + titleParse + "&st_id=" + story_id + "&stc_id=" + chapter_id);
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

    //        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
//                        .setQuote(story_title + " from Mouspad!")
//                        .setContentUrl(dynamicLinkUri)
//                        .build();
//        if(ShareDialog.canShow(ShareLinkContent.class)){
//            mShareDialog.show(shareLinkContent);
//        }
    }

    private void startCommentActivity(){
        Intent intent = new Intent(getActivity(), CommentActivity.class);
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.STORY_TITLE, story_title);
        intent.putExtra(Constants.STORY_CHAPTER_INDEX, chapter_id);
        getActivity().startActivity(intent);
    }

    private void updateInformationAction(){
        Map<String,Object>map = new HashMap<>();
        map.put("action_image", mUser.getAvatar());
        map.put("action_title", mUser.getAccount());
        map.put("action_description", "Liked " + story_title);
        map.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").add(map);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_favorite_content:
                    if(ConnectionUtils.isLoginValid &&ConnectionUtils.isConnectingInternet){
                        setLikeContentFirebase();
                        prepareNavigationItemMenu();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.action_comment_content:
                    if(ConnectionUtils.isConnectingInternet)
                    startCommentActivity();
                    return true;
                case R.id.action_share_content:
                    if(ConnectionUtils.isLoginValid && ConnectionUtils.isConnectingInternet){
                        shareContentViaSocial();
                    }else {
                        Toast.makeText(getContext(), getString(R.string.action_require_account), Toast.LENGTH_SHORT).show();
                    }
                    return true;
            }
            return false;
        }
    };

    private void prepareNavigationItemMenu(){
        Menu menu = mNavigationChapterContent.getMenu();
        final MenuItem menuItem = menu.getItem(0);

        mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + chapter_id + "/likes" )
                .document(user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()){
                            menuItem.setIcon(R.drawable.ic_heart_multiple);
                        } else {
                           menuItem.setIcon(R.drawable.ic_heart_multiple_outline);
                        }
                    }
                });
    }
}
