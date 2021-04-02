package me.juhezi.slowcut.span

import android.text.style.URLSpan

// 支持点击，显示图片，并且有外遮罩
open class ParamsSpan(path: String) : URLSpan(path) {

}