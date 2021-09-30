package me.juhezi.slowcut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class DemoTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint mBackgroundPaint = new Paint();
    private Paint mCirclePaint = new Paint();
    private Rect mRect = new Rect(300, 0, 600, 600);

    public DemoTextView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public DemoTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCirclePaint.setColor(Color.YELLOW);
        mBackgroundPaint.setColor(Color.GREEN);
        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        setTextColor(Color.RED);
        setBackgroundColor(Color.BLUE);

        // SRC 是绿色的方块
        // DST 是黄色的圆
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null);
        canvas.drawCircle(300, 300, 150, mCirclePaint);
        canvas.restoreToCount(x);
        canvas.drawRect(mRect, mBackgroundPaint);
    }
}
