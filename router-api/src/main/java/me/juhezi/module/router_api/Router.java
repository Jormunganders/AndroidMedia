package me.juhezi.module.router_api;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Juhezi[juhezix@163.com] on 2017/8/29.
 */

public class Router {

    private static final String TAG = "Router";

    private static final String SUFFIX = "$Proxy";

    public static void inject(Application application) {
        if (application == null) return;
        IRouterProxy iRouterProxy = findProxy(application.getClass());
        if (iRouterProxy != null) {
            iRouterProxy.proxy();
        }
    }

    /**
     * 不通过反射的方式进行注册
     *
     * @param proxy
     */
    public static void inject(IRouterProxy proxy) {
        if (proxy != null) {
            proxy.proxy();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static IRouterProxy findProxy(Class clazz) {
        try {
            Class proxyClass = clazz.getClassLoader().loadClass(clazz.getName() + SUFFIX);
            if (proxyClass == null) return null;
            Object o = proxyClass.newInstance();
            if (o instanceof IRouterProxy) {
                return (IRouterProxy) o;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
