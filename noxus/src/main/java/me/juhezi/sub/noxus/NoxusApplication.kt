package me.juhezi.sub.noxus

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager

@HiltAndroidApp
class NoxusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FtpConfigManager.init(this)
    }

}