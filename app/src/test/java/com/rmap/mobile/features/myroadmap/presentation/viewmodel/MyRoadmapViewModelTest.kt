package com.rmap.mobile.features.myroadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmapStatus
import com.rmap.mobile.features.dashboard.domain.model.DashboardSkillCategory
import com.rmap.mobile.features.dashboard.domain.model.DashboardSummary
import com.rmap.mobile.features.dashboard.domain.model.DashboardUserProfile
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.myroadmap.domain.model.CompletedSkillPage
import com.rmap.mobile.features.myroadmap.domain.repository.CompletedSkillsRepository
import com.rmap.mobile.features.profile.domain.repository.LearningReminderContextRepository
import com.rmap.mobile.features.profile.domain.model.LearningReminderContext
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyRoadmapViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `loadDashboard successfully updates roadmaps state`() = runTest {
        val viewModel = MyRoadmapViewModel(
            dashboardRepository = FakeDashboardRepository(),
            completedSkillsRepository = FakeCompletedSkillsRepository(),
            learningReminderContextRepository = FakeLearningReminderContextRepository(),
            authRepository = FakeAuthRepository()
        )

        runCurrent()

        assertEquals("RMap", viewModel.uiState.value.userName)
        assertEquals(0, viewModel.uiState.value.roadmaps.size)
    }

    @Test
    fun `onSearchQueryChange updates query and clearSearch resets it`() = runTest {
        val viewModel = MyRoadmapViewModel(
            dashboardRepository = FakeDashboardRepository(),
            completedSkillsRepository = FakeCompletedSkillsRepository(),
            learningReminderContextRepository = FakeLearningReminderContextRepository(),
            authRepository = FakeAuthRepository()
        )
        runCurrent()

        viewModel.onSearchQueryChange("kotlin")
        assertEquals("kotlin", viewModel.uiState.value.searchQuery)

        viewModel.clearSearch()
        assertEquals("", viewModel.uiState.value.searchQuery)
    }
}

private class FakeDashboardRepository : DashboardRepository {
    override suspend fun getDashboard(): Result<Dashboard> = Result.success(dashboard())
}

private class FakeCompletedSkillsRepository : CompletedSkillsRepository {
    override suspend fun getCompletedSkills(
        category: String,
        page: Int,
        perPage: Int
    ): Result<CompletedSkillPage> {
        return Result.failure(IllegalStateException("Not implemented"))
    }
}

private class FakeLearningReminderContextRepository : LearningReminderContextRepository {
    override fun getContext(): LearningReminderContext = LearningReminderContext(activeRoadmapTitle = null)
    override suspend fun setActiveRoadmap(title: String?) {}
}

private class FakeAuthRepository(
    initialState: AuthState = AuthState.Authenticated(
        User(
            id = "user-1",
            email = "user@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "USER",
            createdAt = "2026-01-01T00:00:00Z"
        )
    )
) : AuthRepository {
    override val authState: StateFlow<AuthState> = MutableStateFlow(initialState)
    override suspend fun loginWithGoogle(idToken: String): Result<User> = error("Not used")
    override suspend fun loginWithGithub(code: String): Result<User> = error("Not used")
    override suspend fun linkWithGoogle(idToken: String): Result<Unit> = error("Not used")
    override suspend fun linkWithGithub(code: String): Result<Unit> = error("Not used")
    override suspend fun logout(): Result<Unit> = error("Not used")
    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = error("Not used")
    override suspend fun getCurrentUser(): Result<User> = error("Not used")
}

private fun dashboard(): Dashboard = Dashboard(
    userProfile = DashboardUserProfile(
        id = "user-1",
        email = "user@example.com",
        fullName = "RMap Learner",
        role = "USER",
        createdAt = "2026-01-01T00:00:00Z"
    ),
    roadmaps = emptyList(),
    streakDays = 0,
    activityRecent = emptyList(),
    summary = DashboardSummary(
        totalRoadmaps = 0,
        activeRoadmaps = 0,
        completedRoadmaps = 0,
        totalSkills = 432,
        completedSkills = 7,
        inProgressSkills = 0,
        lockedSkills = 425,
        currentStreak = 0
    ),
    skillCategories = listOf(
        DashboardSkillCategory(
            category = "WEB_DEVELOPMENT",
            label = "Web Development",
            totalSkills = 432,
            completedSkills = 7
        )
    ),
    roadmapStatus = DashboardRoadmapStatus(
        behindPace = 0,
        onTrack = 0,
        completed = 0,
        notStarted = 0
    )
)
