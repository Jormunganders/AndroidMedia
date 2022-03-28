package me.juhezi.sub.noxus

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import me.juhezi.slow_cut_base.core.SlowCutBaseApplication
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager

@HiltAndroidApp
class NoxusApplication : SlowCutBaseApplication() {

    override fun onCreate() {
        super.onCreate()
        FtpConfigManager.init(this)
    }

}