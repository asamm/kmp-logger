package com.asamm.kmp.logger

actual fun getCurrentStackTrace(): Array<StackTraceElement> {
    return Throwable().stackTrace
        .map { StackTraceElement(it) }
        .toTypedArray()
}