package com.asamm.kmp.logger

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * An enum for log priorities.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class LogPriority(val priority: Int) {

    /**
     * Verbose priority.
     */
    VERBOSE(2),

    /**
     * Debug priority.
     * Optimal base minimal priority value for debug builds.
     */
    DEBUG(3),

    /**
     * Info priority.
     */
    INFO(4),

    /**
     * Warning priority.
     * Optimal base minimal priority value for release builds.
     */
    WARN(5),

    /**
     * Error priority.
     */
    ERROR(6),
}