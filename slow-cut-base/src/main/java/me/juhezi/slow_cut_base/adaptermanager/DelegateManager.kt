package me.juhezi.slow_cut_base.adaptermanager

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.containsValue
import me.juhezi.slow_cut_base.adaptermanager.delegate.DefaultAdapterDelegate
import me.juhezi.slow_cut_base.adaptermanager.delegate.IAdapterDelegate
import androidx.core.util.contains as contains2

/**
 * @constructor
 * @param dependency 可以使用 dependency 中注册的 delegate
 */
class DelegateManager internal constructor(private val dependency: AdapterDelegateStore? = null) {

    // FromWhere
    private val delegates = mutableListOf<IAdapterDelegate>()

    // key: viewType，viewType = 0 的时候，直接用兜底的 delegate
    // value: 数据的对应关系
    private val map = SparseArray<IAdapterDelegate>()

    // From Where
    internal var dataList: MutableList<Any> = mutableListOf()

    fun registerAdapterDelegate(delegate: IAdapterDelegate) {
        delegates.add(delegate)
    }

    fun unregisterAdapterDelegate(delegate: IAdapterDelegate) {
        delegates.remove(delegate)
    }

    // 每个数据都会调用一次
    internal fun getItemViewType(position: Int): Int {
        // 没有找到对应的 delegate，直接返回 0
        val delegate = (delegates + dependency?.getDelegates().orEmpty()).find {
            it.modelMatcher.match(dataList, position, dataList[position])
        } ?: return DEFAULT_DELEGATE_VIEW_TYPE
        return if (map.containsValue(delegate)) {  // map 中已经有了对应关系了
            map.run {
                keyAt(indexOfValue(delegate))
            }
        } else {
            val viewType = map.size() + 1
            map.append(viewType, delegate)
            viewType
        }
    }

    internal fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val delegate = if (map.contains2(viewType)) {
            map[viewType]
        } else {
            DefaultAdapterDelegate()
        }
        return BaseViewHolder(delegate.onCreateView(parent), delegate)
    }

    internal fun bindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.adapterDelegate.onBindViewHolder(holder, dataList[position])
    }

    internal fun bindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.adapterDelegate.onBindViewHolder(holder, dataList[position], payloads)
    }

    internal fun getItemCount(): Int = dataList.size

}