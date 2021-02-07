package me.juhezi.mediademo.broom.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.layout_activity_v2ex.*
import kotlinx.android.synthetic.main.layout_activity_v2ex.view.*
import me.juhezi.mediademo.R
import me.juhezi.mediademo.broom.net.V2EXRetrofit
import me.juhezi.mediademo.common.BaseActivity
import me.juhezi.mediademo.kuaishou.AsyncCacheLayoutInflater
import me.juhezi.mediademo.logi


class V2EXActivity : BaseActivity() {

    private var isCenterInSide = true
    private var imeVisible = false
    private var systemBarVisible = true

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            AsyncCacheLayoutInflater.getCacheOrInflate(
                this,
                R.layout.layout_activity_v2ex
            ).apply {
                // 使用屏幕中全部的内容
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false)
                } else {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                }
                ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                    val lp = edit_text.layoutParams as ViewGroup.MarginLayoutParams
                    lp.topMargin = insets.systemWindowInsetTop
                    edit_text.layoutParams = lp
                    logi("Juhezi", "${insets.systemWindowInsets}")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        imeVisible =
                            insets.toWindowInsets()?.isVisible(WindowInsets.Type.ime()) == true
                        val imeHeight =
                            insets.toWindowInsets()?.getInsets(WindowInsets.Type.ime())?.bottom
                        Log.i("Juhezi", "onCreate: imeVisible: $imeVisible imeHeight $imeHeight")
                    }
                    insets
                }
            }
        )
        Glide.with(this@V2EXActivity)
            .load("https://api.ixiaowai.cn/api/api.php")
            .centerInside()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(background_image)
        V2EXRetrofit.get()
            .requestHotTopic()
            .compose(bindToLifecycle())
            .subscribe({
                Log.i("Juhezi", "onCreate: CurrentThread: " + Thread.currentThread().name)
                Log.i("Juhezi", "onCreate: \n$it")
            }, {
                it.printStackTrace()
            })
        background_switch.setOnClickListener {
            isCenterInSide = !isCenterInSide
            background_image.scaleType = if (isCenterInSide) {
                ImageView.ScaleType.CENTER_INSIDE
            } else {
                ImageView.ScaleType.CENTER_CROP
            }
        }
        show_hide_keyboard.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = it.windowInsetsController
                if (imeVisible) {
                    controller?.hide(WindowInsets.Type.ime())
                } else {
                    controller?.show(WindowInsets.Type.ime())
                }
            }
        }
        fullscreen_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                logi("Juhezi", "systemBarVisible: $systemBarVisible")
                val controller = it.windowInsetsController
                controller?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                if (systemBarVisible) {
                    controller?.hide(WindowInsets.Type.systemBars())
                } else {
                    controller?.show(WindowInsets.Type.systemBars())
                }
                systemBarVisible = !systemBarVisible
            }
        }
    }

}