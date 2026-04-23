package com.asamm.kmp.logger

/**
 * JS: `Throwable().stackTraceToString()` returns engine-specific text (`"at foo (file.js:12)"`)
 * that isn't a class name. Return empty so [LogTagGenerator.generateTag] falls back to
 * `"Unknown"`. Consumers should pass an explicit `tag` on JS.
 */
actual fun getCurrentStackTrace(): Array<StackTraceElement> = emptyArray()
