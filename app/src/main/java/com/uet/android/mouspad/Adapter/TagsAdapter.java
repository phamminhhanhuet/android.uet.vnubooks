package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.R;

import java.util.ArrayList;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    private List<String> mTags;
    private Context mContext;


    public TagsAdapter(List<String> mTags, Context mContext) {
        this.mTags = mTags;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.item_tag,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tagbutton.setText(mTags.get(position));
        holder.tagbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mTags.isEmpty()) return 0;
        else return mTags.size();
    }

    public void updateAdapterData(ArrayList<String> arrayList) {
        this.mTags.clear();
        this.mTags.addAll(arrayList);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerViewHolder {

        public Button tagbutton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagbutton = itemView.findViewById(R.id.btnItemTag);
        }
    }
}
