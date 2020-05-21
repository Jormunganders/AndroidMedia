package me.juhezi.mediademo.workers

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import androidx.work.WorkerParameters
import me.juhezi.mediademo.ScriptC_grayscale

class GrayScaleFilterWorker(context: Context, parameters: WorkerParameters) :
    BaseFilterWorker(context, parameters) {

    override fun applyFilter(input: Bitmap): Bitmap {
        var rsContext: RenderScript? = null
        try {
            val output = Bitmap.createBitmap(input.width, input.height, input.config)
            rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG)
            val inAlloc = Allocation.createFromBitmap(rsContext, input)
            val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
            val grayscale = ScriptC_grayscale(rsContext)
            grayscale._script = grayscale
            grayscale._width = input.width.toLong()
            grayscale._height = input.height.toLong()
            grayscale._in = inAlloc
            grayscale._out = outAlloc
            grayscale.invoke_filter()
            outAlloc.copyTo(output)
            return output
        } finally {
            rsContext?.finish()
        }
    }
}