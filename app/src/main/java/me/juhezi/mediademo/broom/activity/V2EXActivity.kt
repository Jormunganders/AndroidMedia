package me.juhezi.mediademo.broom.activity

import android.os.Build
import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.layout_activity_v2ex.*
import kotlinx.android.synthetic.main.layout_activity_v2ex.view.*
import me.juhezi.mediademo.Demo
import me.juhezi.mediademo.R
import me.juhezi.mediademo.broom.net.V2EXRetrofit
import me.juhezi.mediademo.common.BaseActivity
import me.juhezi.mediademo.kuaishou.AsyncCacheLayoutInflater
import me.juhezi.mediademo.logi


class V2EXActivity : BaseActivity() {

    private var isCenterInSide = true
    private var mCurrentImeVisible = false
    private var systemBarVisible = true
    private var mImeHeight: Int = 0

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
                    setWindowInsetsAnimationCallback(object :
                        WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                        override fun onProgress(
                            insets: WindowInsets,
                            runningAnimations: MutableList<WindowInsetsAnimation>
                        ): WindowInsets {
                            val mTargetImeVisible = mCurrentImeVisible
                            val fraction = runningAnimations.maxBy { it.fraction }?.fraction ?: 1F
//                            logi("Juhezix", "fraction: $fraction")  // 从  0  ->  1
                            val percent = if (mTargetImeVisible) {    // 键盘显示的百分比
                                fraction
                            } else {
                                1F - fraction
                            }
//                            logi("Juhezix", "键盘显示百分比: $percent")
                            house.rotation = percent * 180
                            val height = mImeHeight * percent
                            background_switch.translationY = -height
                            fullscreen_button.translationY = -height
                            return insets
                        }

                        override fun onStart(
                            animation: WindowInsetsAnimation,
                            bounds: WindowInsetsAnimation.Bounds
                        ): WindowInsetsAnimation.Bounds {
                            logi("Juhezi", "onStart $animation \n $bounds")
                            mImeHeight = bounds.upperBound.bottom
                            return super.onStart(animation, bounds)
                        }

                    })
                } else {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                }
                ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                    val lp = edit_text.layoutParams as ViewGroup.MarginLayoutParams
                    lp.topMargin = insets.systemWindowInsetTop
                    edit_text.layoutParams = lp
//                    logi("Juhezi", "${insets.systemWindowInsets}")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        mCurrentImeVisible =
                            insets.toWindowInsets()?.isVisible(WindowInsets.Type.ime()) == true
                        var imeHeight =
                            insets.toWindowInsets()?.getInsets(WindowInsets.Type.ime())?.bottom ?: 0
                        Log.i(
                            "Juhezi",
                            "onCreate: imeVisible: $mCurrentImeVisible imeHeight $imeHeight"
                        )
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
                if (mCurrentImeVisible) {
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
        val map = Demo.demo()
        Log.i("Juhezi", "onCreate: " + map.javaClass.name)
        start_anim.setOnClickListener {
            changeState(isNormal)
            isNormal = !isNormal
        }
    }

    private var isNormal = true

    private fun changeState(normal: Boolean) {
        val transition = TransitionSet()
        transition.ordering = TransitionSet.ORDERING_TOGETHER
        transition.duration = 100
        val fade: Transition = Fade()
        val bounds: Transition = ChangeBounds() // 这是啥？
        transition.addTransition(fade)
        transition.addTransition(bounds)
        TransitionManager.beginDelayedTransition(panel, transition)
        arrayOf(view_2, view_3).forEach {
            it.visibility = if (normal) View.GONE else View.VISIBLE
        }
        arrayOf(view_4, view_1).forEach {
            it.visibility = if (normal) View.VISIBLE else View.GONE
        }
    }

}