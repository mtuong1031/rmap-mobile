package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCaseTest {
    @Test
    fun `LoginWithGoogleUseCase calls repository`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginWithGoogleUseCase(repository)

        val result = useCase("google-token")

        assertTrue(result.isSuccess)
        assertEquals("google-token", repository.lastGoogleToken)
    }

    @Test
    fun `LoginWithGithubUseCase calls repository`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginWithGithubUseCase(repository)

        val result = useCase("github-code")

        assertTrue(result.isSuccess)
        assertEquals("github-code", repository.lastGithubCode)
    }

    private class FakeAuthRepository : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var lastGoogleToken: String? = null
        var lastGithubCode: String? = null

        override suspend fun loginWithGoogle(idToken: String): Result<User> {
            lastGoogleToken = idToken
            return Result.success(testUser)
        }

        override suspend fun loginWithGithub(code: String): Result<User> {
            lastGithubCode = code
            return Result.success(testUser)
        }

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

        override suspend fun changePassword(
            currentPassword: String,
            newPassword: String
        ): Result<Unit> = Result.success(Unit)

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
