package com.rmap.mobile.core.auth

import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationAction
import com.rmap.mobile.core.notification.AppNotificationManager
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthGuardTest {
    @Test
    fun `runOrRequestAuth runs action immediately when authenticated`() = runTest {
        val authRepository = FakeAuthRepository(AuthState.Authenticated(testUser))
        val store = PendingProtectedActionStore()
        val notificationManager = AppNotificationManager()
        val authGuard = AuthGuard(authRepository, store, notificationManager)
        var wasRun = false

        val result = authGuard.runOrRequestAuth(PendingProtectedAction.GenerateAiRoadmap) {
            wasRun = true
        }

        assertTrue(result)
        assertTrue(wasRun)
        assertNull(store.pendingAction.value)
    }

    @Test
    fun `runOrRequestAuth stores pending action and sends login notification when guest`() = runTest {
        val authRepository = FakeAuthRepository(AuthState.Unauthenticated)
        val store = PendingProtectedActionStore()
        val notificationManager = AppNotificationManager()
        val authGuard = AuthGuard(authRepository, store, notificationManager)
        val notifications = mutableListOf<AppNotification>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            notificationManager.notifications.collect { notifications.add(it) }
        }

        val result = authGuard.runOrRequestAuth(PendingProtectedAction.GenerateAiRoadmap) {
            error("Protected action should not run for guests")
        }
        runCurrent()

        assertTrue(!result)
        assertEquals(PendingProtectedAction.GenerateAiRoadmap, store.pendingAction.value)
        assertEquals(AppNotificationAction.Login, notifications.single().action)
        assertEquals(AppNotificationVariant.Warning, notifications.single().variant)
    }

    private class FakeAuthRepository(
        initialAuthState: AuthState
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(initialAuthState)

        override suspend fun loginWithGoogle(idToken: String): Result<User> = Result.success(testUser)
        override suspend fun loginWithGithub(code: String): Result<User> = Result.success(testUser)
        override suspend fun linkWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)
        override suspend fun linkWithGithub(code: String): Result<Unit> = Result.success(Unit)
        override suspend fun logout(): Result<Unit> = Result.success(Unit)
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = Result.success(Unit)
        override suspend fun getCurrentUser(): Result<User> = Result.success(testUser)
    }

    private companion object {
        val testUser = User(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
    }
}
