package me.juhezi.slowcut.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import me.juhezi.slow_cut_base.util.CommonUtil
import me.juhezi.slow_cut_base.widget.corner_shadow.ArrowPosition
import me.juhezi.slow_cut_base.widget.corner_shadow.ShadowRounded
import me.juhezi.slow_cut_base.widget.corner_shadow.ShadowRoundedImpl
import me.juhezi.slowcut.R

class CornerShadowConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ShadowRounded by ShadowRoundedImpl(
        Color.WHITE,
        CommonUtil.dimen(R.dimen.corner_shadow_view_shadow_radius).toFloat(),
        CommonUtil.color(R.color.s_6200EF_10),
        CommonUtil.dimen(R.dimen.corner_shadow_view_shadow_radius).toFloat(),
        0f,
        0f,
        0f,
        ArrowPosition.NONE
    ) {

    init {
        bindView(this)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setPadding(CommonUtil.dimen(R.dimen.corner_shadow_view_shadow_padding))
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.let { drawX(it) }
        super.dispatchDraw(canvas)
    }

}