package com.rmap.mobile.features.auth.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toAppException
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.auth.data.mapper.toDomain
import com.rmap.mobile.features.auth.data.model.MobileOAuthRequestDto
import com.rmap.mobile.features.auth.data.model.GithubMobileOAuthRequestDto
import com.rmap.mobile.features.auth.data.model.UserDto
import com.rmap.mobile.features.auth.data.remote.AuthApi
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()



    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        val networkResult = SafeApiCall.execute {
            authApi.loginWithGoogle(
                MobileOAuthRequestDto(idToken = idToken)
            )
        }

        return when (networkResult) {
            is NetworkResult.Success -> getCurrentUser()
            is NetworkResult.Error -> Result.failure(networkResult.toAppException())
        }
    }

    override suspend fun loginWithGithub(code: String): Result<User> {
        val networkResult = SafeApiCall.execute {
            authApi.loginWithGithub(
                GithubMobileOAuthRequestDto(code = code)
            )
        }

        return when (networkResult) {
            is NetworkResult.Success -> getCurrentUser()
            is NetworkResult.Error -> Result.failure(networkResult.toAppException())
        }
    }

    override suspend fun logout(): Result<Unit> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = ::handleUnauthorized
        ) {
            authApi.logout()
        }

        val result = networkResult.toDomainResult { Unit }
        sessionManager.clearSession()
        _authState.value = AuthState.Unauthenticated
        return result
    }

    override suspend fun getCurrentUser(): Result<User> {
        val networkResult = SafeApiCall.execute(
            onUnauthorized = ::handleUnauthorized
        ) {
            authApi.getCurrentUser()
        }

        return networkResult.toUserResult()
            .onSuccess { user ->
                sessionManager.markSessionActive()
                _authState.value = AuthState.Authenticated(user)
            }
            .onFailure { error ->
                if (error is AppException && error.type == NetworkErrorType.Unauthorized) {
                    _authState.value = AuthState.Unauthenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
    }

    private suspend fun handleUnauthorized() {
        sessionManager.handleUnauthorized()
        _authState.value = AuthState.Unauthenticated
    }

    private fun NetworkResult<UserDto>.toUserResult(): Result<User> {
        return when (this) {
            is NetworkResult.Success -> runCatching { data.toDomain() }
                .recoverCatching { error ->
                    throw invalidAuthResponse(code, error)
                }

            is NetworkResult.Error -> Result.failure(toAppException())
        }
    }

    private fun invalidAuthResponse(
        statusCode: Int,
        cause: Throwable? = null
    ): AppException {
        return AppException(
            message = "Unable to read authentication response. Please try again.",
            code = statusCode,
            type = NetworkErrorType.Serialization,
            cause = cause
        )
    }

    private companion object {
    }
}
