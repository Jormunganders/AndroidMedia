package me.juhezi.slow_cut_base.util


fun doAndCheck(closure: () -> Unit, checker: () -> Boolean): Boolean {
    closure()
    return checker()
}

