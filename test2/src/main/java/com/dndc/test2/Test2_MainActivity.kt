package com.dndc.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.dndc.arouter.ARouter
import com.dndc.arouter.CallListener
import com.dndc.arouter.Parameter
import com.dndc.common.ARouterManager
import com.dndc.common.ParameterManager

@ARouter("/test2/Test2_MainActivity")
class Test2_MainActivity : AppCompatActivity() {

    @Parameter("message")
    var messageGet: String = "原始数据"

    @Parameter("count")
    var count: Int = 0

    @Parameter("/test/TestCallListenerImpl")
    var callParamers: Int? = null

    @Parameter("/test/user")
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2_main)

        ParameterManager.instance.loadParameters(this)


        val iv_main = findViewById<ImageView>(R.id.iv_main)
        callParamers?.let {
            iv_main.setImageResource(it)
        }

        iv_main.setOnClickListener {
            ARouterManager.instance.build("/test/Test_MainActivity").navigation(this)
        }

        Log.e("测试下传来的数据", " message=" + messageGet + "   count=" + count)
        Log.e("测试其他模块的数据", "data=" + user.toString())
    }
}