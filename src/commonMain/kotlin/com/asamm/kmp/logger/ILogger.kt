package com.asamm.kmp.logger

/**
 * Interface for custom logger logic.
 */
interface ILogger {

    fun v(tag: String, msg: String)

    fun i(tag: String, msg: String)

    fun d(tag: String, msg: String, ex: Throwable?)

    fun w(tag: String, msg: String, ex: Throwable?)

    fun e(tag: String, msg: String, ex: Throwable?)
}