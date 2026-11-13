@file:OptIn(ExperimentalForeignApi::class, ObsoleteWorkersApi::class)

package com.asamm.kmp.logger

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.stringWithContentsOfFile
import platform.posix.O_CREAT
import platform.posix.O_TRUNC
import platform.posix.O_WRONLY
import platform.posix.STDERR_FILENO
import platform.posix.close
import platform.posix.dup
import platform.posix.dup2
import platform.posix.fflush
import platform.posix.open
import platform.posix.stderr
import kotlin.concurrent.AtomicInt
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Sanity checks of the iOS actuals, executed on the iOS simulator (`iosSimulatorArm64Test`).
 */
class IosLoggerSanityTest {

    @BeforeTest
    fun setUp() {
        // make sure the real NSLog-backed LoggerDefault is used
        Logger.registerLogger(null)
    }

    @AfterTest
    fun tearDown() {
        Logger.registerLogger(null)
    }

    // stack trace / tag generation

    @Test
    fun stackTraceIsEmptyOnIos() {
        assertEquals(0, getCurrentStackTrace().size)
    }

    @Test
    fun autoTagFallsBackToUnknown() {
        assertEquals("Unknown", LogTagGenerator.generateTag())
        assertEquals("Unknown", Logger.getValidTag(LogCategory.CORE, null))
    }

    @Test
    fun explicitTagIsKept() {
        val category = LogCategory(tagPrefix = "P-", minPriority = LogPriority.VERBOSE, title = "t", desc = "d")
        assertEquals("P-MyTag", Logger.getValidTag(category, "MyTag"))
    }

    @Test
    fun stackTraceElementToString() {
        assertEquals("Foo.bar(12)", StackTraceElement("Foo", "bar", "Foo.kt", 12).toString())
        assertEquals("Foo.<unknown>(<unknown>)", StackTraceElement("Foo", null, null, null).toString())
    }

    // LoggerDefault (NSLog)

    @Test
    fun defaultLoggerWritesAllLevelsToNsLog() {
        val marker = NSUUID().UUIDString
        val output = captureStderr {
            logV(tag = "SanityV") { "v $marker" }
            logD(tag = "SanityD") { "d $marker" }
            logI(tag = "SanityI") { "i $marker" }
            logW(tag = "SanityW") { "w $marker" }
            logE(tag = "SanityE") { "e $marker" }
        }

        assertContains(output, "[VERBOSE][SanityV] v $marker")
        assertContains(output, "[DEBUG][SanityD] d $marker")
        assertContains(output, "[INFO][SanityI] i $marker")
        assertContains(output, "[WARNING][SanityW] w $marker")
        assertContains(output, "[ERROR][SanityE] e $marker")
    }

    @Test
    fun defaultLoggerAppendsExceptionStackTrace() {
        val marker = NSUUID().UUIDString
        val output = captureStderr {
            logE(tag = "SanityEx", ex = IllegalStateException("boom $marker")) { "failed" }
        }

        assertContains(output, "[ERROR][SanityEx] failed")
        assertContains(output, "IllegalStateException: boom $marker")
    }

    @Test
    fun defaultLoggerIsSafeForFormatSpecifiers() {
        // message is passed as a "%@" argument, so printf-like sequences must be printed verbatim
        // (and must not crash by reading non-existent varargs)
        val msg = "100% done %@ %s %d %n %%"
        val output = captureStderr {
            logI(tag = "SanityFmt") { msg }
        }

        assertContains(output, "[INFO][SanityFmt] $msg")
    }

    @Test
    fun defaultLoggerHandlesUnicodeAndMultiline() {
        val msg = "Příliš žluťoučký kůň 🐎\nsecond line"
        val output = captureStderr {
            logI(tag = "SanityUni") { msg }
        }

        assertContains(output, "[INFO][SanityUni] Příliš žluťoučký kůň 🐎")
        assertContains(output, "second line")
    }

    @Test
    fun loggingFromBackgroundThreadsDoesNotCrash() {
        val counter = AtomicInt(0)
        val workers = List(4) { Worker.start(name = "logger-sanity-$it") }
        try {
            workers
                .map { worker ->
                    worker.execute(TransferMode.SAFE, { counter }) { c ->
                        repeat(50) { i ->
                            logD(tag = "SanityMT") { "message $i" }
                            c.incrementAndGet()
                        }
                    }
                }
                .forEach { it.result }
        } finally {
            workers.forEach { it.requestTermination().result }
        }

        assertEquals(4 * 50, counter.value)
    }

    // helpers

    private fun assertContains(output: String, expected: String) {
        assertTrue(output.contains(expected), "Expected to find '$expected' in captured output:\n$output")
    }

    /**
     * Redirects the process's stderr (where `NSLog` also writes) into a temp file while [block]
     * runs and returns what was written.
     */
    private fun captureStderr(block: () -> Unit): String {
        val path = NSTemporaryDirectory() + "logger-sanity-${NSUUID().UUIDString}.log"
        val fd = open(path, O_WRONLY or O_CREAT or O_TRUNC, 0x1A4) // 0644
        check(fd >= 0) { "Unable to open $path" }

        fflush(stderr)
        val original = dup(STDERR_FILENO)
        dup2(fd, STDERR_FILENO)
        close(fd)
        try {
            block()
        } finally {
            fflush(stderr)
            dup2(original, STDERR_FILENO)
            close(original)
        }

        val content = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: ""
        NSFileManager.defaultManager.removeItemAtPath(path, null)
        return content
    }
}
