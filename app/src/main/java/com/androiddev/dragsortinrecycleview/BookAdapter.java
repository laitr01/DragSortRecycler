package com.androiddev.dragsortinrecycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by admin on 10/9/2017.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ItemHolder> {
    private List<BookEntity> mBooks;
    public BookAdapter(List<BookEntity> bookEntities) {
        mBooks = bookEntities;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        BookEntity book = mBooks.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvDescription.setText(book.getDescription());
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public void removeBookAt(int i) {
        mBooks.remove(i);
    }

    public void addBookTo(int i, BookEntity book) {
        mBooks.add(i, book);
    }


    public List<BookEntity> getBooks() {
        return mBooks;
    }

    public BookEntity getBookAt(int from) {
        return mBooks.get(from);
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvDescription;

        public ItemHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvDescription = (TextView) itemView.findViewById(R.id.description);
        }

    }

}
