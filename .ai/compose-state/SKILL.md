---
name: compose-state
description: >
  Defines the state management contract for ViewModels in this project.
  Use when designing UiState shape, StateFlow usage, loading/error patterns,
  or one-time event handling (navigation, toasts).

  Scope — this skill covers:
    - UiState data class structure
    - StateFlow / SharedFlow usage in ViewModel
    - Loading, success, error state transitions
    - One-time side effects (navigation events, snackbars)

  Out of scope — handled by android-skills:
    - Compose state hoisting (remember, rememberSaveable, derivedStateOf)
    - Compose lifecycle (LaunchedEffect, DisposableEffect, SideEffect)
    - Compose recomposition optimization (stable types, keys)
---

## UiState Pattern

This project uses **flat data class** for most screens (not sealed class), enabling partial updates:

```kotlin
data class RoadmapUiState(
    val isLoading: Boolean = false,
    val roadmap: Roadmap? = null,
    val error: String? = null
)
```

Use the sealed `UiState<T>` (defined project-wide) for screens with strict loading/success/error states:

```kotlin
// presentation/common/UiState.kt
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

**Use flat data class** when the screen needs partial updates (e.g. a form with simultaneous loading + editable fields).
**Use sealed UiState\<T\>** when the screen is strictly one state at a time (pure list/detail screens).

## ViewModel State Rules

```kotlin
// Private mutable — never expose
private val _uiState = MutableStateFlow(RoadmapUiState())

// Public immutable — screen observes this
val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

// Always update via copy() — never reassign fields
_uiState.update { it.copy(isLoading = true) }
```

**Rules:**
- NEVER expose `MutableStateFlow` or `MutableSharedFlow`
- ALWAYS update state via `.update { it.copy(...) }`
- NEVER share state between ViewModels — each screen owns its UiState

## Loading & Error Transitions

```kotlin
fun loadRoadmap(id: String) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        getRoadmapUseCase(id)
            .onSuccess { roadmap ->
                _uiState.update { it.copy(isLoading = false, roadmap = roadmap) }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
    }
}
```

## One-Time Events (Navigation, Snackbars)

Use `SharedFlow` for fire-and-forget side effects that must NOT be replayed on recomposition:

```kotlin
sealed class RoadmapEvent {
    data class NavigateToDetail(val id: String) : RoadmapEvent()
    data class ShowError(val message: String) : RoadmapEvent()
}

private val _events = MutableSharedFlow<RoadmapEvent>()
val events: SharedFlow<RoadmapEvent> = _events.asSharedFlow()

// Emit from ViewModel
private fun emitNavigation(id: String) {
    viewModelScope.launch {
        _events.emit(RoadmapEvent.NavigateToDetail(id))
    }
}
```

```kotlin
// Collect in Screen
LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
        when (event) {
            is RoadmapEvent.NavigateToDetail -> navController.navigate(DetailRoute(event.id))
            is RoadmapEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
        }
    }
}
```

## Rules

- No `LiveData` in new code
- Never expose mutable state
- Always clear error before retrying: `_uiState.update { it.copy(error = null) }`
- ViewModel contains NO business logic — delegate to UseCase
- One-time events → `SharedFlow`; persistent state → `StateFlow`
