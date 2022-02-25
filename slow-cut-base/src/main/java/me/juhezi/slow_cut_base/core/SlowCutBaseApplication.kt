package me.juhezi.slow_cut_base.core

import android.app.Application
import me.juhezi.slow_cut_base.GlobalConfig

class SlowCutBaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalConfig.setApplicationContext(this)
    }

}