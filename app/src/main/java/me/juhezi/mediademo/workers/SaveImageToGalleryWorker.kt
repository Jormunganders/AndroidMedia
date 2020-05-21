package me.juhezi.mediademo.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import me.juhezi.mediademo.Constants
import me.juhezi.mediademo.media.utils.loge
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToGalleryWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    companion object {
        private const val TAG = "SaveImageToGalleryWorker"
        private const val TITLE = "Filtered Image"
        private val DATE_FORMATTER =
            SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())
    }

    override fun doWork(): Result {
        val resolver = applicationContext.contentResolver
        try {
            val resourceUri = inputData.getString(Constants.KEY_IMAGE_URI)
            val bitmap =
                BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))
            val imageUrl = MediaStore.Images.Media.insertImage(
                resolver, bitmap, TITLE, DATE_FORMATTER.format(Date())
            )
            if (TextUtils.isEmpty(imageUrl)) {
                loge(TAG, "Writing to MediaStore failed")
                return Result.failure()
            }
            val output = Data.Builder()
                .putString(Constants.KEY_IMAGE_URI, imageUrl)
                .build()
            return Result.success(output)
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to save image to Gallery", exception)
            return Result.failure()
        }
    }

}