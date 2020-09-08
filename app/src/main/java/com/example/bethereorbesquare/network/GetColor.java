package com.example.bethereorbesquare.network;

import com.example.bethereorbesquare.model.CustomColor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetColor {
    @GET("retrofit-demo.php")
    Call<CustomColor> getEmployeeData(@Query("color_name") int colorName);
}
