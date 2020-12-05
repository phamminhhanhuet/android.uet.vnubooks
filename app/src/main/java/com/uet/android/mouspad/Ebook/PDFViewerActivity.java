package com.uet.android.mouspad.Ebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shockwave.pdfium.PdfDocument;
import com.uet.android.mouspad.Activity.BookPerfrom.CommentActivity;
import com.uet.android.mouspad.Model.ItemView;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.NotificationSetup;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Service.Action.NotificationAction;
import com.uet.android.mouspad.Service.Notifications.Token;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PDFViewerActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private static final int PICK_PDF_FILE = 2;

    private Toolbar mToolbar;
    private PDFView mPdfView;
    private String mPdfUrl = "";
    private String mPdfDicrectory = "";
    private String story_id ="";
    private String owner_id = "";
    private User mUser;
    private Map<Integer, String> mTableOfContents = new LinkedHashMap<>();
    private Integer pageNumber = 0;
    private String mPdfFileName ="";
    private String TAG="PdfActivity";

    private FirebaseFirestore mFirebaseFirestore;
    private BottomNavigationView mNavigationChapterContent;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        MappingWidgets();
        ActionToolbar();
        initData();
        startPreview();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.story_chapter_content_menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_story_chapter_list:
                showTableOfContents();
                return true;
            case R.id.action_text_attribute_edit:
                Toast.makeText(this, "Fail to edit reading mode for this document!", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void MappingWidgets(){
        mToolbar = findViewById(R.id.toolbarPdfView);
        mPdfView = findViewById(R.id.pdfView);
        mNavigationChapterContent = findViewById(R.id.navigationChapterContent);
        mPdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mToolbar.getVisibility()==View.GONE && mNavigationChapterContent.getVisibility() == View.GONE){
                    mToolbar.setVisibility(View.VISIBLE);
                    mNavigationChapterContent.setVisibility(View.VISIBLE);
                } else {
                    mToolbar.setVisibility(View.GONE);
                    mNavigationChapterContent.setVisibility(View.GONE);
                }
            }
        });
    }

    private void ActionToolbar(){
        mToolbar.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private String user_id ;
    private void initData(){
        mPdfUrl = getIntent().getStringExtra(Constants.STORY_PDF_URL);
        mPdfDicrectory = getIntent().getStringExtra(Constants.FORMAT_PDF);
        story_id = getIntent().getStringExtra(Constants.STORY_INDEX);
        mNavigationChapterContent = findViewById(R.id.navigationChapterContent);
        mNavigationChapterContent.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigationChapterContent.setVisibility(View.GONE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_favorite_content:
                    if(ConnectionUtils.isConnectingInternet){
                        setLikeContentFirebase();
                        prepareNavigationItemMenu();
                    }
                    return true;
                case R.id.action_comment_content:
                    if(ConnectionUtils.isConnectingInternet)
                    startCommentActivity();
                    return true;
                case R.id.action_share_content:
                    if(ConnectionUtils.isConnectingInternet)
                    shareContentViaSocial();
                    return true;
            }
            return false;
        }
    };

    private void prepareNavigationItemMenu(){
        Menu menu = mNavigationChapterContent.getMenu();
        final MenuItem menuItem = menu.getItem(0);
        if(ConnectionUtils.isConnectingInternet){
            mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + story_id + "/likes" )
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

    private void setLikeContentFirebase(){
        mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + story_id + "/likes")
                .document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    Map<String, Object> likeMap = new HashMap<>();
                    likeMap.put("timestamp", FieldValue.serverTimestamp());
                    mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + story_id + "/likes")
                            .document(user_id)
                            .set(likeMap);

                    updateInformationAction();

                    mFirebaseFirestore.collection("tokens").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final Token receiverToken = task.getResult().toObject(Token.class);
                            if(receiverToken != null){
                                mFirebaseFirestore.collection("notification_setups").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                                NotificationAction notificationAction = NotificationAction.getInstance(getApplicationContext());
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
                    mFirebaseFirestore.collection("chapters/" +story_id +"/contain/" + story_id + "/likes")
                            .document(user_id)
                            .delete();
                    Toast.makeText(getApplicationContext(), "Unlike!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void shareContentViaSocial(){
        String titleParse = mPdfFileName.replace(" ", "+");
        String descriptionParse = titleParse;
        String dynamicCustom = "https://mouspad.team/?" +
                "link=https://mouspad.team/" + /*link*/
                titleParse + "&st_id=" + story_id + "&stc_id=" + story_id +
                "&apn=" + /*getPackageName()*/
                "com.uet.android.mouspad" +
                "&st=" + /*titleSocial*/
                titleParse +
                "&sd=" + /*description*/
                descriptionParse +
                "&utm_source=" + /*source*/
                "AndroidApp";
        Uri uri = Uri.parse("https://mouspad.team/" + titleParse + "&st_id=" + story_id + "&stc_id=" + story_id);
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

    private void startCommentActivity(){
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.STORY_TITLE, mPdfFileName);
        intent.putExtra(Constants.STORY_CHAPTER_INDEX, story_id);
        startActivity(intent);
    }

    private void updateInformationAction(){
        Map<String,Object>map = new HashMap<>();
        map.put("action_image", mUser.getAvatar());
        map.put("action_title", mUser.getAccount());
        map.put("action_description", "Liked " + mPdfFileName);
        map.put("timestamp", FieldValue.serverTimestamp());
        mFirebaseFirestore.collection("information_actions/" + user_id + "/contain").add(map);
    }


    private File mPdfFile;
    private void startPreview(){
        if(!mPdfUrl.isEmpty()){
            new RetrivePDFStream().execute(mPdfUrl);
            Toast.makeText(this, "Online mode!", Toast.LENGTH_SHORT).show();
        }
        Log.d("Chaptertitle 2", mPdfDicrectory);
        Log.d("Chaptertitle 2", story_id + "...d");

        if(!mPdfDicrectory.isEmpty() && !story_id.isEmpty()){
            initView();
            Log.d("Chaptertitle 3", mPdfDicrectory);
            mPdfFile = new File(mPdfDicrectory, story_id + ".pdf" );
            mPdfView.fromFile(mPdfFile)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onPageChange(new PDFPageChangeListenter())
                    .enableAnnotationRendering(true)
                    .onLoad(new PDFLoadListener())
                    .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                    .load();
            Toast.makeText(this, "Offile mode!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    private void initView(){
        prepareNavigationItemMenu();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        story_id = getIntent().getStringExtra(Constants.STORY_INDEX);

        HashMap<String, Object> map = new HashMap<>();
        map.put("story_id", story_id);
        mFirebaseFirestore.collection("current_read").document(user_id).set(map);

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
                    int status = 100;
                    map.put("status", status);
                    mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(story_id).set(map);
                }
            }
        });
        mFirebaseFirestore.collection("views").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                mFirebaseFirestore.collection("views").document(story_id).set(map);
            }
        });

        getSupportActionBar().setTitle(mPdfFileName);

        if(ConnectionUtils.isConnectingInternet){
            mFirebaseFirestore.collection("users").document(owner_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mUser = task.getResult().toObject(User.class);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class RetrivePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            mPdfFileName = getIntent().getStringExtra(Constants.STORY_TITLE);
            story_id = getIntent().getStringExtra(Constants.STORY_INDEX);
            owner_id = getIntent().getStringExtra(Constants.USER_ID);
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            initView();

            InputStream inputStream = null;
            try{
                URL url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            mPdfView.fromStream(inputStream)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .onPageChange(new PDFPageChangeListenter())
                    .enableAnnotationRendering(true)
                    .onLoad(new PDFLoadListener())
                    .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
                    .load();

        }
    }

    private class PDFPageChangeListenter implements OnPageChangeListener{
        @Override
        public void onPageChanged(int page, int pageCount) {
            pageNumber = page;
            setTitle(String.format("%s %s / %s", mPdfFileName, page + 1, pageCount));
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", mPdfFileName, page + 1, pageCount));
    }

    private class PDFLoadListener implements OnLoadCompleteListener {
        @Override
        public void loadComplete(int nbPages) {
            PdfDocument.Meta meta = mPdfView.getDocumentMeta();
            printBookmarksTree(mPdfView.getTableOfContents(), "-");
            //initView();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = mPdfView.getDocumentMeta();
        printBookmarksTree(mPdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
            mTableOfContents.put((int) b.getPageIdx(), b.getTitle());
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private void showTableOfContents(){
        PopupMenu tocmenu = new PopupMenu(this, findViewById(R.id.toolbarPdfView));
        for (final Object point: mTableOfContents.keySet()) {
            String text = mTableOfContents.get(point);
            MenuItem m = tocmenu.getMenu().add(text);
            m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mPdfView.jumpTo((Integer) point);
                    return true;
                }
            });
        }
        if (mTableOfContents.size()==0) {
            tocmenu.getMenu().add(R.string.app_name);
        }
        tocmenu.show();
    }
}