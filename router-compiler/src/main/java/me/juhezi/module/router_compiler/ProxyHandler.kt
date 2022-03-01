package me.juhezi.module.router_compiler

import com.juhezi.module.router_annotation.annotation.Proxy
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * Created by Juhezi[juhezix@163.com] on 2017/8/29.
 */
const val SUFFIX = "\$Proxy"

class ProxyHandler(private var proxyElement: Element) {
    private val registerElement: ArrayList<Element> = ArrayList()

    fun add(element: Element) = registerElement.add(element)

    fun remove(element: Element) = registerElement.remove(element)

    fun addAll(collection: Collection<Element>) = registerElement.addAll(collection)

    fun clear() = registerElement.clear()

    /**
     * 生成代理类的内容
     */
    fun generate(): String {
        return """
package ${getPackageName()};

public class ${getSimpleGenerateClassName()} implements me.juhezi.module.router_api.IRouterProxy {

    @Override
    public void proxy() {
        ${getRegisterLines()}
    }

}
        """
    }

    fun getGenerateClassName(): String = proxyElement.toString() + SUFFIX

    private fun getSimpleGenerateClassName() = proxyElement.simpleName.toString() + SUFFIX

    private fun getPackageName(): String {
        val name = proxyElement.toString()
        return name.substring(0, name.lastIndexOf('.'))
    }

    private fun getRegisterLines(): String = buildString {
        registerElement.forEach {
            append("me.juhezi.module.base.router.Repository.register($it.class);")
            append("\n")
        }
    }

}