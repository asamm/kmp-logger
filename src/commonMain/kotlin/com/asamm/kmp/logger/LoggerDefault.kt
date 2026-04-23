package com.asamm.kmp.logger

expect object LoggerDefault : ILogger {

    override fun v(tag: String, msg: String)

    override fun i(tag: String, msg: String)

    override fun d(tag: String, msg: String, ex: Throwable?)

    override fun w(tag: String, msg: String, ex: Throwable?)

    override fun e(tag: String, msg: String, ex: Throwable?)
}