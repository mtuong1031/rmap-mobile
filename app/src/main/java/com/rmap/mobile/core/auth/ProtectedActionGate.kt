package com.rmap.mobile.core.auth

import com.rmap.mobile.features.auth.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface ProtectedActionGate {
    val authState: StateFlow<AuthState>
    val pendingAction: StateFlow<PendingProtectedAction?>

    suspend fun runOrRequestAuth(
        action: PendingProtectedAction,
        onAuthenticated: suspend () -> Unit
    ): Boolean

    fun consumePendingAction(action: PendingProtectedAction): Boolean

    fun clearPendingAction(action: PendingProtectedAction)
}
