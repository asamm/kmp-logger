package com.asamm.kmp.logger

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Category for log messages.
 *
 * @param tagPrefix prefix for the tag that allows to easily find logs from single category
 * @param minPriority minimal defined priority of log messages.
 * @param public flag that define if category may be modified by the user
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class LogCategory(
    val tagPrefix: String,
    var minPriority: LogPriority = LogPriority.WARN,
    val public: Boolean = false,
    val title: String,
    val desc: String,
) {

    companion object {

        val CORE = LogCategory(
            tagPrefix = "",
            minPriority = LogPriority.VERBOSE,
            title = "Core",
            desc = "Core/not specified logs"
        )
    }
}