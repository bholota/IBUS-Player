package com.bholota.ibusplayer.utils

import android.util.Log
import com.bholota.ibusplayer.BuildConfig

class L(val tag: String) {

    fun e(msg: String, e: Exception? = null) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.LOGS) {
            if (e == null) Log.e(tag, msg) else Log.e(tag, msg, e)
        }
    }

    fun w(msg: String, e: Exception? = null) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.LOGS) {
            if (e == null) Log.w(tag, msg) else Log.w(tag, msg, e)
        }
    }

    fun d(msg: String, e: Exception? = null) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.LOGS) {
            if (e == null) Log.d(tag, msg) else Log.d(tag, msg, e)
        }
    }

    fun i(msg: String, e: Exception? = null) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.LOGS) {
            if (e == null) Log.i(tag, msg) else Log.i(tag, msg, e)
        }
    }

    fun v(msg: String, e: Exception? = null) {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.LOGS) {
            if (e == null) Log.v(tag, msg) else Log.v(tag, msg, e)
        }
    }
}