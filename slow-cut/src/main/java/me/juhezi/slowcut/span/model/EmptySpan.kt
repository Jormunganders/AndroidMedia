package me.juhezi.slowcut.span.model

import me.juhezi.slow_cut_base.util.CommonUtil
import me.juhezi.slowcut.R

/**
 * 匹配文本： '#'
 * 真实文本： ' '
 */
class EmptySpan : ParamsSpan(SPACE) {
    override fun getDisplayContent(): String = CommonUtil.string(R.string.span_empty_display_text)
    override fun getDisplayEmoji(): String = CommonUtil.string(R.string.span_empty_display_emoji)

    override fun onClick(originContent: String) {
        // TODO: 2021/4/2
    }

}