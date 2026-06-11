package com.rmap.mobile.features.auth.domain.repository

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun loginWithGoogle(idToken: String): Result<User>

    suspend fun loginWithGithub(code: String): Result<User>

    suspend fun logout(): Result<Unit>

    suspend fun getCurrentUser(): Result<User>
}
