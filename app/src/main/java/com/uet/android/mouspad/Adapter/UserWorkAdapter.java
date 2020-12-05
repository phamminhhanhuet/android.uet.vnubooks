package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryStudioActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryDetailActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;

import java.util.ArrayList;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_LIST;

public class UserWorkAdapter extends RecyclerView.Adapter<UserWorkAdapter.ViewHolder>{
    private ArrayList<Story> mStories;
    private Context mContext;
    private String mUserId;
    private String mListId;
    private boolean isCurrentUser = false;
    private boolean isReadingList = false;

    public UserWorkAdapter(java.util.ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
    }

    public void setUserId(String userId){
        this.mUserId = userId;
        if(ConnectionUtils.isLoginValid){
            if(mUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                isCurrentUser = true;
            }
        } else {
            isCurrentUser = false;
        }
    }

    public void setReadingList(boolean readingList, String list_id){
        this.isReadingList = readingList;
        this.mListId = list_id;
    }

    @NonNull
    @Override
    public UserWorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_works_user,parent,false);
        return new UserWorkAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserWorkAdapter.ViewHolder holder, final int position) {
        Picasso.get()
                .load(Uri.parse(mStories.get(position).getCover()))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into( holder.imageCover);
        holder.imageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditStoryStudioActivity.class);
                intent.putExtra(STORY_INDEX, mStories.get(position).getStory_id());
                mContext.startActivity(intent);
            }
        });
        holder.textTitle.setText(mStories.get(position).getTitle());
        holder.textGenre.setText(mStories.get(position).getGenre());
        holder.textStatus.setText(mStories.get(position).getStatus());

        String storyDes = mStories.get(position).getDescription();
        if(storyDes.length() > 50){
            String descrip = storyDes.substring(0, 47);
            holder.textDescription.setText(descrip + "...");
        } else {
            holder.textDescription.setText(storyDes);
        }
        if(isCurrentUser){
            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(mContext, EditStoryStudioActivity.class);
                    intent.putExtra(STORY_INDEX, mStories.get(position).getStory_id());
                    mContext.startActivity(intent);
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditStoryStudioActivity.class);
                    intent.putExtra(STORY_INDEX, mStories.get(position).getStory_id());
                    mContext.startActivity(intent);
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore.getInstance().collection("stories").document(mStories.get(position).getStory_id()).delete();
                    FirebaseFirestore.getInstance().collection("story_user/" + user_id + "/contain").document(mStories.get(position).getStory_id()).delete();
                    mStories.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else if(isReadingList){
            final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore.getInstance().collection("reading_lists/" + currentUserId + "/contain/" + mListId + "/contain" ).document(mStories.get(position).getStory_id()).delete();
                    mStories.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(mContext, StoryDetailActivity.class);
                    intent.putExtra(STORY_INDEX, position);
                    intent.putExtra(STORY_LIST, mStories);
                    mContext.startActivity(intent);
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, StoryDetailActivity.class);
                    intent.putExtra(STORY_INDEX, position);
                    intent.putExtra(STORY_LIST, mStories);
                    mContext.startActivity(intent);
                }
            });
            holder.linearLayout.setVisibility(View.GONE);
        }
        holder.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtra(STORY_INDEX, position);
                intent.putExtra(STORY_LIST, mStories);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    public void updateAdapterData(ArrayList<Story> arrayList) {
        this.mStories.clear();
        this.mStories.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerViewHolder {

        ImageView imageCover;
        TextView textTitle, textStatus, textDescription, textGenre;
        CardView cardView;
        Button btnRead;
        Button btnDelete;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imgItemCoverWriteStudio);
            textTitle = itemView.findViewById(R.id.txtItemTitleWriteStudio);
            textStatus = itemView.findViewById(R.id.txtStatusWorkUser);
            textGenre = itemView.findViewById(R.id.txtGenreWorkUser);
            textDescription = itemView.findViewById(R.id.txtItemDesWorkUser);
            cardView = itemView.findViewById(R.id.cardViewItemWriteStudio);
            btnRead = itemView.findViewById(R.id.btnReadWorkUser);
            btnDelete = itemView.findViewById(R.id.delete);
            linearLayout = itemView.findViewById(R.id.swipeLayout);
        }
    }
}
