package com.uet.android.mouspad.Adapter.FirebaseUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryStudioActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class WriteStudioAdapter extends FirestoreRecyclerAdapter<Story, WriteStudioAdapter.ViewHolder> {
    private Context mContext;
    private String mUserId ;
    public WriteStudioAdapter(@NonNull FirestoreRecyclerOptions<Story> options) {
        super(options);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Story model) {
        if(model.getUser_id().equals(mUserId)){
            Picasso.get()
                    .load(Uri.parse(model.getCover()))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into( holder.imageCover);
            holder.imageCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditStoryStudioActivity.class);
                    intent.putExtra(STORY_INDEX, model.getStory_id());
                    mContext.startActivity(intent);
                }
            });
            holder.textTitle.setText(model.getTitle());
            holder.textStatus.setText(R.string.text_published);
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(mContext, EditStoryStudioActivity.class);
                    intent.putExtra(STORY_INDEX, model.getStory_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        View itemView = layoutInflater.inflate(R.layout.item_write_studio,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerViewHolder {

        ImageView imageCover;
        TextView textTitle;
        TextView textStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imgItemCoverWriteStudio);
            textTitle = itemView.findViewById(R.id.txtItemTitleWriteStudio);
            textStatus = itemView.findViewById(R.id.txtItemStatusWriteStudio);
        }
    }
}
