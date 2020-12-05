package com.uet.android.mouspad.Ebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uet.android.mouspad.R;

import java.util.HashMap;
import java.util.List;


public class EpubBookAdapter extends  RecyclerView.Adapter<EpubBookAdapter.BookViewHolder> {
    private final List<Integer> mBookIds;
    private final EbookDatabase mDB;
    private final Context mContext;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private HashMap<Integer,BookViewHolder> mHolderlist;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EpubBookAdapter(Context context, EbookDatabase db, List<Integer> bookIds) {
        mContext = context;
        mBookIds = bookIds;
        mDB = db;
        setHasStableIds(true);
        mHolderlist = new HashMap<>();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void notifyItemIdRemoved(long id) {
        int pos = mBookIds.indexOf((int)id);
        if (pos>=0) {
            mBookIds.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void notifyItemIdChanged(long id) {
        int pos = mBookIds.indexOf((int)id);
        if (pos>=0) {
            notifyItemChanged(pos);
        }
    }

    public void setBooks(List<Integer> bookIds) {
        int size = mBookIds.size();
        mBookIds.clear();
        notifyItemRangeRemoved(0, size);
        mBookIds.addAll(bookIds);
        notifyItemRangeInserted(0, mBookIds.size());
    }

    @Override
    public long getItemId(int position) {
        return mBookIds.get(position);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public EpubBookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup listEntry = (ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.item_epub_book_list, parent, false);
        return new BookViewHolder(listEntry);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        int bookid = mBookIds.get(position);
        EbookDatabase.BookRecord book = mDB.getBookRecord(bookid);

        if (book != null && book.filename != null) {
            holder.mTitleView.setText(EpubListActivity.maxlen(book.title, 120));
            holder.mAuthorView.setText(EpubListActivity.maxlen(book.author, 50));
            long lastread = book.lastread;
            long time = lastread;
            int text;
            if (book.status== EbookDatabase.STATUS_DONE) {
                text = R.string.book_status_completed;
            } else if (book.status== EbookDatabase.STATUS_LATER) {
                time = 0;
                text = R.string.book_status_later;
            } else if (lastread>0 && book.status== EbookDatabase.STATUS_STARTED) {
                text = R.string.book_viewed_on;
            } else {
                time = book.added;
                text = R.string.book_added_on;
            }

            CharSequence rtime = android.text.format.DateUtils.getRelativeTimeSpanString(time);
            holder.mStatusView.setTextSize(12);
            if (text==R.string.book_viewed_on) {
                holder.mStatusView.setTextSize(14);
            }
            holder.mStatusView.setText(mContext.getString(text, rtime));
            holder.mBookEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickListener!=null) {
                        mOnClickListener.onClick(view);
                    }
                }
            });

        } else {
            holder.mTitleView.setText("Error with " + bookid);
            holder.mAuthorView.setText("Error");
            holder.mStatusView.setText("");
        }

        holder.mBookEntry.setTag(bookid);
        holder.mBookEntry.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnLongClickListener!=null) {
                    return mOnLongClickListener.onLongClick(view);
                }
                return false;
            }
        });

        if(!mHolderlist.containsKey(position)){
            mHolderlist.put(position,holder);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBookIds.size();
    }

    public BookViewHolder getViewByPosition(int position) {
        return mHolderlist.get(position);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ViewGroup mBookEntry;
        final TextView mTitleView;
        final TextView mAuthorView;
        final TextView mStatusView;
        BookViewHolder(ViewGroup listEntry) {
            super(listEntry);
            mBookEntry = listEntry;
            mTitleView = listEntry.findViewById(R.id.book_title);
            mAuthorView = listEntry.findViewById(R.id.book_author);
            mStatusView = listEntry.findViewById(R.id.book_status);
        }
    }

}

