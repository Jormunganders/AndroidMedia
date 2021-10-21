package me.juhezi.mediademo.letme

import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("pkds.do")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("pagesize") pagesize: Int
    ): Movies

}