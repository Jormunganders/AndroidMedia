package me.juhezi.slow_cut_base.adaptermanager

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter private constructor(
    private val delegateManager: DelegateManager,
    private val diffPredicate: DiffPredicate?
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return delegateManager.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        delegateManager.bindViewHolder(holder, position)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        delegateManager.bindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int = delegateManager.getItemCount()

    override fun getItemViewType(position: Int): Int = delegateManager.getItemViewType(position)

    fun edit(): Editor = Editor(this, delegateManager)

    inner class Editor(
        private val adapter: BaseAdapter,
        private val delegateManager: DelegateManager
    ) {

        fun updateImmediately(newList: List<Any>) {
            diffPredicate ?: return
            val result = DiffUtil.calculateDiff(
                DiffWrapper(
                    delegateManager.dataList,
                    newList,
                    diffPredicate
                )
            )
            with(delegateManager.dataList) {
                clear()
                addAll(newList)
            }
            result.dispatchUpdatesTo(adapter)
        }

        // TODO: 临时方法
        fun notifyDataChanged(newList: List<Any>) {
            with(delegateManager.dataList) {
                clear()
                addAll(newList)
            }
            adapter.notifyDataSetChanged()
        }

    }

    class Builder {

        var delegateStore: AdapterDelegateStore = GlobalDelegateStore

        var diff: DiffPredicate? = null

        fun build(): BaseAdapter {
            val delegateManager = DelegateManager(delegateStore)
            return BaseAdapter(delegateManager, diff)
        }

    }

}