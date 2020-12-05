package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.StoryDetailActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;

import java.util.ArrayList;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;
import static com.uet.android.mouspad.Utils.Constants.STORY_LIST;

public class SearchTopicAdapter extends RecyclerView.Adapter<SearchTopicAdapter.ViewHolder> {
    private ArrayList<Story> mStories;
    private Context mContext ;

    public SearchTopicAdapter(ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_search_topic,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtTitle.setText(mStories.get(position).getTitle());
        holder.txtGenre.setText(mStories.get(position).getGenre());
        holder.txtStatus.setText(mStories.get(position).getStatus());

        String storyDes = mStories.get(position).getDescription();
        if(storyDes.length() > 60){
            String descrip = storyDes.substring(0, 57);
            holder.txtDescription.setText(descrip + "...");
        } else {
            holder.txtDescription.setText(storyDes);
        }

        Picasso.get()
                .load(mStories.get(position).getCover())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
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


    public class ViewHolder extends RecyclerViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView txtTitle, txtDescription, txtStatus, txtGenre;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewItemSearch);
            imageView = itemView.findViewById(R.id.imgItemCoverSearch);
            txtTitle = itemView.findViewById(R.id.txtItemTitleSearch);
            txtDescription = itemView.findViewById(R.id.txtItemDesSearch);
            txtStatus = itemView.findViewById(R.id.txtStatusSearch);
            txtGenre = itemView.findViewById(R.id.txtGenreSearch);
        }
    }
}
