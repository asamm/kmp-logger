package com.asamm.kmp.logger

import android.util.Log

actual object LoggerDefault : ILogger {

    actual override fun v(tag: String, msg: String) {
        Log.v(tag, msg)
    }

    actual override fun i(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    actual override fun d(tag: String, msg: String, ex: Throwable?) {
        Log.d(tag, msg, ex)
    }

    actual override fun w(tag: String, msg: String, ex: Throwable?) {
        Log.w(tag, msg, ex)
    }

    actual override fun e(tag: String, msg: String, ex: Throwable?) {
        Log.e(tag, msg, ex)
    }
}