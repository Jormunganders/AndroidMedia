package me.juhezi.sub.noxus.base.adaptermanager

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import me.juhezi.slow_cut_base.adaptermanager.delegate.AbsAdapterDelegate
import me.juhezi.slow_cut_base.adaptermanager.BaseViewHolder

abstract class AbsBindingAdapterDelegate<T : ViewBinding> : AbsAdapterDelegate() {

    private lateinit var binding: T

    abstract fun onCreateBinding(parent: ViewGroup): T

    override fun onBindViewHolder(holder: BaseViewHolder, data: Any) {
        // check
        assert(holder.itemView == binding.root)
        onBind(binding, data)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, data: Any, payloads: MutableList<Any>) {
        // check
        assert(holder.itemView == binding.root)
        onBind(binding, data, payloads)
    }

    abstract fun onBind(binding: T, data: Any)

    open fun onBind(binding: T, data: Any, payloads: MutableList<Any>) {
        onBind(binding, data)
    }

    override fun onCreateView(parent: ViewGroup): View {
        binding = onCreateBinding(parent)
        return binding.root
    }
}