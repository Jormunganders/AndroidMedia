package me.juhezi.mediademo.broom.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.trello.rxlifecycle4.android.ActivityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.juhezi.mediademo.R
import me.juhezi.mediademo.StringConvertFactory
import me.juhezi.mediademo.broom.net.V2EXApiService
import me.juhezi.mediademo.broom.net.V2EXRetrofit
import me.juhezi.mediademo.common.BaseActivity
import me.juhezi.mediademo.kuaishou.AsyncCacheLayoutInflater
import me.juhezi.mediademo.logi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class V2EXActivity : BaseActivity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            AsyncCacheLayoutInflater.getCacheOrInflate(
                this,
                R.layout.layout_activity_v2ex
            )
        )
        V2EXRetrofit.get()
            .requestHotTopic()
            .compose(bindToLifecycle())
            .subscribe {
                Log.i("Juhezi", "onCreate: CurrentThread: " + Thread.currentThread().name)
                Log.i("Juhezi", "onCreate: \n$it")
            }
    }

}