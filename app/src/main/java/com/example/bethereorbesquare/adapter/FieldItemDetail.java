package com.example.bethereorbesquare.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class FieldItemDetail extends ItemDetailsLookup.ItemDetails<Long> {

    private final int adapterPosition;
    private final long selectionKey;

    public FieldItemDetail(int adapterPosition, long selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return selectionKey;
    }
}
