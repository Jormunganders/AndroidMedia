package me.juhezi.slowcut.gala.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juhezi.slowcut.gala.ui.UiModel
import me.juhezi.slowcut.model.gala.db.entry.Repo

class PagingViewModel : ViewModel() {
    fun getPagingData(): Flow<PagingData<Repo>> {
        // cachedIn 的作用是，把数据缓存在 viewModel 的作用域中
        return Repository.getPagingData().cachedIn(viewModelScope)
    }

    /**
     * 增加分割线逻辑
     */
    fun getPagingData2(): Flow<PagingData<UiModel>> {
        return Repository.getPagingData()
            .map { pagingData ->
                pagingData.map {
                    UiModel.RepoItem(it)
                }
            }
            .map {
                it.insertSeparators<UiModel.RepoItem, UiModel> { before, after ->
                    if (after == null) {
                        // 队尾
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // 队首
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }
                    if (before.roundedStarCount > after.roundedStarCount) {
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        null
                    }
                }
            }.cachedIn(viewModelScope)
    }

}

private val UiModel.RepoItem.roundedStarCount: Int
    get() = this.repo.stars / 10_000
