package me.juhezi.mediademo.kuaishou

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import me.juhezi.mediademo.R
import me.juhezi.mediademo.logi

@ExperimentalStdlibApi
object AsyncCacheLayoutInflater {

    private const val TAG = "AsyncCacheLayoutInflater"

    private val viewCache = ViewCache()
    private val currentAsyncTasks: MutableSet<Int> = mutableSetOf()

    fun getCacheOrInflate(
        context: Context,
        @LayoutRes layoutResId: Int,
        container: ViewGroup? = null
    ): View {
        val view = viewCache.get(layoutResId)?.also {
            log("命中缓存 ${context.resources.getResourceName(layoutResId)}")
        } ?: createView(context, layoutResId, container).also {
            log("没有命中缓存，同步加载")
        }
        // 将context改为当前的context
        (view.getTag(layoutResId) as? MutableContextWrapper)?.baseContext = context
        // 异步inflate view填入到cache中，补充view数量
        createAndCacheViewAsync(context, layoutResId, container)
        return view
    }

    private fun createView(
        context: Context,
        @LayoutRes layoutResId: Int,
        container: ViewGroup?
    ) = LayoutInflater.from(context).inflate(layoutResId, container, false)

    fun createAndCacheViewAsync(
        context: Context,
        @LayoutRes layoutResId: Int,
        container: ViewGroup?
    ) {
        if (viewCache.size(layoutResId) > 0) {
            return
        }
        if (currentAsyncTasks.contains(layoutResId)) {
            return
        }
        currentAsyncTasks.add(layoutResId)
        log("开始缓存 ${context.resources.getResourceName(layoutResId)}")
        val mutableContext = MutableContextWrapper(context)

        Looper.myQueue().addIdleHandler {
            AsyncLayoutInflater(mutableContext)
                .inflate(layoutResId, container) { view, _, _ ->
                    mutableContext.baseContext = null   // 防止内存泄露
                    view.setTag(layoutResId, mutableContext)
                    viewCache.put(layoutResId, view)
                    currentAsyncTasks.remove(layoutResId)
                }
            false
        }
    }

    fun log(message: String) = logi(TAG, message)

}

@ExperimentalStdlibApi
private class ViewCache {

    private val viewCacheMap: MutableMap<Int, MutableList<View>> = mutableMapOf()

    fun put(@LayoutRes layoutResId: Int, view: View) {
        if (viewCacheMap[layoutResId] == null) {
            viewCacheMap[layoutResId] = mutableListOf()
        }
        viewCacheMap[layoutResId]!!.add(view)
    }

    fun get(@LayoutRes layoutResId: Int) = viewCacheMap[layoutResId]?.removeFirstOrNull()

    fun size(@LayoutRes layoutResId: Int) = viewCacheMap[layoutResId]?.size ?: 0

}

