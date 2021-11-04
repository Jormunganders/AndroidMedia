package me.juhezi.slowcut.uzi

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.android.synthetic.main.activity_uzi.*
import me.juhezi.slow_cut_base.core.SlowCutActivity
import me.juhezi.slowcut.R

class UziActivity : SlowCutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uzi)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            root.setPadding(0, 0, 0, imeHeight)
            insets
        }

    }

    /**
     * 关于键盘的相关设置
     */
    private fun setupSoftInput() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
    }

    companion object {
        private const val TAG = "UziActivity"
    }

}