package me.juhezi.slowcut

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private fun testPerLayoutConfigCoordinate(length: Int) {
//        print("Length: $length \n")
        val partConfigs1 = ImageGridLayoutUtils.generateImageLayoutConfigs(length)
        val partConfigs2 = ImageGridLayoutUtils.generateImageLayoutConfigs(length)
        assertEquals(
            ImageGridLayoutUtils.calcCoordinate(partConfigs1),
            ImageGridLayoutUtils.calcCoordinate2(partConfigs2)
        )
    }

    @Test
    fun testLayoutConfigCoordinate() {
        for (i in 2..9) {
            testPerLayoutConfigCoordinate(i)
        }
    }

    @Test
    fun testLayoutConfigEquals() {
        val config1 = LayoutConfig(1 / 3f, 0, 0f, 1 / 3f)
        val config2 = LayoutConfig(1 / 3f, 0, 0f, 1 / 3f)
        assertEquals(listOf(config1), listOf(config2))
    }

}