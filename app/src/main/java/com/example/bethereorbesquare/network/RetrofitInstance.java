package com.example.bethereorbesquare.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class for building instaces of {@link Retrofit} which will communicate with <a href="https://goo.gl">https://goo.gl<a/>
 */
public class RetrofitInstance {

    private static Retrofit retrofit;
    private static Gson gson = new GsonBuilder().setLenient().create();

    private static final String BASE_URL =
            "https://goo.gl";

    /**
     * Factory method for {@link Retrofit} objects
     * @return new object built by {@link retrofit2.Retrofit.Builder}
     */
    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
