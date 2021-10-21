package me.juhezi.slowcut.gala.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import me.juhezi.slowcut.gala.api.GithubService
import me.juhezi.slowcut.model.gala.db.entry.Repo

object Repository {

    private const val PAGE_SIZE = 10
    private val githubService = GithubService.create()

    fun getPagingData(): Flow<PagingData<Repo>> {
        return Pager(config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = 2   // 预加载的阈值
        ), pagingSourceFactory = {
            RepoPagingSource(githubService)
        }).flow
    }

}