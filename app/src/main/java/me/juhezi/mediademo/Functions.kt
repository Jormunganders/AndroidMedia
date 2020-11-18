package me.juhezi.mediademo

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileDescriptor


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

fun Boolean?.isTrue() = this ?: false

fun getVideoUriFromPath(context: Context, path: String): Uri? {
    val cursor = context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Video.Media._ID),
        MediaStore.Video.Media.DATA + "=? ",
        arrayOf(path),
        null
    )
    return if (cursor != null && cursor.moveToFirst()) {
        val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val baseUri = Uri.parse("content://media/external/video/media")
        Uri.withAppendedPath(baseUri, "" + id)
    } else {
        // 如果图片不在手机的共享图片数据库，就先把它插入。
        if (File(path).exists()) {
            val values = ContentValues()
            values.put(MediaStore.Video.Media.DATA, path)
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            null
        }
    }
}

fun getFDFromUri(context: Context, uri: Uri?): FileDescriptor? {
    if (uri == null)
        return null
    val parcelFileDescriptor: ParcelFileDescriptor =
        context.contentResolver.openFileDescriptor(uri, "r")!!
    return parcelFileDescriptor.fileDescriptor
}
