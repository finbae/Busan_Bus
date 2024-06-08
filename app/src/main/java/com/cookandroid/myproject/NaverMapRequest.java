package com.cookandroid.myproject;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NaverMapRequest {
    // Base URL
    public static String BASE_URL = "http://catsong1.dothome.co.kr/";

    private static Retrofit retrofit;
    public static Retrofit getClient(){

        if(retrofit == null){
            retrofit = new Retrofit.Builder() // retrofit 객체 생성
                    .baseUrl(BASE_URL) // BASE_URL로 통신
                    .addConverterFactory(GsonConverterFactory.create()) // gson-converter로 데이터 parsing
                    .build();
        }
        return retrofit;
    }
}