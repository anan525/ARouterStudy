package com.dndc.test

import com.dndc.arouter.ARouter
import com.dndc.arouter.CallListener

@ARouter("/test/TestCallListenerImpl")
class TestCallListenerImpl : CallListener {
    override fun call(): Any = R.mipmap.icon_connect
}