package com.dndc.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.dndc.arouter.ARouter
import com.dndc.arouter.CallListener
import com.dndc.common.ARouterManager

@ARouter("/test2/Test2_MainActivity")
class Test2_MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2_main)
        val iv_main = findViewById<ImageView>(R.id.iv_main)
        ARouterManager.instance.build("/test/TestCallListenerImpl").navigation(this)?.let {
            it as CallListener
            val i = it.call() as Int
            iv_main.setImageResource(i)
        }

        iv_main.setOnClickListener {
            ARouterManager.instance.build("/test/Test_MainActivity").navigation(this)
        }

    }
}