package me.juhezi.mediademo.broom.net;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface V2EXApiService {

    String BASE_URL = "https://www.v2ex.com";

    @GET("api/topics/hot.json")
    Observable<String> requestHotTopic();

}

