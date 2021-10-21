package me.juhezi.slowcut.model.gala

import com.google.gson.annotations.SerializedName
import me.juhezi.slowcut.model.gala.db.entry.Repo

data class RepoSearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<Repo> = emptyList(),
    val nextPage: Int? = null
)