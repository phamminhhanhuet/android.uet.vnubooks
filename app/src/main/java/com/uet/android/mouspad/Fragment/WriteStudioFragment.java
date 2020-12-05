package com.uet.android.mouspad.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryStudioActivity;
import com.uet.android.mouspad.Adapter.PagerAdapter;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.WidgetsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class WriteStudioFragment extends Fragment {

    private ViewPager mViewPager;
    private PagerAdapter mPaperAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    public WriteStudioFragment() {
    }

    public static WriteStudioFragment newInstance() {
        return new WriteStudioFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_write_studio, container, false);
        MappdingWidgets(view);
        ActionToolbar();
        initData();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mToolbar.inflateMenu(R.menu.write_studio_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_story:
                createNewStory();
                return true;
            default:
                return false;
        }
    }

    private void MappdingWidgets(View view) {
        mViewPager = view.findViewById(R.id.viewPagerWriteStudio);
        mPaperAdapter = new PagerAdapter(getContext(), getChildFragmentManager(), Constants.PAGER_ADAPTER_WRITE_STUDIO_REQUEST);
        mViewPager.setAdapter(mPaperAdapter);
        mTabLayout = view.findViewById(R.id.tabLayoutWriteStudio);
        mToolbar = view.findViewById(R.id.toolbarWriteStudio);
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.text_write));
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initData() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initView(View view) {
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorTabLayout));
        mTabLayout.setTabTextColors(android.R.color.darker_gray, LayoutUtils.Constant.colorPrimary);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void storeStoryInformation(Uri uri){
        String user_id = mFirebaseAuth.getCurrentUser().getUid();
        String cover = uri.toString();
        Map<String, Object> story = new HashMap<>();
        story.put("user_id", user_id);
        story.put("story_id", "");
        story.put("title", "");
        story.put("description", "");
        story.put("status", "on working");
        story.put("genre", "");
        story.put("format", "");
        story.put("cover", cover);
        story.put("published", false);

        mFirebaseFirestore.collection("stories/")
                .add(story)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String story_id =documentReference.getId();
                        Intent intent = new Intent(getContext(), EditStoryStudioActivity.class);
                        intent.putExtra(STORY_INDEX, story_id);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });

        Map<Object, Object> map = new HashMap<>();
    }

    private void createNewStory(){
        String randomName = UUID.randomUUID().toString();
        final StorageReference image_path = mStorageReference.child("story_cover" ).child(randomName);
        Uri coverUri = WidgetsUtils.getUriToResource(getContext(), R.drawable.default_avatar);
        image_path.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storeStoryInformation(uri);
                    }
                });
            }
        });
    }
}