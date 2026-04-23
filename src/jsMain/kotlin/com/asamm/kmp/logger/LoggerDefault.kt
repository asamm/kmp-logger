package com.asamm.kmp.logger

actual object LoggerDefault : ILogger {

    actual override fun v(tag: String, msg: String) {
        console.log("$tag: $msg")
    }

    actual override fun i(tag: String, msg: String) {
        console.info("$tag: $msg")
    }

    actual override fun d(tag: String, msg: String, ex: Throwable?) {
        console.log("$tag: $msg")
    }

    actual override fun w(tag: String, msg: String, ex: Throwable?) {
        console.warn("$tag: $msg")
    }

    actual override fun e(tag: String, msg: String, ex: Throwable?) {
        console.error("$tag: $msg")
    }
}