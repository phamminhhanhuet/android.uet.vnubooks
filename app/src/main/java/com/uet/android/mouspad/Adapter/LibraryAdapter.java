package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryChapterContentActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryDetailActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.Ebook.PDFViewerActivity;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.Model.ViewModel.LibraryStoryModel;
import com.uet.android.mouspad.Model.ViewModel.LibraryViewModel;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;

import java.util.ArrayList;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_LIST;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private ArrayList<Story> mStories;
    private ArrayList<LibraryItem> mLibraryItems;
    private Context mContext;
    private LibraryStoryModel libraryStoryModel;

    public void setLibraryStoryModel (LibraryStoryModel libraryViewModel){
        this.libraryStoryModel = libraryViewModel;
    }

    public LibraryAdapter(ArrayList<LibraryItem> libraryItems, ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
        this.mLibraryItems = libraryItems;
    }

    public void setStories(ArrayList<Story> mStories) {
        this.mStories = mStories;
    }

    public ArrayList<Story> getStories() {
        return mStories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_story_libary, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(mStories.get(position).getTitle());
        Uri coverUri = Uri.parse(mStories.get(position).getCover());
        Picasso.get()
                .load(coverUri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionUtils.isConnectingInternet){
                    Intent intent = new Intent(mContext, StoryDetailActivity.class);
                    intent.putExtra(STORY_INDEX, position);
                    intent.putExtra(STORY_LIST, mStories);
                    mContext.startActivity(intent);
                } else {
                    Story story = mStories.get(position);
                    String story_id = story.getStory_id();
                    if(story.getFormat().equals(Constants.FORMAT_DEFAULT_APP)){
                        Intent intent = new Intent(mContext, StoryChapterContentActivity.class);
                        intent.putExtra(Constants.STORY_CHAPTER_INDEX,0);
                        intent.putExtra(Constants.STORY_INDEX, mStories.get(position).getStory_id());
                        intent.putExtra(Constants.STORY_TITLE, mStories.get(position).getTitle());
                        ArrayList<StoryChapter> chapters = libraryStoryModel.getLibraryChapterModel().get(position).getStoryChapters();
                        ArrayList<String> mChapterIds = new ArrayList<>();
                        ArrayList<String> mTitles = new ArrayList<>();
                        for(StoryChapter chapter: chapters){
                            mChapterIds.add(chapter.getChapter_id());
                            mTitles.add(chapter.getTitle());
                        }
                        intent.putExtra(Constants.STORY_CHAPTER_LIST, mChapterIds);
                        intent.putExtra(Constants.STORY_CHAPTER_TITLE, mTitles);
                        Log.d("Chaptertitle 1", mTitles.size() +"");
                        intent.putExtra(Constants.OWNER_ID, mStories.get(position).getUser_id());
                        mContext.startActivity(intent);
                    } else if(story.getFormat().equals(Constants.FORMAT_PDF)){
                        String rootStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "Android";
                        String dataAndroidDirectory = Environment.getDataDirectory().toString() + "/" + mContext.getPackageName() + "/files";
                        String fullDirectory = rootStorageDirectory + dataAndroidDirectory + "/" + story_id;
                        Log.d("Chaptertitle", fullDirectory);
                        int result = DataUtils.checkFileExists(fullDirectory, story.getStory_id()+ ".pdf" );
                        if(result == Constants.FILE_EXISTS){
                            Log.d("Chaptertitle 0", fullDirectory);
                            Intent intent = new Intent(mContext, PDFViewerActivity.class);
                            intent.putExtra(Constants.STORY_PDF_URL, "");
                            intent.putExtra(Constants.FORMAT_PDF, fullDirectory);
                            intent.putExtra(Constants.STORY_INDEX, story.getStory_id());
                            intent.putExtra(Constants.STORY_TITLE,story.getTitle() );
                            intent.putExtra(Constants.USER_ID, story.getUser_id());
                            mContext.startActivity(intent);
                        } else if(result == Constants.FILE_DOES_NOT_EXIST) {
                            Toast.makeText(mContext, "Please check your internet!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent intent = new Intent(mContext, EpubListActivity.class);
                        intent.putExtra(Constants.FORMAT_EPUB, Constants.FORMAT_EPUB);
                        intent.putExtra(Constants.STORY_INDEX, story_id);
                        intent.putExtra(Constants.USER_ID,story.getUser_id() );
                        intent.putExtra(Constants.STORY_TITLE, story.getTitle());
                        intent.putExtra(Constants.READING_MODE, Constants.READING_MODE_DEFAULT);
                        mContext.startActivity(intent);
                    }
                }
            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtra(STORY_INDEX, position);
                intent.putExtra(STORY_LIST, mStories);
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        if(mLibraryItems.get(position).isDownloaded()){
            holder.imageDownloaded.setVisibility(View.VISIBLE);
        } else {
            holder.imageDownloaded.setVisibility(View.GONE);
        }
        holder.progressBarStatus.setProgress(mLibraryItems.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void notifyDataSetChanged(ArrayList<Story> newStories, ArrayList<LibraryItem> newItems){
        mStories = newStories;
        mLibraryItems = newItems;
    }

    public class ViewHolder extends RecyclerViewHolder implements View.OnCreateContextMenuListener {
        ImageView imageView, imageDownloaded;
        TextView textView;
        ProgressBar progressBarStatus;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgItemStory);
            textView = itemView.findViewById(R.id.textNameItemStory);

            imageDownloaded = itemView.findViewById(R.id.imgNotDownloadedLibrary);
            progressBarStatus = itemView.findViewById(R.id.progressStatusLibrary);
            progressBarStatus.setMax(100);

            cardView = itemView.findViewById(R.id.cardviewItemStory);

            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)  {
                //menuInfo is null
//                contextMenu.add(Menu.NONE, R.id.action_library_download,
//                        Menu.NONE, R.string.text_delete);
//                contextMenu.add(Menu.NONE, R.id.action_delete_libary,
//                        Menu.NONE, R.string.text_download);
        }
    }
}
