package com.uet.android.mouspad.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.Ebook.PDFViewerActivity;
import com.uet.android.mouspad.Model.Ebook.PDFUrl;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.Tag;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;

import java.util.ArrayList;

public class StoryDetailAdapter extends RecyclerView.Adapter<StoryDetailAdapter.ViewHolder>{
    private ArrayList<Story> mStories;
    private Context mContext;
    private FirebaseFirestore mFirebaseFirestore;
    private User mUser;
    private boolean isLoaded = false;
    private TagsAdapter mTagsAdapter;
    private String mFormat;
    public int mCurrentPosition;

    public StoryDetailAdapter( ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
        this.mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_story_detail_temp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        mCurrentPosition = position;

        Uri coverUri = Uri.parse(mStories.get(position).getCover());
        Picasso.get()
                .load(coverUri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.mImgCoverStory);

    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    private void getPDFContent(String story_id) {
        if(ConnectionUtils.isConnectingInternet){
            mFirebaseFirestore.collection("story_pdfs").document(story_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        PDFUrl pdfStory = task.getResult().toObject(PDFUrl.class);
                        String url = pdfStory.getUrl();
                        Intent intent = new Intent(mContext, PDFViewerActivity.class);
                        intent.putExtra(Constants.STORY_PDF_URL,url);
                        intent.putExtra(Constants.FORMAT_PDF, "");
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
            String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + mContext.getPackageName() + "/files";
            String fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
            int result = DataUtils.checkFileExists(fullDirectory, story_id+ ".pdf" );
            if(result == Constants.FILE_EXISTS){
                Intent intent = new Intent(mContext, PDFViewerActivity.class);
                intent.putExtra(Constants.STORY_PDF_URL, "");
                intent.putExtra(Constants.FORMAT_PDF, fullDirectory);
                intent.putExtra(Constants.STORY_INDEX, story_id);
                mContext.startActivity(intent);
            } else if(result == Constants.FILE_DOES_NOT_EXIST) {
                Toast.makeText(mContext, "Please check your internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getEPUBContent(String story_id){
        Intent intent = new Intent(mContext, EpubListActivity.class);
        intent.putExtra(Constants.FORMAT_EPUB, Constants.FORMAT_EPUB);
        intent.putExtra(Constants.STORY_INDEX, story_id);
        intent.putExtra(Constants.READING_MODE, Constants.READING_MODE_DEFAULT);
        mContext.startActivity(intent);
    }

    private void getChaptersOfAStory(String story_id, final ArrayList<StoryChapter> storyChapters){
        Query query = mFirebaseFirestore.collection("chapters/" +story_id + "/contain").orderBy("timestamp",  Query.Direction.ASCENDING);
        query.get().addOnCompleteListener((Activity) mContext, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    StoryChapter chapter = documentSnapshot.toObject(StoryChapter.class);
                    storyChapters.add(chapter);
                }
                isLoaded = true;
            }
        });
    }

    private void getTagsOfAStory(String story_id, final ArrayList<String> mTags, final TagsAdapter adapter){
        Query queryTag = mFirebaseFirestore.collection("story_tags/" + story_id + "/contain").limit(100);
        queryTag.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    Tag tag = documentSnapshot.toObject(Tag.class);
                    mTags.add(tag.getTitle());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public int getItemId() {
        return mCurrentPosition;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgCoverStory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgCoverStory = itemView.findViewById(R.id.imgCoverItemStoryDetail);
        }

    }
}
