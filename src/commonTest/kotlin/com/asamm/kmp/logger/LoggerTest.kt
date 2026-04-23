package com.asamm.kmp.logger

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LoggerTest {

    private lateinit var fake: FakeLogger

    @BeforeTest
    fun setUp() {
        fake = FakeLogger()
        Logger.registerLogger(fake)
    }

    @AfterTest
    fun tearDown() {
        Logger.registerLogger(null)
    }

    private val alwaysOn = LogCategory(
        tagPrefix = "T-",
        minPriority = LogPriority.VERBOSE,
        title = "Test",
        desc = "All levels on",
    )

    private val errorOnly = LogCategory(
        tagPrefix = "E-",
        minPriority = LogPriority.ERROR,
        title = "Errors only",
        desc = "Only ERROR passes",
    )

    // registration

    @Test
    fun registerLogger_routesCallsToRegistered() {
        logI(alwaysOn, tag = "hi") { "hello" }
        assertEquals(1, fake.entries.size)
        assertEquals("I", fake.entries[0].level)
        assertEquals("T-hi", fake.entries[0].tag)
        assertEquals("hello", fake.entries[0].msg)
    }

    @Test
    fun registerLogger_null_clearsReference() {
        Logger.registerLogger(null)
        assertNull(Logger.logger)
    }

    // priority filtering

    @Test
    fun filteredOut_doesNotCallLogger() {
        logV(errorOnly) { "skipped" }
        logD(errorOnly) { "skipped" }
        logI(errorOnly) { "skipped" }
        logW(errorOnly) { "skipped" }
        assertTrue(fake.entries.isEmpty())
    }

    @Test
    fun filteredOut_doesNotEvaluateLambda() {
        var evaluations = 0
        logD(errorOnly) {
            evaluations++
            "should not be built"
        }
        assertEquals(0, evaluations, "msg lambda must not run when category is below minPriority")
    }

    @Test
    fun enabled_evaluatesLambda() {
        var evaluations = 0
        logD(alwaysOn) {
            evaluations++
            "built"
        }
        assertEquals(1, evaluations)
    }

    @Test
    fun errorLevel_passesErrorOnlyCategory() {
        logE(errorOnly, tag = "boom") { "kaboom" }
        assertEquals(1, fake.entries.size)
        assertEquals("E", fake.entries[0].level)
        assertEquals("E-boom", fake.entries[0].tag)
    }

    // exception forwarding

    @Test
    fun logD_forwardsException() {
        val ex = RuntimeException("bad")
        logD(alwaysOn, tag = "t", ex = ex) { "msg" }
        assertEquals(1, fake.entries.size)
        assertSame(ex, fake.entries[0].ex)
    }

    @Test
    fun logW_forwardsException() {
        val ex = RuntimeException("bad")
        logW(alwaysOn, tag = "t", ex = ex) { "msg" }
        assertSame(ex, fake.entries[0].ex)
    }

    @Test
    fun logE_forwardsException() {
        val ex = RuntimeException("bad")
        logE(alwaysOn, tag = "t", ex = ex) { "msg" }
        assertSame(ex, fake.entries[0].ex)
    }

    @Test
    fun logD_nullException_isForwarded() {
        logD(alwaysOn, tag = "t") { "msg" }
        assertNull(fake.entries[0].ex)
    }

    // tag prefix composition

    @Test
    fun getValidTag_prependsCategoryPrefix() {
        val tag = Logger.getValidTag(alwaysOn, "explicit")
        assertEquals("T-explicit", tag)
    }

    @Test
    fun getValidTag_withNullTag_autoGenerates() {
        val tag = Logger.getValidTag(alwaysOn, null)
        // auto-generated tag is platform-dependent but must be non-empty after the prefix
        assertTrue(tag.startsWith("T-"), "expected prefix preserved, got: $tag")
        assertTrue(tag.length > "T-".length, "expected auto-generated tag after prefix, got: $tag")
    }

    // all levels

    @Test
    fun allFiveLevels_routeIndependently() {
        logV(alwaysOn, tag = "a") { "vmsg" }
        logD(alwaysOn, tag = "b") { "dmsg" }
        logI(alwaysOn, tag = "c") { "imsg" }
        logW(alwaysOn, tag = "d") { "wmsg" }
        logE(alwaysOn, tag = "e") { "emsg" }

        assertEquals(5, fake.entries.size)
        assertEquals(listOf("V", "D", "I", "W", "E"), fake.entries.map { it.level })
        assertEquals(
            listOf("T-a", "T-b", "T-c", "T-d", "T-e"),
            fake.entries.map { it.tag },
        )
        assertEquals(
            listOf("vmsg", "dmsg", "imsg", "wmsg", "emsg"),
            fake.entries.map { it.msg },
        )
    }
}
