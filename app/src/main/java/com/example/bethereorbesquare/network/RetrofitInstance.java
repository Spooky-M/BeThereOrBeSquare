package com.example.bethereorbesquare.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static Gson gson = new GsonBuilder().setLenient().create();

    //1. problemcic je bio sto si tu stavio kompletan url https://goo.gl/gEhgzs/
    //kad ces radit sa retrofitom u base url je najbolje stavit protocol (http) i hostname (www.example.com)
    private static final String BASE_URL =
            "https://goo.gl";

    public static Retrofit getRetrofitInstance() {
        if( retrofit == null ) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(logging)
//                    .build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl( BASE_URL )
                    .addConverterFactory( GsonConverterFactory.create( gson ) )
//                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
