package me.juhezi.slowcut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.get

/**
 * 命令行页面
 */
class CLActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clactivity)
        /*with(window.decorView as ViewGroup) {
            val root = get(0)
            removeView(root)
            val wrapper = RelativeLayout(context)
            wrapper.addView(root)
            addView(wrapper)
        }*/
        findViewById<View>(R.id.vg_demo).translationY = -100f
    }
}