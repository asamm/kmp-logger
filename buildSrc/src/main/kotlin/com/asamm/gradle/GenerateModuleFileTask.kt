package com.asamm.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Generates a tiny `Module.kt` file exposing `getVersion()` on every platform.
 */
abstract class GenerateModuleFileTask : DefaultTask() {

    @get:Input
    abstract val packageValue: Property<String>

    @get:Input
    abstract val versionValue: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDir.get().asFile
        outputDirectory.mkdirs()
        val file = outputDirectory.resolve("Module.kt")
        file.writeText(
            """
                // !!!
                // !!! Do not modify, generated file by the 'GenerateModuleFileTask' task
                // !!!
                @file:OptIn(ExperimentalJsExport::class)
                package ${packageValue.get()}

                import kotlin.js.ExperimentalJsExport
                import kotlin.js.JsExport

                @JsExport
                fun getVersion() = "${versionValue.get()}"
            """.trimIndent()
        )
    }
}
