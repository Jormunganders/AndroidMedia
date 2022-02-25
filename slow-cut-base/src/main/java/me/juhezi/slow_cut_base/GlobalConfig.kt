package me.juhezi.slow_cut_base

import android.annotation.SuppressLint
import android.content.Context

class GlobalConfig {


    companion object {

        @SuppressLint("StaticFieldLeak")
        private var CONTEXT: Context? = null

        @JvmStatic
        fun setApplicationContext(context: Context) {
            CONTEXT = context
        }

        @JvmStatic
        fun getApplicationContext() = CONTEXT
    }

}