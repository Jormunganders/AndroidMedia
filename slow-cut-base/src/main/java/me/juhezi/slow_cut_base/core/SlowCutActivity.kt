package me.juhezi.slow_cut_base.core

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class SlowCutActivity : RxAppCompatActivity(), CoroutineScope by MainScope()