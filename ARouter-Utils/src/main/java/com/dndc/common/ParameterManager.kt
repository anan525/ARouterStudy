package com.dndc.common

import com.dndc.arouter.ParameterLoadListener
import com.dndc.arouter.RouterConstants

class ParameterManager {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ParameterManager()
        }
    }

    fun loadParameters(target: Any) {
        val simpleName = target::class.java.simpleName

        val finalClassName = simpleName + RouterConstants.buildFileEndName

        try {
            val parameterLoaderImpl =
                Class.forName("${RouterConstants.packageName}." + finalClassName)
                    .newInstance() as ParameterLoadListener
            parameterLoaderImpl.parameterLoad(target)
        } catch (e: Exception) {
        }
    }
}