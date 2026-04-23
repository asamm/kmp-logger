package com.asamm.kmp.logger

import kotlin.test.Test
import kotlin.test.assertTrue

class LogPriorityTest {

    @Test
    fun priorities_monotonicIncreasing() {
        // VERBOSE < DEBUG < INFO < WARN < ERROR — ordering is load-bearing for the filter check.
        assertTrue(LogPriority.VERBOSE.priority < LogPriority.DEBUG.priority)
        assertTrue(LogPriority.DEBUG.priority < LogPriority.INFO.priority)
        assertTrue(LogPriority.INFO.priority < LogPriority.WARN.priority)
        assertTrue(LogPriority.WARN.priority < LogPriority.ERROR.priority)
    }
}
