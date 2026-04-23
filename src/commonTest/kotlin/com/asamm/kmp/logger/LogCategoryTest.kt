package com.asamm.kmp.logger

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class LogCategoryTest {

    @Test
    fun core_hasEmptyPrefixAndVerbosePriority() {
        assertEquals("", LogCategory.CORE.tagPrefix)
        assertEquals(LogPriority.VERBOSE, LogCategory.CORE.minPriority)
        assertFalse(LogCategory.CORE.public, "CORE must not be user-configurable by default")
    }

    @Test
    fun dataClass_equalityOnSameValues() {
        val a = LogCategory("X-", LogPriority.DEBUG, false, "t", "d")
        val b = LogCategory("X-", LogPriority.DEBUG, false, "t", "d")
        assertEquals(a, b)
    }

    @Test
    fun dataClass_inequalityOnPrefixDifference() {
        val a = LogCategory("X-", LogPriority.DEBUG, false, "t", "d")
        val b = LogCategory("Y-", LogPriority.DEBUG, false, "t", "d")
        assertNotEquals(a, b)
    }

    @Test
    fun minPriority_isMutable_forRuntimeLevelChange() {
        val c = LogCategory("X-", LogPriority.DEBUG, public = true, "t", "d")
        c.minPriority = LogPriority.ERROR
        assertEquals(LogPriority.ERROR, c.minPriority)
    }
}
