package com.cookandroid.myproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Retrofit_interface {
    @GET("start.php")
    Call<data_model> test_api_get();
}
//@Query("id") String id