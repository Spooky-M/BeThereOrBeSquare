package com.example.bethereorbesquare.shapes;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.bethereorbesquare.model.CustomColor;

import java.util.Objects;

public class Rectangle {

    private long id = -1;
    private int index;
    private CustomColor color;
    private boolean selected = false;

    public Rectangle(long id, int index, CustomColor color, boolean selected) {
        this.id = id;
        this.index = index;
        this.color = color;
        this.selected = selected;
    }

    public Rectangle(int index, CustomColor color) {
        this.index = index;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public CustomColor getColor() {
        return color;
    }

    public void setColor(CustomColor color) {
        this.color = color;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return id == rectangle.id;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
