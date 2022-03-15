package com.dndc.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.LruCache
import com.dndc.arouter.PathLoadListener
import com.dndc.arouter.RouterBean
import com.dndc.arouter.RouterConstants


class ARouterManager private constructor() {
    private val routerBeanLruCache: LruCache<String, RouterBean> = LruCache(163)
    private var routerBean: RouterBean? = null
    fun build(path: String): BundleManager {
        //检测path格式
        pathFormatCheck(path)
        //处理path逻辑
        //module名称
        val moduleName = path.substring(1, path.lastIndexOf("/"))
        try {
            routerBean = routerBeanLruCache[path]
            if (routerBean == null) {
                val className =
                    RouterConstants.packageName + "." + RouterConstants.buildFileForPathName + moduleName
                val pathLoadListener =
                    Class.forName(className).newInstance() as PathLoadListener
                val routerBeanHashMap =
                    pathLoadListener.loadPath()
                if (routerBeanHashMap.size > 0) {
                    routerBean = routerBeanHashMap[path]
                    if (routerBean != null) {
                        routerBeanLruCache.put(path, routerBean)
                    }
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return BundleManager()
    }

    /**
     * 检测path的合法性 "/module（名称）/path（类名）"
     *
     * @param path
     * @return
     */
    private fun pathFormatCheck(path: String?) {
        check(!(path == null || path.isEmpty())) { "传入的path不能为空..." }
        check(path.contains("/")) { "传入的path必须是/module（名称）/path（类名）..." }
        check(path.lastIndexOf("/") != 0) { "传入的path必须是/module（名称）/path（类名）..." }
    }

    fun navigation(context: Context, bundle: Bundle?): Any? {
        if (routerBean != null) {
            return when (routerBean!!.type) {
                RouterBean.TYPE.ACITIVITY -> {
                    val intent = Intent(context, routerBean!!.clazz).putExtras(bundle!!)
                    context.startActivity(intent)
                    null
                }
                RouterBean.TYPE.CALL -> {
                    routerBean!!.clazz.newInstance()
                }
            }
        }
        return null
    }

    fun navigation(activity: Activity, bundle: Bundle?, code: Int) {
        if (routerBean != null) {
            val intent = Intent(activity, routerBean!!.clazz).putExtras(bundle!!)
            activity.startActivityForResult(intent, code)
        }
    }

    companion object {
        val instance: ARouterManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ARouterManager()
        }
    }

}