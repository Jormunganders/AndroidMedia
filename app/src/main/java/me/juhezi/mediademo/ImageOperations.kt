package me.juhezi.mediademo

import android.content.Context
import android.net.Uri
import androidx.work.*
import me.juhezi.mediademo.workers.*

internal class ImageOperations private constructor(val continuation: WorkContinuation) {

    internal class Builder(private val mContext: Context, private val mImageUri: Uri) {
        private var mApplyWaterColor: Boolean = false
        private var mApplyGrayScale: Boolean = false
        private var mApplyBlur: Boolean = false
        private var mApplySave: Boolean = false

        fun setApplyWaterColor(applyWaterColor: Boolean): Builder {
            mApplyWaterColor = applyWaterColor
            return this
        }

        fun setApplyGrayScale(applyGrayScale: Boolean): Builder {
            mApplyGrayScale = applyGrayScale
            return this
        }

        fun setApplyBlur(applyBlur: Boolean): Builder {
            mApplyBlur = applyBlur
            return this
        }

        fun setApplySave(applySave: Boolean): Builder {
            mApplySave = applySave
            return this
        }

        fun build(): ImageOperations {
            var hasInputData = false
            var continuation = WorkManager.getInstance(mContext)
                .beginUniqueWork(
                    Constants.IMAGE_MANIPULATION_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequest.from(CleanupWorker::class.java)
                )

            if (mApplyWaterColor) {

                val waterColor = OneTimeWorkRequestBuilder<WaterColorFilterWorker>()
                    .setInputData(createInputData())
                    .build()
                continuation = continuation.then(waterColor)
                hasInputData = true
            }

            if (mApplyGrayScale) {
                val grayScaleBuilder = OneTimeWorkRequestBuilder<GrayScaleFilterWorker>()
                if (!hasInputData) {
                    grayScaleBuilder.setInputData(createInputData())
                    hasInputData = true
                }
                val grayScale = grayScaleBuilder.build()
                continuation = continuation.then(grayScale)
            }

            if (mApplyBlur) {
                val blurBuilder = OneTimeWorkRequestBuilder<BlurEffectFilterWorker>()
                if (!hasInputData) {
                    blurBuilder.setInputData(createInputData())
                    hasInputData = true
                }
                val blur = blurBuilder.build()
                continuation = continuation.then(blur)
            }

            if (mApplySave) {
                val save = OneTimeWorkRequestBuilder<SaveImageToGalleryWorker>()
                    .setInputData(createInputData())
                    .addTag(Constants.TAG_OUTPUT)
                    .build()
                continuation = continuation.then(save)
            }
            return ImageOperations(continuation)
        }

        private fun createInputData(): Data {
            return workDataOf(Constants.KEY_IMAGE_URI to mImageUri.toString())
        }
    }
}