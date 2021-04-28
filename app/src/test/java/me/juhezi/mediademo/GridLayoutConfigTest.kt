package me.juhezi.mediademo

import me.juhezi.mediademo.ks.ImageGridLayoutUtils
import me.juhezi.mediademo.ks.LayoutConfig
import org.junit.Assert
import org.junit.Test

class GridLayoutConfigTest {

    private fun testPerLayoutConfigCoordinate(length: Int) {
        val partConfigs1 = ImageGridLayoutUtils.generateImageLayoutConfigs(length)
        val partConfigs2 = ImageGridLayoutUtils.generateImageLayoutConfigs(length)
        Assert.assertEquals(
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
        Assert.assertEquals(listOf(config1), listOf(config2))
    }

}