package com.example.bethereorbesquare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomColorList {

    @SerializedName("Colors")
    private List<CustomColor> colorsList;

    public List<CustomColor> getColorsList() {
        return colorsList;
    }

    public void setColorsList(List<CustomColor> colorsList) {
        this.colorsList = colorsList;
    }
}
