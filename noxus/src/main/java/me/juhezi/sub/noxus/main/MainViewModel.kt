package me.juhezi.sub.noxus.main

import androidx.lifecycle.ViewModel
import me.juhezi.sub.noxus.base.AcceptedLiveData
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager

class MainViewModel : ViewModel() {

    // 页面数据
    val liteConfigsLiveData: AcceptedLiveData<List<Any>> = AcceptedLiveData(emptyList())

    fun requestLiteConfig() {
        FtpConfigManager.getLiteConfig().subscribe(liteConfigsLiveData)
    }

}