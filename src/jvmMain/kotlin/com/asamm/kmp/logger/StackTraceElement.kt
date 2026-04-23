package com.asamm.kmp.logger

actual class StackTraceElement(
    stackTraceElement: java.lang.StackTraceElement
) {

    actual val className: String = stackTraceElement.className

    actual val methodName: String? = stackTraceElement.methodName

    actual val fileName: String? = stackTraceElement.fileName

    actual val lineNumber: Int? = stackTraceElement.lineNumber
}