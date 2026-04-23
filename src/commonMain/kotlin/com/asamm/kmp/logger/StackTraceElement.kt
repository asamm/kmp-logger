package com.asamm.kmp.logger

expect class StackTraceElement {
    val className: String
    val methodName: String?
    val fileName: String?
    val lineNumber: Int?
}