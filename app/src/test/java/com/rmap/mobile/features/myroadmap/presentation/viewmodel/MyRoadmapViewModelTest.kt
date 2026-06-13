package com.rmap.mobile.features.myroadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmap
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
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
            roadmapRepository = FakeRoadmapRepository(),
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
            roadmapRepository = FakeRoadmapRepository(),
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

    @Test
    fun `resetRoadmapProgress calls repository and emits success event`() = runTest {
        val roadmapRepository = FakeRoadmapRepository()
        val viewModel = MyRoadmapViewModel(
            dashboardRepository = FakeDashboardRepository(),
            roadmapRepository = roadmapRepository,
            completedSkillsRepository = FakeCompletedSkillsRepository(),
            learningReminderContextRepository = FakeLearningReminderContextRepository(),
            authRepository = FakeAuthRepository()
        )
        runCurrent()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.resetRoadmapProgress(" roadmap-1 ")
        runCurrent()

        assertEquals(listOf("roadmap-1"), roadmapRepository.resetRoadmapIds)
        assertEquals(MyRoadmapEvent.RoadmapProgressResetSucceeded, event.await())
    }

    @Test
    fun `deleteRoadmap calls repository removes roadmap and emits success event`() = runTest {
        val roadmapRepository = FakeRoadmapRepository()
        val viewModel = MyRoadmapViewModel(
            dashboardRepository = FakeDashboardRepository(dashboardWithRoadmap()),
            roadmapRepository = roadmapRepository,
            completedSkillsRepository = FakeCompletedSkillsRepository(),
            learningReminderContextRepository = FakeLearningReminderContextRepository(),
            authRepository = FakeAuthRepository()
        )
        runCurrent()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.deleteRoadmap("roadmap-1", isTemplate = false)
        runCurrent()

        assertEquals(listOf("roadmap-1"), roadmapRepository.deletedRoadmapIds)
        assertEquals(MyRoadmapEvent.RoadmapDeleted, event.await())
        assertEquals(emptyList<MyRoadmapCardUiModel>(), viewModel.uiState.value.roadmaps)
    }

    @Test
    fun `deleteRoadmap ignores template roadmap`() = runTest {
        val roadmapRepository = FakeRoadmapRepository()
        val viewModel = MyRoadmapViewModel(
            dashboardRepository = FakeDashboardRepository(dashboardWithRoadmap(isTemplate = true)),
            roadmapRepository = roadmapRepository,
            completedSkillsRepository = FakeCompletedSkillsRepository(),
            learningReminderContextRepository = FakeLearningReminderContextRepository(),
            authRepository = FakeAuthRepository()
        )
        runCurrent()

        viewModel.deleteRoadmap("roadmap-1", isTemplate = true)
        runCurrent()

        assertEquals(emptyList<String>(), roadmapRepository.deletedRoadmapIds)
        assertEquals(1, viewModel.uiState.value.roadmaps.size)
    }
}

private class FakeDashboardRepository(
    private val dashboard: Dashboard = dashboard()
) : DashboardRepository {
    override suspend fun getDashboard(): Result<Dashboard> = Result.success(dashboard)
}

private class FakeRoadmapRepository : RoadmapRepository {
    val resetRoadmapIds = mutableListOf<String>()
    val deletedRoadmapIds = mutableListOf<String>()

    override suspend fun getLearningProgress(): Result<LearningProgress> = error("Not used")
    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> = error("Not used")
    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> = error("Not used")
    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> = error("Not used")
    override suspend fun searchRoadmaps(query: String, categoryId: String?, page: Int, perPage: Int): Result<Pair<List<RoadmapSummary>, Int>> = error("Not used")
    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> = error("Not used")
    override suspend fun getLearningNode(roadmapId: String, nodeId: String): Result<LearningNodeDetail> = error("Not used")
    override suspend fun getMilestoneDetail(roadmapId: String, milestoneId: String): Result<MilestoneDetail> = error("Not used")
    override suspend fun submitMilestone(roadmapId: String, milestoneId: String, repoUrl: String): Result<MilestoneSubmission> = error("Not used")
    override suspend fun getNodeQuiz(roadmapId: String, nodeId: String): Result<NodeQuiz> = error("Not used")
    override suspend fun submitNodeQuiz(roadmapId: String, nodeId: String, answers: List<NodeQuizAnswer>): Result<NodeQuizSubmissionResult> = error("Not used")
    override suspend fun getRoadmapNodeLearningContent(roadmapId: String, nodeId: String, skillId: String): Result<SkillLearningContent> = error("Not used")
    override suspend fun startRoadmap(roadmapId: String): Result<Unit> = error("Not used")

    override suspend fun resetRoadmapProgress(roadmapId: String): Result<Unit> {
        resetRoadmapIds += roadmapId
        return Result.success(Unit)
    }

    override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> {
        deletedRoadmapIds += roadmapId
        return Result.success(Unit)
    }

    override suspend fun updateNodeProgress(roadmapId: String, nodeId: String, status: LearningStatus): Result<NodeProgressUpdateResult> = error("Not used")
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

private fun dashboardWithRoadmap(isTemplate: Boolean = false): Dashboard {
    return dashboard().copy(
        roadmaps = listOf(
            DashboardRoadmap(
                roadmapId = "roadmap-1",
                deadlineDate = null,
                description = null,
                estimatedWeeks = 4,
                goalName = null,
                isTemplate = isTemplate,
                roleCategory = "WEB_DEVELOPMENT",
                startedAt = null,
                title = "Backend Roadmap",
                completionPct = 0.0,
                streakDays = 0,
                skillReadinessPct = 0.0,
                nodesTotal = 10,
                nodesCompleted = 0,
                timelineWarning = null
            )
        )
    )
}
