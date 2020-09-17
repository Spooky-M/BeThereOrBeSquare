package com.example.bethereorbesquare.adapter;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public class FieldItemDetailsLookup extends ItemDetailsLookup<Long> {

    private final RecyclerView recyclerView;

    public FieldItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View v = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if(v != null) {
            FieldAdapter.FieldViewHolder viewHolder = (FieldAdapter.FieldViewHolder) recyclerView.getChildViewHolder(v);
            return viewHolder.getItemDetails();
        }
        return null;
    }
}
