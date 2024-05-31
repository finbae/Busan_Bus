package api

//import android.icu.util.TimeUnit
import com.cookandroid.myproject.BuildConfig
import com.cookandroid.myproject.BusSystem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetWorkClient {
    private const val BUSSYSTEM_BASE_URL = "http://apis.data.go.kr/6260000/BusanBIMS/"

    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG)
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(interceptor)
                .build()
    }
    private val BusSystemRetrofit = Retrofit.Builder()
            .baseUrl(BUSSYSTEM_BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(
                    createOkHttpClient()
            ).build()

    val dustNetWork: NetWorkInterface = BusSystemRetrofit.create(NetWorkInterface::class.java)

}