---
name: project-rules
description: >
  Pre-generation enforcement checklist for this project.
  Apply this skill BEFORE generating any code to verify all architectural
  constraints are met. This is the final gate — if any item fails, fix it first.

  This skill does NOT define patterns (see compose-architecture, compose-state,
  api-client for that). It only ENFORCES them.
---

## Mandatory Architecture Constraints

| Rule | Enforcement |
|---|---|
| MVVM + Clean Architecture | Screen → ViewModel → UseCase → Repository → DataSource |
| StateFlow only | No LiveData in new code |
| No XML UI | Jetpack Compose only |
| No direct repo call from UI | ViewModel is the ONLY bridge to domain |
| No DTO in UI | Map to UiModel at ViewModel boundary |
| No business logic in Composables | Delegate everything to ViewModel |
| API follows `docs/openapi.yml` | Never invent fields or endpoints |
| No hardcoded strings in UI | `stringResource()` only |
| No raw exceptions to UI | Map to UiState.Error with user-friendly message |
| Secrets in `local.properties` | Never in source code or BuildConfig |

## Anti-Patterns — REJECT immediately

```
❌ ViewModel calling Retrofit / Room directly
❌ DTO or Entity type used in presentation layer
❌ NavController injected into ViewModel
❌ Business logic inside @Composable function
❌ GlobalScope or runBlocking on main thread
❌ Log.d / Log.e / println in committed code — use Timber
❌ !! (non-null assertion) — use safe calls or ?: return
❌ UseCase that is a 1:1 pass-through with no logic
❌ Don't declare strings.xml variable when it can be hardcoded in the Preview UI Component
```

## Pre-Generation Checklist

Before generating any file, verify:

- [ ] Does the new code belong in the correct layer? (→ `compose-architecture`)
- [ ] Is UiState modeled correctly — sealed or data class with `isLoading`/`error`? (→ `compose-state`)
- [ ] If API-related: does DTO match `docs/openapi.yml` exactly? (→ `api-client`)
- [ ] Is error flow mapped at the right layer? (→ `error-handling`)
- [ ] Are navigation events emitted via SharedFlow from ViewModel? (→ `navigation-flow`)
- [ ] Are all new dependencies added to `gradle/libs.versions.toml`?

## Pre-Commit Checklist

- [ ] Run `./gradlew ktlintFormat` — no lint errors
- [ ] Run `./gradlew test` — all unit tests pass
- [ ] Run `./gradlew assembleDebug` — build succeeds
- [ ] No unused imports, no `TODO` left without tracking
- [ ] All user-facing strings use `stringResource()`
- [ ] No secrets, tokens, or credentials in any source file
