package com.dndc.kotlinarouterproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dndc.arouter.ARouter
import com.dndc.arouter.Parameter
import com.dndc.common.ARouterManager

@ARouter("/app/TestActivity")
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv_main = findViewById<TextView>(R.id.tv_main)
        tv_main.setOnClickListener {
            ARouterManager.instance.build("/test2/Test2_MainActivity")
                .putString("message", "从app传过来的数据")
                .putInt("count", 100)
                .navigation(this)
        }

    }
}