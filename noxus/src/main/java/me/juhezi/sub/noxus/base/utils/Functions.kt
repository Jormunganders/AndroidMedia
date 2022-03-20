package me.juhezi.sub.noxus.base.utils

object Functions {

    fun doAndCheck(closure: () -> Unit, checker: () -> Boolean): Boolean {
        closure()
        return checker()
    }

}