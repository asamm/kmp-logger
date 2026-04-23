package com.asamm.kmp.logger

/**
 * Captures all log calls for assertion in tests. Thread-unsafe by design — tests are single-threaded.
 */
internal class FakeLogger : ILogger {

    data class Entry(
        val level: String,
        val tag: String,
        val msg: String,
        val ex: Throwable? = null,
    )

    val entries: MutableList<Entry> = mutableListOf()

    override fun v(tag: String, msg: String) {
        entries += Entry("V", tag, msg)
    }

    override fun i(tag: String, msg: String) {
        entries += Entry("I", tag, msg)
    }

    override fun d(tag: String, msg: String, ex: Throwable?) {
        entries += Entry("D", tag, msg, ex)
    }

    override fun w(tag: String, msg: String, ex: Throwable?) {
        entries += Entry("W", tag, msg, ex)
    }

    override fun e(tag: String, msg: String, ex: Throwable?) {
        entries += Entry("E", tag, msg, ex)
    }
}
