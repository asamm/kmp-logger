package com.asamm.kmp.logger

import kotlin.test.Test
import kotlin.test.assertEquals

class LogTagGeneratorTest {

    // Simulates the real inlined `logD { }` call path: user code calls Logger.getValidTag,
    // which calls generateTag. The real path does NOT go through reflection.
    @Test
    fun `generateTag picks user class from simulated logD call path`() {
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.Logger"),
            frame("com.example.UserCode"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("UserCode", tag)
    }

    @Test
    fun `generateTag removes anonymous class digit suffix`() {
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.Logger"),
            // Kotlin anonymous objects compile to names like Foo$1, Foo$2
            frame("com.example.LogTagGeneratorTest\$1"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("LogTagGeneratorTest", tag)
    }

    @Test
    fun `generateTag removes chained anonymous suffixes`() {
        val fake = arrayOf(
            frame("com.example.Outer\$1\$2"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("Outer", tag)
    }

    @Test
    fun `generateTag preserves nested class name`() {
        // Fake frame: a proper Kotlin nested class should NOT be stripped back to the outer.
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.Logger"),      // framework frame — skipped
            frame("com.asamm.kmp.logger.LogTagGenerator"), // framework frame — skipped
            frame("com.example.Outer\$Nested"),        // user code — kept as-is
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("Outer\$Nested", tag)
    }

    @Test
    fun `generateTag skips framework frames`() {
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.LoggerKt"),
            frame("com.asamm.kmp.logger.Logger"),
            frame("com.example.UserCode"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("UserCode", tag)
    }

    @Test
    fun `generateTag skips arbitrary depth of framework frames`() {
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.LogTagGenerator\$generateTag\$default"),
            frame("com.asamm.kmp.logger.LoggerKt"),
            frame("com.asamm.kmp.logger.Logger"),
            frame("com.asamm.kmp.logger.LoggerKt\$logD\$1"),
            frame("com.example.UserCode"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("UserCode", tag)
    }

    @Test
    fun `generateTag returns the nearest user frame through wrapper layers`() {
        // If user wraps logD in their own helper, the nearest non-framework frame is the
        // wrapper. That's the semantically correct class to tag with.
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.Logger"),
            frame("com.example.MyLogHelper"),
            frame("com.example.UserCode"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("MyLogHelper", tag)
    }

    @Test
    fun `generateTag truncates long tag`() {
        val longClassName = "com.example.very.long.classname.ThisIsNameOfTheVeryLongClassThatHasToBeTruncated"
        val fake = arrayOf(frame(longClassName))
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals(LogTagGenerator.MAX_TAG_LENGTH, tag.length)
    }

    @Test
    fun `generateTag handles empty stack trace`() {
        val tag = LogTagGenerator.generateTag(emptyArray())
        assertEquals("Unknown", tag)
    }

    @Test
    fun `generateTag handles stack with only framework frames`() {
        val fake = arrayOf(
            frame("com.asamm.kmp.logger.LogTagGenerator"),
            frame("com.asamm.kmp.logger.Logger"),
        )
        val tag = LogTagGenerator.generateTag(fake)
        assertEquals("Unknown", tag)
    }

    // helper — builds an actual-class StackTraceElement via the JVM constructor
    private fun frame(className: String): StackTraceElement =
        StackTraceElement(java.lang.StackTraceElement(className, "method", "File.kt", 1))
}
