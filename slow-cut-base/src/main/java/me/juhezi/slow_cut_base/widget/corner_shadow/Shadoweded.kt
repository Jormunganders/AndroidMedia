package me.juhezi.slow_cut_base.widget.corner_shadow

import android.graphics.*
import android.view.View

enum class ArrowPosition {
    NONE,   // 无箭头
    TOP,
    START,
    END,
    BOTTOM
}

interface ShadowRounded {

    val backgroundColor: Int
    val cornerRadius: Float
    val shadowColor: Int
    val shadowSize: Float
    val arrowHeight: Float
    val arrowWidth: Float
    val arrowCornerRadius: Float
    val arrowPosition: ArrowPosition

    fun setArrowOffset(offset: Float)

    fun setArrowVisible(visible: Boolean)

    fun drawX(canvas: Canvas)

    fun bindView(view: View)

}

class ShadowRoundedImpl(
    override val backgroundColor: Int,
    override val cornerRadius: Float,
    override val shadowColor: Int,
    override val shadowSize: Float,
    override val arrowHeight: Float,
    override val arrowWidth: Float,
    override val arrowCornerRadius: Float,
    override val arrowPosition: ArrowPosition
) : ShadowRounded {

    private var mView: View? = null

    private val shadowPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(
            shadowSize, 0f,
            0f, shadowColor
        )
    }

    private var arrowOffset = -1f
    private var arrowVisible = true
    private val path = Path()
    private val rectF = RectF()
    private val array = Array(5) {
        PointF()
    }

    /**            /\
     *           /   \
     * ________/   *  \______
     *
     * 如果箭头位置是 TOP 或者 BOTTOM，表示箭头长边中心(*)的 x 坐标距离左侧的距离
     * 如果是 START 或者 END，表示箭头中心(*)的 y 坐标距离顶部的距离
     */
    override fun setArrowOffset(offset: Float) {
        arrowOffset = offset
        mView?.invalidate()
    }

    /**
     * 如果 arrowPosition != NONE，那么 showArrow 为 false，只是隐藏箭头，但是位置还是会空出来的
     */
    override fun setArrowVisible(visible: Boolean) {
        arrowVisible = visible
    }

    override fun drawX(canvas: Canvas) {
        path.reset()
        calculateRect(rectF)
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)

        if ((arrowPosition == ArrowPosition.TOP ||
                    arrowPosition == ArrowPosition.BOTTOM ||
                    arrowPosition == ArrowPosition.START ||
                    arrowPosition == ArrowPosition.END) && arrowVisible
        ) {
            // 绘制箭头
            calculateArrow(rectF, array)
            path.moveTo(array[0].x, array[0].y)
            path.lineTo(array[1].x, array[1].y)
            path.quadTo(array[2].x, array[2].y, array[3].x, array[3].y)
            path.lineTo(array[4].x, array[4].y)
        }
        canvas.drawPath(path, shadowPaint)
        canvas.save()
    }

    override fun bindView(view: View) {
        this.mView = view
    }

    /**
     * 计算箭头的范围
     *
     *
     *
     *
     *
     *         (B) ╭(C)╮ (D)
     *            /     \
     *          /        \
     *  ___(A)/           \(E)___
     *
     *  rect 是圆角矩形的位置
     *
     */
    private fun calculateArrow(rect: RectF, array: Array<PointF>) {
        mView ?: return
        // 箭头中心位置
        val xyOffset: Float = if (arrowOffset < 0) {
            when (arrowPosition) {
                ArrowPosition.TOP, ArrowPosition.BOTTOM -> mView!!.width / 2f
                ArrowPosition.START, ArrowPosition.END -> mView!!.height / 2f
                ArrowPosition.NONE -> 0F
            }
        } else {
            arrowOffset
        }
        val a = array[0]
        val b = array[1]
        val c = array[2]
        val d = array[3]
        val e = array[4]
        val offset = arrowCornerRadius / 2f
        when (arrowPosition) {
            ArrowPosition.NONE -> {
                // do nothing
            }
            ArrowPosition.TOP -> {
                a.set(xyOffset - arrowWidth / 2f, rect.top)
                b.set(xyOffset - offset, rect.top - arrowHeight + offset)
                c.set(xyOffset, rect.top - arrowHeight)
                d.set(xyOffset + offset, rect.top - arrowHeight + offset)
                e.set(xyOffset + arrowWidth / 2f, rect.top)
            }
            ArrowPosition.START -> {
                a.set(rect.left, rect.top + xyOffset - arrowWidth / 2f)
                b.set(rect.left - arrowHeight + offset, rect.top + xyOffset - offset)
                c.set(rect.left - arrowHeight, rect.top + xyOffset)
                d.set(rect.left - arrowHeight + offset, rect.top + xyOffset + offset)
                e.set(rect.left, rect.top + xyOffset + arrowWidth / 2f)
            }
            ArrowPosition.BOTTOM -> {
                a.set(xyOffset - arrowWidth / 2f, rect.bottom)
                b.set(xyOffset - offset, rect.bottom + arrowHeight - offset)
                c.set(xyOffset, rect.bottom + arrowHeight)
                d.set(xyOffset + offset, rect.bottom + arrowHeight - offset)
                e.set(xyOffset + arrowWidth / 2f, rect.bottom)
            }
            ArrowPosition.END -> {
                a.set(rect.right, rect.top + xyOffset - arrowWidth / 2f)
                b.set(rect.right + arrowHeight - offset, rect.top + xyOffset - offset)
                c.set(rect.right + arrowHeight, rect.top + xyOffset)
                d.set(rect.right + arrowHeight - offset, rect.top + xyOffset + offset)
                e.set(rect.right, rect.top + xyOffset + arrowWidth / 2f)
            }
        }
    }

    /**
     * 计算矩形范围
     */
    private fun calculateRect(rectF: RectF) {
        mView ?: return
        var start = mView!!.paddingStart.toFloat()
        var end = (mView!!.width - mView!!.paddingEnd).toFloat()
        var top = mView!!.paddingTop.toFloat()
        var bottom = (mView!!.height - mView!!.paddingBottom).toFloat()

        when (arrowPosition) {
            ArrowPosition.NONE -> {
                // do nothing
            }
            ArrowPosition.TOP -> {
                top += arrowHeight
            }
            ArrowPosition.START -> {
                start += arrowHeight
            }
            ArrowPosition.BOTTOM -> {
                bottom -= arrowHeight
            }
            ArrowPosition.END -> {
                end -= arrowHeight
            }
        }
        rectF.set(start, top, end, bottom)
    }

}