package com.example.bethereorbesquare.shapes;


import com.example.bethereorbesquare.model.CustomColor;

public class Rectangle {

    private int id = -1;
    private int index;
    private CustomColor color;
    private boolean selected = false;

    public Rectangle(int id, int index, CustomColor color, boolean selected) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
