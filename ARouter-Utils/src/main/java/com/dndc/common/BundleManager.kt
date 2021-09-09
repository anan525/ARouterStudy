package com.dndc.common

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

class BundleManager internal constructor() {
    private val bundle: Bundle
    fun putString(key: String?, value: String?): BundleManager {
        bundle.putString(key, value)
        return this
    }

    fun putInt(key: String?, value: Int): BundleManager {
        bundle.putInt(key, value)
        return this
    }

    fun putBundle(key: String?, value: Bundle?): BundleManager {
        bundle.putBundle(key, value)
        return this
    }

    fun putBoolean(key: String?, value: Boolean): BundleManager {
        bundle.putBoolean(key, value)
        return this
    }

    fun putSerializable(key: String?, value: Serializable?): BundleManager {
        bundle.putSerializable(key, value)
        return this
    }

    fun putParcelable(key: String?, value: Parcelable?): BundleManager {
        bundle.putParcelable(key, value)
        return this
    }

    fun navigation(context: Context): Any? {
     return   ARouterManager.instance.navigation(context, bundle)
    }

    fun navigationforResult(activity: Activity) {
        ARouterManager.instance.navigation(activity, bundle, 163)
    }

    init {
        bundle = Bundle()
    }
}