package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.Model.Inbox;
import com.uet.android.mouspad.R;

import java.util.ArrayList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private ArrayList<Inbox> mInboxs;
    private String mImageUrl;

    private String currentUserId;
    private boolean isSender = false;

    public InboxAdapter(Context mContext, ArrayList<Inbox> mInboxs, String imageurl){
        this.mInboxs = mInboxs;
        this.mContext = mContext;
        this.mImageUrl = imageurl;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view =layoutInflater.inflate(R.layout.item_chat_right, parent, false);
            return new ViewHolder(view);
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view =layoutInflater.inflate(R.layout.item_chat_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inbox chat = mInboxs.get(position);
        holder.textMessage.setText(chat.getMessage());

        Glide.with(mContext).load(mImageUrl).into(holder.imageProfile);

        if (position == mInboxs.size()-1){
            if (true){
                holder.textSeen.setText("Seen");
            } else {
                holder.textSeen.setText("Delivered");
            }
        } else {
            holder.textSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mInboxs.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mInboxs.get(position).getSender().equals(currentUserId)){
            isSender = true;
            return MSG_TYPE_RIGHT;
        } else {
            isSender = false;
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerViewHolder {
        public TextView textMessage;
        public ImageView imageProfile;
        public TextView textSeen;

        public ViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessageContentInbox);
            imageProfile = itemView.findViewById(R.id.imgProfileInbox);
            textSeen = itemView.findViewById(R.id.textSeenInbox);
        }
    }
}
