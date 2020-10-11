package com.example.bethereorbesquare.entity;


import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomWarnings;

import java.util.Objects;

/**
 * A class representing a rectangle which has an id, a list index, color and can be selected.
 * Two rectangles are equal if their ids match.
 */
@Entity(tableName = "Rectangles")
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
public class Rectangle {

    /**
     * Rectangle's unique id, used in {@link Rectangle#equals(Object)} method,
     * autogenerated and autoincremented by Room database.
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * Rectangle's index, must be a positive value
     */
    @ColumnInfo(name = "index")
    private int index;

    /**
     * Current color of this rectangle
     */
    @Embedded(prefix = "color_")
    private CustomColor color;

    /**
     * Current selection status of this rectangle
     */
    @ColumnInfo(name = "selected")
    private boolean selected = false;

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
        if (!(o instanceof Rectangle)) return false;
        Rectangle rectangle = (Rectangle) o;
        return id == rectangle.id;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
