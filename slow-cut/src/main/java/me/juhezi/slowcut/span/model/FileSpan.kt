package me.juhezi.slowcut.span

import android.view.View
import java.io.File

class FileSpan(path: String) : ParamsSpan(path) {

    var exist: Boolean
    var file: File = File(url)

    init {
        exist = file.exists()
    }

    override fun onClick(widget: View) {
        // TODO: 2021/4/1 显示对话框
        // 1. 预览文件
        // 2. 创建文件夹
    }


}