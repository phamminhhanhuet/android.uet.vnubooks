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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uet.android.mouspad.Activity.UserPerform.InboxActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.Model.Inbox;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class InboxListAdapter  extends RecyclerView.Adapter<InboxListAdapter.ViewHolder> {
    private Context mContext;
    private FirebaseFirestore mFirebaseFirestore;
    private ArrayList<User> mUsers;
    private boolean ischat;

    private String theLastMessage;

    public InboxListAdapter(Context mContext, ArrayList<User> mUsers, boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_user_inbox_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getAccount());
        if (user.getAvatar().equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getAvatar()).into(holder.profileImage);
        }

        if (ischat){
            lastMessage(user.getUser_id(), holder.lastMessage);
        } else {
            holder.lastMessage.setVisibility(View.GONE);
        }

        if (ischat){
            if (true){
                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            } else {
                holder.imgOn.setVisibility(View.GONE);
                holder.imgOff.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, InboxActivity.class);
                intent.putExtra(Constants.USER_ID, user.getUser_id());
                mContext.startActivity(intent);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, InboxActivity.class);
                intent.putExtra(Constants.USER_ID, user.getUser_id());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerViewHolder {

        public TextView username;
        public ImageView profileImage;
        private ImageView imgOn;
        private ImageView imgOff;
        private TextView lastMessage;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameItemInboxList);
            profileImage = itemView.findViewById(R.id.imgProfileItemInboxList);
            imgOn = itemView.findViewById(R.id.imgOnItemInboxList);
            imgOff = itemView.findViewById(R.id.imgOffItemInboxList);
            lastMessage = itemView.findViewById(R.id.lastMessageItemInboxList);
            cardView = itemView.findViewById(R.id.cardViewInboxList);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView textview){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query =  mFirebaseFirestore.collection("inboxs/"  +firebaseUser.getUid() + "/contain").orderBy("timestamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Inbox inbox = document.toObject(Inbox.class);
                    if (inbox.getReceiver().equals(firebaseUser.getUid()) && inbox.getSender().equals(userid) ||
                            inbox.getReceiver().equals(userid) && inbox.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = inbox.getMessage();
                        break;
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        textview.setText("No Message");
                        break;

                    default:
                        textview.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }
        });
    }
}
