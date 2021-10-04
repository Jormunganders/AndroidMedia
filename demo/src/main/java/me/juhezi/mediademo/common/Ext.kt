package me.juhezi.mediademo.common

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.doOnLayout
import me.juhezi.mediademo.isTrue

fun Any?.isTrue() = this == true

fun Any?.isFalse() = this != true

/**
 * 只是简单的监听键盘可见性
 */
@RequiresApi(Build.VERSION_CODES.R)
fun View.addIMEInsetListener(imeCallback: (visible: Boolean) -> Unit) {
    doOnLayout {
        var imeVisible = rootWindowInsets?.isVisible(WindowInsets.Type.ime()).isTrue()
        imeCallback(imeVisible)

        setOnApplyWindowInsetsListener { _, windowInsets ->
            val imeUpdateCheck = rootWindowInsets?.isVisible(WindowInsets.Type.ime()).isTrue()
            if (imeUpdateCheck != imeVisible) {
                imeCallback(imeUpdateCheck)
                imeVisible = imeUpdateCheck
            }
            windowInsets
        }
    }
}