package com.uet.android.mouspad.Ebook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.App;
import com.uet.android.mouspad.Model.Ebook.Book;
import com.uet.android.mouspad.Model.Ebook.BookMetadata;
import com.uet.android.mouspad.Model.Ebook.PDFUrl;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;


public class EpubListActivity extends AppCompatActivity {
    private static final String SORTORDER_KEY = "sortorder";
    private static final String LASTSHOW_STATUS_KEY = "LastshowStatus";
    private static final String STARTWITH_KEY = "startwith";
    private static final String ENABLE_SCREEN_PAGE_KEY = "screenpaging";
    private static final String ENABLE_DRAG_SCROLL_KEY = "dragscroll";

    private static final int STARTLASTREAD = 1;
    private static final int STARTOPEN = 2;
    private static final int STARTALL = 3;

    private static final String ACTION_SHOW_OPEN = "SHOW_OPEN_BOOKS";
    private static final String ACTION_SHOW_UNREAD = "SHOW_UNREAD_BOOKS";
    public static final String ACTION_SHOW_LAST_STATUS = "SHOW_LAST_STATUS";

    private SharedPreferences sharedPreferences;
    private EpubBookAdapter mBookAdapter;

    private BookListAdderHandler mBookListAdderHandler;

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private TextView mTextViewProgress;
    private RecyclerView mRecyclerView;

    private EbookDatabase mEbookDatabase;
    private int recentread;
    private boolean showingSearch;
    private boolean isUpload = false;
    private boolean isVisit = false;
    private int showStatus = EbookDatabase.STATUS_ANY;
    private int readingMode = 0;

    public final String SHOW_STATUS = "showStatus";
    public final static String prefname = "booklist";

    private boolean openLastread = false;
    private static boolean alreadyStarted=false;

    private  String story_id, owner_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        //toolbar
        mToolbar = findViewById(R.id.toolbarEpubList);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTextViewProgress = findViewById(R.id.txtProgressEpubList);
        checkStorageAccess(false);

        sharedPreferences = getSharedPreferences(prefname, Context.MODE_PRIVATE);
        mBookListAdderHandler = new BookListAdderHandler(this);
        if (!sharedPreferences.contains(SORTORDER_KEY)) {
            setSortOrder(SortOrder.Default);
        }
        readingMode = getIntent().getIntExtra(Constants.READING_MODE, 0);

        mEbookDatabase = App.getDB(this);
        mEbookDatabase.setReadmode(readingMode);

        mRecyclerView = findViewById(R.id.recyclerEpubList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mBookAdapter = new EpubBookAdapter(this, mEbookDatabase, new ArrayList<Integer>());
        mBookAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readBook((int)view.getTag());
            }
        });
        mBookAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickBook(view);
                return false;
            }
        });

        mRecyclerView.setAdapter(mBookAdapter);

        processIntent(getIntent());

        mProgressBar = findViewById(R.id.progressBarEpubList);
        mLinearLayout = findViewById(R.id.linearLayoutEpubList);

        String format_extra = getIntent().getStringExtra(Constants.FORMAT_EPUB);
        story_id = getIntent().getStringExtra(Constants.STORY_INDEX);
        owner_id = getIntent().getStringExtra(Constants.USER_ID);
        if(format_extra != null && !format_extra.isEmpty()){
            mProgressBar.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
            checkFileExistInDatabase(story_id);
        } else {
            Toast.makeText(getApplicationContext(), "Wrong Path", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void checkFileExistInDatabase(String story_id){
        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getPackageName() + "/files";
        fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
        fileDirectory = fullDirectory + "/" + story_id + ".epub";
        int result = DataUtils.checkFileExists(fullDirectory, story_id+ ".epub");
        if(result == Constants.FILE_EXISTS) {
            Log.d("StoryDetailLog", "Exist");
            isVisit = true;
            findFileWithDirection(fullDirectory);
        } else if(result == Constants.FILE_DOES_NOT_EXIST){
            Log.d("StoryDetailLog", "Non exi");
            isUpload = true;
            findFileOnFirebase(story_id);
        } else {
            Log.d("StoryDetailLog", "Nothing");
        }
    }

    private String fullDirectory ;
    private String fileDirectory ;

    private void findFileOnFirebase(final String storyId){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("story_pdfs").document(storyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    PDFUrl pdfStory = task.getResult().toObject(PDFUrl.class);
                    String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
                    String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getPackageName() + "/files";
                    fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + storyId;
                    fileDirectory = fullDirectory + "/" + storyId + ".epub";
                    Log.d("Directory full sta", fullDirectory);
                    downloadFileFromFirebase(storyId, ".epub", storyId,  pdfStory.getUrl());
                    Log.d("PDFVIEW", pdfStory.getUrl());
                }
            }
        });
    }

    private void downloadFileFromFirebase( String filename ,String fileExtension, String directory, String url) {
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(), directory, filename + fileExtension);
        downloadManager.enqueue(request);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            findFileWithDirection(fullDirectory);
                            // So something here on success
                        } else {
                            int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            // So something here on failed.
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        recentread = mEbookDatabase.getMostRecentlyRead();
        showStatus = EbookDatabase.STATUS_ANY;
        openLastread = false;
        boolean hadSpecialOpen = false;
        if (intent != null) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_SHOW_OPEN:
                        showStatus = EbookDatabase.STATUS_STARTED;
                        hadSpecialOpen = true;
                        break;
                    case ACTION_SHOW_UNREAD:
                        showStatus = EbookDatabase.STATUS_NONE;
                        hadSpecialOpen = true;
                        break;
                    case ACTION_SHOW_LAST_STATUS:
                        showStatus = sharedPreferences.getInt(LASTSHOW_STATUS_KEY, EbookDatabase.STATUS_ANY);
                        hadSpecialOpen = true;
                        break;
                }
            }
        }
        if (!hadSpecialOpen){
            switch (sharedPreferences.getInt(STARTWITH_KEY, STARTLASTREAD)) {
                case STARTLASTREAD:
                    if (recentread!=-1 && sharedPreferences.getBoolean(EpubViewerActivity.READEREXITEDNORMALLY, true)) openLastread = true;
                    break;
                case STARTOPEN:
                    showStatus = EbookDatabase.STATUS_STARTED; break;
                case STARTALL:
                    showStatus = EbookDatabase.STATUS_ANY;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (showingSearch || showStatus!= EbookDatabase.STATUS_ANY) {
            setTitle(R.string.app_name);
            populateBooks();
            showingSearch = false;
        } else {
            super.onBackPressed();
        }
    }

    private void setSortOrder(SortOrder sortOrder) {
        sharedPreferences.edit().putString(SORTORDER_KEY,sortOrder.name()).apply();
    }

    @NonNull
    private SortOrder getSortOrder() {
        try {
            return SortOrder.valueOf(sharedPreferences.getString(SORTORDER_KEY, SortOrder.Default.name()));
        } catch (IllegalArgumentException e) {
            Log.e("Booklist", e.getMessage(), e);
            return SortOrder.Default;
        }
    }

    private void populateBooks() {
        populateBooks(EbookDatabase.STATUS_ANY);
    }

    private void populateBooks(int status) {
        showStatus = status;
        sharedPreferences.edit().putInt(LASTSHOW_STATUS_KEY, showStatus).apply();
        boolean showRecent = false;
        int title = R.string.app_name;
        switch (status) {
            case EbookDatabase.STATUS_SEARCH:
                String lastSearch = sharedPreferences.getString("__LAST_SEARCH_STR__","");
                if (!lastSearch.trim().isEmpty()) {
                    boolean stitle = sharedPreferences.getBoolean("__LAST_TITLE__", true);
                    boolean sauthor = sharedPreferences.getBoolean("__LAST_AUTHOR__", true);
                    searchBooks(lastSearch, stitle, sauthor);
                    return;
                }
            case EbookDatabase.STATUS_ANY:
                title = R.string.book_status_any;
                showRecent = true;
                showingSearch = false;
                break;
            case EbookDatabase.STATUS_NONE:
                title = R.string.book_status_none;
                showingSearch = false;
                break;
            case EbookDatabase.STATUS_STARTED:
                title = R.string.book_status_started;
                showRecent = true;
                showingSearch = false;
                break;
            case EbookDatabase.STATUS_DONE:
                title = R.string.book_status_completed2;
                showingSearch = false;
                break;
            case EbookDatabase.STATUS_LATER:
                title = R.string.book_status_later2;
                showingSearch = false;
                break;
        }
        EpubListActivity.this.setTitle(title);
        SortOrder sortorder = getSortOrder();
        final List<Integer> books = mEbookDatabase.getBookIds(sortorder, status);
        try {
            populateBooks(books,  showRecent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        invalidateOptionsMenu();
    }

    private void searchBooks(String searchfor, boolean stitle, boolean sauthor) {
        showStatus = EbookDatabase.STATUS_SEARCH;
        sharedPreferences.edit().putInt(LASTSHOW_STATUS_KEY, showStatus).apply();
        List<Integer> books = mEbookDatabase.searchBooks(searchfor, stitle, sauthor);
        try {
            populateBooks(books, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EpubListActivity.this.setTitle(getString(R.string.search_res_title, searchfor, books.size()));
        showingSearch = true;
        invalidateOptionsMenu();
    }

    private void populateBooks(final List<Integer> books, boolean showRecent) throws IOException {
        if (showRecent) {
            recentread = mEbookDatabase.getMostRecentlyRead();
            if (recentread >= 0) {
                books.remove((Integer) recentread);
                books.add(0, (Integer)recentread);
            }
        }
        mBookAdapter.setBooks(books);
        Log.d("StoryDetailLog", "start 1");
        if(isVisit == true){
            BookMetadata metadata = Book.getBookMetaData(this, fileDirectory);
            if (metadata!=null) {
                List<Integer> ids = mEbookDatabase.searchBooksWithDirection(fileDirectory, true, false);
                if(ids.size() ==1){
                    readBook(ids.get(0));
                } else {
                    Log.d("StoryDetailLog", "size >1");
                }
            }
        }
        if(isUpload == true){
            if(books.size() <= 1) {
                readBook(books.get(0));
            } else {
                readBook(books.get(1));
            }
            Log.d("StoryDetailLog", "read 1");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.ebook_menu);
        SortOrder sortorder = getSortOrder();

        switch (sortorder) {
            case Default:
                menu.findItem(R.id.menu_sort_default).setChecked(true);
                break;
            case Author:
                menu.findItem(R.id.menu_sort_author).setChecked(true);
                break;
            case Title:
                menu.findItem(R.id.menu_sort_title).setChecked(true);
                break;
            case Added:
                menu.findItem(R.id.menu_sort_added).setChecked(true);
                break;
        }

        switch (sharedPreferences.getInt(STARTWITH_KEY, STARTLASTREAD)) {
            case STARTALL:
                menu.findItem(R.id.menu_start_all_books).setChecked(true);
                break;
            case STARTOPEN:
                menu.findItem(R.id.menu_start_open_books).setChecked(true);
                break;
            case STARTLASTREAD:
                menu.findItem(R.id.menu_start_last_read).setChecked(true);
                break;
        }

        return true;
    }

    private MenuItem mEnableScrollMenu;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_add).setVisible(!showingSearch);
        menu.findItem(R.id.menu_add_dir).setVisible(!showingSearch);
        menu.findItem(R.id.menu_sort).setVisible(!showingSearch);

        MenuItem screenPaging = menu.findItem(R.id.menu_enable_screen_paging);
        screenPaging.setChecked(sharedPreferences.getBoolean(ENABLE_SCREEN_PAGE_KEY, true));

        mEnableScrollMenu = menu.findItem(R.id.menu_enable_scroll);
        mEnableScrollMenu.setChecked(sharedPreferences.getBoolean(ENABLE_DRAG_SCROLL_KEY, true));
        mEnableScrollMenu.setEnabled(screenPaging.isChecked());

        switch (showStatus) {
            case EbookDatabase.STATUS_ANY:
                menu.findItem(R.id.menu_all_books).setChecked(true);
                break;
            case EbookDatabase.STATUS_DONE:
                menu.findItem(R.id.menu_completed_books).setChecked(true);
                break;
            case EbookDatabase.STATUS_LATER:
                menu.findItem(R.id.menu_later_books).setChecked(true);
                break;
            case EbookDatabase.STATUS_NONE:
                menu.findItem(R.id.menu_unopen_books).setChecked(true);
                break;
            case EbookDatabase.STATUS_STARTED:
                menu.findItem(R.id.menu_open_books).setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int status = EbookDatabase.STATUS_ANY;
        boolean pop = false;
        switch (item.getItemId()) {
            case R.id.menu_add:
                findFile();
                break;
            case R.id.menu_add_dir:
                findDir();
                break;
            case R.id.menu_sort_default:
                item.setChecked(true);
                setSortOrder(SortOrder.Default);
                pop = true;
                break;
            case R.id.menu_sort_author:
                item.setChecked(true);
                setSortOrder(SortOrder.Author);
                pop = true;
                break;
            case R.id.menu_sort_title:
                item.setChecked(true);
                setSortOrder(SortOrder.Title);
                pop = true;
                break;
            case R.id.menu_sort_added:
                item.setChecked(true);
                setSortOrder(SortOrder.Added);
                pop = true;
                break;
            case R.id.menu_completed_books:
                pop = true;
                status = EbookDatabase.STATUS_DONE;
                break;
            case R.id.menu_later_books:
                pop = true;
                status = EbookDatabase.STATUS_LATER;
                break;
            case R.id.menu_open_books:
                pop = true;
                status = EbookDatabase.STATUS_STARTED;
                break;
            case R.id.menu_unopen_books:
                pop = true;
                status = EbookDatabase.STATUS_NONE;
                break;
            case R.id.menu_search_books:
                showSearch();
                break;
            case R.id.menu_all_books:
                pop = true;
                status = EbookDatabase.STATUS_ANY;
                break;
            case R.id.menu_start_all_books:
                sharedPreferences.edit().putInt(STARTWITH_KEY, STARTALL).apply(); break;
            case R.id.menu_start_open_books:
                sharedPreferences.edit().putInt(STARTWITH_KEY, STARTOPEN).apply(); break;
            case R.id.menu_start_last_read:
                sharedPreferences.edit().putInt(STARTWITH_KEY, STARTLASTREAD).apply(); break;
            case R.id.menu_enable_screen_paging:
                item.setChecked(!item.isChecked());
                sharedPreferences.edit().putBoolean(ENABLE_SCREEN_PAGE_KEY, item.isChecked()).apply();
                if (mEnableScrollMenu!=null) mEnableScrollMenu.setEnabled(item.isChecked());
                break;
            case R.id.menu_enable_scroll:
                item.setChecked(!item.isChecked());
                sharedPreferences.edit().putBoolean(ENABLE_DRAG_SCROLL_KEY, item.isChecked()).apply(); break;
            default:

                return super.onOptionsItemSelected(item);
        }


        final int statusf = status;
        if (pop) {
            mBookListAdderHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateBooks(statusf);
                    invalidateOptionsMenu();
                }
            }, 120);
        }

        invalidateOptionsMenu();
        return true;
    }

    public static String maxlen(String text, int maxlen) {
        if (text!=null && text.length() > maxlen) {
            int minus = text.length()>3?3:0;
            return text.substring(0, maxlen-minus) + "...";
        }
        return text;
    }

    private void readBook(final int bookid) {
        final EbookDatabase.BookRecord book = mEbookDatabase.getBookRecord(bookid);
        if (book!=null && book.filename!=null) {
            final long now = System.currentTimeMillis();
            mEbookDatabase.updateLastRead(bookid, now);
            recentread = bookid;
            mBookListAdderHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getReader(book,true);
                }
            }, 300);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                try {
                    ShortcutManager shortcutManager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
                    if (shortcutManager!=null) {
                        Intent readBook = getReader(book,false);
                        ShortcutInfo readShortcut = new ShortcutInfo.Builder(this, "id1")
                                .setShortLabel(getString(R.string.shortcut_latest))
                                .setLongLabel(getString(R.string.shortcut_latest_title, maxlen(book.title, 24)))
                                .setIcon(Icon.createWithResource(EpubListActivity.this, R.mipmap.ic_launcher_round))
                                .setIntent(readBook)
                                .build();
                        shortcutManager.setDynamicShortcuts(Collections.singletonList(readShortcut));
                    }
                } catch(Exception e) {
                    Log.e("Booky", e.getMessage(), e);
                }
            }
        }
    }

    private Intent getReader(EbookDatabase.BookRecord book, boolean start) {
        Intent readBook = new Intent(EpubListActivity.this, EpubViewerActivity.class);
        readBook.putExtra(EpubViewerActivity.FILENAME, book.filename);
        readBook.putExtra(Constants.STORY_INDEX, story_id );
        readBook.putExtra(Constants.USER_ID, owner_id);
        readBook.putExtra(EpubViewerActivity.SCREEN_PAGING, sharedPreferences.getBoolean(ENABLE_SCREEN_PAGE_KEY, true));
        readBook.putExtra(EpubViewerActivity.DRAG_SCROLL, sharedPreferences.getBoolean(ENABLE_DRAG_SCROLL_KEY, true));
        readBook.setAction(Intent.ACTION_VIEW);
        readBook.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (start) {
            mBookAdapter.notifyItemIdChanged(book.id);
            startActivity(readBook);
            finish();
        }
        return readBook;
    }

    private void removeBook(int bookid, boolean delete) {
        EbookDatabase.BookRecord book = mEbookDatabase.getBookRecord(bookid);
        if (book==null) {
            Toast.makeText(this, "Bug? The book doesn't seem to be in the database",Toast.LENGTH_LONG).show();
            return;
        }
        if (book.filename!=null && book.filename.length()>0) {
            Book.remove(this, new File(book.filename));
        }
        if (delete) {
            mEbookDatabase.removeBook(bookid);
            if (mBookAdapter!=null) mBookAdapter.notifyItemIdRemoved(bookid);
        }
        recentread = mEbookDatabase.getMostRecentlyRead();
    }

    private boolean addBook(String filename) {
        return addBook(filename, true, System.currentTimeMillis());
    }

    private boolean addBook(String filename, boolean showToastWarnings, long dateadded) {
        try {
            if (mEbookDatabase.containsBook(filename)) {
                if (showToastWarnings) {
                    Toast.makeText(this, getString(R.string.already_added, new File(filename).getName()), Toast.LENGTH_SHORT).show();
                    Log.d("StoryDetailLog", "already add");
                }
                return false;
            }
            BookMetadata metadata = Book.getBookMetaData(this, filename);
            if (metadata!=null) {
                return mEbookDatabase.addBook(filename, metadata.getTitle(), metadata.getAuthor(), dateadded) > -1;
            } else if (showToastWarnings) {
                Toast.makeText(this,getString(R.string.coulndt_add_book, new File(filename).getName()),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("BookList", "File: " + filename  + ", " + e.getMessage(), e);
        }
        return false;
    }

    private void findFile() {
        FilestorageTools fsTools = new FilestorageTools(this);
        if (checkStorageAccess(false)) {
            fsTools.selectExternalLocation(new FilestorageTools.SelectionMadeListener() {
                @Override
                public void selected(File selection) {
                    Log.d("Directory null", selection.getPath());
                    addBook(selection.getPath());
                    populateBooks();
                }
            }, getString(R.string.find_book), false, Book.getFileExtensionRX());
        }
    }

    private void findFileWithDirection(String directory) {
        Log.d("Directory", fileDirectory);
        addBook(fileDirectory);
        populateBooks();
    }

    private void showProgress(int added) {

        if (mTextViewProgress.getVisibility() != View.VISIBLE) {
            mTextViewProgress.setVisibility(View.VISIBLE);
            mTextViewProgress.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        if (added>0) {
            mTextViewProgress.setText(getString(R.string.added_numbooks, added));
        } else {
            mTextViewProgress.setText(R.string.loading);
        }
    }

    private void hideProgress() {
        mTextViewProgress.setVisibility(View.GONE);
    }

    private void addDir( File dir) {

        mBookListAdderHandler.showProgress(0);
        new AddDirTask(this, dir).execute(dir);
    }

    private static class AddDirTask extends  AsyncTask<File,Void,Void> {
        int added=0;
        private final WeakReference<EpubListActivity> ebookListActivityRef;
        private final File dir;

        AddDirTask(EpubListActivity blact, File dir) {
            ebookListActivityRef = new WeakReference<>(blact);
            this.dir = dir;
            Log.d("FileChoose 1", dir.getAbsolutePath());

        }

        @Override
        protected Void doInBackground(File... dirs) {
            EpubListActivity ebookListActivity = ebookListActivityRef.get();
            if (ebookListActivity!=null && dirs!=null) {
                long time = System.currentTimeMillis();
                for (File d : dirs) {
                    try {
                        if (d == null || !d.isDirectory()) continue;
                        for (final File file : d.listFiles()) {
                            try {
                                if (file == null) continue;
                                if (file.isFile() && file.getName().matches(Book.getFileExtensionRX())) {
                                    if (ebookListActivity.addBook(file.getPath(), false, time)) {
                                        added++;
                                    }
                                    ebookListActivity.mBookListAdderHandler.showProgress(added);

                                } else if (file.isDirectory()) {
                                    doInBackground(file);
                                }
                            } catch (Exception e) {
                                Log.e("Booky", e.getMessage(), e);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Booky", e.getMessage(), e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            EpubListActivity ebookListActivity = ebookListActivityRef.get();
            if (ebookListActivity!=null) {
                ebookListActivity.mBookListAdderHandler.hideProgress();
                Toast.makeText(ebookListActivity, ebookListActivity.getString(R.string.books_added, added), Toast.LENGTH_LONG).show();
                ebookListActivity.populateBooks();
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            EpubListActivity ebookListActivity = ebookListActivityRef.get();
            if (ebookListActivity!=null) {
                ebookListActivity.mBookListAdderHandler.hideProgress();
            }
            super.onCancelled(aVoid);
        }
    }

    private void findDir() {
        FilestorageTools fsTools = new FilestorageTools(this);
        if (checkStorageAccess(false)) {
            fsTools.selectExternalLocation(new FilestorageTools.SelectionMadeListener() {
                @Override
                public void selected(File selection) {
                    addDir(selection);
                }
            }, getString(R.string.find_folder), true);
        }
    }

    private void longClickBook(final View view) {
        final int bookid = (int)view.getTag();
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenu().add(R.string.open_book).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                readBook(bookid);
                return false;
            }
        });

        final int status = mEbookDatabase.getStatus(bookid);
        final long lastread = mEbookDatabase.getLastReadTime(bookid);

        if (status!= EbookDatabase.STATUS_DONE) {
            menu.getMenu().add(R.string.mark_completed).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (lastread > 0) {
                        removeBook(bookid, false);
                    } else {
                        mEbookDatabase.updateLastRead(bookid, System.currentTimeMillis());
                    }
                    updateBookStatus(bookid, view, EbookDatabase.STATUS_DONE);
                    return false;
                }
            });
        }

        if (status!= EbookDatabase.STATUS_LATER && status!= EbookDatabase.STATUS_DONE) {
            menu.getMenu().add(R.string.mark_later).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    updateBookStatus(bookid, view, EbookDatabase.STATUS_LATER);
                    return false;
                }
            });
        }

        if (status== EbookDatabase.STATUS_LATER || status== EbookDatabase.STATUS_DONE) {
            menu.getMenu().add(R.string.un_mark).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    updateBookStatus(bookid, view, lastread>0 ? EbookDatabase.STATUS_STARTED : EbookDatabase.STATUS_NONE);
                    return false;
                }
            });
        }
        if (status== EbookDatabase.STATUS_STARTED) {
            menu.getMenu().add(R.string.close_book).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    removeBook(bookid, false);
                    updateBookStatus(bookid, view, EbookDatabase.STATUS_NONE);
                    return false;
                }
            });
        }
        menu.getMenu().add(R.string.remove_book).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                removeBook(bookid, true);
                return false;
            }
        });
        menu.show();
    }

    private void updateBookStatus(int bookid, View view, int status) {
        mEbookDatabase.updateStatus(bookid, status);
        if (mBookAdapter!=null) mBookAdapter.notifyItemIdChanged(bookid);
    }

    private boolean checkStorageAccess(boolean yay) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    yay? REQUEST_READ_EXTERNAL_STORAGE : REQUEST_READ_EXTERNAL_STORAGE_NOYAY);
            return false;
        }
        return true;
    }

    private static final int REQUEST_READ_EXTERNAL_STORAGE_NOYAY = 4333;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 4334;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean yay = true;
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_NOYAY:
                yay = false;
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (yay) Toast.makeText(this, "Yay", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Boo", Toast.LENGTH_LONG).show();
                }

        }
    }

    private void showSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(android.R.string.search_go);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.menu_search_epub_list, null);
        builder.setView(dialogView);

        final EditText editText =  dialogView.findViewById(R.id.search_text);
        final RadioButton author = dialogView.findViewById(R.id.search_author);
        final RadioButton title = dialogView.findViewById(R.id.search_title);
        final RadioButton authortitle = dialogView.findViewById(R.id.search_authortitle);
        builder.setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String searchfor = editText.getText().toString();

                if (!searchfor.trim().isEmpty()) {
                    boolean stitle = title.isChecked() || authortitle.isChecked();
                    boolean sauthor = author.isChecked() || authortitle.isChecked();
                    sharedPreferences.edit()
                            .putString("__LAST_SEARCH_STR__", searchfor)
                            .putBoolean("__LAST_TITLE__", stitle)
                            .putBoolean("__LAST_AUTHOR__", sauthor)
                            .apply();

                    searchBooks(searchfor, stitle, sauthor);
                } else {
                    dialogInterface.cancel();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        editText.setFocusable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        title.setChecked(sharedPreferences.getBoolean("__LAST_TITLE__", false));
        author.setChecked(sharedPreferences.getBoolean("__LAST_AUTHOR__", false));

        String lastSearch = sharedPreferences.getString("__LAST_SEARCH_STR__","");
        editText.setText(lastSearch);
        editText.setSelection(lastSearch.length());
        editText.setSelection(0, lastSearch.length());

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!lastSearch.isEmpty());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(!editText.getText().toString().trim().isEmpty());
            }
        });


        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setImeActionLabel(getString(android.R.string.search_go), EditorInfo.IME_ACTION_SEARCH);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!editText.getText().toString().trim().isEmpty()) {
                        editText.clearFocus();

                        if (imm != null) imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
                    }
                    return true;
                }

                return false;
            }
        });

        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imm!=null) imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);

    }

    private static class BookListAdderHandler extends Handler {

        private static final int SHOW_PROGRESS = 1002;
        private static final int HIDE_PROGRESS = 1003;
        private final WeakReference<EpubListActivity> weakReference;

        BookListAdderHandler(EpubListActivity ebookListInstance) {
            weakReference = new WeakReference<>(ebookListInstance);
        }

        void showProgress(int progress) {
            Message msg=new Message();
            msg.arg1 = BookListAdderHandler.SHOW_PROGRESS;
            msg.arg2 = progress;
            sendMessage(msg);
        }
        void hideProgress() {
            Message msg=new Message();
            msg.arg1 = BookListAdderHandler.HIDE_PROGRESS;
            sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            EpubListActivity ebookListInstance = weakReference.get();
            if (ebookListInstance != null) {
                switch (msg.arg1) {
                    case SHOW_PROGRESS:
                        ebookListInstance.showProgress(msg.arg2);
                        break;
                    case HIDE_PROGRESS:
                        ebookListInstance.hideProgress();
                        break;
                }
            }
        }
    }

}
