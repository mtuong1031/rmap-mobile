package com.rmap.mobile.features.auth.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.LoginWithGithubUseCase
import com.rmap.mobile.features.auth.domain.usecase.LoginWithGoogleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val fakeAuthRepository = FakeAuthRepository()
    private val loginWithGoogleUseCase = LoginWithGoogleUseCase(fakeAuthRepository)
    private val loginWithGithubUseCase = LoginWithGithubUseCase(fakeAuthRepository)

    @Test
    fun `login error sets message`() {
        val viewModel = newViewModel()
        viewModel.onLoginError("Failed to sign in")
        assertEquals("Failed to sign in", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `google sign in success emits navigate home`() = runTest {
        val viewModel = newViewModel()
        val events = mutableListOf<AuthEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        fakeAuthRepository.result = Result.success(testUser)
        viewModel.onGoogleIdTokenReceived("google-token")
        runCurrent()

        assertEquals(listOf(AuthEvent.NavigateToHome), events)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `google sign in failure sets error message`() = runTest {
        val viewModel = newViewModel()
        fakeAuthRepository.result = Result.failure(IllegalArgumentException("Invalid Google token"))
        viewModel.onGoogleIdTokenReceived("google-token")
        runCurrent()

        assertTrue(viewModel.uiState.value.errorMessage?.contains("Invalid Google token") == true)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `github sign in success emits navigate home`() = runTest {
        val viewModel = newViewModel()
        val events = mutableListOf<AuthEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        fakeAuthRepository.result = Result.success(testUser)
        viewModel.onGithubCodeReceived("github-code")
        runCurrent()

        assertEquals(listOf(AuthEvent.NavigateToHome), events)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun newViewModel(): AuthViewModel {
        return AuthViewModel(
            loginWithGoogleUseCase = loginWithGoogleUseCase,
            loginWithGithubUseCase = loginWithGithubUseCase
        )
    }

    private class FakeAuthRepository : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var result: Result<User> = Result.success(testUser)

        override suspend fun loginWithGoogle(idToken: String): Result<User> = result
        override suspend fun loginWithGithub(code: String): Result<User> = result
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
