package com.rmap.mobile.features.auth.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.auth.data.model.AuthMessageResponseDto
import com.rmap.mobile.features.auth.data.model.MobileOAuthRequestDto
import com.rmap.mobile.features.auth.data.model.GithubMobileOAuthRequestDto
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
    fun `loginWithGoogle success calls current user and stores authenticated state`() = runTest {
        val api = FakeAuthApi()
        val repository = newRepository(api = api)

        val result = repository.loginWithGoogle("google-token")

        assertTrue(result.isSuccess)
        assertEquals("learner@example.com", result.getOrNull()?.email)
        assertEquals(1, api.loginWithGoogleCallCount)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `loginWithGithub success calls current user and stores authenticated state`() = runTest {
        val api = FakeAuthApi()
        val repository = newRepository(api = api)

        val result = repository.loginWithGithub("github-code")

        assertTrue(result.isSuccess)
        assertEquals("learner@example.com", result.getOrNull()?.email)
        assertEquals(1, api.loginWithGithubCallCount)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
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

        val result = repository.logout()

        assertTrue(result.isSuccess)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
    }

    @Test
    fun `google sign in API error returns app exception`() = runTest {
        val api = FakeAuthApi().apply {
            loginWithGoogleResponse = Response.error(
                401,
                """{"code":40101,"message":"Invalid OAuth token"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api = api)

        val result = repository.loginWithGoogle("bad-token")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
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
        var googleRequest: MobileOAuthRequestDto? = null
        var githubRequest: GithubMobileOAuthRequestDto? = null
        var loginWithGoogleCallCount = 0
        var loginWithGithubCallCount = 0
        var getCurrentUserCallCount = 0
        var loginWithGoogleResponse: Response<AuthMessageResponseDto> = Response.success(AuthMessageResponseDto("Login successful"))
        var loginWithGithubResponse: Response<AuthMessageResponseDto> = Response.success(AuthMessageResponseDto("Login successful"))
        var logoutResponse: Response<AuthMessageResponseDto> = Response.success(AuthMessageResponseDto("Logged out successfully"))
        var getCurrentUserResponse: Response<UserDto> = Response.success(testUser)

        override suspend fun loginWithGoogle(request: MobileOAuthRequestDto): Response<AuthMessageResponseDto> {
            loginWithGoogleCallCount++
            googleRequest = request
            return loginWithGoogleResponse
        }

        override suspend fun loginWithGithub(request: GithubMobileOAuthRequestDto): Response<AuthMessageResponseDto> {
            loginWithGithubCallCount++
            githubRequest = request
            return loginWithGithubResponse
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
