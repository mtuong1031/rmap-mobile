package com.rmap.mobile.features.auth.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val isAuthenticated: StateFlow<Boolean>
    suspend fun signInWithDemoProvider(): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
