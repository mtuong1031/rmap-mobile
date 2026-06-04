package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.R
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(RoadmapPrimaryAction.StartLearning, state.primaryAction)
        assertEquals("REST API", state.nextActionTitle)
    }

    @Test
    fun `onContinueClick starts first not-started node and navigates to learning`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
                isCompleted = false
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
                isCompleted = false
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.InProgress, viewModel.uiState.value.groups.first().nodes.first().status)
        assertEquals(null, viewModel.uiState.value.updatingNodeId)
    }

    @Test
    fun `onContinueClick emits failure when backend start fails`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(notStartedDetail),
            startResult = Result.failure(IllegalStateException("Server error"))
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
        )
        viewModel.loadRoadmap("roadmap-1")

        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onContinueClick()

        assertEquals(RoadmapDetailEvent.NodeProgressUpdateFailed, event.await())
        assertEquals(listOf("roadmap-1"), repository.startedRoadmapIds)
        assertTrue(repository.progressUpdates.isEmpty())
        assertEquals(RoadmapNodeStatus.NotStarted, viewModel.uiState.value.groups.first().nodes.first().status)
        assertEquals(null, viewModel.uiState.value.updatingNodeId)
    }

    @Test
    fun `loadRoadmap with blank id shows invalid id error without repository call`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
        )

        viewModel.loadRoadmap("roadmap-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEmpty)
        assertTrue(state.groups.isEmpty())
        assertEquals(null, state.errorMessageResId)
    }

    @Test
    fun `onNodeActionClick with in-progress node navigates to learning without progress update`() = runTest {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
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
                isCompleted = false
            ),
            event.await()
        )
        assertEquals(RoadmapNodeStatus.InProgress, state.groups.first().nodes.first().status)
        assertEquals(null, state.updatingNodeId)
    }

    @Test
    fun `onNodeActionClick with locked node emits locked event without progress update`() = runTest {
        val repository = FakeRoadmapRepository(
            detailResult = Result.success(lockedDetail)
        )
        val viewModel = RoadmapDetailViewModel(
            repository = repository,
            bookmarkRepository = FakeBookmarkRepository()
        )
        viewModel.loadRoadmap("roadmap-1")
        val node = viewModel.uiState.value.groups.first().nodes.first()
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onNodeActionClick(node)

        assertEquals(RoadmapDetailEvent.NodeLocked, event.await())
        assertTrue(repository.progressUpdates.isEmpty())
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

        override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
            return Result.success(emptyList())
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

    private class FakeBookmarkRepository : BookmarkRepository {
        override fun observeSavedRoadmaps(): Flow<List<RoadmapBookmark>> {
            return flowOf(emptyList())
        }

        override fun observeSavedSkills(): Flow<List<SkillBookmark>> {
            return flowOf(emptyList())
        }

        override suspend fun getSavedRoadmaps(): Result<List<RoadmapBookmark>> {
            return Result.success(emptyList())
        }

        override suspend fun getSavedSkills(): Result<List<SkillBookmark>> {
            return Result.success(emptyList())
        }

        override suspend fun saveRoadmap(roadmapId: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun saveRoadmap(snapshot: RoadmapBookmarkSnapshot): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun isRoadmapSaved(roadmapId: String): Result<Boolean> {
            return Result.success(false)
        }

        override suspend fun saveSkill(
            skillId: String,
            roadmapId: String
        ): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun saveSkill(snapshot: SkillBookmarkSnapshot): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun deleteSkill(skillId: String): Result<Unit> {
            return Result.success(Unit)
        }

        override suspend fun isSkillSaved(skillId: String): Result<Boolean> {
            return Result.success(false)
        }
    }

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
    }
}
