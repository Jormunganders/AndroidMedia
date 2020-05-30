package me.juhezi.mediademo

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.facebook.soloader.SoLoader

class App : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
    }
}