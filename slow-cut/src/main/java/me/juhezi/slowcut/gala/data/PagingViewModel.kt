package me.juhezi.slowcut.gala.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import me.juhezi.slowcut.model.gala.db.entry.Repo

class PagingViewModel : ViewModel() {
    fun getPagingData(): Flow<PagingData<Repo>> {
        // cachedIn 的作用是，把数据缓存在 viewModel 的作用域中
        return Repository.getPagingData().cachedIn(viewModelScope)
    }
}