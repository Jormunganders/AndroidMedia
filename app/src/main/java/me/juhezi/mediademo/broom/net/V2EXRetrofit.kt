package me.juhezi.mediademo.broom.net

import me.juhezi.mediademo.StringConvertFactory
import me.juhezi.mediademo.common.net.OkHttpClientBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

object V2EXRetrofit {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(V2EXApiService.BASE_URL)
            .client(OkHttpClientBuilder.build())
            .addConverterFactory(StringConvertFactory())    // todo 换成 Gson
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    fun get() = retrofit.create(V2EXApiService::class.java)

}