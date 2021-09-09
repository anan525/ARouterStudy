package com.dndc.arouter

class RouterBean {
    lateinit var path: String
    lateinit var clazz: Class<*>
    lateinit var type: TYPE
    lateinit var clazzPath: String

    fun build(path: String, classPath: String, type: TYPE): RouterBean {
        this.path = path
        this.type = type
        this.clazzPath = classPath
        return this
    }

    fun build(path: String, clazz: Class<*>, type: TYPE): RouterBean {
        this.path = path
        this.type = type
        this.clazz = clazz
        return this
    }

    enum class TYPE {
        ACITIVITY, CALL
    }

}