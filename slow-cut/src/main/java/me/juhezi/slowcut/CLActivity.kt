package me.juhezi.slowcut

import android.os.Bundle
import me.juhezi.slow_cut_base.core.SlowCutActivity

/**
 * 命令行页面
 */
class CLActivity : SlowCutActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cl)
    }
}