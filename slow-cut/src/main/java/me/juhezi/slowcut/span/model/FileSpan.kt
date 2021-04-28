package me.juhezi.slowcut.span.model

import android.util.Log
import android.view.View
import me.juhezi.slow_cut_base.util.CommonUtil
import me.juhezi.slowcut.R
import java.io.File

/**
 * 匹配文本： 'path'
 * 真实文本： 'path'
 */
class FileSpan(path: String) : ParamsSpan(path) {

    private var mFile: File = File(path)

    override fun getDisplayContent(): String {
        val isDir = mFile.isDirectory
        val isExist = mFile.exists()
        return buildString {
            if (isDir) {
                append(CommonUtil.string(R.string.span_file_display_text_folder))
            } else {
                append(CommonUtil.string(R.string.span_file_display_text_file))
            }
            append(CommonUtil.string(R.string.colon))
            if (isExist) {
                append(CommonUtil.string(R.string.span_file_display_text_exist))
            } else {
                append(CommonUtil.string(R.string.span_file_display_text_not_exist))
            }
            append(CommonUtil.string(R.string.colon))
            append(mFile.name)
        }
    }

    override fun getDisplayEmoji(): String {
        return if (mFile.isDirectory) {
            CommonUtil.string(R.string.span_file_display_text_folder_emoji)
        } else {
            CommonUtil.string(R.string.span_file_display_text_file_emoji)
        }
    }

    override fun onClick(originContent: String) {
        // TODO: 2021/4/2
    }

}