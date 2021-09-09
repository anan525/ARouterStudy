package com.dndc.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dndc.arouter.ARouter

@ARouter("/test/Test_MainActivity")
class Test_MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test__main)
    }
}
