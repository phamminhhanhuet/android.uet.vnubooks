package com.uet.android.mouspad.Adapter.ViewHolder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.uet.android.mouspad.EventInterface.ItemClickListener;

public class RecyclerViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
{
    private ItemClickListener itemClickListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }
}


