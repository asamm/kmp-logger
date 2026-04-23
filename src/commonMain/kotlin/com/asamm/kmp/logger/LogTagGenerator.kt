package com.asamm.kmp.logger

import kotlin.jvm.JvmStatic
import kotlin.text.Regex

object LogTagGenerator {

    // Android's historical Log tag length limit (API ≤ 23). Kept as a consistency rule across
    // platforms — produces identical tags regardless of backend.
    const val MAX_TAG_LENGTH = 23

    // Class-name prefixes skipped during scanning: the logger itself, plus JDK reflection
    // infrastructure (present when the stack reaches user code via reflection — e.g. JUnit).
    private val FRAMEWORK_PREFIXES = arrayOf(
        "com.asamm.kmp.logger.",
        "java.lang.reflect.",
        "java.lang.invoke.",
        "jdk.internal.reflect.",
        "sun.reflect.",
    )

    // Strips trailing Kotlin/Java anonymous-class suffixes like $1, $12, $1$2 — not all
    // nested-class $ separators. `Foo$Bar` (nested) stays as `Foo$Bar`; `Foo$1` becomes `Foo`.
    private val ANONYMOUS_CLASS = Regex("(\\$\\d+)+$")

    /**
     * Extracts a tag from the current call stack — the simple class name of the first frame
     * that is not part of the logger framework itself. Anonymous-class suffixes are stripped
     * and the result is truncated to [MAX_TAG_LENGTH].
     *
     * Returns `"Unknown"` if the platform does not provide a usable stack trace (iOS, JS) or
     * the stack contains only framework frames.
     */
    @JvmStatic
    fun generateTag(customStackTrace: Array<StackTraceElement>? = null): String {
        val stack = customStackTrace ?: getCurrentStackTrace()

        // first frame outside the logger's own package (and JDK reflection plumbing) —
        // that's the user's code
        val frame = stack.firstOrNull { element ->
            FRAMEWORK_PREFIXES.none { element.className.startsWith(it) }
        } ?: return "Unknown"

        var tag = frame.className
        tag = ANONYMOUS_CLASS.replace(tag, "")
        tag = tag.substring(tag.lastIndexOf('.') + 1)

        return if (tag.length <= MAX_TAG_LENGTH) {
            tag
        } else {
            tag.substring(0, MAX_TAG_LENGTH)
        }
    }
}

/**
 * Get the current thread's stack trace, mapped to the multiplatform [StackTraceElement].
 * Platforms that cannot reliably parse stack traces (iOS, JS) return an empty array — callers
 * should provide an explicit tag.
 */
expect fun getCurrentStackTrace(): Array<StackTraceElement>
