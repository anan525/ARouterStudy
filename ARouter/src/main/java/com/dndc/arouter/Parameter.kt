package com.dndc.arouter


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class Parameter(val name: String)