---
name: navigation-flow
description: >
  Defines the project-specific event-driven navigation pattern.
  Use when implementing navigation side effects from a ViewModel.

  THIS SKILL covers:
    - NavEvent sealed class convention for this project
    - How ViewModel emits navigation events
    - How Screen collects and executes navigation

  THIS SKILL does NOT cover:
    - Navigation 3 setup, NavHost, NavKey, back stack, deep links
      → use android-skills/navigation/navigation-3 for those
    - Route definition and NavGraph structure
      → see presentation/navigation/ in the codebase
---

## Core Rule

**ViewModel MUST NOT hold a reference to `NavController`.**

Navigation is event-driven:
1. ViewModel emits a `NavEvent` via `SharedFlow`
2. Screen collects the event in `LaunchedEffect`
3. Screen calls `NavController.navigate()`

## NavEvent Convention

```kotlin
// Define per-feature in the viewmodel package
// e.g. presentation/viewmodel/auth/AuthEvent.kt
sealed class AuthEvent {
    object NavigateToHome : AuthEvent()
    object NavigateToRegister : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
}
```

## ViewModel — Emitting Events

```kotlin
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun onLoginSuccess() {
        viewModelScope.launch {
            _events.emit(AuthEvent.NavigateToHome)
        }
    }
}
```

## Screen — Collecting and Executing Navigation

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // One-time events — use LaunchedEffect to collect
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToHome -> navController.navigate(HomeRoute) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
                is AuthEvent.NavigateToRegister -> navController.navigate(RegisterRoute)
                is AuthEvent.ShowError -> { /* handled by UiState */ }
            }
        }
    }

    // ... UI content
}
```

## Rules

- NavEvent sealed class lives next to its ViewModel (same feature package)
- Screen is the ONLY executor of navigation calls
- Use `popUpTo` with `inclusive = true` for auth flows (clear back stack after login)
- For Navigation 3 specifics (NavKey, NavDisplay, scenes) → read `android-skills/navigation/navigation-3/SKILL.md`
