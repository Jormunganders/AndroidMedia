package me.juhezi.slow_cut_base.adaptermanager

interface ModelMatcher {

    fun match(list: List<Any>, position: Int, data: Any): Boolean

}