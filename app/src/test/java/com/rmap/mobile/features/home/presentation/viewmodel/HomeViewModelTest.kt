package com.rmap.mobile.features.home.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeMetrics
import com.rmap.mobile.features.home.domain.model.HomeNextUnlock
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomePlanNode
import com.rmap.mobile.features.home.domain.model.HomeRoadmapChapter
import com.rmap.mobile.features.home.domain.model.HomeRoadmapGroup
import com.rmap.mobile.features.home.domain.model.HomeRoadmapProgress
import com.rmap.mobile.features.home.domain.model.HomeTemplateCategory
import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
            bookmarkRepository = FakeBookmarkRepository()
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.learningPlans.size)
        assertEquals(1, state.recommendedRoadmaps.size)
        assertEquals(1, state.categories.size)
        assertEquals(1, state.trendingRoadmaps.size)
        assertEquals(2, state.streakDays)
        assertEquals(0.5f, state.readinessFraction, 0.0f)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `warning is only available on roadmap with pace warning`() = runTest {
        val viewModel = HomeViewModel(
            homeRepository = FakeHomeRepository(),
            bookmarkRepository = FakeBookmarkRepository()
        )

        advanceUntilIdle()

        val plans = viewModel.uiState.value.learningPlans
        assertNotNull(plans.first { it.id == "roadmap-warning" }.paceWarning)
        assertNull(plans.first { it.id == "roadmap-normal" }.paceWarning)
    }

    @Test
    fun `bookmark recommendation saves snapshot and updates saved ids`() = runTest {
        val bookmarkRepository = FakeBookmarkRepository()
        val viewModel = HomeViewModel(
            homeRepository = FakeHomeRepository(),
            bookmarkRepository = bookmarkRepository
        )
        advanceUntilIdle()

        viewModel.onRecommendedRoadmapBookmarkClick(viewModel.uiState.value.recommendedRoadmaps.single())
        advanceUntilIdle()

        assertEquals("template-1", bookmarkRepository.savedSnapshot?.roadmapId)
        assertTrue("template-1" in viewModel.uiState.value.savedRoadmapIds)
    }
}

private class FakeHomeRepository : HomeRepository {
    override suspend fun getHomeContent(): Result<HomeContent> = Result.success(testHomeContent())
}

private class FakeBookmarkRepository : BookmarkRepository {
    private val savedRoadmaps = MutableStateFlow<List<RoadmapBookmark>>(emptyList())
    var savedSnapshot: RoadmapBookmarkSnapshot? = null

    override fun observeSavedRoadmaps(): Flow<List<RoadmapBookmark>> = savedRoadmaps

    override fun observeSavedSkills(): Flow<List<SkillBookmark>> = MutableStateFlow(emptyList())

    override suspend fun getSavedRoadmaps(): Result<List<RoadmapBookmark>> = Result.success(savedRoadmaps.value)

    override suspend fun getSavedSkills(): Result<List<SkillBookmark>> = Result.success(emptyList())

    override suspend fun saveRoadmap(roadmapId: String): Result<Unit> = Result.success(Unit)

    override suspend fun saveRoadmap(snapshot: RoadmapBookmarkSnapshot): Result<Unit> {
        savedSnapshot = snapshot
        return Result.success(Unit)
    }

    override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> = Result.success(Unit)

    override suspend fun isRoadmapSaved(roadmapId: String): Result<Boolean> = Result.success(false)

    override suspend fun saveSkill(skillId: String, roadmapId: String): Result<Unit> = Result.success(Unit)

    override suspend fun deleteSkill(skillId: String): Result<Unit> = Result.success(Unit)

    override suspend fun isSkillSaved(skillId: String): Result<Boolean> = Result.success(false)
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
    categories = listOf(HomeTemplateCategory("WEB_DEVELOPMENT", "Web Development", 9)),
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
