package com.uet.android.mouspad.Adapter.FirebaseUI;

import android.app.Activity;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

public class TableContentChapterAdapter extends FirestoreRecyclerAdapter<StoryChapter, TableContentChapterAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private FirebaseFirestore mFirebaseFirestore;
    public TableContentChapterAdapter(@NonNull FirestoreRecyclerOptions<StoryChapter> options) {
        super(options);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final StoryChapter model) {
        holder.txtTitle.setText(model.getTitle());
        holder.txtStatus.setText("Status: published!");
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, model.getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX,model.getChapter_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, model.getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX,model.getChapter_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditStoryChapterContentActivity.class);
                intent.putExtra(Constants.STORY_INDEX, model.getStory_id());
                intent.putExtra(Constants.STORY_CHAPTER_INDEX, model.getChapter_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
        getTotalVote(holder.txtTotalVote, model);
        getTotalComment(holder.txtTotalComment,model);
        getTotalView(holder.txtTotalView,model);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseFirestore.collection("chapters/" + model.getStory_id() + "/contain").document(model.getChapter_id()).delete();
                notifyDataSetChanged();
                Toast.makeText(mContext, "Remove", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_table_content_of_story,parent,false);
        return new ViewHolder(itemView);
    }

    private void getTotalVote(final TextView textView, StoryChapter storyChapter) {
        String chapterId = storyChapter.getChapter_id();
        if(!chapterId.isEmpty() || !chapterId.equals("")){
            Query query = mFirebaseFirestore.collection("chapters/" + storyChapter.getStory_id() + "/contain/" + storyChapter.getChapter_id() + "/likes").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size() + "");
                    } else {
                        textView.setText("0");
                    }
                }
            });
//            FirebaseFirestore.getInstance().collection("chapters/" + storyChapter.getStory_id() + "/contain/" + storyChapter.getChapter_id() + "/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                    if(!value.isEmpty()){
//                        textView.setText(value.size());
//                    } else {
//                        textView.setText("0");
//                    }
//                }
//            });
        }
       else textView.setText("0");
    }
    private void getTotalComment(final TextView textView, StoryChapter storyChapter) {
        String chapterId = storyChapter.getChapter_id();
        if(!chapterId.isEmpty() || !chapterId.equals("")){

            Query query = mFirebaseFirestore.collection("comments/" + storyChapter.getChapter_id() + "/contain").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size() + "");
                    } else {
                        textView.setText("0");
                    }
                }
            });
//            FirebaseFirestore.getInstance().collection("comments/" + storyChapter.getChapter_id() + "/contain").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                    if(!value.isEmpty()){
//                        textView.setText(value.size());
//                    } else {
//                        textView.setText("0");
//                    }
//                }
//            });
        }
        else textView.setText("0");
    }

    @Override
    public void onDataChanged() {

        super.onDataChanged();
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
