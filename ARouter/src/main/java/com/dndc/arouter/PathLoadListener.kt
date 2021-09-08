package com.dndc.arouter

interface PathLoadListener {

    fun loadPath(): HashMap<String, RouterBean>
}