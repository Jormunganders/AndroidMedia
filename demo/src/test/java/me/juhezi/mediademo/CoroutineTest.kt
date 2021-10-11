package me.juhezi.mediademo

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineTest {

    @Test
    fun testHelloWorld(): Unit = runBlocking {
        launch { // 运行在父协程的上下文中，即 runBlocking 主协程
            delay(1000)
            println("Hello")
        }
        async {

        }
        println("End")
    }

    private fun simple(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            println("Emitting $i")
            emit(i)
        }
    }

    @Test
    fun testFlow() = runBlocking {
        val time = measureTimeMillis {
            simple()
                .conflate()
                .collect {
                    delay(300)
                    println(it)
                }
        }
        println("Collected in $time ms")
    }

}