---
name: feature-generator
description: >
  Orchestrator skill — generates a complete feature from requirement to working code.
  Coordinates all project skills to produce all layers: UI, ViewModel, UseCase, Repository, API.
  Use when creating a new screen or implementing a feature end-to-end.
---

## When to Use

- Creating a new screen (e.g. "Create Roadmap screen")
- Implementing a feature from API contract to UI
- Scaffolding all layers for a domain area (auth, profile, roadmap, etc.)

## Step-by-Step Workflow

### Step 1 — Understand the requirement

- Identify the feature name (e.g. `Roadmap`, `Profile`, `Auth`)
- Define what the screen does (display, create, edit, delete)
- Identify if API calls are needed

### Step 2 — Read API contract (if needed)

- Open `docs/openapi.yml`
- Find relevant endpoint(s), request body, response schema, error codes
- Note exact field names — DTOs must match exactly

### Step 3 — Apply all relevant skills in order

```
1. project-rules   → verify architecture constraints before generating anything
2. compose-architecture → confirm which layer each piece belongs to
3. api-client      → generate DTO + Mapper + Service interface (if API needed)
4. error-handling  → confirm error flow for the feature
5. compose-state   → design UiState + events for the ViewModel
6. navigation-flow → define NavEvent if the feature triggers navigation
```

### Step 4 — Generate layers (bottom-up)

Generate in this order to respect dependency direction:

```
1. data/remote/api/XxxApiService.kt       ← Retrofit interface
2. data/remote/model/XxxDto.kt            ← DTO classes
3. data/mapper/XxxMapper.kt               ← DTO → Domain extension functions
4. domain/model/Xxx.kt                    ← Domain model (pure Kotlin)
5. domain/repository/XxxRepository.kt    ← Repository interface
6. data/repository/XxxRepositoryImpl.kt  ← Repository implementation
7. domain/usecase/GetXxxUseCase.kt        ← (only if logic warrants it)
8. presentation/viewmodel/XxxViewModel.kt ← ViewModel + UiState + events
9. presentation/ui/xxx/XxxScreen.kt       ← Compose screen
```

### Step 5 — Wire DI (Hilt)

```kotlin
// di/XxxModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class XxxModule {
    @Binds
    abstract fun bindXxxRepository(impl: XxxRepositoryImpl): XxxRepository
}
```

### Step 6 — Add navigation route (if new screen)

```kotlin
// presentation/navigation/NavRoutes.kt — add route constant
// presentation/navigation/NavGraph.kt (or NavHost) — add composable { }
// Trigger navigation via NavEvent from ViewModel
```

## Output Checklist

- [ ] All files in correct package per project structure
- [ ] DTO matches `docs/openapi.yml` field names exactly (`@SerializedName`)
- [ ] Domain model is pure Kotlin (no Android imports)
- [ ] Repository interface in `domain/`, implementation in `data/`
- [ ] ViewModel uses `viewModelScope`, no `runBlocking`
- [ ] UiState is a data class with `isLoading`, `error`, and data fields
- [ ] Navigation events emitted via `SharedFlow<NavEvent>` (see `navigation-flow`)
- [ ] Hilt module created for new bindings
- [ ] All strings use `stringResource()` in Compose
- [ ] `project-rules` pre-commit checklist passed

## Example

**Prompt:** "Create the Roadmap Detail screen"

**Output:**
```
data/remote/api/RoadmapApiService.kt     ← GET /roadmaps/{id}
data/remote/model/RoadmapDto.kt          ← RoadmapResponseDto, RoadmapStepDto
data/mapper/RoadmapMapper.kt             ← .toDomain() extensions
domain/model/Roadmap.kt                  ← Roadmap, RoadmapStep
domain/repository/RoadmapRepository.kt  ← interface
data/repository/RoadmapRepositoryImpl.kt ← implementation
domain/usecase/GetRoadmapUseCase.kt      ← only if multiple repos combined
presentation/viewmodel/RoadmapViewModel.kt
presentation/ui/roadmap/RoadmapScreen.kt
di/RoadmapModule.kt
```
