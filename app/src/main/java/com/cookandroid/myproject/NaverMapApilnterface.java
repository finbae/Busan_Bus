package com.cookandroid.myproject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NaverMapApilnterface {
    @GET("start.php")
    Call<NaverMapItem> getMapData();
}
