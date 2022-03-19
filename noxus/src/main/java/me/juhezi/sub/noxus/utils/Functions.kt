package me.juhezi.sub.noxus.utils

object Functions {

    fun doAndCheck(closure: () -> Unit, checker: () -> Boolean): Boolean {
        closure()
        return checker()
    }

}