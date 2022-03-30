package me.juhezi.slow_cut_base.adaptermanager.delegate

import android.view.View
import android.view.ViewGroup
import me.juhezi.slow_cut_base.adaptermanager.BaseViewHolder
import me.juhezi.slow_cut_base.adaptermanager.ModelMatcher

interface IAdapterDelegate {

    var modelMatcher: ModelMatcher

    fun onCreateView(parent: ViewGroup): View

    fun onBindViewHolder(holder: BaseViewHolder, data: Any)

    fun onBindViewHolder(
        holder: BaseViewHolder,
        data: Any,
        payloads: MutableList<Any>
    )

}