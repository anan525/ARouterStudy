package com.dndc.test

import com.dndc.arouter.ARouter
import com.dndc.arouter.CallListener
import com.google.gson.Gson

@ARouter("/test/TestCallListenerImpl")
class TestCallListenerImpl : CallListener {
    override fun call(): Any = R.mipmap.icon_connect
}

@ARouter("/test/user")//建议对象类型都用json传递,里面有gson解析出具体类型
class UserCallListenerImpl : CallListener {
    override fun call(): Any = Gson().toJson(User("张三", 19))

}