# AGENTS

## Use skills before working

- Before handling any request, check `.agents/skills` for a relevant skill.
- If a relevant skill exists, read its `SKILL.md` and follow the required steps and checklist.
- If no suitable skill exists, state the reason and proceed with the normal approach.
- If multiple skills seem relevant, prefer the one with the clearest and most up-to-date guidance.

## Minimum workflow

1. Identify the request topic.
2. Compare it against skills in `.agents/skills`.
3. Apply the chosen skill, including mandatory steps and checklist items.
4. Report results and explicitly note any steps that could not be performed.
# AGENTS instructions

## Project overview

RMap Mobile is the Android frontend for the RMap platform — helping learners map current skills to career goals and navigate personalized developer learning roadmaps. The backend API lives in a separate repository.

## Development Environment

- Language: Kotlin (JVM 17+)
- Build tool: Gradle (Kotlin DSL)
- Package manager: Gradle with version catalog (`libs.versions.toml`)
- Min SDK: 26 (Android 8.0) | Target SDK: 35 (Android 15)
- Sync dependencies: `./gradlew --refresh-dependencies`
- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease`
- Install on device: `./gradlew installDebug`
- Run all checks: `./gradlew check`
- Run lint: `./gradlew lint`
- Run unit tests: `./gradlew test`
- Run instrumented tests: `./gradlew connectedAndroidTest`
- Clean build: `./gradlew clean`

## Codebase Structure

```
app/                          — Single application module
  src/
    main/
      java/com/rmap/mobile/   — Kotlin source files (folder named java, language is Kotlin)
        di/                   — Dependency injection (Hilt modules)
        data/                 — Data layer
          local/              — Room database (DAOs, entities)
          remote/             — API service (Retrofit, DTOs)
          repository/         — Repository implementations
        domain/               — Domain layer
          model/              — Domain models
          repository/         — Repository interfaces
          usecase/            — Use cases / interactors
        presentation/         — UI layer
          ui/                 — Screens and components (Compose)
            auth/             — Authentication screens
            home/             — Home / dashboard
            roadmap/          — Roadmap viewer
            profile/          — User profile
            common/           — Shared composables
          viewmodel/          — ViewModels (per feature)
          navigation/         — NavHost, routes, deep links
        util/                 — Extension functions, helpers
        MainActivity.kt       — Single-activity entry point
      res/                    — Resources (strings, themes, drawables)
      AndroidManifest.xml
    test/                     — JVM unit tests [unitTest]
    androidTest/              — Instrumented / UI tests (Espresso / Compose UI Test)
  build.gradle.kts            — App-level Gradle config
  proguard-rules.pro          — R8/ProGuard rules

gradle/
  libs.versions.toml          — Version catalog (add all new dependencies here)
build.gradle.kts              — Root Gradle config
settings.gradle.kts           — Module declarations
gradle.properties             — Gradle JVM args and flags
local.properties              — Local secrets (not committed to VCS)
openapi.yaml              — Backend API contract (source of truth for DTOs and endpoints)
AGENTS.md                     — This file
README.md                     — Project overview
```

> **Note:** The source folder is named `java/` (Android Studio default) but all code is written in Kotlin. Do not add Java source files. The package name is `com.rmap.mobile`.

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose (Material 3) |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Local DB | Room |
| Auth | JWT — Bearer token in `Authorization` header |
| Image loading | Coil |
| Navigation | Compose Navigation |
| Async | Kotlin Coroutines + Flow |
| Logging | Timber |
| Serialization | Gson |

All new dependencies must be declared in `gradle/libs.versions.toml` before adding to `build.gradle.kts`.

## Backend API

- This app is **frontend only**; all business logic lives in the backend (separate repo).
- Base URL: stored in `local.properties` as `BASE_URL` — never hardcode.
- Auth: Bearer token in `Authorization` header; token stored in `EncryptedSharedPreferences`.
- API contract: see [`openapi.yaml`](./openapi.yaml) at the project root for all endpoints, request/response shapes, and error codes. Always refer to this file before writing DTOs or API service interfaces.
- Handle all API errors in the `data` layer; surface them to `presentation` via `Result<T>` or `UiState`.

## UI Conventions

- Use **Material 3** components and theming throughout.
- Color, typography, and shape tokens are defined in `presentation/ui/theme/`.
- All screens follow the pattern: `XxxScreen.kt` (Compose) → `XxxViewModel.kt` → `XxxUseCase.kt`.
- Model loading/error/success with a sealed `UiState<T>`:
  ```kotlin
  sealed class UiState<out T> {
      object Loading : UiState<Nothing>()
      data class Success<T>(val data: T) : UiState<T>()
      data class Error(val message: String) : UiState<Nothing>()
  }
  ```
- Never put business logic inside Composable functions — delegate to ViewModel.
- All user-facing strings must use `stringResource()`; no hardcoded string literals in UI.

## Code Style & Conventions

- Use Kotlin across all modules; avoid Java unless integrating a Java-only library.
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and the Android team's style guide.
- Format with `ktlint` (enforced via Gradle plugin); run `./gradlew ktlintFormat` before committing.
- Use `detekt` for static analysis; resolve all issues before submitting.
- Keep imports sorted; remove unused imports.
- Prefer Kotlin idioms: `data class`, `sealed class`, `object`, `companion object`, extension functions, `when` expressions.
- Follow Clean Architecture: **Presentation → Domain → Data**; dependencies only flow inward.
- Use **Jetpack Compose** for all UI; avoid XML layouts unless absolutely necessary.
- Use **Hilt** for dependency injection across all layers.
- Use **StateFlow** / **SharedFlow** for reactive state; avoid `LiveData` in new code.
- Use **Kotlin Coroutines** and **Flow** for async operations; avoid RxJava.
- Keep `ViewModel` free of Android framework dependencies where possible.
- Use Conventional Commits (configured with commitlint).

### Architecture Pattern

- **MVVM + Clean Architecture**
- Screen → ViewModel → UseCase → Repository → DataSource
- UI state modeled as sealed classes or data classes (`UiState<T>`)
- Side effects emitted via `SharedFlow` (e.g., navigation events, toasts)

### TypeSafety & Validation

- Keep strict typing; avoid `!!` (non-null assertion); prefer `?: return` or safe calls.
- Use `sealed class` / `Result<T>` for error handling across layers.
- Validate API responses with `kotlinx.serialization` or `Gson`; handle malformed payloads early.
- Use `@StringRes`, `@DrawableRes`, `@ColorRes` annotations for resource references.

### Naming Conventions

- Folders/Files: kebab-case for Gradle modules; PascalCase for Kotlin files
- Composables: PascalCase (e.g., `RoadmapCard`, `AuthScreen`)
- ViewModels: PascalCase + `ViewModel` suffix (e.g., `RoadmapViewModel`)
- Use Cases: PascalCase + `UseCase` suffix (e.g., `GetRoadmapUseCase`)
- Repository interfaces: PascalCase + `Repository` suffix
- Variables/Functions: camelCase
- Constants: `UPPER_SNAKE_CASE` (in `companion object` or top-level `const val`)
- Composable preview functions: suffix with `Preview` (e.g., `RoadmapCardPreview`)

## Testing Instructions

- Run all unit tests: `./gradlew test`
- Run instrumented tests: `./gradlew connectedAndroidTest`
- Coverage report: `./gradlew testDebugUnitTestCoverage`
- Add/update tests for behavior changes; do not ship logic-only changes without test coverage updates.

### Testing Layers

- **Unit tests** (`src/test/`): Test ViewModels, UseCases, Repositories, and pure utility functions.
    - Use `JUnit 4` or `JUnit 5` with `kotlinx-coroutines-test` for coroutine testing.
    - Mock dependencies with `MockK`.
    - Use `Turbine` for testing `Flow` emissions.
- **Instrumented / UI tests** (`src/androidTest/`): Test Compose screens and navigation.
    - Use `Compose UI Test` (`composeTestRule`) for screen-level tests.
    - Use `Espresso` for legacy/integration scenarios if needed.
- Before creating a PR, run: `./gradlew lint test assembleDebug`

## Security Considerations

- Treat authentication and account flows as sensitive (SRS FR-01): never log credentials, tokens, or secrets.
- Store passwords only as secure hashes; never persist plaintext passwords or tokens in `SharedPreferences` without encryption.
- Use **EncryptedSharedPreferences** or the **Android Keystore** system for sensitive local storage.
- Enforce HTTPS for all API communication; use certificate pinning in production builds.
- Validate and sanitize all API inputs/responses; reject malformed payloads early in the data layer.
- Use least-privilege permissions in `AndroidManifest.xml`; request only permissions strictly necessary.
- Keep secrets (API keys, base URLs) out of source code; use `local.properties` or a secrets Gradle plugin.
- Add security-relevant logs (auth failures, permission denials) without leaking sensitive data; use `Timber` instead of `Log` directly.
- Review third-party resource links/content before exposing them to users in learning resources.
- Obfuscate release builds with **R8/ProGuard**; maintain appropriate keep rules.

## Do Not

- Do not leave `Log.d` / `Log.e` / `println` in committed code; use `Timber` with appropriate log levels.
- Do not store secrets or API keys in `BuildConfig` fields that end up in version control.
- Do not use `runBlocking` on the main thread.
- Do not access the network or disk I/O on the main thread.
- Do not use `GlobalScope`; use structured concurrency with scoped coroutines (`viewModelScope`, `lifecycleScope`).
- Do not hardcode string literals in Compose UI; use `stringResource()`.
