package me.juhezi.slow_cut_base.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.juhezi.slow_cut_base.GlobalConfig;

/**
 * 通用工具类.
 */
public class CommonUtil {
    public static final String TAG = "CommonUtil";

    private CommonUtil() {
    }

    public static Context context() {
        return GlobalConfig.getApplicationContext();
    }

    public static Resources res() {
        return context().getResources();
    }

    public static int dip2px(float dip) {
        return dip2px(context(), dip);
    }

    public static int px2dip(float px) {
        return px2dip(context(), px);
    }

    public static int dimen(@DimenRes int res) {
        return context().getResources().getDimensionPixelOffset(res);
    }

    public static String string(@StringRes int res) {
        return context().getResources().getString(res);
    }

    public static String string(@StringRes int res, int number) {
        return context().getResources().getString(res, number);
    }

    public static String string(@StringRes int res, String label) {
        return context().getResources().getString(res, label);
    }

    public static int color(@ColorRes int res) {
        return ContextCompat.getColor(context(), res);
    }

    public static int colorFromTheme(int styleId) {
        TypedValue typedValue = new TypedValue();
        context().getTheme().resolveAttribute(styleId, typedValue, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(TAG, "colorFromTheme: " + typedValue.data + " " + typedValue.isColorType());
        }
        return typedValue.data;
    }

    public static ColorStateList colorStateList(@ColorRes int res) {
        return ContextCompat.getColorStateList(context(), res);
    }

    public static Drawable drawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(context(), res);
    }

    private static int sScreenLongAxis = 0;
    private static int sScreenShortAxis = 0;
    private static final int mRatio = 160;

    public static void clearScreenAxisCache() {
        sScreenLongAxis = 0;
        sScreenShortAxis = 0;
    }

    // TODO 发现Galaxy S8沉浸式的时候值返回不对，暂时没有找到更好的方式
    public static int getScreenLongAxis() {
        if (sScreenLongAxis == 0) {
            Context context = context();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            sScreenLongAxis = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        return sScreenLongAxis;
    }

    public static int getScreenShortAxis() {
        if (sScreenShortAxis == 0) {
            Context context = context();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            sScreenShortAxis = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        return sScreenShortAxis;
    }

    /*
    返回显示窗口的长边
     */
    public static int getDisplayWindowLongAxis(Configuration configuration) {
        int screenWidthDp = configuration.screenWidthDp;
        int screenHeightDp = configuration.screenHeightDp;
        int densityDpi = configuration.densityDpi;
        int windowWidth = screenWidthDp * densityDpi / mRatio;
        int windowHeight = screenHeightDp * densityDpi / mRatio;
        return Math.max(windowWidth, windowHeight);
    }

  /*
  返回显示窗口的短边
 */

    public static int getDisplayWindowShortAxis(Configuration configuration) {
        int screenWidthDp = configuration.screenWidthDp;
        int screenHeightDp = configuration.screenHeightDp;
        int densityDpi = configuration.densityDpi;
        int windowWidth = screenWidthDp * densityDpi / mRatio;
        int windowHeight = screenHeightDp * densityDpi / mRatio;
        return Math.min(windowWidth, windowHeight);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long since(long time) {
        return System.currentTimeMillis() - time;
    }

    public static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(now()));
    }

    public static int toInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static boolean isActivityAvailable(Activity activity) {
        return activity != null && !activity.isFinishing();
    }

    public static boolean isActivityAvailable(WeakReference<? extends Activity> activityRef) {
        return activityRef != null && isActivityAvailable(activityRef.get());
    }

    public static int rgbaToColor(float red, float green, float blue, float alpha) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
