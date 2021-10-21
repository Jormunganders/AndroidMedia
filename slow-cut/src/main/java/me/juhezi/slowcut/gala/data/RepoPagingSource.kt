package me.juhezi.slowcut.gala.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.juhezi.slowcut.gala.api.GithubService
import me.juhezi.slowcut.model.gala.db.entry.Repo

// PagingSource 的两个泛型分别是页数类型和数据 item 类型
class RepoPagingSource(private val apiService: GithubService) : PagingSource<Int, Repo>() {

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            val page = params.key ?: 1  // key 为 null 将默认为第一页
            val pageSize = params.loadSize
            val repoResponse = apiService.searchRepos(page = page, itemsPerPage = pageSize)
            val repoItems = repoResponse.items
            // 如果当前页已经是第一页或者是最后一页，那么它的上一页或下一页为 null
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (repoItems.isNotEmpty()) page + 1 else null
            LoadResult.Page(repoItems, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}