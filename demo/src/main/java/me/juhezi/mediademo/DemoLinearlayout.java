package me.juhezi.mediademo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class DemoLinearlayout extends LinearLayout {

    private Paint mPaint;

    public DemoLinearlayout(Context context) {
        this(context, null);
    }

    public DemoLinearlayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoLinearlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int layerSave = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.drawRect(0, 0, getWidth(), getHeight() / 5f, mPaint);
        canvas.restoreToCount(layerSave);
        return result;
    }

    private LinearGradient mGradient;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mGradient = new LinearGradient(0, 0, 0, h / 5f,
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mPaint.setShader(mGradient);
    }
}
