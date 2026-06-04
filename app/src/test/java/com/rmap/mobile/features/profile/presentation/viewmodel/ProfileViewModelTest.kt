package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.LogoutUseCase
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.domain.model.UserProfile
import com.rmap.mobile.features.profile.domain.model.UserRoadmapProgress
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadProfile exposes all active roadmaps in backend order`() = runTest {
        val activeRoadmaps = (1..8).map { index ->
            roadmap(
                id = "roadmap-$index",
                startedAt = "2026-05-${20 + index}T00:00:00.000Z"
            )
        }
        val profile = profile(roadmaps = activeRoadmaps + roadmap(id = "not-started", startedAt = null))
        val viewModel = newViewModel(profile)

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(8, viewModel.uiState.value.activeRoadmaps.size)
        assertEquals("roadmap-1", viewModel.uiState.value.activeRoadmaps.first().id)
        assertEquals("roadmap-8", viewModel.uiState.value.activeRoadmaps.last().id)
    }

    @Test
    fun `loadProfile exposes recent activity from backend dashboard`() = runTest {
        val activity = listOf(
            UserDailyActivity(activityDate = "2026-06-01", nodesCompleted = 2),
            UserDailyActivity(activityDate = "2026-06-02", nodesCompleted = 0)
        )
        val viewModel = newViewModel(profile(roadmaps = emptyList(), recentActivity = activity))

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(activity, viewModel.uiState.value.recentActivity)
    }

    @Test
    fun `loadProfile exposes empty active roadmaps when backend has no started roadmap`() = runTest {
        val viewModel = newViewModel(
            profile(
                roadmaps = listOf(
                    roadmap(id = "roadmap-1", startedAt = null),
                    roadmap(id = "roadmap-2", startedAt = null)
                )
            )
        )

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(emptyList<UserRoadmapProgress>(), viewModel.uiState.value.activeRoadmaps)
    }

    private fun newViewModel(profile: UserProfile): ProfileViewModel {
        return ProfileViewModel(
            profileRepository = FakeProfileRepository(profile),
            logoutUseCase = LogoutUseCase(FakeAuthRepository())
        )
    }

    private class FakeProfileRepository(
        private val profile: UserProfile
    ) : ProfileRepository {
        override suspend fun getProfile(): Result<UserProfile> = Result.success(profile)
    }

    private class FakeAuthRepository : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)

        override suspend fun login(email: String, password: String): Result<User> = Result.success(testUser)

        override suspend fun register(email: String, password: String, fullName: String): Result<User> {
            return Result.success(testUser)
        }

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

        override suspend fun getCurrentUser(): Result<User> = Result.success(testUser)
    }

    private companion object {
        fun profile(
            roadmaps: List<UserRoadmapProgress>,
            recentActivity: List<UserDailyActivity> = emptyList()
        ): UserProfile {
            return UserProfile(
                userName = "Thinh",
                name = "Thinh Duy",
                role = "Learner",
                avatarUrl = "",
                xp = 0,
                streakDays = 5,
                certificates = 0,
                roadmaps = roadmaps,
                recentActivity = recentActivity
            )
        }

        fun roadmap(id: String, startedAt: String?): UserRoadmapProgress {
            return UserRoadmapProgress(
                id = id,
                title = "Roadmap $id",
                description = null,
                deadlineDate = null,
                estimatedWeeks = 8,
                roleCategory = "WEB_DEVELOPMENT",
                startedAt = startedAt,
                completionPercent = 25,
                nodesTotal = 10,
                nodesCompleted = 3,
                timelineWarning = null
            )
        }

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
