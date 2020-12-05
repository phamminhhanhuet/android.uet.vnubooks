package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryChapterContentActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.ItemView;
import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class TableContentChapterAdapter extends RecyclerView.Adapter<TableContentChapterAdapter.ViewHolder> {
    private ArrayList<StoryChapter> mChapters;
    private Context mContext;
    private FirebaseFirestore mFirebaseFirestore;

    public TableContentChapterAdapter(ArrayList<StoryChapter> mChapters, Context mContext) {
        this.mChapters = mChapters;
        this.mContext = mContext;
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_table_content_of_story,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtTitle.setText(mChapters.get(position).getTitle());
        holder.txtStatus.setText("Status: published");
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, mChapters.get(position).getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX, mChapters.get(position).getChapter_id());
                mContext.startActivity(intent);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, mChapters.get(position).getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX, mChapters.get(position).getChapter_id());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, mChapters.get(position).getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX, mChapters.get(position).getChapter_id());
                mContext.startActivity(intent);
            }
        });
        getTotalVote(holder.txtTotalVote, mChapters.get(position));
        getTotalComment(holder.txtTotalComment, mChapters.get(position));
        getTotalView(holder.txtTotalView, mChapters.get(position));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseFirestore.collection("chapters/" + mChapters.get(position).getStory_id() + "/contain").document(mChapters.get(position).getChapter_id()).delete();
                mChapters.remove(position);
                notifyDataSetChanged();
                Toast.makeText(mContext, "Remove", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTotalVote(final TextView textView, StoryChapter storyChapter) {
        String chapterId = storyChapter.getChapter_id();
        if(!chapterId.isEmpty() || !chapterId.equals("")){
            Query query = mFirebaseFirestore.collection("chapters/" + storyChapter.getStory_id() + "/contain/" + storyChapter.getChapter_id() + "/likes").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size());
                    } else {
                        textView.setText("0");
                    }
                }
            });
        } else {
            textView.setText("0");
        }

    }
    private void getTotalComment(final TextView textView, StoryChapter storyChapter) {
        String chapterId = storyChapter.getChapter_id();
        if(!chapterId.isEmpty() || !chapterId.equals("")){
            Query query = mFirebaseFirestore.collection("comments/" + storyChapter.getChapter_id() + "/contain").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size());
                    } else {
                        textView.setText("0");
                    }
                }
            });

        } else {
            textView.setText("0");
        }

    }

    private void getTotalView(final TextView textView, StoryChapter storyChapter) {
        FirebaseFirestore.getInstance().collection("views").document(storyChapter.getChapter_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    ItemView itemView = task.getResult().toObject(ItemView.class);
                    textView.setText(itemView.getTotal() + "");
                } else {
                    textView.setText("0");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mChapters.size();
    }

    public void updateAdapterData(ArrayList<StoryChapter> arrayList) {
        this.mChapters.clear();
        this.mChapters.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerViewHolder {
        TextView txtTitle, txtTotalView, txtTotalComment, txtTotalVote;
        TextView txtStatus;
        CardView cardView;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtItemChapterTitle);
            txtStatus = itemView.findViewById(R.id.txtItemChapterStatus);
            cardView = itemView.findViewById(R.id.cardViewItemChapter);
            txtTotalView = itemView.findViewById(R.id.txtTotalView);
            txtTotalComment = itemView.findViewById(R.id.txtTotalComment);
            txtTotalVote = itemView.findViewById(R.id.txtTotalVote);
            btnDelete = itemView.findViewById(R.id.delete);
        }
    }
}
