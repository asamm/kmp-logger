import com.asamm.gradle.moduleVersion
import dev.petuska.npm.publish.task.NpmPublishTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.npmPublish)
    `maven-publish`
}

val modulePackage = "com.asamm.kmp"
val moduleName = "logger"
val moduleVersion = moduleVersion(libs.versions.version.get())

group = modulePackage
version = moduleVersion

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "$modulePackage.$moduleName"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    iosArm64()
    iosSimulatorArm64()

    jvm()

    js(IR) {
        useEsModules()
        browser {}
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nonJsMain by creating { dependsOn(commonMain) }
        val androidMain by getting { dependsOn(nonJsMain) }
        val jvmMain by getting { dependsOn(nonJsMain) }
        val iosMain by creating { dependsOn(nonJsMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        val nonJsTest by creating { dependsOn(commonTest) }
        val jvmTest by getting { dependsOn(nonJsTest) }
        val iosTest by creating { dependsOn(nonJsTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }
    }
}

publishing {
    repositories {
        // GitHub Packages — auth'd channel for the team.
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/asamm/kmp-logger")
            credentials {
                username = System.getenv("GPR_USERNAME")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }

    // Publications (mavenLocal, jitpack) come from the default maven-publish setup.
    // JitPack consumes the generated POMs directly from the tag — no extra config needed.
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("kmp-logger")
            description.set("Multiplatform logger for Asamm projects (Android, JVM, iOS, JS).")
            url.set("https://github.com/asamm/kmp-logger")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            scm {
                url.set("https://github.com/asamm/kmp-logger")
                connection.set("scm:git:https://github.com/asamm/kmp-logger.git")
                developerConnection.set("scm:git:ssh://git@github.com/asamm/kmp-logger.git")
            }
            developers {
                developer {
                    organization.set("Asamm Software")
                    organizationUrl.set("https://www.asamm.com")
                }
            }
        }
    }
}

npmPublish {
    organization.set("asamm")
    packages {
        named("js") {
            packageName.set("kmp-logger")
        }
        configureEach {
            packageJson {
                repository {
                    type = "git"
                    url = "https://github.com/asamm/kmp-logger.git"
                }
            }
        }
    }
    registries {
        github {
            authToken.set(System.getenv("GPR_TOKEN"))
        }
    }
}

// Snapshots are published with "next" tag, to keep "latest" intact.
if (findProperty("isSnapshot") != null) {
    tasks.withType<NpmPublishTask>().configureEach {
        tag.set("next")
    }
}
