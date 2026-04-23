package com.asamm.kmp.logger

import platform.Foundation.NSLog

actual object LoggerDefault : ILogger {

    private fun log(level: String, tag: String, msg: String, ex: Throwable? = null) {
        val errorText = ex?.let { "\n${it.stackTraceToString()}" } ?: ""
        NSLog("%@", "[$level][$tag] $msg$errorText")
    }

    actual override fun v(tag: String, msg: String) {
        log("VERBOSE", tag, msg)
    }

    actual override fun i(tag: String, msg: String) {
        log("INFO", tag, msg)
    }

    actual override fun d(tag: String, msg: String, ex: Throwable?) {
        log("DEBUG", tag, msg, ex)
    }

    actual override fun w(tag: String, msg: String, ex: Throwable?) {
        log("WARNING", tag, msg, ex)
    }

    actual override fun e(tag: String, msg: String, ex: Throwable?) {
        log("ERROR", tag, msg, ex)
    }
}