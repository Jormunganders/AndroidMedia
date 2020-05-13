package me.juhezi.mediademo.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import me.juhezi.mediademo.Constants
import me.juhezi.mediademo.media.utils.loge
import java.io.*
import java.util.*

abstract class BaseFilterWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val TAG = "BaseFilterWorker"
        const val ASSET_PREFIX = "file:///android_asset/"   // ? 作用

        @VisibleForTesting
        @Throws(IOException::class)
        fun inputStreamFor(
            context: Context,
            resourceUri: String
        ): InputStream? {

            return if (resourceUri.startsWith(ASSET_PREFIX)) {
                val assetManager = context.resources.assets
                assetManager.open(resourceUri.substring(ASSET_PREFIX.length))
            } else {
                val resolver = context.contentResolver
                resolver.openInputStream(Uri.parse(resourceUri))
            }

        }

    }

    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(Constants.KEY_IMAGE_URI)
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                loge(TAG, "Invalid input uri.")
                throw IllegalArgumentException("Invalid input uri.")
            }
            val context = applicationContext
            val inputStream = inputStreamFor(context, resourceUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val output = applyFilter(bitmap)
            val outputUri = writeBitmapToFile(applicationContext, output)
            return Result.success(
                workDataOf(Constants.KEY_IMAGE_URI to outputUri.toString())
            )
        } catch (fileNotFoundException: FileNotFoundException) {
            Log.e(
                TAG, "Failed to decode input stream",
                fileNotFoundException
            )
            throw RuntimeException(
                "Failed to decode input stream",
                fileNotFoundException
            )
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying filter", throwable)
            return Result.failure()
        }
    }

    abstract fun applyFilter(input: Bitmap): Bitmap

    @Throws(FileNotFoundException::class)
    private fun writeBitmapToFile(
        applicationContext: Context,
        bitmap: Bitmap
    ): Uri {
        val name = String.format(
            "filter-output-%s.png",
            UUID.randomUUID().toString()
        )
        val outputDir = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val outputFile = File(outputDir, name)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                }
            }
        }
        return Uri.fromFile(outputFile)
    }

}