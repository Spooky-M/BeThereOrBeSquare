package com.example.bethereorbesquare.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.List;

public class FieldItemKeyProvider extends ItemKeyProvider<Long> {

    private final List<Rectangle> itemList;

    /**
     * Creates a new provider with the given scope.
     *
     * @param scope Scope can't be changed at runtime.
     */
    protected FieldItemKeyProvider(int scope, List<Rectangle> itemList) {
        super(scope);
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public int getPosition(@NonNull Long key) {
        int i = 0;
        for(Rectangle r : itemList) {
            if(r.getId() == key) return i;
            i++;
        }
        return RecyclerView.NO_POSITION;
    }
}
