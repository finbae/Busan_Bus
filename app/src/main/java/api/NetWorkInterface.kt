package api

import com.cookandroid.myproject.BusSystem
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NetWorkInterface {
    @GET("busStopList")
    suspend fun getBusSystem(@QueryMap param: HashMap<String, String>): BusSystem
}