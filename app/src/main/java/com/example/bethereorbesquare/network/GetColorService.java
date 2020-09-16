package com.example.bethereorbesquare.network;

import com.example.bethereorbesquare.model.CustomColor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetColorService {
    //2. greskica je sto si tu postavio da je path /
    //fora je da se path koji se postavi nadoda na base url koji je postavljen u RetrofitInstance
    //ako to spojimo s prvom greskom dobio bi sljedeci link https://goo.gl/gEhgzs// i ako ga otvoris dobijes Invalid Dynamic Link
    @GET("/gEhgzs/")
    Call<List<CustomColor>> getColorData(); // posto ti response vraca listu CustomColor objekata ne treba ti novi CustomColorList objekt koji drzi tu listu
//    [
//        {
//            "hex": "#EFDECD",
//            "name": "Almond",
//            "rgb": "(239, 222, 205)"
//        },
//        {
//            "hex": "#CD9575",
//            "name": "Antique Brass",
//            "rgb": "(205, 149, 117)"
//        },
//        {
//            "hex": "#FDD9B5",
//            "name": "Apricot",
//            "rgb": "(253, 217, 181)"
//        },
//        {
//            "hex": "#78DBE2",
//            "name": "Aquamarine",
//            "rgb": "(120, 219, 226)"
//        } ...
//    ]

    //u sljedecem responsu bi ti trebao CustomColorList objekt za dohvat liste
//    {
//        "Colors": [
//            {
//                "hex": "#EFDECD",
//                "name": "Almond",
//                "rgb": "(239, 222, 205)"
//            },
//            {
//                "hex": "#CD9575",
//                "name": "Antique Brass",
//                "rgb": "(205, 149, 117)"
//            },
//            {
//                "hex": "#FDD9B5",
//                "name": "Apricot",
//                "rgb": "(253, 217, 181)"
//            },
//            {
//                "hex": "#78DBE2",
//                "name": "Aquamarine",
//                "rgb": "(120, 219, 226)"
//            } ...
//        ]
//    }

    //takoder najbolje ti je postavljat iste nazive kao u responsu jer se zna desit da je i to uzrok ne dohvacanja podataka
//    public class CustomColorList {
//
//        @SerializedName("Colors")
//        private List<CustomColor> colorsList; - znaci tu postaviti Colors umjesto colorsList
}
