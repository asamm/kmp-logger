# kmp-logger

A small, dependency-free multiplatform logger for Kotlin/Multiplatform projects.
Used across Asamm projects (Locus Map, Locus GIS, Locus API).

**Targets:** Android · JVM · iOS (arm64 + simulator arm64) · JS (browser)

**Package:** `com.asamm.kmp.logger`

## Install

### JitPack (public, anonymous)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.asamm:kmp-logger:1.0.0")
}
```

### GitHub Packages (Asamm team)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://maven.pkg.github.com/asamm/kmp-logger") {
            credentials {
                username = System.getenv("GPR_USERNAME")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}
```

```kotlin
dependencies {
    implementation("com.asamm.kmp:kmp-logger:1.0.0")
}
```

Kotlin/Multiplatform consumers resolve the correct platform variant automatically via Gradle
module metadata. Android-only or JVM-only (non-KMP) consumers can also depend on a specific
platform artifact directly: `com.asamm.kmp:kmp-logger-android:1.0.0`,
`com.asamm.kmp:kmp-logger-jvm:1.0.0`, `com.asamm.kmp:kmp-logger-js:1.0.0`.

## Usage

```kotlin
import com.asamm.kmp.logger.logD
import com.asamm.kmp.logger.logE
import com.asamm.kmp.logger.LogCategory
import com.asamm.kmp.logger.LogPriority

// Define a category (once, somewhere central).
val MY_CATEGORY = LogCategory(
    tagPrefix = "MAP-",
    minPriority = LogPriority.DEBUG,
    public = false, // set true if the user may change minPriority at runtime
    title = "Map rendering",
    desc = "Logs from the map rendering pipeline"
)

// Log.
logD(MY_CATEGORY) { "tile loaded: $tileId" }
logE(MY_CATEGORY, ex = throwable) { "failed to render tile $tileId" }
```

The `msg` parameter is a lambda — it is only evaluated when the category's minimum
priority allows the call, so disabled categories have zero allocation cost.

### Custom backends

Register a custom `ILogger` implementation to route log calls elsewhere (file sink, crash
reporter, etc.):

```kotlin
Logger.registerLogger(object : ILogger {
    override fun v(tag: String, msg: String) { /* ... */ }
    override fun i(tag: String, msg: String) { /* ... */ }
    override fun d(tag: String, msg: String, ex: Throwable?) { /* ... */ }
    override fun w(tag: String, msg: String, ex: Throwable?) { /* ... */ }
    override fun e(tag: String, msg: String, ex: Throwable?) { /* ... */ }
})
```

If no custom logger is registered, the module falls back to a platform-appropriate default:

| Platform | Backend |
|---|---|
| Android | `android.util.Log` (Logcat) |
| JVM | `java.util.logging.Logger` + `ConsoleHandler` |
| iOS | `println()` |
| JS | `console.log` / `console.warn` / `console.error` |

## Migrating from `logger-asamm`

The API is a straight descendant of `com.asamm.loggerV2.*`. Migration is a single rename:

```
com.asamm.loggerV2  →  com.asamm.kmp.logger
```

All DSL names (`logV`, `logD`, `logI`, `logW`, `logE`), `LogCategory`, `LogPriority`, and
`ILogger` are preserved.

## License

[Apache 2.0](LICENSE)
