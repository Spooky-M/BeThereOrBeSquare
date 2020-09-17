package com.example.bethereorbesquare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomColorList {

    @SerializedName("colors")
    private List<CustomColor> colors;

    public List<CustomColor> getColorsList() {
        return colors;
    }

    public void setColorsList(List<CustomColor> colors) {
        this.colors = colors;
    }
}
