```skill
---
name: KMP Seraphim Architecture
description: Standards for Kotlin Multiplatform module design in this repository. Use whenever implementing or refactoring KMP modules in this workspace, especially in apps/*, shareds/*, core/*, domain/*, and utils/*.
metadata:
  labels: [kmp, kotlin-multiplatform, architecture, gradle, modularization]
  triggers:
    files: ['**/*.kt', '**/*.kts', 'settings.gradle.kts', 'build.gradle.kts', 'gradle/libs.versions.toml']
    keywords: [kmp, kotlin multiplatform, commonMain, androidMain, nativeMain, expect/actual, sourceSets]
---

# KMP Seraphim Architecture

## **Priority: P0 (CRITICAL)**

Repository-specific rules for building Kotlin Multiplatform code that matches this project’s current module layout and dependency direction.

## Target Module Layout (This Repo)

- **app entrypoints**: `apps/<feature>/` for Android application packaging and runtime wiring.
- **shared feature code**: `shareds/<feature>/` for KMP feature logic (`commonMain`, `androidMain`, `nativeMain`).
- **cross-feature core**: `core/<capability>/` for reusable KMP capabilities (for example networking).
- **domain contracts/scaffolding**: `domain/<feature>/` for pure domain abstractions and use-case direction.
- **cross-cutting helpers**: `utils/` for shared utility APIs used by multiple modules.

## Dependency Direction (Enforce)

- `apps/*` can depend on `shareds/*`, `core/*`, `domain/*`, `utils`.
- `shareds/*` can depend on `core/*`, `domain/*`, `utils`.
- `core/*` can depend on `domain/*`, `utils` only when needed.
- `domain/*` should remain pure Kotlin business layer and avoid platform APIs.
- `utils` must stay lightweight and must not depend on feature modules.

## Source Set Standards

- Put business rules and contracts in `commonMain` first.
- Keep platform-specific code in `androidMain` / `nativeMain` only when unavoidable.
- Use `expect/actual` for platform bridges (filesystem, device info, secure storage, etc.).
- Keep source set APIs stable: `commonMain` defines interfaces, platform sets provide implementations.

## Build and Gradle Standards

- Use Kotlin DSL (`build.gradle.kts`) and Version Catalog (`gradle/libs.versions.toml`).
- Prefer convention plugins from `build-logic/convention/` for shared configuration.
- Keep target declarations explicit and minimal; do not add unused targets.
- Put dependency versions in catalog, not inline in module build scripts.

## Implementation Checklist

- [ ] Is this module placed in the correct layer (`apps`, `shareds`, `core`, `domain`, `utils`)?
- [ ] Is business logic in `commonMain` before creating platform variants?
- [ ] Are `expect/actual` abstractions used instead of leaking platform APIs?
- [ ] Does dependency direction follow the rules above (no reverse coupling)?
- [ ] Is the Gradle config aligned with existing convention plugins and catalog aliases?

## Anti-Patterns

- **Platform leakage in `commonMain`**: no Android SDK or iOS-specific APIs directly in shared code.
- **Feature-to-feature coupling**: avoid direct dependency between unrelated `shareds/*` modules.
- **Bloated `utils`**: do not move domain/feature logic into utility modules.
- **Inline versions**: avoid hardcoded dependency/plugin versions in module scripts.
- **Unbounded target growth**: avoid adding new KMP targets without product/runtime need.

## Minimal Module Template

```kotlin
kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:network"))
            }
        }
        val androidMain by getting
        val nativeMain by getting
    }
}
```

## Expect/Actual Template

```kotlin
// commonMain
expect interface PlatformLogger {
    fun log(message: String)
}

// androidMain or nativeMain
actual class PlatformLoggerImpl : PlatformLogger {
    actual override fun log(message: String) {
        println(message)
    }
}
```

## Definition of Done

A KMP change is complete only when module placement, source set boundaries, dependency direction, and Gradle conventions all match this repository’s architecture.

```