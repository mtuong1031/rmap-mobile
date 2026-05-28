package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCaseTest {
    @Test
    fun `login rejects invalid email before repository call`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginUseCase(repository)

        val result = useCase("not-email", "password123")

        assertTrue(result.isFailure)
        assertNull(repository.lastLoginEmail)
    }

    @Test
    fun `login rejects short password before repository call`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginUseCase(repository)

        val result = useCase("learner@example.com", "short")

        assertTrue(result.isFailure)
        assertNull(repository.lastLoginEmail)
    }

    @Test
    fun `login calls repository with trimmed email when valid`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = LoginUseCase(repository)

        val result = useCase(" learner@example.com ", "password123")

        assertTrue(result.isSuccess)
        assertEquals("learner@example.com", repository.lastLoginEmail)
    }

    @Test
    fun `register rejects blank full name before repository call`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = RegisterUseCase(repository)

        val result = useCase("learner@example.com", "password123", " ")

        assertTrue(result.isFailure)
        assertNull(repository.lastRegisterEmail)
    }

    @Test
    fun `register calls repository with trimmed values when valid`() = runTest {
        val repository = FakeAuthRepository()
        val useCase = RegisterUseCase(repository)

        val result = useCase(" learner@example.com ", "password123", " Learner ")

        assertTrue(result.isSuccess)
        assertEquals("learner@example.com", repository.lastRegisterEmail)
        assertEquals("Learner", repository.lastRegisterFullName)
    }

    private class FakeAuthRepository : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var lastLoginEmail: String? = null
        var lastRegisterEmail: String? = null
        var lastRegisterFullName: String? = null

        override suspend fun login(email: String, password: String): Result<User> {
            lastLoginEmail = email
            return Result.success(testUser)
        }

        override suspend fun register(email: String, password: String, fullName: String): Result<User> {
            lastRegisterEmail = email
            lastRegisterFullName = fullName
            return Result.success(testUser)
        }

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
