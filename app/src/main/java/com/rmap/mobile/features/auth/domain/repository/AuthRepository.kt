package com.rmap.mobile.features.auth.domain.repository

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun loginWithGoogle(idToken: String): Result<User>

    suspend fun loginWithGithub(code: String): Result<User>

    suspend fun linkWithGoogle(idToken: String): Result<Unit>

    suspend fun linkWithGithub(code: String): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>

    suspend fun getCurrentUser(): Result<User>
}
