package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.R
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapContentItem
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationManager
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RoadmapDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadRoadmap success updates detail state`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("roadmap-1", state.roadmapId)
        assertEquals("Backend Roadmap", state.title)
        assertEquals("Backend Developer", state.categoryLabel)
        assertEquals(RoadmapPrimaryAction.ContinueLearning, state.primaryAction)
        assertEquals("REST API", state.nextActionTitle)
        assertTrue(state.groups.isNotEmpty())
        assertTrue(state.groups.first().nodes.isNotEmpty())
    }

    @Test
    fun `loadRoadmap success with no learning progress shows start learning action`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(RoadmapPrimaryAction.StartLearning, state.primaryAction)
        assertEquals("REST API", state.nextActionTitle)
    }

    @Test
    fun `loadRoadmap success with started roadmap displays not-started nodes as in-progress`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail.copy(hasStartedLearning = true))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertEquals(RoadmapPrimaryAction.ContinueLearning, state.primaryAction)
        assertEquals(RoadmapNodeStatus.InProgress, state.groups.first().nodes.first().status)
        assertEquals(RoadmapNodeAction.Continue, state.groups.first().nodes.first().action)
    }

    @Test
    fun `loadRoadmap node description prioritizes estimated hours over resources`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(templateResourceDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val node = viewModel.uiState.value.groups.first().nodes.first()
        assertEquals(R.string.roadmap_detail_node_estimated_hours, node.descriptionResId)
        assertEquals(listOf("4"), node.descriptionArgs)
        assertEquals(4, node.resourcesCount)
    }

    @Test
    fun `loadRoadmap node description falls back to resources when estimated hours are missing`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(resourcesOnlyDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val node = viewModel.uiState.value.groups.first().nodes.first()
        assertEquals(R.string.roadmap_detail_node_resources_available, node.descriptionResId)
        assertEquals(listOf("4"), node.descriptionArgs)
        assertEquals(4, node.resourcesCount)
    }

    @Test
    fun `onContinueClick starts first not-started node and navigates to learning`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")

        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onContinueClick()

        assertEquals(
            listOf("roadmap-1"),
            repository.startedRoadmapIds
        )

        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = "roadmap-1",
                nodeId = "node-api",
                skillId = "skill-api",
                isCompleted = false,
                groupTitle = "API Foundations"
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.InProgress, viewModel.uiState.value.groups.first().nodes.first().status)
    }

    @Test
    fun `onContinueClick with template roadmap starts roadmap before learning`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail.copy(isTemplate = true))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")

        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onContinueClick()

        assertEquals(
            listOf("roadmap-1"),
            repository.startedRoadmapIds
        )
        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = "roadmap-1",
                nodeId = "node-api",
                skillId = "skill-api",
                isCompleted = false,
                groupTitle = "API Foundations"
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.InProgress, viewModel.uiState.value.groups.first().nodes.first().status)
        assertEquals(null, viewModel.uiState.value.updatingNodeId)
    }

    @Test
    fun `onContinueClick emits failure when backend start fails`() = runTest {
        val failureMessage = "Server error"
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail),
            startResult = Result.failure(IllegalStateException(failureMessage))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")

        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onContinueClick()

        assertEquals(RoadmapDetailEvent.NodeProgressUpdateFailed(failureMessage), event.await())
        assertEquals(listOf("roadmap-1"), repository.startedRoadmapIds)
        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(RoadmapNodeStatus.NotStarted, viewModel.uiState.value.groups.first().nodes.first().status)
        assertEquals(null, viewModel.uiState.value.updatingNodeId)
    }

    @Test
    fun `onContinueClick does not emit failure event when backend start fails with Unauthorized`() = runTest {
        val failureMessage = "Unauthorized"
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail),
            startResult = Result.failure(
                AppException(
                    message = failureMessage,
                    type = NetworkErrorType.Unauthorized
                )
            )
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")

        var didEmitEvent = false
        val job = launch {
            viewModel.events.collect {
                didEmitEvent = true
            }
        }

        viewModel.onContinueClick()

        assertFalse(didEmitEvent)
        assertEquals(listOf("roadmap-1"), repository.startedRoadmapIds)
        job.cancel()
    }

    @Test
    fun `onContinueClick as guest enqueues sign in required notification`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val authRepository = FakeAuthRepository(AuthState.Unauthenticated)
        val appNotificationManager = AppNotificationManager()
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            authRepository = authRepository,
            appNotificationManager = appNotificationManager
        )
        viewModel.loadRoadmap("roadmap-1")
        val notification = async(start = CoroutineStart.UNDISPATCHED) { appNotificationManager.notifications.first() }

        viewModel.onContinueClick()

        assertTrue(repository.startedRoadmapIds.isEmpty())
        assertEquals(R.string.auth_required_title, notification.await().titleResId)
        assertEquals(R.string.auth_required_start_roadmap_message, notification.await().messageResId)
        assertEquals(AppNotificationVariant.Warning, notification.await().variant)
    }

    @Test
    fun `onNodeActionClick as guest enqueues sign in required notification`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val authRepository = FakeAuthRepository(AuthState.Unauthenticated)
        val appNotificationManager = AppNotificationManager()
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            authRepository = authRepository,
            appNotificationManager = appNotificationManager
        )
        viewModel.loadRoadmap("roadmap-1")
        val notification = async(start = CoroutineStart.UNDISPATCHED) { appNotificationManager.notifications.first() }

        val targetNode = viewModel.uiState.value.groups.first().nodes.first()
        viewModel.onNodeActionClick(targetNode)

        assertEquals(R.string.auth_required_title, notification.await().titleResId)
        assertEquals(R.string.auth_required_view_lesson_message, notification.await().messageResId)
        assertEquals(AppNotificationVariant.Warning, notification.await().variant)
    }

    @Test
    fun `loadRoadmap with blank id shows invalid id error without repository call`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap(" ")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.roadmap_detail_error_invalid_id, state.errorMessageResId)
        assertTrue(repository.requestedIds.isEmpty())
    }

    @Test
    fun `loadRoadmap failure updates generic error state`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.failure(IllegalStateException("raw server error"))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("missing")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("missing", state.roadmapId)
        assertEquals(R.string.roadmap_detail_error_load_failed, state.errorMessageResId)
    }

    @Test
    fun `retryLoadRoadmap retries failed roadmap request`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.failure(IllegalStateException("raw server error"))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")
        viewModel.retryLoadRoadmap()

        assertEquals(listOf("roadmap-1", "roadmap-1"), repository.requestedIds)
    }

    @Test
    fun `loadRoadmap success with no sections updates empty state`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(testDetail.copy(sections = emptyList(), totalLessons = 0))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
        assertTrue(state.groups.isEmpty())
        assertEquals(null, state.errorMessageResId)
    }

    @Test
    fun `loadRoadmap maps content items in roadmap order while preserving search lists`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        val orderedTitles = state.contentItems.map { item ->
            when (item) {
                is RoadmapDetailContentUiItem.Group -> item.group.title
                is RoadmapDetailContentUiItem.Milestone -> item.milestone.title
            }
        }

        assertEquals(
            listOf(
                "Internet Fundamentals",
                "Node.js Environment",
                "Basic API Server",
                "Data Management",
                "Web Security",
                "Secure API Project"
            ),
            orderedTitles
        )
        assertEquals(4, state.groups.size)
        assertEquals(2, state.milestones.size)
        assertEquals(RoadmapMilestoneState.Available, state.milestones.first().state)
        assertEquals(RoadmapMilestoneState.Locked, state.milestones.last().state)
        assertEquals("Basic API Server", state.nextActionTitle)
        assertEquals("Basic API Server", state.nextUnlockTitle)
        assertEquals(RoadmapNextActionTarget.Milestone("milestone-api"), state.nextActionTarget)
    }

    @Test
    fun `search returns no results when query does not match roadmap content`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")
        viewModel.onSearchQueryChange("no matching content")

        val state = viewModel.uiState.value
        assertTrue(state.searchResultNodes().isEmpty())
        assertTrue(state.searchResultGroups().isEmpty())
        assertTrue(state.searchResultMilestones().isEmpty())
    }

    @Test
    fun `search matches milestone title and description`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")
        viewModel.onSearchQueryChange("secure api")

        val milestones = viewModel.uiState.value.searchResultMilestones()
        assertEquals(listOf("milestone-secure"), milestones.map { it.id })
    }

    @Test
    fun `search quick filter locked returns locked roadmap items only`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")
        viewModel.onSearchQueryChange("locked")

        val state = viewModel.uiState.value
        assertEquals(listOf("data", "security"), state.searchResultNodes().map { it.id })
        assertEquals(listOf("Data Management", "Web Security"), state.searchResultGroups().map { it.title })
        assertEquals(listOf("milestone-secure"), state.searchResultMilestones().map { it.id })
    }

    @Test
    fun `refreshRoadmap preserves active search state for same roadmap`() {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )

        viewModel.loadRoadmap("roadmap-1")
        viewModel.onSearchFocus()
        viewModel.onSearchQueryChange("secure api")
        viewModel.refreshRoadmap()

        val state = viewModel.uiState.value
        assertEquals(listOf("roadmap-1", "roadmap-1"), repository.requestedIds)
        assertEquals("secure api", state.searchQuery)
        assertTrue(state.isSearchActive)
        assertTrue(state.isSearchInputFocused)
        assertEquals(listOf("milestone-secure"), state.searchResultMilestones().map { it.id })
    }

    @Test
    fun `onContinueClick with milestone next action selects milestone instead of locked node`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(orderedMilestoneDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onContinueClick()

        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(
            RoadmapDetailEvent.MilestoneSelected("milestone-api"),
            event.await()
        )
    }

    @Test
    fun `onNodeActionClick with in-progress node navigates to learning without progress update`() = runTest {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")
        val node = viewModel.uiState.value.groups.first().nodes.first()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onNodeActionClick(node)

        val state = viewModel.uiState.value
        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(listOf("roadmap-1"), repository.requestedIds)
        assertEquals(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = "roadmap-1",
                nodeId = "node-api",
                skillId = "skill-api",
                isCompleted = false,
                groupTitle = "API Foundations"
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.InProgress, state.groups.first().nodes.first().status)
        assertEquals(null, state.updatingNodeId)
    }

    @Test
    fun `onNodeActionClick with not-started node navigates without starting roadmap`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")
        val node = viewModel.uiState.value.groups.first().nodes.first()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onNodeActionClick(node)

        assertTrue(repository.startedRoadmapIds.isEmpty())
        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(
            listOf("roadmap-1"),
            repository.requestedIds
        )
        assertEquals(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = "roadmap-1",
                nodeId = "node-api",
                skillId = "skill-api",
                isCompleted = false,
                groupTitle = "API Foundations"
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.NotStarted, viewModel.uiState.value.groups.first().nodes.first().status)
        assertEquals(null, viewModel.uiState.value.updatingNodeId)
    }

    @Test
    fun `onNodeActionClick with locked node navigates to learning without progress update`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(lockedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository
        )
        viewModel.loadRoadmap("roadmap-1")
        val node = viewModel.uiState.value.groups.first().nodes.first()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onNodeActionClick(node)

        assertEquals(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = "roadmap-1",
                nodeId = "node-api",
                skillId = "skill-api",
                isCompleted = false,
                groupTitle = "API Foundations"
            ),
            event.await()
        )
        assertTrue(repository.progressUpdates.isEmpty())
    }

    private class FakeDashboardRepository : DashboardRepository {
        var refreshCount = 0
            private set

        override suspend fun getDashboard(): Result<Dashboard> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun refreshDashboard(): Result<Dashboard> {
            refreshCount += 1
            return Result.failure(UnsupportedOperationException())
        }
    }

    private class FakeRoadmapRepository(
        private var detailResult: Result<RoadmapDetail> = Result.success(testDetail),
        private val startResult: Result<Unit> = Result.success(Unit),
        private val updateResult: Result<NodeProgressUpdateResult> = Result.success(
            NodeProgressUpdateResult(
                nodeId = "node-api",
                status = LearningStatus.Completed,
                unlockedNodeIds = listOf("node-auth")
            )
        )
    ) : RoadmapRepository {
        val requestedIds = mutableListOf<String>()
        val startedRoadmapIds = mutableListOf<String>()
        val progressUpdates = mutableListOf<ProgressUpdateRequest>()

        override suspend fun getLearningProgress(): Result<LearningProgress> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> {
            return Result.success(emptyList())
        }

        override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
            return Result.success(emptyList())
        }

        override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> {
            return Result.success(emptyList())
        }

        override suspend fun searchRoadmaps(
            query: String,
            categoryId: String?,
            page: Int,
            perPage: Int
        ): Result<Pair<List<RoadmapSummary>, Int>> {
            return Result.success(Pair(emptyList(), 0))
        }

        override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
            requestedIds += id
            return detailResult
        }

        override suspend fun getLearningNode(
            roadmapId: String,
            nodeId: String
        ): Result<LearningNodeDetail> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun getMilestoneDetail(
            roadmapId: String,
            milestoneId: String
        ): Result<MilestoneDetail> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun submitMilestone(
            roadmapId: String,
            milestoneId: String,
            repoUrl: String
        ): Result<MilestoneSubmission> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun getNodeQuiz(
            roadmapId: String,
            nodeId: String
        ): Result<NodeQuiz> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun submitNodeQuiz(
            roadmapId: String,
            nodeId: String,
            answers: List<NodeQuizAnswer>
        ): Result<NodeQuizSubmissionResult> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun getRoadmapNodeLearningContent(
            roadmapId: String,
            nodeId: String,
            skillId: String
        ): Result<SkillLearningContent> {
            return Result.failure(UnsupportedOperationException())
        }

        override suspend fun startRoadmap(roadmapId: String): Result<Unit> {
            startedRoadmapIds += roadmapId
            if (startResult.isSuccess) {
                detailResult = Result.success(testDetail)
            }
            return startResult
        }

        override suspend fun updateNodeProgress(
            roadmapId: String,
            nodeId: String,
            status: LearningStatus
        ): Result<NodeProgressUpdateResult> {
            progressUpdates += ProgressUpdateRequest(
                roadmapId = roadmapId,
                nodeId = nodeId,
                status = status
            )
            detailResult = Result.success(
                testDetail.copy(
                    completedLessons = 1,
                    sections = listOf(
                        LearningModuleSection(
                            title = "API Foundations",
                            modules = listOf(
                                LearningModule(
                                    title = "REST API",
                                    status = LearningStatus.Completed,
                                    progressPercent = 100,
                                    icon = LearningTopicIcon.Storage,
                                    subLessons = emptyList(),
                                    id = "node-api",
                                    skillId = "skill-api"
                                )
                            )
                        )
                    )
                )
            )
            return updateResult
        }
    }

    private data class ProgressUpdateRequest(
        val roadmapId: String,
        val nodeId: String,
        val status: LearningStatus
    )



    private companion object {
        val testDetail = RoadmapDetail(
            id = "roadmap-1",
            title = "Backend Roadmap",
            completedLessons = 0,
            totalLessons = 1,
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API",
                            status = LearningStatus.InProgress,
                            progressPercent = 50,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api"
                        )
                    )
                )
            ),
            aiTip = AiScholarTip(
                currentModule = "REST API",
                recommendedTopic = "HTTP",
                nextModule = "Authentication"
            ),
            roleId = "role-backend",
            roleName = "Backend Developer"
        )

        val notStartedDetail = testDetail.copy(
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API",
                            status = LearningStatus.NotStarted,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api"
                        )
                    )
                )
            )
        )

        val lockedDetail = testDetail.copy(
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API",
                            status = LearningStatus.Locked,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api"
                        )
                    )
                )
            )
        )

        val templateResourceDetail = testDetail.copy(
            isTemplate = true,
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API",
                            status = LearningStatus.NotStarted,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api",
                            estimatedHours = 4,
                            resourcesCount = 4
                        )
                    )
                )
            )
        )

        val resourcesOnlyDetail = templateResourceDetail.copy(
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API",
                            status = LearningStatus.NotStarted,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api",
                            estimatedHours = null,
                            resourcesCount = 4
                        )
                    )
                )
            )
        )

        val orderedMilestoneDetail = run {
            val sections = listOf(
                LearningModuleSection(
                    title = "Internet Fundamentals",
                    modules = listOf(learningModule("internet", LearningStatus.Completed))
                ),
                LearningModuleSection(
                    title = "Node.js Environment",
                    modules = listOf(learningModule("node", LearningStatus.Completed))
                ),
                LearningModuleSection(
                    title = "Data Management",
                    modules = listOf(learningModule("data", LearningStatus.Locked))
                ),
                LearningModuleSection(
                    title = "Web Security",
                    modules = listOf(learningModule("security", LearningStatus.Locked))
                )
            )
            val milestones = listOf(
                RoadmapMilestone(
                    id = "milestone-api",
                    title = "Basic API Server",
                    description = null,
                    status = LearningStatus.InProgress
                ),
                RoadmapMilestone(
                    id = "milestone-secure",
                    title = "Secure API Project",
                    description = null,
                    status = LearningStatus.Locked
                )
            )

            testDetail.copy(
                sections = sections,
                milestones = milestones,
                contentItems = listOf(
                    RoadmapContentItem.Group(sections[0]),
                    RoadmapContentItem.Group(sections[1]),
                    RoadmapContentItem.Milestone(milestones[0]),
                    RoadmapContentItem.Group(sections[2]),
                    RoadmapContentItem.Group(sections[3]),
                    RoadmapContentItem.Milestone(milestones[1])
                )
            )
        }

        private fun learningModule(
            id: String,
            status: LearningStatus
        ): LearningModule {
            return LearningModule(
                title = id,
                status = status,
                progressPercent = if (status == LearningStatus.Completed) 100 else 0,
                icon = LearningTopicIcon.Storage,
                subLessons = emptyList(),
                id = id,
                skillId = "skill-$id"
            )
        }
    }

    private class FakeAuthRepository(
        initialState: AuthState = AuthState.Unauthenticated
    ) : AuthRepository {
        override val authState = MutableStateFlow(initialState)
        override suspend fun loginWithGoogle(idToken: String): Result<User> = Result.failure(UnsupportedOperationException())
        override suspend fun loginWithGithub(code: String): Result<User> = Result.failure(UnsupportedOperationException())
        override suspend fun linkWithGoogle(idToken: String): Result<Unit> = Result.failure(UnsupportedOperationException())
        override suspend fun linkWithGithub(code: String): Result<Unit> = Result.failure(UnsupportedOperationException())
        override suspend fun logout(): Result<Unit> = Result.success(Unit)
        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = Result.failure(UnsupportedOperationException())
        override suspend fun getCurrentUser(): Result<User> = Result.failure(UnsupportedOperationException())
    }
}
