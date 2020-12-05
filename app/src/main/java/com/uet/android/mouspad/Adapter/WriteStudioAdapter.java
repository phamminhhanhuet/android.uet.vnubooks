package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.EditStoryStudioActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.R;

import java.util.ArrayList;
import java.util.List;

import static com.uet.android.mouspad.Utils.Constants.STORY_INDEX;

public class WriteStudioAdapter  extends RecyclerView.Adapter<WriteStudioAdapter.ViewHolder> {
    private ArrayList<Story> mStories;
    private Context mContext;

    public WriteStudioAdapter(ArrayList<Story> mStories, Context mContext) {
        this.mStories = mStories;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_write_studio,parent,false);
        return new ViewHolder(itemView);
    }
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
        if(mStories.get(position).getPublished()){
            holder.textStatus.setText(R.string.text_published);
        } else {
            holder.textStatus.setText(R.string.text_drafts);
        }
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

        holder.btnFormat.setText(mStories.get(position).getFormat());
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

    private void showOptionMenu (final Boolean status, View view, final int position) {
        List<String> menus = new ArrayList<>();
        if(status) {
            menus.add("Unpublish");
            menus.add("Delete");
        } else {
            menus.add("Publish");
            menus.add("Delete");
        }
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        for (int i = 0 ; i < menus.size(); i ++) {
            String action = menus.get(i);
            MenuItem m = popupMenu.getMenu().add(action);
            if(action.equals("Delete")){
                m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mStories.remove(position);
                        notifyDataSetChanged();
                        return true;
                    }
                });
            } else if(action.equals("") )
            m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(status){
                        mStories.get(position).setPublished(false);
                        mStories.remove(position);
                        notifyDataSetChanged();
                    } else {
                        mStories.get(position).setPublished(true);
                        mStories.remove(position);
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
        popupMenu.show();
    }

    public class ViewHolder extends RecyclerViewHolder {

        ImageView imageCover;
        TextView textTitle;
        TextView textStatus;
        CardView cardView;
        Button btnDelete ;
        Button btnFormat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imgItemCoverWriteStudio);
            textTitle = itemView.findViewById(R.id.txtItemTitleWriteStudio);
            textStatus = itemView.findViewById(R.id.txtItemStatusWriteStudio);
            cardView = itemView.findViewById(R.id.cardViewItemWriteStudio);
            btnDelete = itemView.findViewById(R.id.delete);
            btnFormat = itemView.findViewById(R.id.btnFormatWriteStudio);
        }
    }
}
