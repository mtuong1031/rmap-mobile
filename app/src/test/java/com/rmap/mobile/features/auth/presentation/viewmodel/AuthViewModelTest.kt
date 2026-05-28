package com.rmap.mobile.features.auth.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.LoginUseCase
import com.rmap.mobile.features.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `field updates mutate ui state and clear error`() {
        val viewModel = newViewModel()

        viewModel.onEmailChange("learner@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onFullNameChange("Learner")

        assertEquals("learner@example.com", viewModel.uiState.value.email)
        assertEquals("password123", viewModel.uiState.value.password)
        assertEquals("Learner", viewModel.uiState.value.fullName)
    }

    @Test
    fun `toggle mode switches mode and clears form error`() {
        val viewModel = newViewModel()

        viewModel.onEmailChange("bad")
        viewModel.onSubmit()
        viewModel.onToggleMode()

        assertEquals(AuthMode.Register, viewModel.uiState.value.mode)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login success emits navigate home`() = runTest {
        val viewModel = newViewModel()
        val events = mutableListOf<AuthEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        viewModel.onEmailChange("learner@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onSubmit()
        runCurrent()

        assertEquals(listOf(AuthEvent.NavigateToHome), events)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `register success emits navigate home`() = runTest {
        val viewModel = newViewModel()
        val events = mutableListOf<AuthEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        viewModel.onToggleMode()
        viewModel.onFullNameChange("Learner")
        viewModel.onEmailChange("learner@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onSubmit()
        runCurrent()

        assertEquals(listOf(AuthEvent.NavigateToHome), events)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `failure surfaces user safe error and stops loading`() {
        val repository = FakeAuthRepository(
            loginResult = Result.failure(IllegalArgumentException("Invalid email or password."))
        )
        val viewModel = newViewModel(repository)

        viewModel.onEmailChange("learner@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onSubmit()

        assertEquals("Invalid email or password.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    private fun newViewModel(
        repository: FakeAuthRepository = FakeAuthRepository()
    ): AuthViewModel {
        return AuthViewModel(
            loginUseCase = LoginUseCase(repository),
            registerUseCase = RegisterUseCase(repository)
        )
    }

    private class FakeAuthRepository(
        private val loginResult: Result<User> = Result.success(testUser),
        private val registerResult: Result<User> = Result.success(testUser)
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)

        override suspend fun login(email: String, password: String): Result<User> = loginResult

        override suspend fun register(email: String, password: String, fullName: String): Result<User> = registerResult

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

        override suspend fun getCurrentUser(): Result<User> = Result.success(testUser)
    }

    private companion object {
        val testUser = User(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
    }
}
