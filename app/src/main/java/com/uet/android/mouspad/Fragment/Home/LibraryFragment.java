package com.uet.android.mouspad.Fragment.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Activity.Authorize.LoginActivity;
import com.uet.android.mouspad.Adapter.LibraryAdapter;
import com.uet.android.mouspad.Ebook.EbookDatabase;
import com.uet.android.mouspad.App;
import com.uet.android.mouspad.Ebook.SortOrder;
import com.uet.android.mouspad.Fragment.HomeFragment;

import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Model.ViewModel.LibraryStoryModel;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.ViewModel.LibraryViewModel;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;
import com.uet.android.mouspad.Utils.LayoutUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.uet.android.mouspad.Ebook.EpubListActivity.prefname;

public class LibraryFragment extends Fragment {
    private ArrayList<Story> mStories;
    private ArrayList<LibraryItem> mLibraryItems;
    private Toolbar mToolbar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String user_id;

    private LibraryAdapter mLibraryAdapter;
    private RecyclerView mRecyclerView;

    private TextView mTxtReset;
    private LibraryStoryModel mLibraryStoryModel;
    private LibraryViewModel mLibraryViewModel ;

    private EbookDatabase mEbookDatabase;
    private SharedPreferences sharedPreferences;
    private BookListAdderHandler mBookListAdderHandler;
    private static final String SORTORDER_KEY = "sortorder";
    private static final String LASTSHOW_STATUS_KEY = "LastshowStatus";
    private HashMap<Integer, String> mapDatabase;

    private boolean showingSearch = false;

    public LibraryFragment() {
    }

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(ConnectionUtils.isLoginValid){
            sharedPreferences = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(SORTORDER_KEY)) {
                setSortOrder(SortOrder.Default);
            }
            mBookListAdderHandler = new BookListAdderHandler(this);
            mEbookDatabase = App.getDB(getContext());
            mEbookDatabase.setReadmode(Constants.READING_MODE_LIBRARY);
            mapDatabase = new HashMap<>();
            getEbookDatabase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        HomeFragment.mTabLayout.setBackgroundColor(LayoutUtils.Constant.color);
        View view= inflater.inflate(R.layout.fragment_library, container, false);
        MappingWidgets(view);
        ActionToolbar();
        if(ConnectionUtils.isLoginValid){
            initData();
            registerForContextMenu(mRecyclerView);
            LinearLayout linearLayout = view.findViewById(R.id.blankText);
            linearLayout.setVisibility(View.GONE);
        } else {
            LinearLayout linearLayout = view.findViewById(R.id.blankText);
            linearLayout.setVisibility(View.VISIBLE);
            TextView textView = view.findViewById(R.id.title);
            textView.setVisibility(View.GONE);


            CardView cardView =  view.findViewById(R.id.gotoLoginCard);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean pop = false;
        switch (item.getItemId()){
            case R.id.action_reload_library:
                Toast.makeText(getContext(), getString(R.string.text_reload), Toast.LENGTH_SHORT).show();
                if(mRecyclerView != null && mTxtReset != null && ConnectionUtils.isConnectingInternet){
                    mTxtReset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getStoryLibraryData();
                        }
                    });
                }
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
            default:
                return false;
        }
        final int statusf = EbookDatabase.STATUS_ANY;
        if (pop) {
            mBookListAdderHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateBooks(statusf);
                }
            }, 120);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(ConnectionUtils.isLoginValid){
            mToolbar.inflateMenu(R.menu.library_menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(ConnectionUtils.isLoginValid){
            getActivity().getMenuInflater().inflate(R.menu.library_context_menu,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((LibraryAdapter) mRecyclerView.getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d("Adapter", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.action_library_download:
                downloadContent();
                return true;
            case R.id.action_library_delete:
                String storyId = mLibraryAdapter.getStories().get(position).getStory_id();
                removeBook(storyId);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void getEbookDatabase(){
        List<Integer> integerList = mEbookDatabase.getBookIds(SortOrder.Default, EbookDatabase.STATUS_ANY);
        Log.d("LibrarayFragmnet s", integerList.size() + "");
        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getActivity().getPackageName() + "/files";
        String directory = rootStorageDirectory + dataAndroidDirectory + "/" ;
        for(Integer id: integerList){
            String fileName = mEbookDatabase.getBookRecord(id).filename;
            if(fileName.contains(directory)){
                String rawId = fileName.replace(directory, "");
                int posFormat = rawId.indexOf(".");
                if(posFormat >= 0){
                    String scaledId = rawId.substring(0, posFormat);
                    int quFormat = scaledId.indexOf("/");
                    if(quFormat >= 0){
                        String lastId = scaledId.substring(0, quFormat);
                        Log.d("LibrarayFragmnet", lastId);
                        mapDatabase.put(id, lastId);
                    }
                }
                else {
                    Log.d("LibrarayFragmnet", rawId);
                    mapDatabase.put(id, rawId);
                }
            } else {
                Log.d("LibrarayFragmnet", fileName);
                mapDatabase.put(id, fileName);
            }
            Log.d("LibrarayFragmnet id", id + "");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRecyclerView != null && mTxtReset != null && ConnectionUtils.isConnectingInternet){
            mTxtReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getStoryLibraryData();
                    mTxtReset.setText(R.string.text_reload);
                }
            });
        }
    }

    public void getStoryLibraryData(){
        Query query = mFirebaseFirestore.collection("libraries/" + user_id + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mStories.clear();
                mLibraryItems.clear();
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    LibraryItem libraryItem = documentSnapshot.toObject(LibraryItem.class);
                    mLibraryItems.add(libraryItem);
                    final String story_id = libraryItem.getStory_id();
                    mFirebaseFirestore.collection("stories/").document(story_id).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Story story = task.getResult().toObject(Story.class);
                                    if(!mStories.contains(story)){
//                                        mStories.add(story);
//                                        mLibraryAdapter.notifyDataSetChanged();
                                        mStories.add(story);
                                        mLibraryAdapter.notifyDataSetChanged();
                                        Log.d("ViewModelnew", story_id +"");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void removeBook(String story_id){
        if(!mapDatabase.isEmpty()){
            Log.d("mapdatabase", "" + story_id);
            for(int i = 1; i <= mapDatabase.size(); i ++){
                if(mapDatabase.containsKey(i)){
                    if(mapDatabase.get(i).equals(story_id)){
                        mEbookDatabase.removeBook(i);
                        Log.d("mapdatabase", "" + story_id);
                        break;
                    }
                }
            }

            int index = -1;
            for(int j = 0 ; j < mStories.size(); j ++){
                if(mStories.get(j).getStory_id().equals(story_id)){
                    Log.d("mapdatabase", mLibraryStoryModel.getStories().size() + "");
                    Log.d("mapdatabase j", j + "");
                    index = j;
                    if(mStories.get(index).getFormat().equals(Constants.FORMAT_EPUB) || mStories.get(index).getFormat().equals(Constants.FORMAT_PDF)){
                        deleteFileFromStorage(story_id, mStories.get(index).getFormat());
                    }
                    break;
                }
            }
            mLibraryStoryModel.getStories().remove(index);
            mLibraryStoryModel.getLibraryItems().remove(index);
            mLibraryStoryModel.getLibraryChapterModel().remove(index);
            mLibraryStoryModel.getUsers().remove(index);
            //mStories = mLibraryStoryModel.getStories(); mLibraryItems = mLibraryStoryModel.getLibraryItems();
            mLibraryAdapter.notifyDataSetChanged(mLibraryStoryModel.getStories(), mLibraryStoryModel.getLibraryItems());
            Log.d("mapdatabase k", mLibraryStoryModel.getStories().size() + "");
            Log.d("mapdatabase kk", mLibraryStoryModel.getLibraryItems().size() + "");
            mRecyclerView.setAdapter(mLibraryAdapter);
            mFirebaseFirestore.collection("libraries/" + user_id + "/contain").document(story_id).delete();
        }
    }
    private String fullDirectory ;

    private void deleteFileFromStorage(String story_id,  String format){
        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getActivity().getPackageName() + "/files";
        fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
        DataUtils.deleteFile(fullDirectory,story_id+ "." + format );
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

    private void populateBooks(int status) {
        sharedPreferences.edit().putInt(LASTSHOW_STATUS_KEY, status).apply();
        boolean showRecent = false;
        int title = R.string.app_name;
        switch (status) {
            case EbookDatabase.STATUS_ANY:
                title = R.string.book_status_any;
                showRecent = true;
                break;
        }
        SortOrder sortorder = getSortOrder();
        final List<Integer> books = mEbookDatabase.getBookIds(sortorder, status);

        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + getActivity().getPackageName() + "/files";
        String directory = rootStorageDirectory + dataAndroidDirectory + "/" ;
        List<String> storiesID = new ArrayList<>();
        Log.d("LibrarayFragmnet s", books.size() + "");
        for(Integer id: books){
            String fileName = mEbookDatabase.getBookRecord(id).filename;
            if(fileName.contains(directory)){
                String rawId = fileName.replace(directory, "");
                int posFormat = rawId.indexOf(".");
                if(posFormat >= 0){
                    String scaledId = rawId.substring(0, posFormat);
                    int quFormat = scaledId.indexOf("/");
                    if(quFormat >= 0){
                        String lastId = scaledId.substring(0, quFormat);
                        Log.d("LibrarayFragmnet", lastId);
                        storiesID.add(lastId);
                    }

                }
                else {
                    Log.d("LibrarayFragmnet", rawId);
                    storiesID.add(rawId);
                }
            } else {
                Log.d("LibrarayFragmnet", fileName);
                storiesID.add(fileName);
            }
            Log.d("LibrarayFragmnet id", id + "");
        }

        ArrayList<Story> newStories = new ArrayList<>();
        ArrayList<LibraryItem> newItems = new ArrayList<>();
        for(int i = 0; i < storiesID.size(); i ++){
            for(int j = 0; j < mStories.size() ; j ++){
                if (mStories.get(j).getStory_id().equals(storiesID.get(i))){
                    newStories.add(mStories.get(j));
                    newItems.add(mLibraryItems.get(j));
                    break;
                }
            }
        }
        Log.d("LibrarayFragmnet 2",newStories.size() + "" );
        mLibraryAdapter.notifyDataSetChanged(newStories, newItems);
        mRecyclerView.setAdapter(mLibraryAdapter);
        try {
            populateBooks(books,  showRecent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateBooks(final List<Integer> books, boolean showRecent) throws IOException {
        if (showRecent) {
            int  recentread = mEbookDatabase.getMostRecentlyRead();
            if (recentread >= 0) {
                books.remove((Integer) recentread);
                books.add(0, (Integer)recentread);
            }
        }
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarLibrary);
        mRecyclerView = view.findViewById(R.id.recyclerViewLibrary);
        mTxtReset = view.findViewById(R.id.title);
        mTxtReset.setText(R.string.text_reload);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setTitle(R.string.text_library);
    }

    private void initData()  {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        user_id = mFirebaseAuth.getCurrentUser().getUid();

        mLibraryStoryModel = new LibraryStoryModel(getContext());
        mStories = new ArrayList<>();
        mLibraryItems = new ArrayList<>();

        mEbookDatabase = App.getDB(getContext());
        mEbookDatabase.setReadmode(Constants.READING_MODE_LIBRARY);

        if(ConnectionUtils.isConnectingInternet){

            mLibraryViewModel = new ViewModelProvider(getActivity()).get(LibraryViewModel.class);
            mLibraryViewModel.getModelData().observe(getViewLifecycleOwner(), new Observer<LibraryStoryModel>() {
                @Override
                public void onChanged(LibraryStoryModel libraryStoryModel) {
                    mLibraryStoryModel = libraryStoryModel;
                    mStories = libraryStoryModel.getStories();
                    mLibraryItems = libraryStoryModel.getLibraryItems();
                    Log.d("ViewModelnew", libraryStoryModel.getStories().size() +"");
                    try {
                        initView(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            try {
                initView(null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView(View view) throws IOException, JSONException {
        Log.d("LoadStory", "load 0");
        if(ConnectionUtils.isConnectingInternet){
            mLibraryAdapter = new LibraryAdapter(mLibraryItems, mStories,getContext());
        } else {
            Log.d("LoadStory", "load");
            mLibraryStoryModel = DataUtils.loadDataFromInternalStorage(getContext(),mLibraryStoryModel);
            loadLibraryContent();
            mLibraryAdapter = new LibraryAdapter(mLibraryItems, mStories,getContext());
            mLibraryAdapter.setLibraryStoryModel(mLibraryStoryModel);
        }
        mRecyclerView.setHasFixedSize(true);
        int numberOfColumns = 3;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),numberOfColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setAdapter(mLibraryAdapter);
    }

    private void downloadContent(){
        DataUtils.saveToInternalStorage(getContext(), mLibraryStoryModel, "Story Library");
    }

    private void loadLibraryContent(){
        mStories = mLibraryStoryModel.getStories();
        mLibraryItems = mLibraryStoryModel.getLibraryItems();
    }

    private static class BookListAdderHandler extends Handler {

        private static final int SHOW_PROGRESS = 1002;
        private static final int HIDE_PROGRESS = 1003;
        private final WeakReference<LibraryFragment> weakReference;

        BookListAdderHandler(LibraryFragment ebookListInstance) {
            weakReference = new WeakReference<>(ebookListInstance);
        }
        @Override
        public void handleMessage(Message msg) {
            LibraryFragment ebookListInstance = weakReference.get();
            if (ebookListInstance != null) {
                switch (msg.arg1) {
                    case SHOW_PROGRESS:
                        break;
                    case HIDE_PROGRESS:
                        break;
                }
            }
        }
    }
}
