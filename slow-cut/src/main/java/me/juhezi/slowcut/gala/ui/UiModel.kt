package me.juhezi.slowcut.gala.ui

import me.juhezi.slowcut.model.gala.db.entry.Repo

sealed class UiModel {

    data class RepoItem(val repo: Repo) : UiModel()

    data class SeparatorItem(val description: String) : UiModel()

}
