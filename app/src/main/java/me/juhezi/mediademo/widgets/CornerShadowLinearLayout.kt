package me.juhezi.mediademo.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout

class CornerShadowLinearLayout : LinearLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private val mHelper = ShadowRoundedHelper(
        Color.WHITE,
        30f,
        Color.RED,
        20f,
        36f,
        64f,
        12f,
        ShadowRoundedHelper.ArrowPosition.END,
        object : ShadowRoundedHelper.Callback {
            override fun onInvalidate() {
                invalidate()
            }

            override fun getHeight(): Float {
                return this@CornerShadowLinearLayout.height.toFloat()
            }

            override fun getWidth(): Float {
                return this@CornerShadowLinearLayout.width.toFloat()
            }

            override fun getPaddingStart(): Float {
                return this@CornerShadowLinearLayout.paddingStart.toFloat()
            }

            override fun getPaddingEnd(): Float {
                return this@CornerShadowLinearLayout.paddingEnd.toFloat()
            }

            override fun getPaddingTop(): Float {
                return this@CornerShadowLinearLayout.paddingTop.toFloat()
            }

            override fun getPaddingBottom(): Float {
                return this@CornerShadowLinearLayout.paddingBottom.toFloat()
            }

        }
    )

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.let { mHelper.draw(it) }
        super.dispatchDraw(canvas)
    }

}