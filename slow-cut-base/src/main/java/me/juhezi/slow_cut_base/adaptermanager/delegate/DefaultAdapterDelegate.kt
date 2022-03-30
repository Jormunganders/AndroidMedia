package me.juhezi.slow_cut_base.adaptermanager.delegate

import android.util.Log
import android.view.View
import android.view.ViewGroup
import me.juhezi.slow_cut_base.adaptermanager.BaseViewHolder
import me.juhezi.slow_cut_base.adaptermanager.ModelMatcher
import me.juhezi.slow_cut_base.util.CommonUtil

class DefaultAdapterDelegate : IAdapterDelegate {

    companion object {
        private const val TAG = "DefaultAdapterDelegate"
    }

    override var modelMatcher: ModelMatcher = object : ModelMatcher {
        override fun match(list: List<Any>, position: Int, data: Any): Boolean {
            return true
        }
    }

    override fun onCreateView(parent: ViewGroup): View = View(parent.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams(
                CommonUtil.dip2px(100f),
                CommonUtil.dip2px(100f)
            )
        )
        setBackgroundColor(CommonUtil.color(android.R.color.darker_gray))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, data: Any) {
        Log.i(TAG, "onBindViewHolder: ")
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        data: Any,
        payloads: MutableList<Any>
    ) {
        Log.i(TAG, "onBindViewHolderWithPayloads: ")
    }
}