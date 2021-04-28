package me.juhezi.slowcut.span.core;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import me.juhezi.slow_cut_base.util.CommonUtil;
import me.juhezi.slowcut.R;

// PressedSpanHelper
public class RoundSpan extends ReplacementSpan implements PressedSpan {

    private static final String TAG = "RoundTagSpan";

    private static final int mStrokeWidth = 1;
    private final int mBgColor;
    private final int mCorner;
    private final int mLeftPadding;
    private final int mRightPadding;
    private final int mTextColor;
    private final int mPressedColor;
    private final int mTextSize;
    private final int mStrokeColor;
    private boolean mIsPressed;
    private final int mSpace;
    private final RectF mRectF;

    protected CharSequence mOverrideText;

    private boolean mHasSetupOverrideContent = false;

    public RoundSpan(int textColor, int pressedColor) {
        super();
        mTextColor = textColor;
        mPressedColor = pressedColor;
        mBgColor = CommonUtil
                .color(R.color.s_F8F8F8);
        mStrokeColor = CommonUtil
                .color(R.color.s_EAEAEA);
        mTextSize = CommonUtil.dimen(R.dimen.dimen_14sp);
        mCorner = CommonUtil.dimen(R.dimen.dimen_11dp);
        mLeftPadding = CommonUtil.dimen(R.dimen.dimen_8dp);
        mRightPadding = CommonUtil.dimen(R.dimen.dimen_8dp);
        mSpace = CommonUtil.dimen(R.dimen.dimen_2dp);
        mRectF = new RectF();
    }

    @Override
    public int getSize(@NotNull Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        float textSizeOld = paint.getTextSize();
        paint.setTextSize(mTextSize);
        int space = isSpace(text, end) ? 0 : 2 * mSpace;
        int size = (int) (getContentWidth(text, start, end, paint) + mLeftPadding + mRightPadding + space);
        paint.setTextSize(textSizeOld);
        return size;
    }

    // y 是 baseline
    @Override
    public void draw(@NotNull Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom, @NotNull Paint paint) {

        CharSequence realContent = getRealContent(text, start, end);
        float textSizeOld = paint.getTextSize();
        paint.setTextSize(mTextSize);
        float contentWidth = paint.measureText(realContent.toString());
        float right = x + contentWidth + mLeftPadding + mRightPadding;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        paint.setColor(this.mBgColor);
        x += mSpace;
        mRectF.set(x, y + fontMetrics.top, right, y + fontMetrics.bottom);
        paint.setStyle(Paint.Style.FILL);
        // 绘制背景
        canvas.drawRoundRect(mRectF, mCorner, mCorner, paint);

        // 绘制边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mStrokeColor);
        paint.setStrokeWidth(mStrokeWidth);
        canvas.drawRoundRect(mRectF, mCorner, mCorner, paint);

        if (mTextColor == 0) {
            paint.setColor(CommonUtil.color(R.color.primary_text));
        } else {
            paint.setColor(mIsPressed && mPressedColor != 0 ? mPressedColor : mTextColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(realContent.toString(), x + mLeftPadding, y, paint);
        paint.setTextSize(textSizeOld);
    }

    @Override
    public void setPressed(View widget, boolean isSelected) {
        mIsPressed = isSelected;
        widget.invalidate();
    }

    private CharSequence getRealContent(CharSequence source, int start, int end) {
        if (!mHasSetupOverrideContent) {
            onSetupOverrideContent();
            mHasSetupOverrideContent = true;
        }
        CharSequence realContent;
        if (mOverrideText == null) {
            realContent = source.subSequence(start, end);
        } else {
            realContent = mOverrideText;
        }
        return realContent;
    }

    private float getContentWidth(CharSequence source, int start, int end, Paint paint) {
        return paint.measureText(getRealContent(source, start, end).toString());
    }

    private static boolean isSpace(@NonNull CharSequence source, int where) {
        if (where >= source.length()) {
            return true;
        }
        return source.charAt(where) == ' ' || source.charAt(where) == '\u0020';
    }

    protected void onSetupOverrideContent() {
    }

}