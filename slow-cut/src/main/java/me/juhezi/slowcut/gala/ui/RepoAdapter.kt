package me.juhezi.slowcut.gala.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.juhezi.slowcut.R
import me.juhezi.slowcut.model.gala.db.entry.Repo

class RepoAdapter : PagingDataAdapter<Repo, RepoViewHolder>(COMPARATOR) {

    // Paging3 内部会使用 DiffUtil 管理数据变化
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {

            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repo_view_item, parent, false)
        return RepoViewHolder(view)
    }

}