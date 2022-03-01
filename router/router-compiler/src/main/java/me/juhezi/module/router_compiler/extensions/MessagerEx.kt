package me.juhezi.module.router_compiler.extensions

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Created by Juhezi[juhezix@163.com] on 2017/8/23.
 */

/**
 * Print message in [Gradle Console]
 */
fun Messager.print(message: String) = printMessage(Diagnostic.Kind.NOTE, message)


