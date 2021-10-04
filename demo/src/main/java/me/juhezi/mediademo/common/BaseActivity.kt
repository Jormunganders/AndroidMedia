package me.juhezi.mediademo.common

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseActivity : RxAppCompatActivity(), CoroutineScope by MainScope()
