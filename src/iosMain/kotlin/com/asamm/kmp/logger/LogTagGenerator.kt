package com.asamm.kmp.logger

/**
 * iOS: `NSThread.callStackSymbols` yields unparseable strings like
 * `"0   MyApp  0x00000001000abc $s7MyApp..."` — attempting to turn them into class names
 * produces garbage. Return empty so [LogTagGenerator.generateTag] falls back to `"Unknown"`.
 * Consumers should pass an explicit `tag` on iOS.
 */
actual fun getCurrentStackTrace(): Array<StackTraceElement> = emptyArray()
