package com.rmap.mobile.features.auth.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.auth.data.model.AuthMessageResponseDto
import com.rmap.mobile.features.auth.data.model.GithubMobileOAuthRequestDto
import com.rmap.mobile.features.auth.data.model.MobileOAuthRequestDto
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
    fun `google login fetches current user and stores authenticated state`() = runTest {
        val api = FakeAuthApi()
        val repository = newRepository(api)

        val result = repository.loginWithGoogle("google-token")

        assertTrue(result.isSuccess)
        assertEquals("google-token", api.googleRequest?.idToken)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `github login fetches current user and stores authenticated state`() = runTest {
        val api = FakeAuthApi()
        val repository = newRepository(api)

        val result = repository.loginWithGithub("github-code")

        assertTrue(result.isSuccess)
        assertEquals("github-code", api.githubRequest?.code)
        assertEquals(1, api.getCurrentUserCallCount)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `oauth error returns app exception without fetching current user`() = runTest {
        val api = FakeAuthApi().apply {
            googleResponse = Response.error(
                401,
                """{"code":40100,"message":"Authentication required."}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.loginWithGoogle("invalid-token")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException)
        assertEquals(0, api.getCurrentUserCallCount)
    }

    @Test
    fun `get current user unauthorized clears session and state`() = runTest {
        var clearCount = 0
        val api = FakeAuthApi().apply {
            currentUserResponse = Response.error(
                401,
                """{"code":40100,"message":"Authentication required."}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api, SessionManager { clearCount++ })

        val result = repository.getCurrentUser()

        assertTrue(result.isFailure)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
    }

    @Test
    fun `logout clears session and authenticated state`() = runTest {
        var clearCount = 0
        val repository = newRepository(sessionManager = SessionManager { clearCount++ })
        repository.loginWithGoogle("google-token")

        val result = repository.logout()

        assertTrue(result.isSuccess)
        assertEquals(1, clearCount)
        assertEquals(AuthState.Unauthenticated, repository.authState.value)
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
        var getCurrentUserCallCount = 0
        var googleResponse: Response<AuthMessageResponseDto> =
            Response.success(AuthMessageResponseDto("Signed in"))
        var githubResponse: Response<AuthMessageResponseDto> =
            Response.success(AuthMessageResponseDto("Signed in"))
        var logoutResponse: Response<AuthMessageResponseDto> =
            Response.success(AuthMessageResponseDto("Signed out"))
        var currentUserResponse: Response<UserDto> = Response.success(testUser)

        override suspend fun loginWithGoogle(
            request: MobileOAuthRequestDto
        ): Response<AuthMessageResponseDto> {
            googleRequest = request
            return googleResponse
        }

        override suspend fun loginWithGithub(
            request: GithubMobileOAuthRequestDto
        ): Response<AuthMessageResponseDto> {
            githubRequest = request
            return githubResponse
        }

        override suspend fun logout(): Response<AuthMessageResponseDto> = logoutResponse

        override suspend fun getCurrentUser(): Response<UserDto> {
            getCurrentUserCallCount++
            return currentUserResponse
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
