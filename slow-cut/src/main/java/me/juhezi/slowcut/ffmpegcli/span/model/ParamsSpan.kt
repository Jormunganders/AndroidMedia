package me.juhezi.slowcut.ffmpegcli.span.model

import me.juhezi.slowcut.R
import me.juhezi.slowcut.ffmpegcli.span.core.RoundSpan

/**
 * @param originContent 原本的文本，在执行命令的时候会把这个字符串拼接到命令行中。
 */
abstract class ParamsSpan(originContent: String) :
    RoundSpan(R.color.primary_text, R.color.secondary_text) {

    companion object {
        const val SPACE = " "
    }
    
    override fun onSetupOverrideContent() {
        mOverrideText = buildString {
            append(getDisplayEmoji())
            append(SPACE)
            append(getDisplayContent())
        }
    }

    abstract fun getDisplayContent(): String

    abstract fun getDisplayEmoji(): String

    abstract fun onClick(originContent: String)

}