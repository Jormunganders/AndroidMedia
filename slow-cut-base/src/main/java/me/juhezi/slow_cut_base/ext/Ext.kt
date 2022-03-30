package me.juhezi.slow_cut_base.ext

import android.content.res.AssetManager
import android.view.LayoutInflater
import android.view.View
import me.juhezi.slow_cut_base.util.doAndCheck
import java.io.BufferedReader
import java.io.InputStreamReader

fun AssetManager.readFromFile(fileName: String): String {
    try {
        val inputReader = InputStreamReader(open(fileName))
        val bufferReader = BufferedReader(inputReader)
        var line: String? = null
        return buildString {
            while (doAndCheck({
                    line = bufferReader.readLine()
                }, {
                    line != null
                })) {
                append(line)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun View.inflater(): LayoutInflater = LayoutInflater.from(context)