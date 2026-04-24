# Changelog

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Removed
- Generated `Module.kt` with `getVersion()`. Runtime version queries are not a core logger
  need — the version is already part of the published artifact coordinate
  (`com.asamm.kmp:kmp-logger:<version>`). Dropped `GenerateModuleFileTask` from `buildSrc/`
  and the `generateModuleFile` task + source-set wiring from `build.gradle.kts`.
- **API-breaking (in theory):** `com.asamm.kmp.logger.getVersion()` is no longer exported.
  No known consumer — the function existed only in v0.9.x pre-release validation builds.

## [0.9.1] - 2026-04-24

### Changed
- JitPack no longer publishes iOS klibs — avoids container status-check timeout on
  JitPack's Linux runners. JitPack now ships Android, JVM, JS, and KMP metadata only.
  iOS consumers must resolve from GitHub Packages.

## [0.9.0] - 2026-04-24

Pre-release for end-to-end pipeline validation (JitPack, GitHub Packages, Maven variant resolution
from real consumers). Functionally equivalent to the planned v1.0.0; bumped to v1.0.0 once a few
integration cycles confirm everything is stable.

Extracted from `asamm/kmp-commons:logger` module and relicensed under Apache 2.0.

### Added
- Multiplatform logger targeting Android, JVM, iOS (arm64 + simulator arm64), and JS (browser)
- Top-level DSL functions: `logV`, `logD`, `logI`, `logW`, `logE`
- `LogCategory` + `LogPriority` for category-based filtering with zero-cost disabled calls
- `ILogger` interface for custom backends; `Logger.registerLogger()` to plug one in
- Platform default backends:
  - Android → `android.util.Log`
  - JVM → `java.util.logging.Logger`
  - iOS → `println`
  - JS → `console.log` / `console.warn` / `console.error`
- Publishing: Maven (GitHub Packages + JitPack), npm (GitHub Packages npm registry)
- Package: `com.asamm.kmp.logger` (renamed from `com.asamm.kmp.commons.logger` during extraction)

### Changed from `kmp-commons:logger`
- Default backends on Android and iOS now forward the `Throwable?` argument to `d()`, `w()`,
  and `e()` — previously the exception was silently dropped.
