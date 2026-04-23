package com.asamm.kmp.logger

actual class StackTraceElement(
    actual val className: String,
    actual val methodName: String?,
    actual val fileName: String?,
    actual val lineNumber: Int?,
) {

    override fun toString(): String {
        return "${className}.${methodName ?: "<unknown>"}(${lineNumber ?: "<unknown>"})"
    }
}