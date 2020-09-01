package com.example.bethereorbesquare.shapes;


import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Rectangle implements Parcelable {

    private int left, top, right, bottom;
    private int color;
    private boolean isSelected;

    public Rectangle(int left, int top, int right, int bottom, int color) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.color = color;
    }

    public Rectangle(Parcel in){
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
        color = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Rectangle> CREATOR = new Creator<Rectangle>() {
        @Override
        public Rectangle createFromParcel(Parcel in) {
            return new Rectangle(in);
        }

        @Override
        public Rectangle[] newArray(int size) {
            return new Rectangle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(left);
        dest.writeInt(top);
        dest.writeInt(right);
        dest.writeInt(bottom);
        dest.writeInt(color);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    public void setDimensions(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return left == rectangle.left &&
                top == rectangle.top &&
                right == rectangle.right &&
                bottom == rectangle.bottom &&
                isSelected == rectangle.isSelected;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(left, top, right, bottom, isSelected);
    }
}
