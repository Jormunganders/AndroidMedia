package me.juhezi.slow_cut_base.adaptermanager

import androidx.recyclerview.widget.DiffUtil
import me.juhezi.slow_cut_base.adaptermanager.DiffPredicate

class DiffWrapper(
    private val oldList: List<Any>,
    private val newList: List<Any>,
    private val diffPredicate: DiffPredicate
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return diffPredicate.areItemsTheSame(oldList, newList, oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return diffPredicate.areContentsTheSame(oldList, newList, oldItemPosition, newItemPosition)
    }
}