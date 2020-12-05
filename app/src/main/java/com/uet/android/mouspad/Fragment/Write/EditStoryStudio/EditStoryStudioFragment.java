package com.uet.android.mouspad.Fragment.Write.EditStoryStudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryChapterContentActivity;
import com.uet.android.mouspad.Activity.MapsActivity;
import com.uet.android.mouspad.Adapter.EbookEditAdapter;
import com.uet.android.mouspad.Adapter.FirebaseUI.TableContentChapterAdapter;
import com.uet.android.mouspad.Adapter.FirebaseUI.TagsAdapter;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.Ebook.PDFViewerActivity;
import com.uet.android.mouspad.Model.Genre;
import com.uet.android.mouspad.Model.RepoLocation;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.Tag;

import com.uet.android.mouspad.Model.ViewModel.TokenFlViewModel;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.uet.android.mouspad.Utils.Constants.STORY_CHAPTER_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class EditStoryStudioFragment extends Fragment {
    private Story mStory;
    private Toolbar mToolbar, mToolbarCenter;
    private ImageButton mButtonOptionCenter, mButtonAddContent;
    private ImageView mImgCover;
    private EditText mEditTitle, mEditDescription, mEditInfo, mEditGenre;
    private CardView mCardviewTags, mCardviewLocation;
    private TextView mTxtLocation;
    private Switch mSwitchCompletedStatus;
    private RecyclerView mRecyclerView_content;
    private RecyclerView mRecyclerView_tag ;
    private RecyclerView mRecyclerView_ebook;

    private View mDashboardFormat;
    private View mLayoutMouspadFormat, mLayoutEbookFormat;
    private CardView mCardviewPDFFormat, mCardviewEpubFormat, mCardviewDefaultFormat;
    private TableContentChapterAdapter mTableContentChapterAdapter;
    private TagsAdapter mTagsAdapter;


    private ArrayList<StoryChapter> mChapters;
    private ArrayList<String> mGenres;
    private ArrayList<String> mTags;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;
    private String user_id;
    private String story_id;
    private String title;
    private String description;
    private String status;
    private String genre;
    private Uri coverUri;
    private boolean published;

    private boolean isSaved = false;
    private boolean isPublished = false;
    private boolean isCreatingNewChapter =false;

    private TokenFlViewModel tokenFlViewModel;
    private List<Token> mTokensFl;
    private List<Boolean> mFlNotifcations;

    public EditStoryStudioFragment() {
    }

    public static EditStoryStudioFragment newInstance(String storyId) {
        Bundle args = new Bundle();
        args.putString(Constants.STORY_INDEX, storyId);
        EditStoryStudioFragment fragment = new EditStoryStudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        story_id = getArguments().getString(Constants.STORY_INDEX);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTagsAdapter.startListening();
        mTableContentChapterAdapter.startListening();
    }

    @Override
    public void onStop() {
        mTagsAdapter.stopListening();
        mTableContentChapterAdapter.stopListening();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
//        Log.d("Fileandroid", Environment.getDataDirectory().toString());
//        Log.d("Fileandroid 2", Environment.getDownloadCacheDirectory().toString());
//        Log.d("Fileandroid 3", Environment.DIRECTORY_DOWNLOADS);

        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view=  inflater.inflate(R.layout.fragment_edit_story_studio, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initModelView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRepoLocation != null){
            mFirebaseFirestore.collection("locations/").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mTxtLocation.setText(mRepoLocation.getDescription());
                }
            });
        }

        if(mStory != null && mStory.getFormat() != null){
            checkFormatOfStory();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == Constants.GALLERY_REQUEST_CODE_FOR_COVER && data != null){
            coverUri = data.getData();
            final StorageReference image_path = mStorageReference.child("story_covers")
                    .child(coverUri.getLastPathSegment());
            image_path.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            coverUri = uri;
                            Picasso.get()
                                    .load(coverUri)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(mImgCover);
                            storeStoryInformation(uri);
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
        } else if(requestCode == Constants.INTENT_PICK_PDF && data != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference image_path = storageReference.child("story_pdfs")
                    .child(story_id);
            image_path.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object>map = new HashMap<>();
                            map.put("url", uri.toString());
                            mFirebaseFirestore.collection("story_pdfs").document(story_id).set(map);
                            startPreviewPdfStory(uri, mEditTitle.getText().toString(), user_id);
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

        } else if(requestCode == Constants.INTENT_PICK_EPUB && data != null){
            final String FilePath = data.getData().getPath();
            String FileName = data.getData().getLastPathSegment();
            int lastPos = FilePath.length() - FileName.length();
            String Folder = FilePath.substring(0, lastPos);
            Log.d("File fullpath", FilePath);
            Log.d("Fire folder", Folder);
            Log.d("File filename", FileName);
            final File myFile = new File(data.getData().getPath());
            myFile.getAbsolutePath();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ebook_path = storageReference.child("story_pdfs")
                    .child(data.getData().getLastPathSegment() + ".epub");
            ebook_path.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    ebook_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object>map = new HashMap<>();
                            map.put("url", uri.toString());
                            mFirebaseFirestore.collection("story_pdfs").document(story_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Intent intent = new Intent(getActivity(), EpubListActivity.class);
                                    intent.putExtra(Constants.FORMAT_EPUB, Constants.FORMAT_EPUB);
                                    intent.putExtra(Constants.STORY_INDEX, story_id);
                                    intent.putExtra(Constants.USER_ID, user_id);
                                    intent.putExtra(Constants.READING_MODE, Constants.READING_MODE_COMPOSE);
                                    startActivity(intent);
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
        } else if(requestCode == Constants.MAP_REQUEST_USER_CODE){
            mFirebaseFirestore.collection("locations/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mTxtLocation.setText(mRepoLocation.getDescription());
                }
            });
        }

    }

    @Override
    public void onDestroy() {
        if(isSaved == false){
            if(isCreatingNewChapter == true){
                storeStoryInformation(coverUri);
            } else if(mStory.getStory_id().equals("") || TextUtils.isEmpty(mStory.getStory_id())){
                mFirebaseFirestore.collection("stories").document(story_id).delete();
                mFirebaseFirestore.collection("story_user/" + user_id + "/contain").document(story_id).delete();
            }
        } else {
            if(isPublished == true && published == false){
                published = true;
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                String token = task.getResult().getToken();
                                for(int i = 0; i < mTokensFl.size(); i ++){
                                    if(mFlNotifcations.get(i)){
                                        NotificationAction notificationAction = NotificationAction.getInstance(getContext());
                                        notificationAction.sendNotificationWithServiceMode(mTokensFl.get(i).getToken(), token, Constants.NOTIFICATION_COMMENT);
                                    }
                                }
                            }
                        });
            } else {
                storeStoryInformation(coverUri);
            }
        }
        storeStoryInformation(coverUri);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(published == false){
            mToolbar.inflateMenu(R.menu.edit_story_unpublished_menu);
        } else {
            mToolbar.inflateMenu(R.menu.edit_story_menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_chapter:
                if(mStory.getFormat().equals(Constants.FORMAT_DEFAULT_APP)){
                    createNewChapter();
                    storeStoryInformation(coverUri);
                } else {
                    Toast.makeText(getContext(), "Can not create new chapter for ebook type!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_save_story:
                isSaved = true;
                return true;
            case R.id.action_unpublish_story:
                if(published = true) {
                    published = false;
                    isPublished = false;
                }
                isSaved = true;
                return true;
            case R.id.action_publish_story:
                isSaved = true;
                isPublished = true;
                return true;
            case R.id.action_delete_story:
                mFirebaseFirestore.collection("stories").document(story_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
                    }
                });
                mFirebaseFirestore.collection("story_user/" + user_id + "/contain").document(story_id).delete();
                return true;
            case R.id.action_share_story:
                Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarEditStoryStudio);
        mToolbarCenter = view.findViewById(R.id.toolbarCenterEditStoryStudio);
        mToolbarCenter.setTitle(getString(R.string.text_table_of_contents));
        mImgCover = view.findViewById(R.id.imgCoverEditStoryStudio);
        mEditTitle = view.findViewById(R.id.editTitleEditStoryStudio);
        mEditDescription = view.findViewById(R.id.editDescriptionEditStoryStudio);
        mEditInfo = view.findViewById(R.id.editInfoEditStoryStudio);
        mEditGenre = view.findViewById(R.id.genreEditStoryStudio);
        mCardviewTags = view.findViewById(R.id.cardViewTagEditStoryStudio);
        mCardviewLocation = view.findViewById(R.id.cardviewLocationStory);
        mTxtLocation = view.findViewById(R.id.txtLocationStory);
        mSwitchCompletedStatus = view.findViewById(R.id.switchCompletedEditStoryStudio);
        mRecyclerView_content = view.findViewById(R.id.recyclerViewChapterContentStoryStudio);
        mRecyclerView_tag = view.findViewById(R.id.recyclerViewTagsStoryStudio);
        mRecyclerView_ebook = view.findViewById(R.id.recyclerViewEbookContentStoryStudio);

        mDashboardFormat = view.findViewById(R.id.dashboardFormatEditStoryStudio);
        mCardviewPDFFormat = mDashboardFormat.findViewById(R.id.dashboardPdfFormat);
        mCardviewEpubFormat = mDashboardFormat.findViewById(R.id.dashboardEpubFormat);
        mCardviewDefaultFormat = mDashboardFormat.findViewById(R.id.dashboardMouspadFormat);
        mLayoutEbookFormat = view.findViewById(R.id.layoutEbookEditStoryStudio);
        mLayoutMouspadFormat = view.findViewById(R.id.layoutChaptersEditStoryStudio);
        mButtonOptionCenter = mLayoutMouspadFormat.findViewById(R.id.btnOptionCenterEditStoryStudio);
        mButtonAddContent = mLayoutMouspadFormat.findViewById(R.id.btnAddChapterEditStoryStudio);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_continuing_writing);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        mButtonOptionCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private RepoLocation mRepoLocation;
    private void initModelView(View view){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        user_id = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection("stories/").document(story_id)
                .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mStory = task.getResult().toObject(Story.class);
                    coverUri = Uri.parse(mStory.getCover());
                    published = mStory.getPublished();
                    mEditTitle.setText(mStory.getTitle());
                    mEditDescription.setText(mStory.getDescription());
                    mEditInfo.setText("Your Information here");
                    mEditGenre.setText(mStory.getGenre());
                    mSwitchCompletedStatus.setChecked(mStory.getStatus().equals("completed"));
                    Picasso.get()
                            .load(coverUri)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(mImgCover);
                    checkFormatOfStory();
                }
            }
        });

        mFirebaseFirestore.collection("locations").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.getResult().exists()){
                   mRepoLocation = task.getResult().toObject(RepoLocation.class);
                   String place = mRepoLocation.getDescription();
                   if(place!= null && !place.equals("")){
                       mTxtLocation.setText(place);
                   } else {
                       mTxtLocation.setText("This story has not had a location yet.");
                   }
               }else {
                   mTxtLocation.setText("This story has not had a location yet.");
               }
            }
        });
        mCardviewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra(Constants.MAP_REQUEST, Constants.MAP_REQUEST_STORY_CODE);
                intent.putExtra(STORY_INDEX, mStory);
                //intent.putExtra(Constants.GOOGLE_MAP_BASE_URL, mRepoLocation);
                startActivityForResult(intent, Constants.MAP_REQUEST_STORY_CODE);
            }
        });


        mTags = new ArrayList<>();
        Query queryTag = mFirebaseFirestore.collection("story_tags/" + story_id + "/contain").limit(100);
        queryTag.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Tag tag = documentSnapshot.toObject(Tag.class);
                    mTags.add(tag.getTitle());
                }
            }
        });
        
        mCardviewTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflateDataToBlankEditText(getContext(), mTags, R.string.text_tags);
            }
        });
        
        FirestoreRecyclerOptions<Tag> optionsTag = new FirestoreRecyclerOptions.Builder<Tag>()
                .setQuery(queryTag, Tag.class)
                .build();
        mTagsAdapter = new TagsAdapter(optionsTag);
        mRecyclerView_tag.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_tags = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView_tag.setLayoutManager(linearLayoutManager_tags);
        mRecyclerView_tag.setAdapter(mTagsAdapter);

        mImgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.startActivityToPickImage(EditStoryStudioFragment.this, Constants.GALLERY_REQUEST_CODE_FOR_COVER);
            }
        });

        Query queryChapter = mFirebaseFirestore.collection("chapters/" +story_id + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<StoryChapter> optionsChapter = new FirestoreRecyclerOptions.Builder<StoryChapter>()
                .setQuery(queryChapter, StoryChapter.class)
                .build();
        mTableContentChapterAdapter = new TableContentChapterAdapter(optionsChapter);
        mTableContentChapterAdapter.setActivity(getActivity());
        mRecyclerView_content.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_content = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView_content.setLayoutManager(linearLayoutManager_content);
        DividerItemDecoration dividerItemDecoration_content = new DividerItemDecoration(getContext(), linearLayoutManager_content.getOrientation());
        dividerItemDecoration_content.setDrawable(getResources().getDrawable(R.drawable.border_bottom));
        mRecyclerView_content.addItemDecoration(dividerItemDecoration_content);
        mRecyclerView_content.setAdapter(mTableContentChapterAdapter);

        mGenres = new ArrayList<>();
        Query queryGenre = mFirebaseFirestore.collection("genres");
        queryGenre.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Genre object = documentSnapshot.toObject(Genre.class);
                    String title = object.getGenre_title();
                    mGenres.add(title);
                }
            }
        });

        mEditGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflateGenreToListView();
            }
        });

        mButtonAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChapter();
            }
        });

        //follow model
        mTokensFl = new ArrayList<>();
        mFlNotifcations= new ArrayList<>();
        tokenFlViewModel = new ViewModelProvider(getActivity()).get(TokenFlViewModel.class);
        tokenFlViewModel.getTokensData().observe(getViewLifecycleOwner(), new Observer<List<Token>>() {
            @Override
            public void onChanged(List<Token> tokenList) {
                List<Token> tokens = tokenList;
                for(Token token: tokens){
                    mTokensFl.add(token);
                }
            }
        });
        tokenFlViewModel.getNotificationData().observe(getViewLifecycleOwner(), new Observer<List<Boolean>>() {
            @Override
            public void onChanged(List<Boolean> booleans) {
                List<Boolean> booleanList = booleans;
                for(Boolean item: booleanList){
                    mFlNotifcations.add(item);
                }
            }
        });
    }

    private void storeTagInformation(){
        Query query = mFirebaseFirestore.collection("story_tags/" + story_id + "/contain").limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    documentSnapshot.getReference().delete();
                }
                for(int i = 0 ;i < mTags.size(); i ++){
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", mTags.get(i));
                    final int finalI = i;
                    mFirebaseFirestore.collection("story_tags/" + story_id + "/contain").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.d("Tags", mTags.get(finalI));
                        }
                    });
                }
            }
        });
    }

    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    private void storeStoryInformation(final Uri uriCover){
        String cover = "";

        title = mEditTitle.getText().toString();
        description = mEditDescription.getText().toString();
        status = mSwitchCompletedStatus.isChecked()? "completed" : "on working";
        genre = mEditGenre.getText().toString();

        Map<String, Object> story = new HashMap<>();
        story.put("user_id", user_id);
        story.put("story_id", story_id);
        story.put("title", title);
        story.put("description", description);
        story.put("genre", genre);
        story.put("status", status);
        story.put("format", mStory.getFormat());
        story.put("published",published);
        cover = uriCover.toString();
        story.put("cover", cover);

        mFirebaseFirestore.collection("stories/").document(story_id).set(story).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Picasso.get()
                        .load(uriCover)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(mImgCover);
            }}
        )
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String mes = e.getMessage();
                Log.d("Exception Firestore", mes);
            }
        });

        if(true){
            HashMap<Object, Object>map = new HashMap<>();
            map.put("story_id", story_id);
            map.put("timestamp",FieldValue.serverTimestamp() );
            map.put("published", published);
            mFirebaseFirestore.collection("story_user/" + user_id + "/contain").document(story_id).set(map);
        } else {
            mFirebaseFirestore.collection("story_user/" + user_id + "/contain").document(story_id).delete();
        }


        //save to agolia
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index index = client.getIndex("firebase_story");
        String allTitle = title + " " + genre + " ";
        for(int i = 0; i < mTags.size(); i ++){
            allTitle += mTags.get(i) + " ";
        }
        try {
            JSONObject jsonObjects = new JSONObject().put("storyInfo", allTitle).
                    put("storyTitle", title)
                    .put("instanceId", story_id)
                    .put("storyCover", coverUri)
                    .put("ownerId", user_id)
                    .put("storyGenre", genre)
                    .put("storyStatus", status)
                    .put("storyDes", description)
                    .put("storyPublish", published)
                    .put("storyFormat", mStory.getFormat());
            index.addObjectAsync(jsonObjects,story_id, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeChapterInfromation(Uri uri, boolean isChange){
        String cover = uri.toString();
        Map<String, Object> chapter = new HashMap<>();
        chapter.put("story_id", story_id);
        chapter.put("chapter_id", "");
        chapter.put("title", "");
        chapter.put("content", "");
        chapter.put("cover", cover);
        chapter.put("audio", "");
        chapter.put("youtube", "");
        chapter.put("published", false);

        if(isChange == false){
            chapter.put("timestamp", FieldValue.serverTimestamp());
        }

        mFirebaseFirestore.collection("chapters/"+ story_id +"/contain")
                .add(chapter)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String chapter_id =documentReference.getId();
                        Intent intent = new Intent(getContext(), EditStoryChapterContentActivity.class);
                        intent.putExtra(STORY_INDEX, story_id);
                        intent.putExtra(STORY_CHAPTER_INDEX, chapter_id);
                        startActivity(intent);
                       // getActivity().finish();
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

    private void createNewChapter(){
        String randomName = UUID.randomUUID().toString();
        final StorageReference image_path = mStorageReference.child("chapter_covers" ).child(randomName);
        Uri coverUri = WidgetsUtils.getUriToResource(getContext(), R.drawable.item_add_media_background);
        image_path.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storeChapterInfromation(uri, false);
                    }
                });
            }
        });
    }

    private void checkFormatOfStory(){
        if(!mStory.getFormat().isEmpty()) {
            mDashboardFormat.setVisibility(View.GONE);
        } else {
            mDashboardFormat.setVisibility(View.VISIBLE);
            mLayoutMouspadFormat.setVisibility(View.GONE);
            dashboardOnClickItem();
        }
        if(mStory.getFormat().equals(Constants.FORMAT_DEFAULT_APP)){
            mLayoutMouspadFormat.setVisibility(View.VISIBLE);
            mLayoutEbookFormat.setVisibility(View.GONE);
        } else if(mStory.getFormat().equals(Constants.FORMAT_PDF)){
            mLayoutMouspadFormat.setVisibility(View.GONE);
            mLayoutEbookFormat.setVisibility(View.VISIBLE);
            ArrayList<Story> stories = new ArrayList<>();
            stories.add(mStory);
            EbookEditAdapter adapter = new EbookEditAdapter(stories, getContext());
            mRecyclerView_ebook.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager_content = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            mRecyclerView_ebook.setLayoutManager(linearLayoutManager_content);
            DividerItemDecoration dividerItemDecoration_content = new DividerItemDecoration(getContext(), linearLayoutManager_content.getOrientation());
            dividerItemDecoration_content.setDrawable(getResources().getDrawable(R.drawable.border_bottom));
            mRecyclerView_ebook.addItemDecoration(dividerItemDecoration_content);
            mRecyclerView_ebook.setAdapter(adapter);
        } else if(mStory.getFormat().equals(Constants.FORMAT_EPUB)){
            mLayoutMouspadFormat.setVisibility(View.GONE);
            mLayoutEbookFormat.setVisibility(View.VISIBLE);
            ArrayList<Story> stories = new ArrayList<>();
            stories.add(mStory);
            EbookEditAdapter adapter = new EbookEditAdapter(stories, getContext());
            mRecyclerView_ebook.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager_content = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            mRecyclerView_ebook.setLayoutManager(linearLayoutManager_content);
            DividerItemDecoration dividerItemDecoration_content = new DividerItemDecoration(getContext(), linearLayoutManager_content.getOrientation());
            dividerItemDecoration_content.setDrawable(getResources().getDrawable(R.drawable.border_bottom));
            mRecyclerView_ebook.addItemDecoration(dividerItemDecoration_content);
            mRecyclerView_ebook.setAdapter(adapter);
        }
    }

    private void dashboardOnClickItem(){
        mCardviewPDFFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStory.setFormat(Constants.FORMAT_PDF);
                pickPDFIntent();
            }
        });
        mCardviewEpubFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStory.setFormat(Constants.FORMAT_EPUB);
                pickEPUBIntent();
            }
        });
        mCardviewDefaultFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStory.setFormat(Constants.FORMAT_DEFAULT_APP);
                isCreatingNewChapter = true;
                createNewChapter();
            }
        });
    }

    private void pickPDFIntent(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Open with"), Constants.INTENT_PICK_PDF);
    }

    private void pickEPUBIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String [] mimeTypes = { "text/html", "application/epub+zip", "text/plain"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, Constants.INTENT_PICK_EPUB);
    }
    private void startPreviewPdfStory(Uri uri, String title, String ower_id){
        Intent intent = new Intent(getActivity(), PDFViewerActivity.class);
        intent.putExtra(Constants.STORY_PDF_URL,uri.toString());
        intent.putExtra(Constants.FORMAT_PDF, "");
        intent.putExtra(Constants.STORY_TITLE,title );
        intent.putExtra(Constants.USER_ID, ower_id);
        startActivity(intent);
    }

    private void inflateDataToBlankEditText(Context context, final ArrayList<String> arrayListContent, int stringId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View blankFragment = layoutInflater.inflate(R.layout.fragment_blank_edittext, null);
        final EditText editText = blankFragment.findViewById(R.id.editBlankEditText);
        Button positiveButton = blankFragment.findViewById(R.id.btnPositiveBlankEditText);
        Button negativeButton = blankFragment.findViewById(R.id.btnNegativeBlankEditText);
        Editable editable = editText.getText();
        for(String string : arrayListContent){
            CharSequence charSequence = string.subSequence(0, string.length()) + ", ";
            editable.append(charSequence);
        }
        alertDialog.setView(blankFragment);
        final  AlertDialog dialogParam = alertDialog.create();
        dialogParam.show();

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogParam.hide();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WidgetsUtils.splitStringToWords(editText.getText().toString(), arrayListContent);
                dialogParam.hide();
                storeTagInformation();
                mTagsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void inflateGenreToListView(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        TextView textView = dialogChapter.findViewById(R.id.txtNameListDialog);
        textView.setText(getContext().getResources().getString(R.string.text_genre));
        ArrayAdapter<String> arrayAdapter;
        ArrayList<String> arrayList = mGenres;
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l_id) {
                mEditGenre.setText(mGenres.get(pos));
                dialog.hide();
            }
        });
    }
}