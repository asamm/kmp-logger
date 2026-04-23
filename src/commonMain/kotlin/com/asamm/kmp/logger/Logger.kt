@file:OptIn(ExperimentalJsExport::class)

package com.asamm.kmp.logger

import kotlin.concurrent.Volatile
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

// LOG CALLS

/**
 * "VERBOSE" log message.
 */
@JsExport
inline fun logV(
    category: LogCategory = LogCategory.CORE,
    tag: String? = null,
    msg: () -> String
) {
    if (category.minPriority.priority <= LogPriority.VERBOSE.priority) {
        (Logger.logger ?: LoggerDefault).v(
            tag = Logger.getValidTag(category, tag),
            msg = msg()
        )
    }
}

/**
 * "DEBUG" log message.
 */
@JsExport
inline fun logD(
    category: LogCategory = LogCategory.CORE,
    tag: String? = null,
    ex: Throwable? = null,
    msg: () -> String
) {
    if (category.minPriority.priority <= LogPriority.DEBUG.priority) {
        (Logger.logger ?: LoggerDefault).d(
            tag = Logger.getValidTag(category, tag),
            msg = msg(),
            ex = ex
        )
    }
}

/**
 * "INFO" log message.
 */
@JsExport
inline fun logI(
    category: LogCategory = LogCategory.CORE,
    tag: String? = null,
    msg: () -> String
) {
    if (category.minPriority.priority <= LogPriority.INFO.priority) {
        (Logger.logger ?: LoggerDefault).i(
            tag = Logger.getValidTag(category, tag),
            msg = msg()
        )
    }
}

/**
 * "WARNING" log message.
 */
@JsExport
inline fun logW(
    category: LogCategory = LogCategory.CORE,
    tag: String? = null,
    ex: Throwable? = null,
    msg: () -> String
) {
    if (category.minPriority.priority <= LogPriority.WARN.priority) {
        (Logger.logger ?: LoggerDefault).w(
            tag = Logger.getValidTag(category, tag),
            msg = msg(),
            ex = ex
        )
    }
}

/**
 * "ERROR" log message.
 */
@JsExport
inline fun logE(
    category: LogCategory = LogCategory.CORE,
    tag: String? = null,
    ex: Throwable? = null,
    msg: () -> String
) {
    if (category.minPriority.priority <= LogPriority.ERROR.priority) {
        (Logger.logger ?: LoggerDefault).e(
            tag = Logger.getValidTag(category, tag),
            msg = msg(),
            ex = ex
        )
    }
}

object Logger {

    fun getValidTag(category: LogCategory, tag: String?): String {
        return "${category.tagPrefix}${tag ?: LogTagGenerator.generateTag()}"
    }

    //*************************************************
    // LOGGER INTERFACE
    //*************************************************

    // reference to logger implementation
    @Volatile
    var logger: ILogger? = null
        private set

    /**
     * Register [logger] implementation.
     */
    fun registerLogger(logger: ILogger?) {
        this.logger = logger
    }
}