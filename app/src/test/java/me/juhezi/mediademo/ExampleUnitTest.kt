package me.juhezi.mediademo

import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    // 周期执行
    @Test
    fun test1() {
        val executorService: ScheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleAtFixedRate({
            println("Hello --> ${System.currentTimeMillis()}")
        }, 1_000, 100, TimeUnit.MILLISECONDS)   // 1s 后执行，每隔 100毫秒执行一次
        Thread.sleep(10_000)
    }

    @Test
    fun test2() {
        val executorService: ScheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor()
        val future = executorService.schedule(Callable {
            println("Callback Start")
            "HelloWorld"
        }, 4L, TimeUnit.SECONDS)
        println("======")
        println("结果：${future.get()}")
        Thread.sleep(10_000)
    }

    @Test
    fun test3() {
        val executorService: ScheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleWithFixedDelay({
            println("====:: ${System.currentTimeMillis()}")
        }, 1, 2, TimeUnit.SECONDS)
        Thread.sleep(10_000)
    }

}
