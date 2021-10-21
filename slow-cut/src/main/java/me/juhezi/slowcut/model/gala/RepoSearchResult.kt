package me.juhezi.slowcut.model.gala

import me.juhezi.slowcut.model.gala.db.entry.Repo
import java.lang.Exception

sealed class RepoSearchResult {
    data class Success(val data: List<Repo>) : RepoSearchResult()
    data class Error(val error: Exception) : RepoSearchResult()
}
