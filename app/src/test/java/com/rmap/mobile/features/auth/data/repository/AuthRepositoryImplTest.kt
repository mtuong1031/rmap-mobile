package com.rmap.mobile.features.auth.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.auth.data.model.AuthMessageResponseDto
import com.rmap.mobile.features.auth.data.model.LoginRequestDto
import com.rmap.mobile.features.auth.data.model.RegisterRequestDto
import com.rmap.mobile.features.auth.data.model.UserDto
import com.rmap.mobile.features.auth.data.remote.AuthApi
import com.rmap.mobile.features.auth.domain.model.AuthState
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {
    @Test
    fun `login success calls current user and stores authenticated state`() = runTest {
        val api = FakeAuthApi()
        val repository = newRepository(api = api)

        val result = repository.login("learner@example.com", "password123")

        assertTrue(result.isSuccess)
        assertEquals("learner@example.com", result.getOrNull()?.email)
        assertEquals(1, api.loginCallCount)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `login success but current user unauthorized clears session and sets unauthenticated state`() = runTest {
        var clearCount = 0
        val api = FakeAuthApi().apply {
            getCurrentUserResponse = Response.error(
                401,
                """{"code":40100,"message":"Authentication required."}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(
            api = api,
            sessionManager = SessionManager { clearCount++ }
        )

        val result = repository.login("learner@example.com", "password123")

        assertTrue(result.isFailure)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
    }

    @Test
    fun `register success logs in and stores authenticated state from current user`() = runTest {
        val api = FakeAuthApi().apply {
            registerResponse = Response.success(testUser.copy(id = "registered-user"))
            getCurrentUserResponse = Response.success(testUser.copy(fullName = "Learner"))
        }
        val repository = newRepository(api = api)

        val result = repository.register("learner@example.com", "password123", "Learner")

        assertTrue(result.isSuccess)
        assertEquals("Learner", result.getOrNull()?.fullName)
        assertEquals(1, api.registerCallCount)
        assertEquals(1, api.loginCallCount)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `register api error returns app exception`() = runTest {
        val api = FakeAuthApi().apply {
            registerResponse = Response.error(
                409,
                """{"code":40901,"message":"Email already registered"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api = api)

        val result = repository.register("learner@example.com", "password123", "Learner")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
        assertEquals(0, api.loginCallCount)
    }

    @Test
    fun `getCurrentUser success restores authenticated state`() = runTest {
        val repository = newRepository()

        val result = repository.getCurrentUser()

        assertTrue(result.isSuccess)
        assertEquals("learner", result.getOrNull()?.id)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `getCurrentUser unauthorized clears session and sets unauthenticated state`() = runTest {
        var clearCount = 0
        val api = FakeAuthApi().apply {
            getCurrentUserResponse = Response.error(
                401,
                """{"code":40100,"message":"Authentication required."}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(
            api = api,
            sessionManager = SessionManager { clearCount++ }
        )

        val result = repository.getCurrentUser()

        assertTrue(result.isFailure)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
    }

    @Test
    fun `logout success message clears session and sets unauthenticated state`() = runTest {
        var clearCount = 0
        val repository = newRepository(
            sessionManager = SessionManager { clearCount++ }
        )

        repository.login("learner@example.com", "password123")
        val result = repository.logout()

        assertTrue(result.isSuccess)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
    }

    @Test
    fun `api error returns app exception`() = runTest {
        val api = FakeAuthApi().apply {
            loginResponse = Response.error(
                401,
                """{"code":40101,"message":"Invalid email or password"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api = api)

        val result = repository.login("learner@example.com", "bad-password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
        assertEquals("Invalid email or password.", result.exceptionOrNull()?.message)
    }

    private fun newRepository(
        api: FakeAuthApi = FakeAuthApi(),
        sessionManager: SessionManager = SessionManager {}
    ): AuthRepositoryImpl {
        return AuthRepositoryImpl(
            authApi = api,
            sessionManager = sessionManager
        )
    }

    private class FakeAuthApi : AuthApi {
        var loginRequest: LoginRequestDto? = null
        var registerRequest: RegisterRequestDto? = null
        var loginCallCount = 0
        var registerCallCount = 0
        var getCurrentUserCallCount = 0
        var loginResponse: Response<AuthMessageResponseDto> = Response.success(AuthMessageResponseDto("Login successful"))
        var registerResponse: Response<UserDto> = Response.success(testUser.copy(fullName = "Learner"))
        var logoutResponse: Response<AuthMessageResponseDto> = Response.success(AuthMessageResponseDto("Logged out successfully"))
        var getCurrentUserResponse: Response<UserDto> = Response.success(testUser)

        override suspend fun login(request: LoginRequestDto): Response<AuthMessageResponseDto> {
            loginCallCount++
            loginRequest = request
            return loginResponse
        }

        override suspend fun register(request: RegisterRequestDto): Response<UserDto> {
            registerCallCount++
            registerRequest = request
            return registerResponse
        }

        override suspend fun logout(): Response<AuthMessageResponseDto> = logoutResponse

        override suspend fun getCurrentUser(): Response<UserDto> {
            getCurrentUserCallCount++
            return getCurrentUserResponse
        }
    }

    private companion object {
        val testUser = UserDto(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
    }
}
