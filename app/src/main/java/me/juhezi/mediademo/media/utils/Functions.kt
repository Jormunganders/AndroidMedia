package me.juhezi.mediademo.media.utils

import android.util.Log
import me.juhezi.mediademo.BuildConfig

const val LOG = true

fun logi(tag: String, message: String) {
    if (LOG || BuildConfig.DEBUG) {
        Log.i(tag, message)
    }
}

fun loge(tag: String, message: String) {
    if (LOG || BuildConfig.DEBUG) {
        Log.e(tag, message)
    }
}

fun logw(tag: String, message: String) {
    if (LOG || BuildConfig.DEBUG) {
        Log.w(tag, message)
    }
}

fun logd(tag: String, message: String) {
    if (LOG || BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}