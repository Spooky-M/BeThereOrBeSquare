package com.example.bethereorbesquare.model;

import com.google.gson.annotations.SerializedName;

public class CustomColor {

    @SerializedName("hex")
    private String hex;

    @SerializedName("name")
    private String name;

    @SerializedName("rgb")
    private String rgb;

    public CustomColor(String hex, String name, String rgb) {
        this.hex = hex;
        this.name = name;
        this.rgb = rgb;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }
}
