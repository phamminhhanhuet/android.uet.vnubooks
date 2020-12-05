package com.uet.android.mouspad.Adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uet.android.mouspad.Activity.UserPerform.UserActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class InformationActionAdapter extends RecyclerView.Adapter<InformationActionAdapter.ViewHolder> {

    private ArrayList<InformationAction> mInformationActions;
    private Context mContext;
    private Fragment mFragment;
    private int mRequestCode = -1;
    private ArrayList<String> mUserId;

    public void setUserId(ArrayList<String> mUserId) {
        this.mUserId = mUserId;
    }

    public void setRequestCode(int requestCode){
        this.mRequestCode = requestCode;
    }

    public int getRequestCode(){
        return this.mRequestCode;
    }

    public InformationActionAdapter(ArrayList<InformationAction> mInformationActions, Context mContext, Fragment fragment) {
        this.mInformationActions = mInformationActions;
        this.mContext = mContext;
        this.mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_row_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Uri uri = Uri.parse(mInformationActions.get(position).getAction_image());
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.mImageView);
        holder.mTextTitle.setText(mInformationActions.get(position).getAction_title());
        String description = mInformationActions.get(position).getAction_description();
        if(description.length() > 50){
            String trim = description.substring(0, 47);
            holder.mTextAction.setText(trim + "...");
        }else {
            holder.mTextAction.setText(mInformationActions.get(position).getAction_description());
        }
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mRequestCode){
                    case Constants
                            .GALLERY_REQUEST_CODE_FOR_COVER:
                    case Constants
                            .GALLERY_REQUEST_CODE_FOR_BACKGROUND:
                        ActivityUtils.startActivityToPickImage(mFragment,mRequestCode);
                        break;
                    case Constants.GALLERY_REQUEST_CODE_FOR_USER:
                        Intent intent = new Intent(mContext, UserActivity.class);
                        intent.putExtra(Constants.USER_ID, mUserId.get(position));
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                switch (mRequestCode){
                    case Constants
                            .GALLERY_REQUEST_CODE_FOR_COVER:
                    case Constants
                            .GALLERY_REQUEST_CODE_FOR_BACKGROUND:
                        ActivityUtils.startActivityToPickImage(mFragment,mRequestCode);
                        break;
                    case Constants.GALLERY_REQUEST_CODE_FOR_USER:
                        Intent intent = new Intent(mContext, UserActivity.class);
                        intent.putExtra(Constants.USER_ID, mUserId.get(position));
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }

    public void updateAdapterData(ArrayList<InformationAction> arrayList) {
        this.mInformationActions.clear();
        this.mInformationActions.addAll(arrayList);
        this.notifyDataSetChanged();
        switch (mRequestCode){
            case Constants
                    .GALLERY_REQUEST_CODE_FOR_COVER:
            case Constants
                    .GALLERY_REQUEST_CODE_FOR_BACKGROUND:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mInformationActions.size();
    }

    public class ViewHolder extends RecyclerViewHolder {
        ImageView mImageView ;
        TextView mTextTitle;
        TextView mTextAction;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgItemInformationCover);
            mTextTitle = itemView.findViewById(R.id.txtItemInformationTitle);
            mTextAction = itemView.findViewById(R.id.txtItemInformationActionDescription);
            mCardView = itemView.findViewById(R.id.cardViewItemInformation);
        }
    }
}
