package me.juhezi.mediademo.widgets

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF

/**
 * ViewGroup 实现圆角带阴影
 */
class ShadowRoundedHelper(
    private val backgroundColor: Int,  // 背景颜色
    private val cornerRadius: Float, // 圆角半径
    private val shadowColor: Int, // 阴影颜色
    private val shadowSize: Float, // 阴影宽度
    private val arrowHeight: Float,  // 箭头高度
    private val arrowWidth: Float,   // 箭头宽度，箭头所对的那个边长度
    private val arrowCornerRadius: Float, // 箭头圆角
    private val arrowPosition: ArrowPosition, // 箭头位置
    private val callback: Callback
) {

    private val shadowPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(
            shadowSize, 0f,
            0f, shadowColor
        )
    }

    /**            /\
     *           /   \
     * ________/   *  \______
     *
     * 如果箭头位置是 TOP 或者 BOTTOM，表示箭头长边中心(*)的 x 坐标距离左侧的距离
     * 如果是 START 或者 END，表示箭头中心(*)的 y 坐标距离顶部的距离
     */
    var arrowOffset = -1f
        set(value) {
            field = value
            callback.onInvalidate()
        }

    /**
     * 如果 arrowPosition != NONE，那么 showArrow 为 false，只是隐藏箭头，但是位置还是会空出来的
     */
    var showArrow = true
        set(value) {
            field = value
            callback.onInvalidate()
        }

    private val path = Path()
    private val rectF = RectF()
    private val array = Array(5) {
        PointF()
    }

    fun draw(canvas: Canvas) {
        path.reset()
        calculateRect(rectF)
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)

        if ((arrowPosition == ArrowPosition.TOP ||
                    arrowPosition == ArrowPosition.BOTTOM ||
                    arrowPosition == ArrowPosition.START ||
                    arrowPosition == ArrowPosition.END) && showArrow
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
        // 箭头中心位置
        val xyOffset: Float = if (arrowOffset < 0) {
            when (arrowPosition) {
                ArrowPosition.TOP, ArrowPosition.BOTTOM -> callback.getWidth() / 2f
                ArrowPosition.START, ArrowPosition.END -> callback.getHeight() / 2f
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
        var start = callback.getPaddingStart()
        var end = callback.getWidth() - callback.getPaddingEnd()
        var top = callback.getPaddingTop()
        var bottom = callback.getHeight() - callback.getPaddingBottom()

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

    enum class ArrowPosition {
        NONE,   // 无箭头
        TOP,
        START,
        END,
        BOTTOM
    }

    interface Callback {

        fun onInvalidate()

        fun getHeight(): Float
        fun getWidth(): Float
        fun getPaddingStart(): Float
        fun getPaddingEnd(): Float
        fun getPaddingTop(): Float
        fun getPaddingBottom(): Float

    }

}
