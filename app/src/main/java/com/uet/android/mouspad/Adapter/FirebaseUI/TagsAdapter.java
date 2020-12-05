package com.uet.android.mouspad.Adapter.FirebaseUI;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.Tag;
import com.uet.android.mouspad.R;

public class TagsAdapter  extends FirestoreRecyclerAdapter<Tag, TagsAdapter.ViewHolder> {

    public TagsAdapter(@NonNull FirestoreRecyclerOptions<Tag> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tag model) {
        holder.tagbutton.setText(model.getTitle());
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerViewHolder {

        public Button tagbutton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagbutton = itemView.findViewById(R.id.btnItemTag);
        }
    }
}
