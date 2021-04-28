package me.juhezi.slowcut

import android.util.Log

object ImageGridLayoutUtils {

    @JvmStatic
    fun generateImageLayoutConfigs(count: Int): List<LayoutConfig> {

        val minLayoutConfigMaker = LayoutConfig.Companion::makeLayoutConfig.curried()(1 / 3f)
        val midLayoutConfigMaker = LayoutConfig.Companion::makeLayoutConfig.curried()(1 / 2f)
        val maxLayoutConfigMaker = LayoutConfig.Companion::makeLayoutConfig.curried()(2 / 3f)


        return when {
            count <= 1 -> emptyList()
            count == 2 -> {
                listOf(midLayoutConfigMaker(0), midLayoutConfigMaker(1))
            }
            count in 3 until 6 -> { //  3 <=  x < 6
                listOf(maxLayoutConfigMaker(0), minLayoutConfigMaker(1), minLayoutConfigMaker(2))
            }
            else -> { // x >= 6
                mutableListOf(maxLayoutConfigMaker(0)).apply {
                    addAll(1.until(6).map { minLayoutConfigMaker(it) })
                }
            }
        }
    }

    @JvmStatic
    fun getGridLayoutConfigCount(count: Int) = when {
        count <= 1 -> 0
        count in 3 until 6 -> 3
        count >= 6 -> 6
        else -> count
    }

    /**
     * 计算每个 LayoutConfig 的坐标
     * 先手动赋值
     */
    fun calcCoordinate(configs: List<LayoutConfig>): List<LayoutConfig> {
        when (configs.size) {
            2 -> {
                configs[0].x = 0f
                configs[0].y = 0f
                configs[1].x = 1 / 2f
                configs[1].y = 0f
            }
            3 -> {
                configs[0].x = 0f
                configs[0].y = 0f
                // --
                configs[1].x = 2 / 3f
                configs[1].y = 0f
                configs[2].x = 2 / 3f
                configs[2].y = 1 / 3f
            }
            6 -> {
                configs[0].x = 0f
                configs[0].y = 0f
                // ---
                configs[1].x = 2 / 3f
                configs[1].y = 0f
                configs[2].x = 2 / 3f
                configs[2].y = 1 / 3f
                // ---
                configs[3].x = 0f
                configs[3].y = 2 / 3f
                configs[4].x = 1 / 3f
                configs[4].y = 2 / 3f
                configs[5].x = 2 / 3f
                configs[5].y = 2 / 3f
            }
            else -> { // count >= 6
                // do nothing
            }
        }
        return configs
    }

    fun calcCoordinate2(configs: List<LayoutConfig>): List<LayoutConfig> {

        fun canRight(current: Node, pre: Node): Boolean {
            // 右侧起始位置
            val startX = pre.layoutConfig.x + pre.layoutConfig.fraction
//            println("检测右边：current: [${current.layoutConfig}] pre: [${pre.layoutConfig}] hasRight: ${pre.right == null}")
            return pre.right == null  // 右侧为空
                    && startX + current.layoutConfig.fraction <= 1  // 右侧宽度合适
                    && current.layoutConfig.fraction <= pre.layoutConfig.fraction // 右侧高度合适
        }

        fun canBottom(current: Node, pre: Node): Boolean {
            // 下方起始位置
            val startY = pre.layoutConfig.y + pre.layoutConfig.fraction
//            println("检测下边：current: [${current.layoutConfig}] pre: [${pre.layoutConfig}] hasBottom: ${pre.bottom == null}\nleftYLimit: ${pre.getLeftYLimit()}")
//            println("检测下边：宽度：${pre.layoutConfig.x} + ${current.layoutConfig.fraction} = ${pre.layoutConfig.x + current.layoutConfig.fraction} <= 1 ${pre.layoutConfig.x + current.layoutConfig.fraction <= 1}")
//            println("检测下边：高度：${startY} + ${current.layoutConfig.fraction} = ${startY + current.layoutConfig.fraction} <= ${pre.getLeftYLimit()} ${startY + current.layoutConfig.fraction <= pre.getLeftYLimit()}")
            return pre.bottom == null // 下方为空
                    && pre.layoutConfig.x + current.layoutConfig.fraction <= 1  // 下方空间宽度合适
                    && startY + current.layoutConfig.fraction <= pre.getLeftYLimit()  // 下方空间高度合适
        }

        fun setupCoordinate(currentNode: Node, preNode: Node): Boolean {
            // 先判断右侧有没有位置放置 View
            // 右侧起始位置
            val startX = preNode.layoutConfig.x + preNode.layoutConfig.fraction
            // 下方起始位置
            val startY = preNode.layoutConfig.y + preNode.layoutConfig.fraction
//            println("开始设置坐标：current: [${currentNode.layoutConfig.index}] pre: [${preNode.layoutConfig.index}]")
            return when {
                canRight(currentNode, preNode) -> {   // 右侧可放置
                    currentNode.layoutConfig.x = startX
                    currentNode.layoutConfig.y = preNode.layoutConfig.y
                    preNode.right = currentNode
                    currentNode.pre = preNode
                    if (startX + currentNode.layoutConfig.fraction == 1f) { // 右侧无法再放置 View 了
                        currentNode.right = INVALID_NODE
                    }
                    true
                }
                canBottom(currentNode, preNode) -> {  // 下方可放置
                    currentNode.layoutConfig.x = preNode.layoutConfig.x
                    currentNode.layoutConfig.y = startY
                    preNode.bottom = currentNode
                    currentNode.pre = preNode
                    true
                }
                else -> {
                    false
                }
            }
        }

        var rootNode: Node? = null  // 根节点，左上的 View
        var currentNode: Node?
        var preNode: Node? = null //
        for (config in configs) {
            currentNode = Node(config)
            if (rootNode == null) { // 当前节点是根节点
                with(currentNode.layoutConfig) {
                    x = 0f
                    y = 0f
                }
                rootNode = currentNode
                preNode = currentNode
                continue
            } else {
                if (setupCoordinate(currentNode, preNode!!)) {  // 成功放置了当前 View
                    preNode = currentNode
                } else {  // 没有位置放置，需要回溯
//                    println("开始回溯：current: [${currentNode.layoutConfig.index}] pre: [${preNode.layoutConfig.index}]")
                    while (preNode!!.pre != null
                        && !canRight(currentNode, preNode.pre!!)
                        && !canBottom(currentNode, preNode.pre!!)
                    ) {  // 左侧和右侧都不符合条件，继续向上
                        preNode = preNode.pre
//                        println("继续回溯：current: [${currentNode.layoutConfig.index}] pre: [${preNode?.layoutConfig?.index}]")
                    }
//                    println("回溯结束：current: [${currentNode.layoutConfig.index}] pre: [${preNode.layoutConfig.index}] Target: [${preNode.pre?.layoutConfig?.index}]")
                    if (preNode.pre == null) {
//                        println("preNode.pre == null")
                        break
                    }
                    if (setupCoordinate(currentNode, preNode.pre!!)) {
                        preNode = currentNode
                    } else {
                        // 不应该出现这种情况
//                        println("回溯之后，设置位置失败：current: [${currentNode.layoutConfig.index}] pre: [${preNode.layoutConfig.index}]")
                        break
                    }
                }
            }
        }
        return configs
    }

    private val INVALID_NODE = Node(LayoutConfig.makeLayoutConfig(0f, -1), null, null)

    class Node(
        val layoutConfig: LayoutConfig,
        var bottom: Node? = null,
        var right: Node? = null,
        var pre: Node? = null
    ) {

        /**
         * 获取左侧节点的 Y 轴限制
         * 向上遍历当前节点，直到不是左右关系
         */
        fun getLeftYLimit(): Float {
            var temp = this
            while (temp.pre != null && temp.pre!!.bottom == temp) {
                temp = temp.pre!!
            }
            temp.pre?.apply {
                return layoutConfig.y + layoutConfig.fraction
            }
            // temp 已经是根节点了
            return Float.MAX_VALUE
        }

    }

}

class LayoutConfig(val fraction: Float, val index: Int, var x: Float = -1f, var y: Float = -1f) {

    companion object {
        @JvmStatic
        fun makeLayoutConfig(fraction: Float, index: Int) = LayoutConfig(fraction, index)

    }

    override fun equals(other: Any?): Boolean {
        return other != null
                && other is LayoutConfig
                && fraction == other.fraction
                && index == other.index
                && x == other.x
                && y == other.y
    }

    override fun toString(): String {
        return "LayoutConfig(fraction=$fraction, index=$index, x=$x, y=$y)"
    }


}


private fun <P1, P2, R> Function2<P1, P2, R>.curried() = fun(p1: P1) = fun(p2: P2) = this(p1, p2)


