package me.juhezi.sub.noxus.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager

class MainViewModel : ViewModel() {

    // 页面数据
    val liteConfigsLiveData: LiveData<List<Any>> by lazy {
        LiveDataReactiveStreams.fromPublisher(
            FtpConfigManager.getLiteConfig().toFlowable(BackpressureStrategy.BUFFER)
        )
    }

}