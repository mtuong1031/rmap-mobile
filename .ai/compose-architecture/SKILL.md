---
name: compose-architecture
description: >
  Answers ONE question: "Where does this code belong?"
  Layer boundary referee for Android Clean Architecture + MVVM.
  Use when deciding layer placement, reviewing architectural correctness,
  or designing module structure.

  Scope — this skill covers:
    - Layer definitions and dependency rules
    - Responsibility mapping (what goes where)
    - Anti-patterns to reject

  Out of scope — handled by other skills:
    - Compose UI implementation → android-skills (jetpack-compose)
    - ViewModel state / StateFlow → compose-state
    - API code generation → api-client
    - Error flow → error-handling
    - Navigation mechanics → android-skills (navigation-3)
    - Nav event pattern → navigation-flow

  Priority: android-skills = platform source of truth; this skill = layer decisions.
---

## Layer Overview

```
presentation/          ← UI Layer
  ui/                  ← Compose Screens (XxxScreen.kt)
  viewmodel/           ← ViewModels (XxxViewModel.kt)
  navigation/          ← NavHost, NavGraph, NavEvent

domain/                ← Domain Layer (pure Kotlin — zero Android dependencies)
  model/               ← Domain models
  repository/          ← Repository interfaces
  usecase/             ← Use cases / interactors

data/                  ← Data Layer
  remote/
    api/               ← Retrofit @Service interfaces
    model/             ← DTOs (request + response)
  local/               ← Room DAOs + Entities
  mapper/              ← DTO/Entity → Domain mapping
  repository/          ← Repository implementations
```

**Dependency rule (strict):**
```
UI → Domain → Data
Domain NEVER imports from UI or Data.
Data NEVER imports from UI.
```

## Responsibility Mapping

| Question | Answer |
|---|---|
| Where do API calls live? | `data/remote/api/` |
| Where do DTOs live? | `data/remote/model/` |
| Where does DTO → Domain mapping live? | `data/mapper/` |
| Where does business logic live? | `domain/usecase/` |
| Where does UI state mapping live? | `presentation/viewmodel/` |
| Where do navigation events originate? | `presentation/viewmodel/` (emit only) |
| Where is navigation executed? | `presentation/ui/` (observes event) |
| Where is error mapped to UiState? | `presentation/viewmodel/` |

## UseCase — When to Create One

**Create a UseCase when:**
- Logic is reused across multiple ViewModels
- Combining data from multiple repositories
- Business transformation / filtering is needed

**Do NOT create a UseCase for:**
- Simple 1:1 pass-through to a single repository method
- Data fetching with no transformation

## Data Layer Constraints

- DTO and Entity types **MUST NOT** be referenced outside `data/`
- Mapping is required at every layer boundary
- Repository = single source of truth (network + cache managed here)

## Module Structure (recommended for growth)

```
:app
:feature:roadmap
:feature:auth
:feature:profile
:domain
:data
:core:ui
:core:common
```

Rules:
- `:feature:*` depends on `:domain` only
- `:data` depends on `:domain` only
- `:feature:*` NEVER depends on another `:feature:*`

## Anti-Patterns

```
❌ ViewModel calls Retrofit or Room directly
❌ Domain model imports android.* or androidx.*
❌ DTO type referenced in ViewModel or Screen
❌ NavController injected into ViewModel
❌ UseCase that is a 1:1 pass-through with no logic
❌ Screen calls repository directly
```

## Architecture Checklist

- [ ] Domain layer has zero Android dependencies
- [ ] DTO / Entity not referenced outside `data/`
- [ ] ViewModel only interacts with UseCase or Repository interface
- [ ] Screen uses UiModel / UiState only (not domain model directly)
- [ ] Mapping done at the correct layer boundary
- [ ] API contract matches `openapi.yaml`
- [ ] No unnecessary UseCase wrapper
