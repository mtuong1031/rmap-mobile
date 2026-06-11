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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `google login success emits navigate home`() = runTest {
        val viewModel = newViewModel()
        val events = mutableListOf<AuthEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect(events::add)
        }

        viewModel.onGoogleIdTokenReceived("google-token")
        runCurrent()

        assertEquals(listOf(AuthEvent.NavigateToHome), events)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `github login failure surfaces error and stops loading`() = runTest {
        val repository = FakeAuthRepository(
            githubResult = Result.failure(IllegalStateException("GitHub sign-in failed"))
        )
        val viewModel = newViewModel(repository)

        viewModel.onGithubCodeReceived("invalid-code")
        runCurrent()

        assertEquals("GitHub sign-in failed", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `credential error is shown without starting login`() {
        val viewModel = newViewModel()

        viewModel.onLoginError("No Google account available")

        assertEquals("No Google account available", viewModel.uiState.value.errorMessage)
    }

    private fun newViewModel(
        repository: FakeAuthRepository = FakeAuthRepository()
    ): AuthViewModel {
        return AuthViewModel(
            loginWithGoogleUseCase = LoginWithGoogleUseCase(repository),
            loginWithGithubUseCase = LoginWithGithubUseCase(repository)
        )
    }

    private class FakeAuthRepository(
        private val googleResult: Result<User> = Result.success(testUser),
        private val githubResult: Result<User> = Result.success(testUser)
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> =
            MutableStateFlow(AuthState.Unauthenticated)

        override suspend fun loginWithGoogle(idToken: String): Result<User> = googleResult

        override suspend fun loginWithGithub(code: String): Result<User> = githubResult

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
