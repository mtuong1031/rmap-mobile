package com.rmap.mobile.features.auth.data

import com.rmap.mobile.features.auth.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSessionRepository(
    authenticatedByDefault: Boolean = false
) : SessionRepository {
    private val _isAuthenticated = MutableStateFlow(authenticatedByDefault)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun signInWithDemoProvider(): Result<Unit> {
        _isAuthenticated.value = true
        return Result.success(Unit)
    }

    override suspend fun signOut(): Result<Unit> {
        _isAuthenticated.value = false
        return Result.success(Unit)
    }
}
