package me.juhezi.slow_cut_base.widget.corner_shadow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout

class CornerShadowLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    ShadowRounded by ShadowRoundedImpl(
        Color.WHITE,
        30f,
        Color.RED,
        20f,
        36f,
        64f,
        12f,
        ArrowPosition.BOTTOM
    ) {

    init {
        bindView(this)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.let { drawX(it) }
        super.dispatchDraw(canvas)
    }

}