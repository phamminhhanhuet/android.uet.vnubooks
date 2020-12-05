package com.uet.android.mouspad.Fragment.Write.EditStoryStudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;


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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.strategies.VideoStrategy;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_BackgroundColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontSize;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryChapterContentActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.YoutubeSearchActivity;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.URLUtils;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class EditStoryChapterContentFragment extends Fragment {

    private Toolbar mToolbar;
    private EditText mEditTitle;
    private ImageView mImageAddMedia, mImageMediaBackground, mImagePressAudio, mImageMenuMedia;
    private TextView mTextAddMedia, mTextCurrentTimeAudio, mTextDurationAudio;
    private SeekBar mSeekBarAudio;
    private View mViewAudio, mYoutubeView;
    private YouTubePlayerView mYouTubePlayer;

    private LinearLayout mLinearLayoutAddMedia;
    private ConstraintLayout mConstraintLayoutAudio;

    private BottomNavigationView mNavigationChapterContent;
    private BottomNavigationView mNavigationTextAttr;

    private int mPositionIndex;
    private Story mStory;
    private StoryChapter mStoryChapter;

    private boolean isBoldText = false;
    private boolean isItalicText = false;
    private boolean isSaved = false;
    private boolean isPublished = false;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;
    private String user_id;
    private String story_id;
    private String chapter_id;
    private String title;
    private String content;
    private String youtubeApi = "";
    private Uri coverUri = null;
    private Uri audioUri = null;
    private Date timestamp;
    private boolean published = false;

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();

    AREditText arEditText;
    IARE_Toolbar are_toolbar;
    LinearLayout areLinearLayout;

    public EditStoryChapterContentFragment() {
    }

    public static EditStoryChapterContentFragment newInstance(String storyId, String chapterId) {
        Bundle args = new Bundle();
        args.putString(STORY_INDEX, storyId);
        args.putString(Constants.STORY_CHAPTER_INDEX, chapterId);
        EditStoryChapterContentFragment editStoryChapterContentFragment = new EditStoryChapterContentFragment();
        editStoryChapterContentFragment.setArguments(args);
        return editStoryChapterContentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        story_id = getArguments().getString(Constants.STORY_INDEX);
        chapter_id = getArguments().getString(Constants.STORY_CHAPTER_INDEX);
        Log.d("chapterContents", story_id);
        mFirebaseFirestore.collection("stories").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mStory = task.getResult().toObject(Story.class);
            }
        });
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_edit_story_chapter_content, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initViewModel();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.GALLERY_REQUEST_CODE_FOR_CONTENT && data!= null){
            coverUri = data.getData();
            final StorageReference image_path = mStorageReference.child("chapter_media")
                    .child(coverUri.getLastPathSegment());
            image_path.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            SpannableString spannableString = WidgetsUtils.transIntentDataToSpannableString(data, getActivity(), uri);
                          //  WidgetsUtils.appendImageToEditText(mEditContent,spannableString);
                            WidgetsUtils.appendImageToEditText(arEditText, spannableString);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String mes = e.getMessage();
                    Toast.makeText(getActivity(), mes , Toast.LENGTH_SHORT).show();
                }
            });
        } else if(requestCode == Constants.GALLERY_REQUEST_CODE_FOR_COVER && data != null){
            coverUri = data.getData();
            final StorageReference image_path = mStorageReference.child("chapter_covers")
                    .child(coverUri.getLastPathSegment());
            image_path.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(Uri uri) {
                            storeChapterInfromation(uri.toString(), Constants.GALLERY_REQUEST_CODE_FOR_COVER);
                            coverUri = uri;
                            if(audioUri != null){
                                Picasso.get()
                                        .load(uri)
                                        .placeholder(R.drawable.default_avatar)
                                        .error(R.drawable.default_avatar)
                                        .into(mImageMediaBackground);
                            } else {
                                Picasso.get()
                                        .load(uri)
                                        .placeholder(R.drawable.default_avatar)
                                        .error(R.drawable.default_avatar)
                                        .into(mImageAddMedia);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String mes = e.getMessage();
                    Toast.makeText(getActivity(), mes , Toast.LENGTH_SHORT).show();
                }
            });
        } else if(requestCode == Constants.GALLERY_REQUEST_CODE_FOR_AUDIO && data != null){
            audioUri = data.getData();
            final StorageReference image_path = mStorageReference.child("chapter_audios")
                    .child(audioUri.getLastPathSegment());
            image_path.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(final Uri uri) {
                            storeChapterInfromation(uri.toString(), Constants.GALLERY_REQUEST_CODE_FOR_AUDIO);
                            audioUri = uri;
                            mConstraintLayoutAudio.setVisibility(View.VISIBLE);
                            mImageAddMedia.setVisibility(View.GONE);
                            mTextAddMedia.setVisibility(View.GONE);
                            if(coverUri != null){
                                Picasso.get()
                                        .load(coverUri)
                                        .placeholder(R.drawable.default_avatar)
                                        .error(R.drawable.default_avatar)
                                        .into(mImageMediaBackground);
                            }
                            prepareMediaPlayer(uri);
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
                                    prepareMediaPlayer(uri);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String mes = e.getMessage();
                    Toast.makeText(getActivity(), mes , Toast.LENGTH_SHORT).show();
                }
            });
        } else if(requestCode == Constants.YOUTUBE_REQUEST_CODE){
            final String videoId = data.getStringExtra(Constants.YOUTUBE_RESULT_ID);
            youtubeApi = videoId;
            Log.d("Youtubes", videoId);
            if(!youtubeApi.isEmpty()) {
                mYoutubeView.setVisibility(View.VISIBLE);
                mConstraintLayoutAudio.setVisibility(View.GONE);
                mImageAddMedia.setVisibility(View.GONE);
                mTextAddMedia.setVisibility(View.GONE);

                mYouTubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0);
                    }
                });
            }
            storeChapterInfromation(videoId, Constants.YOUTUBE_REQUEST_CODE);
            Toast.makeText(getContext(), youtubeApi, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        mYouTubePlayer.release();
        if(isPublished == true && published == false &&mStory.getPublished()){
            published = true;
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            final String token = task.getResult().getToken();
                            Query query = mFirebaseFirestore.collection("follows/" + story_id + "/contain").orderBy("timestamp");
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        final FollowItem receiver = documentSnapshot.toObject(FollowItem.class);
                                        mFirebaseFirestore.collection("notification_setups").document(receiver.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                NotificationSetup notificationSetup = task.getResult().toObject(NotificationSetup.class);
                                                if(notificationSetup.isUpdates_from_following()){
                                                    mFirebaseFirestore.collection("tokens").document(receiver.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            final Token receiverToken = task.getResult().toObject(Token.class);
                                                            if(receiverToken != null){
                                                                NotificationAction notificationAction = NotificationAction.getInstance(getContext());
                                                                notificationAction.sendNotification(receiverToken.getToken(), token, Constants.NOTIFICATION_COMMENT);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
        }
        if(isSaved == false){
            if(mStoryChapter.getChapter_id().equals("") || TextUtils.isEmpty(mStoryChapter.getChapter_id())){
                mFirebaseFirestore.collection("chapters/" + story_id +"/contain/").document(chapter_id).delete();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mToolbar.inflateMenu(R.menu.edit_story_chapter_content_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_publish_chapter:
                isPublished = true;
                isSaved = true;
                return true;
            case R.id.action_save_chapter:
                isSaved = true;
                storeChapterInfromation(coverUri.toString(), Constants.DEFAULT_NOT_CHANGE);
                Toast.makeText(getContext(), "save", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_preview_chapter:
                Toast.makeText(getContext(), "preview", Toast.LENGTH_SHORT).show();
                startPreviewChapter();
                return true;
            case R.id.action_delete_chapter:
                Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startPreviewChapter(){
        String cover  ;
        String audio;
        String youtube;
        cover = coverUri.toString();
        audio = audioUri.toString();
        youtube = youtubeApi;

        title = mEditTitle.getText().toString();
        content = getHtmlContent();
        timestamp = mStoryChapter.getTimestamp();
        Map<String, Object> chapter = new HashMap<>();
        chapter.put("story_id", story_id);
        chapter.put("chapter_id", chapter_id);
        chapter.put("title", title);
        chapter.put("content", content);
        chapter.put("cover", cover);
        chapter.put("audio", audio);
        chapter.put("youtube", youtube);
        chapter.put("timestamp", timestamp);
        chapter.put("published", published);

        mFirebaseFirestore.collection("chapters/"+ story_id +"/contain")
                .document(chapter_id)
                .set(chapter)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ArrayList<String> chapterId = new ArrayList<>();
                        chapterId.add(chapter_id);
                        ArrayList<String> titles = new ArrayList<>();
                        titles.add(title);
                        Intent intent = new Intent(getContext(), StoryChapterContentActivity.class);
                        intent.putExtra(Constants.STORY_CHAPTER_INDEX,0);
                        intent.putExtra(Constants.STORY_INDEX, story_id);
                        intent.putExtra(Constants.STORY_TITLE, title);
                        intent.putExtra(Constants.STORY_CHAPTER_LIST, chapterId);
                        intent.putExtra(Constants.STORY_CHAPTER_TITLE, titles);
                        intent.putExtra(Constants.OWNER_ID, user_id);
                        startActivity(intent);
                    }}
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });
    }

    private VideoStrategy mVideoStrategy = new VideoStrategy() {
        @Override
        public String uploadVideo(Uri uri) {
            try {
                Thread.sleep(3000); // Do upload here
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "http://www.xx.com/x.mp4";
        }

        @Override
        public String uploadVideo(String videoPath) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "http://www.xx.com/x.mp4";
        }
    };

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarEditStoryChapterContent);
        mEditTitle = view.findViewById(R.id.editTitleChapterContent);
      //  mEditContent = view.findViewById(R.id.editContentChapterContent);
        arEditText = view.findViewById(R.id.arEditText);
        are_toolbar = view.findViewById(R.id.areToolbar);
        areLinearLayout = view.findViewById(R.id.bottombar);

        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem strikethrough = new ARE_ToolItem_Strikethrough();
        IARE_ToolItem fontSize = new ARE_ToolItem_FontSize();
        IARE_ToolItem fontColor = new ARE_ToolItem_FontColor();
        IARE_ToolItem backgroundColor = new ARE_ToolItem_BackgroundColor();
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem at = new ARE_ToolItem_At();

        are_toolbar.addToolbarItem(underline);
        are_toolbar.addToolbarItem(strikethrough);
        are_toolbar.addToolbarItem(fontSize);
        are_toolbar.addToolbarItem(fontColor);
        are_toolbar.addToolbarItem(backgroundColor);
        are_toolbar.addToolbarItem(quote);
        are_toolbar.addToolbarItem(listNumber);
        are_toolbar.addToolbarItem(listBullet);
        are_toolbar.addToolbarItem(hr);
        are_toolbar.addToolbarItem(link);
        are_toolbar.addToolbarItem(subscript);
        are_toolbar.addToolbarItem(superscript);
        are_toolbar.addToolbarItem(left);
        are_toolbar.addToolbarItem(center);
        are_toolbar.addToolbarItem(right);
        are_toolbar.addToolbarItem(video);
        are_toolbar.addToolbarItem(at);

        arEditText.setToolbar(are_toolbar);
        arEditText.setVideoStrategy(mVideoStrategy);
        arEditText.setVideoStrategy(mVideoStrategy);
        areLinearLayout.setVisibility(View.GONE);


        mNavigationChapterContent = view.findViewById(R.id.navigationEditChapterContent);
        mNavigationChapterContent.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigationTextAttr = view.findViewById(R.id.navigationTextAttrEditChapterContent);
        mNavigationTextAttr.setOnNavigationItemSelectedListener(mOnNavigationTextAttrItemSelectedListener);
        mNavigationTextAttr.setVisibility(View.GONE);


        mLinearLayoutAddMedia = view.findViewById(R.id.linearLayoutEditChapterContent);
        View viewAddMedia = mLinearLayoutAddMedia.getChildAt(0);
        mImageAddMedia = viewAddMedia.findViewById(R.id.imgItemAddMedia);
        mTextAddMedia = viewAddMedia.findViewById(R.id.textItemAddMedia);

        mImageMenuMedia = viewAddMedia.findViewById(R.id.menuAddMedia);

        mConstraintLayoutAudio = viewAddMedia.findViewById(R.id.constraintLayoutAudioAddMedia);
        mViewAudio = viewAddMedia.findViewById(R.id.viewBackgroundAudio);
        mImageMediaBackground = viewAddMedia.findViewById(R.id.imgItemAddMedia2);
        mImagePressAudio = viewAddMedia.findViewById(R.id.imgAudioPressAddMedia);
        mTextCurrentTimeAudio = viewAddMedia.findViewById(R.id.textCurrentTimeAddMedia);
        mTextDurationAudio = viewAddMedia.findViewById(R.id.textDurationAddMedia);
        mSeekBarAudio = viewAddMedia.findViewById(R.id.seekBarAudioAddMedia);

        mYoutubeView= view.findViewById(R.id.youtubeAddMedia);
        mYouTubePlayer = mYoutubeView.findViewById(R.id.item_youtube_view);
        getLifecycle().addObserver(mYouTubePlayer);

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

        mConstraintLayoutAudio.setVisibility(View.GONE);

        mImageMenuMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mConstraintLayoutAudio.getVisibility() == View.GONE && mYoutubeView.getVisibility() == View.GONE){
                   inflateAllMediaMenuIntoDialog();
                } else if(mConstraintLayoutAudio.getVisibility()== View.VISIBLE){
                    inflateMediaMenuWithAudioIntoDialog();
                } else if(mYouTubePlayer.getVisibility() == View.VISIBLE){
                    inflateMediaMenuWithYoutubeIntoDialog();
                }
            }
        });

        mImageAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmptyMedia()){
                    inflateAllMediaMenuIntoDialog();
                }
            }
        });
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

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
    AbstractYouTubePlayerListener youTubePlayerListener = new AbstractYouTubePlayerListener() {
        @Override
        public void onReady(YouTubePlayer youTubePlayer) {
            //super.onReady(youTubePlayer);
            youTubePlayer.loadVideo(youtubeApi, 0);
        }
    };
    private void initViewModel() {
        mFirebaseFirestore.collection("chapters/" + story_id +"/contain/").document(chapter_id)
                .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mStoryChapter = task.getResult().toObject(StoryChapter.class);
                    coverUri = Uri.parse(mStoryChapter.getCover());
                    audioUri = Uri.parse(mStoryChapter.getAudio());
                    youtubeApi = mStoryChapter.getYoutube();
                    mEditTitle.setText(mStoryChapter.getTitle());

                    published = mStoryChapter.isPublished();

                    if(!youtubeApi.isEmpty()){
                        mYoutubeView.setVisibility(View.VISIBLE);
                        mConstraintLayoutAudio.setVisibility(View.GONE);
                        mImageAddMedia.setVisibility(View.GONE);
                        mTextAddMedia.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Youtube exists", Toast.LENGTH_LONG).show();
//                        mYouTubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//                            @Override
//                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                                youTubePlayer.loadVideo(youtubeApi, 0);
//                            }
//                        });
                        mYouTubePlayer.addYouTubePlayerListener(youTubePlayerListener);
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
                    String string = mStoryChapter.getContent();
                    SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) Html.fromHtml(string, FROM_HTML_MODE_LEGACY, new URLUtils.URLImageGetter(getContext()), null);
                    arEditText.fromHtml(string);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void storeChapterInfromation(final String uri , final int codeUri){
        String cover ;
        String audio;
        String youtube;
        if(codeUri == Constants.GALLERY_REQUEST_CODE_FOR_COVER){
            cover = uri;
        } else cover = coverUri.toString();
        if(codeUri == Constants.GALLERY_REQUEST_CODE_FOR_AUDIO){
            audio = uri;
        } else  audio = audioUri.toString();
        if(codeUri == Constants.YOUTUBE_REQUEST_CODE){
            youtube = uri;
        } else youtube = youtubeApi;

        title = mEditTitle.getText().toString();
        content = getHtmlContent();
        timestamp = mStoryChapter.getTimestamp();
        Map<String, Object> chapter = new HashMap<>();
        chapter.put("story_id", story_id);
        chapter.put("chapter_id", chapter_id);
        chapter.put("title", title);
        chapter.put("content", content);
        chapter.put("cover", cover);
        chapter.put("audio", audio);
        chapter.put("youtube", youtube);
        chapter.put("timestamp", timestamp);
        chapter.put("published", published);

        mFirebaseFirestore.collection("chapters/"+ story_id +"/contain")
                .document(chapter_id)
                .set(chapter)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }}
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getHtmlContent(){
      //  Spannable spannable = mEditContent.getText();
       // String string = Html.toHtml(spannable, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
      //  return string;
        Spannable spannable = arEditText.getText();
         String string = Html.toHtml(spannable, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
         return string;
    }

    private void startActivityToPickAudio(){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, Constants.GALLERY_REQUEST_CODE_FOR_AUDIO);
    }

    private boolean isEmptyMedia(){
        if(youtubeApi.isEmpty() && coverUri == null && audioUri == null) return true;
        else return false;
    }

    private void inflateAllMediaMenuIntoDialog(){
        ArrayList<String> listMediaAction = new ArrayList<>();
        String menu1, menu2, menu3;
        if(coverUri != null) menu1 = "Remove photo";
        else menu1 = "Upload new photo";
        menu2 = "Upload new audio";
        menu3 = "Add youtube video";
        listMediaAction.add(menu1);
        listMediaAction.add(menu2);
        listMediaAction.add(menu3);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listMediaAction);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i ==0){
                    ActivityUtils.startActivityToPickImage(EditStoryChapterContentFragment.this, Constants.GALLERY_REQUEST_CODE_FOR_COVER);
                } else if(i ==1 ){
                    startActivityToPickAudio();
                } else if(i == 2){
                    Intent intent = new Intent(getActivity(), YoutubeSearchActivity.class);
                    startActivityForResult(intent, Constants.YOUTUBE_REQUEST_CODE);
                }
                dialog.hide();
            }
        });
    }

    private void inflateMediaMenuWithAudioIntoDialog(){
        ArrayList<String> listMediaAction = new ArrayList<>();
        String menu1, menu2;
        if(coverUri != null) menu1 = "Remove photo";
        else menu1 = "Upload new photo";
        if(audioUri != null) menu2 = "Remove audio";
        else menu2 = "Upload new audio";
        listMediaAction.add(menu1);
        listMediaAction.add(menu2);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listMediaAction);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i ==0){
                    coverUri = null;
                } else if(i ==1 ){
                    mHandler.removeCallbacks(mUpdater);
                    mMediaPlayer.reset();
                    audioUri = null;
                    mConstraintLayoutAudio.setVisibility(View.GONE);
                    mImageAddMedia.setVisibility(View.VISIBLE);
                }
                dialog.hide();
            }
        });
    }

    private void inflateMediaMenuWithYoutubeIntoDialog(){
        ArrayList<String> listMediaAction = new ArrayList<>();
        String menu1 = "Remove youtube video";
        listMediaAction.add(menu1);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listMediaAction);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i ==0){
                    youtubeApi = "";
                    mYoutubeView.setVisibility(View.GONE);
                    if(coverUri != null){
                    } else {
                        mTextAddMedia.setVisibility(View.VISIBLE);
                    }
                    mImageAddMedia.setVisibility(View.VISIBLE);
                  //  mYouTubePlayer.release();
                    mYouTubePlayer.removeYouTubePlayerListener(youTubePlayerListener);
                    storeChapterInfromation(youtubeApi, Constants.YOUTUBE_REQUEST_CODE);
                }
                dialog.hide();
            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_text_attribute_edit:
                    if(mNavigationTextAttr.getVisibility() == View.GONE || mNavigationTextAttr.getVisibility() ==View.INVISIBLE){
                        mNavigationTextAttr.setVisibility(View.VISIBLE);
                        areLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        mNavigationTextAttr.setVisibility(View.GONE);
                        areLinearLayout.setVisibility(View.GONE);
                    }
                    return true;
                case R.id.action_pick_image_edit:
                    String string = mStoryChapter.getContent();
                    String tag = "img";
                    int count = 0;
                    while(string.contains(tag)){
                        String ccString = string.replace(tag, "");
                        count ++;
                    }
                    if(count >= 20) {
                        Toast.makeText(getContext(), "The amount of images in this chapter has to be less than 20!", Toast.LENGTH_LONG).show();
                    } else {
                        ActivityUtils.startActivityToPickImage(EditStoryChapterContentFragment.this, Constants.GALLERY_REQUEST_CODE_FOR_CONTENT);
                    }
                    return true;
            }
            return false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationTextAttrItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_text_bold:
                    if(isBoldText == false){
                        WidgetsUtils.setTextBoldInEditText(arEditText);
                        isBoldText = true;
                    } else {
                        WidgetsUtils.setTextNormalInEditText(arEditText);
                        isBoldText = false;
                    }
                    return  true;
                case R.id.action_text_italic:
                    if(isItalicText == false){
                        WidgetsUtils.setTextItalicInEditText(arEditText);
                        isItalicText = true;
                    } else {
                        WidgetsUtils.setTextNormalInEditText(arEditText);
                        isItalicText = false;
                    }
                    return true;
                case R.id.action_align_left:
                    WidgetsUtils.setTextAlignLeftInEditText(arEditText);
                    return true;
                case R.id.action_align_center:
                    WidgetsUtils.setTextAlignCenterInEditText(arEditText);
                    return  true;
                case R.id.action_align_right:
                    WidgetsUtils.setTextAlignRightInEditText(arEditText);
                    return true;
            }
            return false;
        }
    };
}