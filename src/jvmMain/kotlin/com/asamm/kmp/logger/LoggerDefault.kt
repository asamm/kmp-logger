package com.asamm.kmp.logger

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

actual object LoggerDefault : ILogger {

    private val logger = Logger.getLogger(LoggerDefault::class.java.name).apply {
        // enable logger for all levels by default
        level = Level.ALL

        // prevent duplicate logs by disabling propagation
        useParentHandlers = false

        // special handler
        addHandler(ConsoleHandler().apply {
            level = Level.ALL
            formatter = SimpleFormatter()
        })
    }

    actual override fun v(tag: String, msg: String) {
        logger.log(Level.FINE, "[$tag] $msg")
    }

    actual override fun i(tag: String, msg: String) {
        logger.log(Level.INFO, "[$tag] $msg")
    }

    actual override fun d(tag: String, msg: String, ex: Throwable?) {
        logger.log(Level.FINE, "[$tag] $msg", ex)
    }

    actual override fun w(tag: String, msg: String, ex: Throwable?) {
        logger.log(Level.WARNING, "[$tag] $msg", ex)
    }

    actual override fun e(tag: String, msg: String, ex: Throwable?) {
        logger.log(Level.SEVERE, "[$tag] $msg", ex)
    }
}