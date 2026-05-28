package com.rmap.mobile.features.auth.domain.repository

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): Result<User>

    suspend fun logout(): Result<Unit>

    suspend fun getCurrentUser(): Result<User>
}
