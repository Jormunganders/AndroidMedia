package me.juhezi.slow_cut_base.adaptermanager.delegate

import android.util.Log
import me.juhezi.slow_cut_base.adaptermanager.BaseViewHolder
import me.juhezi.slow_cut_base.adaptermanager.ModelMatcher

/**
 * 根据数据类型直接进行匹配
 */
abstract class AbsAdapterDelegate : IAdapterDelegate {

    override var modelMatcher: ModelMatcher = object : ModelMatcher {
        override fun match(list: List<Any>, position: Int, data: Any): Boolean {
            Log.i("Juhezi", "match: ${data::class.java.name} ${getModelType().name} $data")
            return data::class.java.name == getModelType().name
        }
    }

    abstract fun getModelType(): Class<*>

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        data: Any,
        payloads: MutableList<Any>
    ) {
        onBindViewHolder(holder, data)
    }

}