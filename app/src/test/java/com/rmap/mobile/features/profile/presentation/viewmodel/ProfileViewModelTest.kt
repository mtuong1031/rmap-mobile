package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.LogoutUseCase
import com.rmap.mobile.features.profile.domain.model.UserActivitySummary
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadProfile uses activity endpoint summary`() = runTest {
        val activity = UserActivitySummary(
            streakDays = 5,
            longestStreak = 7,
            activity = listOf(
                UserDailyActivity(activityDate = "2026-06-01", nodesCompleted = 2),
                UserDailyActivity(activityDate = "2026-06-02", nodesCompleted = 0)
            )
        )
        val repository = FakeProfileRepository(activity)
        val viewModel = newViewModel(repository = repository)

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals(1, repository.getActivityCalls)
        assertEquals(5, viewModel.uiState.value.streak)
        assertEquals(7, viewModel.uiState.value.longestStreak)
        assertEquals(activity.activity, viewModel.uiState.value.recentActivity)
    }

    @Test
    fun `identity comes from auth state not dashboard`() = runTest {
        val authRepository = FakeAuthRepository(
            initialState = AuthState.Authenticated(
                testUser.copy(role = "frontend_developer")
            )
        )
        val viewModel = newViewModel(authRepository = authRepository)

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("RMap Learner", viewModel.uiState.value.name)
        assertEquals("Frontend Developer", viewModel.uiState.value.role)
    }

    @Test
    fun `guest profile does not request authenticated activity`() = runTest {
        val repository = FakeProfileRepository()
        val viewModel = newViewModel(
            repository = repository,
            authRepository = FakeAuthRepository(AuthState.Unauthenticated)
        )

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isAuthenticated)
        assertEquals(0, repository.getActivityCalls)
    }

    @Test
    fun `loadProfile exposes error when activity endpoint fails`() = runTest {
        val viewModel = newViewModel(repository = FakeProfileRepository(error = IllegalStateException("No activity")))

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("No activity", viewModel.uiState.value.errorMessage)
    }

    private fun newViewModel(
        repository: FakeProfileRepository = FakeProfileRepository(),
        authRepository: FakeAuthRepository = FakeAuthRepository()
    ): ProfileViewModel {
        return ProfileViewModel(
            profileRepository = repository,
            authRepository = authRepository,
            logoutUseCase = LogoutUseCase(authRepository)
        )
    }

    private class FakeProfileRepository(
        private val activity: UserActivitySummary = UserActivitySummary(
            streakDays = 5,
            longestStreak = 5,
            activity = emptyList()
        ),
        private val error: Throwable? = null
    ) : ProfileRepository {
        var getActivityCalls: Int = 0
            private set

        override suspend fun getActivity(): Result<UserActivitySummary> {
            getActivityCalls += 1
            return error?.let { Result.failure(it) } ?: Result.success(activity)
        }
    }

    private class FakeAuthRepository(
        initialState: AuthState = AuthState.Authenticated(testUser)
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(initialState)

        override suspend fun loginWithGoogle(idToken: String): Result<User> = Result.success(testUser)

        override suspend fun loginWithGithub(code: String): Result<User> = Result.success(testUser)

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

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
