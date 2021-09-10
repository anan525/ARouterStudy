package com.dndc.arouter

object RouterConstants {
    /**
     * 包名
     */
    const val packageName: String = "com.dndc.arouter"

    /**
     * arouter注解包名
     */
    const val annotationPath = "com.dndc.arouter.ARouter"

    /**
     * Parameter注解包名
     */
    const val parameterPath = "com.dndc.arouter.Parameter"
    /**
     * aroutermanage的包名
     */
    const val arouterManagePath="com.dndc.common.ARouterManager"
    /**
     * moduleName
     */
    const val MODULE_NAME = "moduleName"

    /**
     * activity的包名
     */
    const val ACTIVITYTYPE = "android.app.Activity"

    /**
     * call类型的包名
     */
    const val CALLTYPE = "com.dndc.arouter.CallListener"

    /**
     * context的包名
     */
    const val CONTEXTTYPE = "android.content.Context"

    /**
     *生成文件的前缀
     */
    const val buildFileForPathName = "ARouter_Path_"

    /**
     *生成parameter文件的后缀
     */
    const val buildFileEndName = "_ParameterImpl"

    /**
     * kotlin的int
     */
    const val intType="Kotlin.Int"

    /**
     * kotlin的String
     */
    const val stringType="java.lang.String"
}