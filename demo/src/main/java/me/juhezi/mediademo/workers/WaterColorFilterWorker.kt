package me.juhezi.mediademo.workers

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import androidx.work.WorkerParameters
import me.juhezi.mediademo.ScriptC_waterColorEffect

class WaterColorFilterWorker(context: Context, parameters: WorkerParameters) :
    BaseFilterWorker(context, parameters) {

    override fun applyFilter(input: Bitmap): Bitmap {
        var rsContext: RenderScript? = null
        try {
            val output = Bitmap.createBitmap(
                input.width, input.height,
                input.config
            )
            rsContext = RenderScript.create(
                applicationContext,
                RenderScript.ContextType.DEBUG
            )
            val inAlloc = Allocation.createFromBitmap(rsContext, input)
            val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
            val oilFilterEffect = ScriptC_waterColorEffect(rsContext)
            oilFilterEffect._script = oilFilterEffect
            oilFilterEffect._width = input.width.toLong()
            oilFilterEffect._height = input.height.toLong()
            oilFilterEffect._in = inAlloc
            oilFilterEffect._out = outAlloc
            oilFilterEffect.invoke_filter()
            outAlloc.copyTo(output)
            return output
        } finally {
            rsContext?.finish()
        }
    }

}