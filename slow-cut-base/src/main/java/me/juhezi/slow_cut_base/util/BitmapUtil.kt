package me.juhezi.slow_cut_base.util

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtil {

    @JvmStatic
    fun resize(originBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        if (originBitmap.isRecycled) {
            return null
        }
        val originWidth = originBitmap.width
        val originHeight = originBitmap.height
        if (originHeight == 0 || originWidth == 0) {
            return null
        }
        val widthScale = newWidth.toFloat() / originWidth
        val heightScale = newHeight.toFloat() / originHeight
        val matrix = Matrix()
        matrix.postScale(widthScale, heightScale)
        return Bitmap.createBitmap(originBitmap, 0, 0, originWidth, originHeight, matrix, true)
    }

}