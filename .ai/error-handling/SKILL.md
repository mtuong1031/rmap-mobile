---
name: error-handling
description: >
  Defines the standard error flow for this project: data layer → domain → ViewModel → UiState.
  Use when implementing repository error mapping, UseCase result propagation,
  or surfacing errors to the UI.
---

## Error Flow

```
API/DB Exception
    ↓
Repository (catches + wraps in Result)
    ↓
UseCase (propagates Result, applies business rules)
    ↓
ViewModel (maps Result → UiState.Error)
    ↓
Screen (renders UiState.Error as user-facing message)
```

## Layer Rules

| Layer | Rule |
|---|---|
| `data/repository` | Catch ALL exceptions; return `Result.failure()` with domain exception |
| `domain/usecase` | Propagate `Result<T>`; add business logic on success path |
| `presentation/viewmodel` | Collect `Result<T>`; map to `UiState.Error(message)` |
| `presentation/ui` | Render `UiState.Error` — never access raw exceptions |

## Code Templates

### Domain — Sealed Result (already in UiState)

```kotlin
// presentation/common/UiState.kt — already defined project-wide
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Data Layer — Repository error wrapping

```kotlin
// Wrap ALL API calls in runCatching — never let exceptions escape the data layer
override suspend fun getRoadmap(id: String): Result<Roadmap> = runCatching {
    val response = apiService.getRoadmap(id)
    if (response.isSuccessful) {
        response.body()?.toDomain() ?: error("Empty response body")
    } else {
        error("HTTP ${response.code()}")
    }
}.onFailure { e ->
    Timber.e(e, "getRoadmap failed for id=$id")
}
```

### Domain Layer — UseCase propagation

```kotlin
class GetRoadmapUseCase @Inject constructor(
    private val repository: RoadmapRepository
) {
    suspend operator fun invoke(id: String): Result<Roadmap> =
        repository.getRoadmap(id)  // propagate Result, no try/catch needed
}
```

### Presentation Layer — ViewModel mapping

```kotlin
fun loadRoadmap(id: String) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        getRoadmapUseCase(id)
            .onSuccess { roadmap ->
                _uiState.update { it.copy(isLoading = false, roadmap = roadmap) }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.toUserMessage())
                }
            }
    }
}

// Helper — maps technical exceptions to user-friendly messages
private fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException -> "No internet connection"
    is SocketTimeoutException -> "Request timed out"
    else -> message ?: "Something went wrong"
}
```

### UI Layer — Screen error rendering

```kotlin
when (val state = uiState) {
    is UiState.Loading -> LoadingIndicator()
    is UiState.Success -> RoadmapContent(state.data)
    is UiState.Error -> ErrorMessage(
        message = state.message,
        onRetry = viewModel::retry
    )
}
```

## Rules

- **NEVER** expose raw `Exception` or `Throwable` to the UI layer.
- **NEVER** use `try/catch` in ViewModel — map in `onFailure {}` instead.
- Technical error details (stack traces, HTTP codes) → `Timber.e()` only.
- User-facing messages must be human-readable strings.
- For auth errors (401/403): emit a navigation side-effect to re-login screen, do not display inline error.
