package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.Ebook.PDFViewerActivity;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Ebook.PDFUrl;
import com.uet.android.mouspad.Model.ItemView;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.DataUtils;

import java.util.ArrayList;

public class EbookEditAdapter extends RecyclerView.Adapter<EbookEditAdapter.ViewHolder> {
    private ArrayList<Story> mStories;
    private Context mContext;
    private FirebaseFirestore mFirebaseFirestore;

    public EbookEditAdapter(ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
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
        holder.txtTitle.setText(mStories.get(position).getTitle());
        holder.txtStatus.setText("Status: published");
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(mStories.get(position).getFormat().equals(Constants.FORMAT_EPUB)){
                    getEPUBContent(mStories.get(position).getStory_id());
                } else {
                    getPDFContent(mStories.get(position).getStory_id());
                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStories.get(position).getFormat().equals(Constants.FORMAT_EPUB)){
                    getEPUBContent(mStories.get(position).getStory_id());
                } else {
                    getPDFContent(mStories.get(position).getStory_id());
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStories.get(position).getFormat().equals(Constants.FORMAT_EPUB)){
                    getEPUBContent(mStories.get(position).getStory_id());
                } else {
                    getPDFContent(mStories.get(position).getStory_id());
                }
            }
        });
        getTotalVote(holder.txtTotalVote, mStories.get(position));
        getTotalComment(holder.txtTotalComment, mStories.get(position));
        getTotalView(holder.txtTotalView, mStories.get(position));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                Toast.makeText(mContext, "Can't not remove!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalVote(final TextView textView, Story story) {
        String storyId = story.getStory_id();
        if(!storyId.isEmpty() || !storyId.equals("")){
            Query query = mFirebaseFirestore.collection("chapters/" + storyId + "/contain/" + storyId + "/likes").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size() +"");
                    } else {
                        textView.setText("0");
                    }
                }
            });
        } else {
            textView.setText("0");
        }

    }

    private void getTotalComment(final TextView textView, Story story) {
        String story_id = story.getStory_id();
        if(!story_id.isEmpty() || !story_id.equals("")){
            Query query = mFirebaseFirestore.collection("comments/" + story_id + "/contain").limit(1000);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        textView.setText(task.getResult().size() +"");
                    } else {
                        textView.setText("0");
                    }
                }
            });

        } else {
            textView.setText("0");
        }

    }

    private void getTotalView(final TextView textView, Story story) {
        FirebaseFirestore.getInstance().collection("views").document(story.getStory_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        return mStories.size();
    }

    private void getPDFContent(final String story_id) {
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
                        intent.putExtra(Constants.STORY_INDEX, story_id);
                        intent.putExtra(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                intent.putExtra(Constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
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
