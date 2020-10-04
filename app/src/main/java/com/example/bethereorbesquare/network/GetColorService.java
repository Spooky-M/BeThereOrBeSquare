package com.example.bethereorbesquare.network;

import com.example.bethereorbesquare.entity.CustomColor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Service which uses {@link GET} method to fetch a list of colors.
 */
public interface GetColorService {

    /**
     * Derives a list of colors from GET response's body.
     * @return Retrofit's {@link Call} object containing a list of colors.
     */
    @GET("/gEhgzs/")
    Call<List<CustomColor>> getColorData();
}
