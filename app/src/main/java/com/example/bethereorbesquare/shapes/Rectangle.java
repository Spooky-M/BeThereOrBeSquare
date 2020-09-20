package com.example.bethereorbesquare.shapes;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.bethereorbesquare.model.CustomColor;

import java.util.Objects;

/**
 * A class representing a rectangle which has an id, a list index, color and can be selected.
 * Two rectangles are equal if their ids match.
 */
public class Rectangle {

    /**
     * Rectangle's unique id, used in {@link Rectangle#equals(Object)} method. Must be positive, unless default.
     * When database autoincrements an integer, the ids start either from 0 or 1.
     * So a rectangle with default id value of -1 won't match any of the generated ids in the database.
     */
    private long id = -1;

    /**
     * Rectangle's index, must be a positive value
     */
    private int index;

    /**
     * Current color of this rectangle
     */
    private CustomColor color;

    /**
     * Current selection status of this rectangle
     */
    private boolean selected = false;

    /**
     * Full constructor, initialises all of the atributes of this rectangle
     * @param id {@link Rectangle#id}
     * @param index {@link Rectangle#index}
     * @param color {@link Rectangle#color}
     * @param selected {@link Rectangle#selected}
     */
    public Rectangle(long id, int index, CustomColor color, boolean selected) {
        this.id = id;
        this.index = index;
        this.color = color;
        this.selected = selected;
    }

    /**
     * Basic constructor for creating a template object
     * @param index {@link Rectangle#index}
     * @param color {@link Rectangle#color}
     */
    public Rectangle(int index, CustomColor color) {
        this.index = index;
        this.color = color;
    }

    /**
     * @return {@link Rectangle#index}
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index {@link Rectangle#index}
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return {@link Rectangle#color}
     */
    public CustomColor getColor() {
        return color;
    }

    /**
     * @param color {@link Rectangle#color}
     */
    public void setColor(CustomColor color) {
        this.color = color;
    }

    /**
     * @return {@link Rectangle#selected}
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected {@link Rectangle#selected}
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return {@link Rectangle#id}
     */
    public long getId() {
        return id;
    }

    /**
     * @param id {@link Rectangle#id}
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Overriden equals and hashCode methods, two {@link Rectangle} objects are equal if their {@code id}s match.
     */
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
