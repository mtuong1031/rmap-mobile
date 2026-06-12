package com.rmap.mobile.core.auth

import com.rmap.mobile.R
import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationAction
import com.rmap.mobile.core.notification.AppNotificationManager
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class AuthGuard(
    private val authRepository: AuthRepository,
    private val pendingProtectedActionStore: PendingProtectedActionStore,
    private val appNotificationManager: AppNotificationManager
) : ProtectedActionGate {
    override val authState: StateFlow<AuthState> = authRepository.authState
    override val pendingAction: StateFlow<PendingProtectedAction?> = pendingProtectedActionStore.pendingAction

    override suspend fun runOrRequestAuth(
        action: PendingProtectedAction,
        onAuthenticated: suspend () -> Unit
    ): Boolean {
        return if (authRepository.authState.value is AuthState.Authenticated) {
            onAuthenticated()
            true
        } else {
            pendingProtectedActionStore.store(action)
            appNotificationManager.enqueue(
                AppNotification(
                    titleResId = R.string.auth_required_title,
                    messageResId = R.string.auth_required_generate_roadmap_message,
                    variant = AppNotificationVariant.Warning,
                    actionLabelResId = R.string.action_login,
                    action = AppNotificationAction.Login
                )
            )
            false
        }
    }

    override fun consumePendingAction(action: PendingProtectedAction): Boolean {
        return pendingProtectedActionStore.consume(action)
    }

    override fun clearPendingAction(action: PendingProtectedAction) {
        pendingProtectedActionStore.clear(action)
    }
}
