package com.example.bethereorbesquare.entity;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Entity for storing colors. Objects of this class can be created from JSON response body.
 */
@Entity(tableName = "Colors")
public class CustomColor {

    /**
     * String representation of color's hex code, eg. "#FFFFFF"
     */
    @ColumnInfo(name = "hex")
    @SerializedName("hex")
    private String hex;

    /**
     * String representation of color name
     */
    @PrimaryKey
    @NonNull
    @SerializedName("name")
    private String name;

    /**
     * String representation of rgb, as gotten from JSON body. Eg. "(255, 0, 0)"
     */
    @ColumnInfo(name = "rgb")
    @SerializedName("rgb")
    private String rgb;

    /**
     * Basic constructor for {@code CustomColor}, apart from the 3 variables, sets {@code rgbInt} as well.
     * @param hex {@link CustomColor#hex}
     * @param name {@link CustomColor#name}
     * @param rgb {@link CustomColor#rgb}
     */
    public CustomColor(String hex, @NonNull String name, String rgb) {
        this.hex = hex;
        this.name = name;
        this.rgb = rgb;
    }

    /**
     * @return {@link CustomColor#hex}
     */
    public String getHex() {
        return hex;
    }

    /**
     * @param hex {@link CustomColor#hex}
     */
    public void setHex(String hex) {
        this.hex = hex;
    }

    /**
     * @return {@link CustomColor#name}
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * @param name {@link CustomColor#name}
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * @return {@link CustomColor#rgb}
     */
    public String getRgb() {
        return rgb;
    }

    /**
     * @param rgb {@link CustomColor#rgb}
     */
    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    /**
     * Int representation of rgb, gotten from passing {@code hex} to {@link Color#parseColor(String)} method
     */
    public int getRgbInt() {
        return Color.parseColor(hex);
    }

    /**
     * Overriden equals and hashCode methods, two {@link CustomColor} objects are equal if their {@code name}s match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomColor)) return false;
        CustomColor that = (CustomColor) o;
        return name.equals(that.name);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

