package com.example.bethereorbesquare.model;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

/**
 * Objects of this class can be created from JSON response body, as
 */
public class CustomColor {

    /**
     * String representation of color's hex code, eg. "#FFFFFF"
     */
    @SerializedName("hex")
    private String hex;

    /**
     * String representation of color name
     */
    @SerializedName("name")
    private String name;

    /**
     * String representation of rgb, as gotten from JSON body. Eg. "(255, 0, 0)"
     */
    @SerializedName("rgb")
    private String rgb;

    /**
     * Int representation of rgb, gotten from passing {@code hex} to {@link Color#parseColor(String)} method
     */
    private int rgbInt;

    /**
     * Basic constructor for {@code CustomColor}, apart from the 3 variables, sets {@code rgbInt} as well.
     * @param hex {@link CustomColor#hex}
     * @param name {@link CustomColor#name}
     * @param rgb {@link CustomColor#rgb}
     */
    public CustomColor(String hex, String name, String rgb) {
        this.hex = hex;
        this.name = name;
        this.rgb = rgb;
        this.rgbInt = Color.parseColor(hex);
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
    public String getName() {
        return name;
    }

    /**
     * @param name {@link CustomColor#name}
     */
    public void setName(String name) {
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
     * @return {@link CustomColor#rgbInt}
     */
    public int getRgbInt() {
        return rgbInt;
    }
}
