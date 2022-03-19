package me.juhezi.sub.noxus

import android.app.Application
import android.content.Context
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager

class NoxusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FtpConfigManager.init(this)
    }

}