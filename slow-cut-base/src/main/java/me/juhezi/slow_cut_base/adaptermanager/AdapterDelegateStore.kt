package me.juhezi.slow_cut_base.adaptermanager

import me.juhezi.slow_cut_base.adaptermanager.delegate.IAdapterDelegate

open class AdapterDelegateStore(private val dependency: AdapterDelegateStore? = null) {

    private val delegateSet = mutableSetOf<IAdapterDelegate>()

    fun registerAdapterDelegate(delegate: IAdapterDelegate) {
        delegateSet.add(delegate)
    }

    fun unregisterAdapterDelegate(delegate: IAdapterDelegate) {
        delegateSet.remove(delegate)
    }

    fun clear() {
        delegateSet.clear()
    }

    fun getDelegates() = dependency?.delegateSet.orEmpty() + delegateSet

}