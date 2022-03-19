package me.juhezi.sub.noxus.utils

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.InputStreamReader


fun AssetManager.readFromFile(fileName: String): String {
    try {
        val inputReader = InputStreamReader(open(fileName))
        val bufferReader = BufferedReader(inputReader)
        var line: String? = null
        return buildString {
            while (Functions.doAndCheck({
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

