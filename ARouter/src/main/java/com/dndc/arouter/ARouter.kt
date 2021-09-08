package com.dndc.arouter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ARouter(
    /**
     * 路由地址
     */
    val path: String
)