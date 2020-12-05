package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private ArrayList<Story> mStories ;
    private Context mContext;
    private boolean titleVisible = true;

    public StoryAdapter(ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
    }

    public void setTitleVisible(boolean visible){
        this.titleVisible = visible;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_story, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(titleVisible == false){
            holder.textName.setVisibility(View.GONE);
            holder.textGenre.setVisibility(View.GONE);
        }
        holder.textName.setText(mStories.get(position).getTitle());
        holder.textGenre.setText(mStories.get(position).getGenre());
        Uri coverUri = Uri.parse(mStories.get(position).getCover());
        Picasso.get()
                .load(coverUri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imageCover);
        holder.imageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtra(STORY_INDEX, position);
                intent.putExtra(STORY_LIST, mStories);
                mContext.startActivity(intent);
            }
        });
        holder.imageCover.setOnTouchListener(listener);

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
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }


    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView image = (ImageView) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    image.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                    image.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    image.getDrawable().clearColorFilter();
                    image.invalidate();
                    break;
                }
            }

            return true;
        }
    };

    public class ViewHolder extends RecyclerViewHolder {
        TextView textName ;
        TextView textGenre;
        ImageView imageCover ;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textNameItemStory);
            textGenre = itemView.findViewById(R.id.textGenreItemStory);
            imageCover = itemView.findViewById(R.id.imgItemStory);
            cardView = itemView.findViewById(R.id.cardviewItemStory);
        }
    }
}
