package com.example.bethereorbesquare.network;

import com.example.bethereorbesquare.model.CustomColorList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetColorService {
    @GET("/")
    Call<CustomColorList> getColorData();
}
