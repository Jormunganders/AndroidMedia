package me.juhezi.slowcut.gala.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.repos_load_state_footer_view_item.view.*
import me.juhezi.slowcut.R

class ReposLoadStateViewHolder(
    private val rootView: View,
    retry: () -> Unit
) : RecyclerView.ViewHolder(rootView) {

    init {
        rootView.retry_button.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            rootView.error_msg.text = loadState.error.localizedMessage
        }
        rootView.progress_bar.isVisible = loadState is LoadState.Loading
        rootView.retry_button.isVisible = loadState is LoadState.Error
        rootView.error_msg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ReposLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.repos_load_state_footer_view_item, parent, false)
            return ReposLoadStateViewHolder(view, retry)
        }
    }
}
