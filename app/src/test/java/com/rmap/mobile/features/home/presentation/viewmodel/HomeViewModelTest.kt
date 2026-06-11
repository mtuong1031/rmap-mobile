package com.rmap.mobile.features.home.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeMetrics
import com.rmap.mobile.features.home.domain.model.HomeNextUnlock
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomePlanNode
import com.rmap.mobile.features.home.domain.model.HomeRoadmapChapter
import com.rmap.mobile.features.home.domain.model.HomeRoadmapGroup
import com.rmap.mobile.features.home.domain.model.HomeRoadmapProgress
import com.rmap.mobile.features.home.domain.model.HomeSearchResult

import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial load populates home sections`() = runTest {
        val viewModel = HomeViewModel(
            homeRepository = FakeHomeRepository(),
            authRepository = FakeAuthRepository()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.learningPlans.size)
        assertEquals(1, state.recommendedRoadmaps.size)
        assertEquals(1, state.trendingRoadmaps.size)
        assertEquals(2, state.streakDays)
        assertEquals(0.5f, state.readinessFraction, 0.0f)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `warning is only available on roadmap with pace warning`() = runTest {
        val viewModel = HomeViewModel(
            homeRepository = FakeHomeRepository(),
            authRepository = FakeAuthRepository()
        )

        advanceUntilIdle()

        val plans = viewModel.uiState.value.learningPlans
        assertNotNull(plans.first { it.id == "roadmap-warning" }.paceWarning)
        assertNull(plans.first { it.id == "roadmap-normal" }.paceWarning)
    }

    @Test
    fun `authenticated greeting uses first name from full name`() = runTest {
        val viewModel = HomeViewModel(
            homeRepository = FakeHomeRepository(),
            authRepository = FakeAuthRepository()
        )

        advanceUntilIdle()

        assertEquals("Thinh", viewModel.uiState.value.userName)
        assertEquals(true, viewModel.uiState.value.isAuthenticated)
    }

    @Test
    fun `maps vietnam hour to greeting period`() {
        assertEquals(HomeGreetingPeriod.Morning, currentVietnamGreetingPeriod(hourOfDay = 8))
        assertEquals(HomeGreetingPeriod.Afternoon, currentVietnamGreetingPeriod(hourOfDay = 13))
        assertEquals(HomeGreetingPeriod.Evening, currentVietnamGreetingPeriod(hourOfDay = 19))
        assertEquals(HomeGreetingPeriod.Night, currentVietnamGreetingPeriod(hourOfDay = 23))
    }

    @Test
    fun `extracts first name safely`() {
        assertEquals("Thinh", " Thinh Hoang Duy ".toFirstName())
        assertEquals("", "   ".toFirstName())
    }
}

private class FakeHomeRepository : HomeRepository {
    override suspend fun getHomeContent(): Result<HomeContent> = Result.success(testHomeContent())

    override suspend fun searchDashboard(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ): Result<HomeSearchResult> = error("Not used in HomeViewModelTest")
}

private class FakeAuthRepository : AuthRepository {
    override val authState = MutableStateFlow<AuthState>(
        AuthState.Authenticated(
            User(
                id = "user-1",
                email = "thinh@gmail.com",
                fullName = "Thinh Hoang Duy",
                avatarUrl = null,
                role = "user",
                createdAt = "2026-05-31T08:11:01.885Z"
            )
        )
    )

    override suspend fun loginWithGoogle(idToken: String): Result<User> = error("Not used")

    override suspend fun loginWithGithub(code: String): Result<User> = error("Not used")

    override suspend fun logout(): Result<Unit> = error("Not used")

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = error("Not used")

    override suspend fun getCurrentUser(): Result<User> = error("Not used")
}

private fun testHomeContent(): HomeContent = HomeContent(
    activeRoadmaps = listOf(
        activeRoadmap("roadmap-warning", warning = true),
        activeRoadmap("roadmap-normal", warning = false)
    ),
    metrics = HomeMetrics(
        roadmapCompletionPct = 12.5,
        streakDays = 2,
        readinessPct = 50.0
    ),
    recommendations = listOf(
        HomeTemplateRoadmap(
            roadmapId = "template-1",
            title = "GraphQL Roadmap",
            description = null,
            goalName = "GraphQL",
            roleCategory = "WEB_DEVELOPMENT",
            categoryLabel = "Web Development",
            estimatedWeeks = null,
            durationLabel = null,
            nodesTotal = 0,
            requiredNodesTotal = 50
        )
    ),

    trendings = listOf(
        HomeTrendingRoadmap(
            rank = 1,
            roadmapId = "trend-1",
            title = "Kotlin",
            roleCategory = "LANGUAGES_AND_PLATFORMS",
            categoryLabel = "Languages And Platforms",
            estimatedWeeks = null,
            durationLabel = null,
            nodesTotal = 10,
            trendText = "Trending now"
        )
    )
)

private fun activeRoadmap(
    id: String,
    warning: Boolean
): HomeActiveRoadmap = HomeActiveRoadmap(
    roadmapId = id,
    title = "Backend Roadmap",
    goalName = "Backend",
    roleCategory = "WEB_DEVELOPMENT",
    startedAt = "2026-06-01T03:22:18.055Z",
    currentGroup = HomeRoadmapGroup("group-1", "Internet"),
    planNode = HomePlanNode("node-1", "HTTP", null, "REQUIRED", 3),
    chapter = HomeRoadmapChapter(1, 14, "Chapter 1/14"),
    progress = HomeRoadmapProgress(1, 49, 2.0),
    nextUnlock = HomeNextUnlock("unlock-1", "Frontend Core"),
    paceWarning = if (warning) {
        HomePaceWarning(
            isBehind = true,
            paceDeficitPct = 33.3,
            estimatedDelayDays = 1,
            message = "Finish 1 skill node today.",
            title = "You are behind.",
            actionLabel = "Adjust plan"
        )
    } else {
        null
    }
)
