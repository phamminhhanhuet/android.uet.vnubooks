package com.uet.android.mouspad.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.BookPerfrom.YoutubeSearchActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;


import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class YoutubeSearchAdapter extends RecyclerView.Adapter<YoutubeSearchAdapter.ViewHolder> {
    private ArrayList<InformationAction> mInformationActions;
    private Activity mActivity;
    private ArrayList<String> mIds;
    private String choosenId ="";

    public void setIds(ArrayList<String> mIds) {
        this.mIds = mIds;
    }

    public String getChoosenId(){
        return this.choosenId;
    }

    public YoutubeSearchAdapter(ArrayList<InformationAction> mInformationActions, Activity mActivity) {
        this.mInformationActions = mInformationActions;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public YoutubeSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_row_update_square, parent, false);
        return new YoutubeSearchAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull YoutubeSearchAdapter.ViewHolder holder, final int position) {
        Uri uri = Uri.parse(mInformationActions.get(position).getAction_image());
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenId = mIds.get(position);
                YoutubeSearchActivity.isChoosen = true;
                Toast.makeText(mActivity, "choose " + choosenId, Toast.LENGTH_LONG).show();
            }
        });
        holder.mTextTitle.setText(mInformationActions.get(position).getAction_title());
       String description = mInformationActions.get(position).getAction_description();
       if(description.length() > 50){
           String trim = description.substring(0, 47);
           holder.mTextAction.setText(trim + "...");
       }else {
           holder.mTextAction.setText(description);
       }
     //   holder.mTextAction.setText(mInformationActions.get(position).getAction_description());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                choosenId = mIds.get(position);
               // YoutubeSearchActivity.isChoosen = true;
                Toast.makeText(mActivity, "choose " + choosenId, Toast.LENGTH_LONG).show();

                Intent data = new Intent();
                data.putExtra(Constants.YOUTUBE_RESULT_ID, choosenId);
                mActivity.setResult(RESULT_OK, data);
                data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.setResult(RESULT_OK,data);
                mActivity.finish();
            }
        });
    }

    public void updateAdapterData(ArrayList<InformationAction> arrayList) {
        this.mInformationActions.clear();
        this.mInformationActions.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mInformationActions.size();
    }

    public class ViewHolder extends RecyclerViewHolder {
        ImageView mImageView ;
        TextView mTextTitle;
        TextView mTextAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgItemInformationCover);
            mTextTitle = itemView.findViewById(R.id.txtItemInformationTitle);
            mTextAction = itemView.findViewById(R.id.txtItemInformationActionDescription);
        }
    }
}
