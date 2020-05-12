package me.juhezi.mediademo.media.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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

// 使用 id 拼出 content uri
fun getContentUri(id: String) = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    .buildUpon().appendPath(id).build()
