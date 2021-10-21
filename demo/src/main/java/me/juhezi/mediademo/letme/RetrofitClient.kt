package me.juhezi.mediademo.letme

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.17.114:8080/pagingserver_war/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getApi(clazz: Class<T>): T {
        return instance.create(clazz) as T
    }
}