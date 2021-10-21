package me.juhezi.mediademo.letme

import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("subjects")
    val movies: List<Movie>,
    @SerializedName("has_more")
    val hasMore: Boolean
)