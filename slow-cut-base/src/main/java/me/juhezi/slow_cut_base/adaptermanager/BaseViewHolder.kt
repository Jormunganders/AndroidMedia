package me.juhezi.slow_cut_base.adaptermanager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.juhezi.slow_cut_base.adaptermanager.delegate.IAdapterDelegate

class BaseViewHolder(itemView: View, val adapterDelegate: IAdapterDelegate) :
    RecyclerView.ViewHolder(itemView) {
}