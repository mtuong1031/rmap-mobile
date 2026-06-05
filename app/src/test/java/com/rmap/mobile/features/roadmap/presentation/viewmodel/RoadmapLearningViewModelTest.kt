package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.R
import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
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
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.SkillLevelTag
import com.rmap.mobile.features.roadmap.domain.model.SkillResource
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RoadmapLearningViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadLearningContent success maps skill detail and resources`() {
        val skillRepository = FakeSkillLearningRepository()
        val roadmapRepository = FakeRoadmapRepository()
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = skillRepository,
            roadmapRepository = roadmapRepository
        )

        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("roadmap-1", state.roadmapId)
        assertEquals("node-api", state.nodeId)
        assertEquals("skill-api", state.skillId)
        assertEquals("REST API", state.skill?.name)
        assertEquals(1, state.resources.size)
        assertEquals("HTTP course", state.resources.single().title)
        assertTrue(state.canTakeQuiz)
        assertTrue(state.canMarkCompleted)
        assertTrue(skillRepository.requestedSkillIds.isEmpty())
        assertEquals(listOf(NodeContentRequest("roadmap-1", "node-api", "skill-api")), roadmapRepository.nodeContentRequests)
        assertTrue(roadmapRepository.progressUpdates.isEmpty())
    }

    @Test
    fun `loadLearningContent with invalid args shows invalid state without repository call`() {
        val skillRepository = FakeSkillLearningRepository()
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = skillRepository,
            roadmapRepository = FakeRoadmapRepository()
        )

        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = " ",
            isCompleted = false
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.roadmap_learning_error_invalid_args, state.errorMessageResId)
        assertTrue(skillRepository.requestedSkillIds.isEmpty())
    }

    @Test
    fun `loadLearningContent falls back to roadmap node when skill api returns not found`() {
        val skillRepository = FakeSkillLearningRepository(
            result = Result.failure(
                AppException(
                    message = "Skill not found.",
                    type = NetworkErrorType.NotFound
                )
            )
        )
        val roadmapRepository = FakeRoadmapRepository(
            nodeContentResult = Result.failure(
                AppException(
                    message = "Roadmap node not found.",
                    type = NetworkErrorType.NotFound
                )
            ),
            detailResult = Result.success(fallbackDetail)
        )
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = skillRepository,
            roadmapRepository = roadmapRepository
        )

        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(null, state.errorMessageResId)
        assertEquals("REST API fallback", state.skill?.name)
        assertEquals("Fallback node description.", state.skill?.description)
        assertEquals(4, state.skill?.estimatedHours)
        assertTrue(state.resources.isEmpty())
        assertEquals(listOf("roadmap-1"), roadmapRepository.requestedDetailIds)
        assertTrue(roadmapRepository.progressUpdates.isEmpty())
    }

    @Test
    fun `onMarkCompletedClick patches completed status and emits completed event`() = runTest {
        val roadmapRepository = FakeRoadmapRepository()
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = FakeSkillLearningRepository(),
            roadmapRepository = roadmapRepository
        )
        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onMarkCompletedClick()

        assertEquals(
            listOf(
                ProgressUpdateRequest(
                    roadmapId = "roadmap-1",
                    nodeId = "node-api",
                    status = LearningStatus.Completed
                )
            ),
            roadmapRepository.progressUpdates
        )
        assertEquals(RoadmapLearningEvent.NodeCompleted, event.await())
        assertTrue(viewModel.uiState.value.isCompleted)
        assertFalse(viewModel.uiState.value.isCompleting)
    }

    @Test
    fun `onMarkCompletedClick without passed quiz does not patch completed status`() = runTest {
        val roadmapRepository = FakeRoadmapRepository(
            nodeContentResult = Result.success(testContent.copy(status = LearningStatus.InProgress, quizPassed = false))
        )
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = FakeSkillLearningRepository(),
            roadmapRepository = roadmapRepository
        )
        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onMarkCompletedClick()

        assertEquals(RoadmapLearningEvent.NodeCompletionRequiresQuiz, event.await())
        assertTrue(roadmapRepository.progressUpdates.isEmpty())
        assertFalse(viewModel.uiState.value.isCompleted)
        assertFalse(viewModel.uiState.value.isCompleting)
    }

    @Test
    fun `loadLearningContent for locked node disables take quiz and mark completed`() {
        val roadmapRepository = FakeRoadmapRepository(
            nodeContentResult = Result.success(testContent.copy(status = LearningStatus.Locked, quizPassed = false))
        )
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = FakeSkillLearningRepository(),
            roadmapRepository = roadmapRepository
        )

        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )

        val state = viewModel.uiState.value
        assertTrue(state.isNodeLocked)
        assertFalse(state.canTakeQuiz)
        assertFalse(state.canMarkCompleted)
    }

    @Test
    fun `onMarkCompletedClick with template roadmap emits failure when backend update fails`() = runTest {
        val roadmapRepository = FakeRoadmapRepository(
            detailResult = Result.success(fallbackDetail.copy(isTemplate = true)),
            updateResult = Result.failure(
                AppException(
                    message = "Node progress not found.",
                    type = NetworkErrorType.NotFound
                )
            )
        )
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = FakeSkillLearningRepository(),
            roadmapRepository = roadmapRepository
        )
        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onMarkCompletedClick()

        assertEquals(RoadmapLearningEvent.NodeCompletionFailed, event.await())
        assertFalse(viewModel.uiState.value.isCompleted)
        assertFalse(viewModel.uiState.value.isCompleting)
        assertEquals(
            listOf(
                ProgressUpdateRequest(
                    roadmapId = "roadmap-1",
                    nodeId = "node-api",
                    status = LearningStatus.Completed
                )
            ),
            roadmapRepository.progressUpdates
        )
    }

    @Test
    fun `onMarkCompletedClick with personal roadmap emits failure when backend update fails`() = runTest {
        val roadmapRepository = FakeRoadmapRepository(
            detailResult = Result.success(fallbackDetail),
            updateResult = Result.failure(IllegalStateException("Server error"))
        )
        val viewModel = RoadmapLearningViewModel(
            skillLearningRepository = FakeSkillLearningRepository(),
            roadmapRepository = roadmapRepository
        )
        viewModel.loadLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api",
            isCompleted = false
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onMarkCompletedClick()

        assertEquals(RoadmapLearningEvent.NodeCompletionFailed, event.await())
        assertFalse(viewModel.uiState.value.isCompleted)
        assertFalse(viewModel.uiState.value.isCompleting)
    }

    private class FakeSkillLearningRepository(
        private val result: Result<SkillLearningContent> = Result.success(testContent)
    ) : SkillLearningRepository {
        val requestedSkillIds = mutableListOf<String>()

        override suspend fun getSkillLearningContent(skillId: String): Result<SkillLearningContent> {
            requestedSkillIds += skillId
            return result
        }
    }

    private class FakeRoadmapRepository(
        private val nodeContentResult: Result<SkillLearningContent> = Result.success(
            testContent.copy(status = LearningStatus.InProgress, quizPassed = true)
        ),
        private val detailResult: Result<RoadmapDetail> = Result.failure(UnsupportedOperationException()),
        private val updateResult: Result<NodeProgressUpdateResult>? = null
    ) : RoadmapRepository {
        val progressUpdates = mutableListOf<ProgressUpdateRequest>()
        val requestedDetailIds = mutableListOf<String>()
        val nodeContentRequests = mutableListOf<NodeContentRequest>()

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
            requestedDetailIds += id
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
            nodeContentRequests += NodeContentRequest(
                roadmapId = roadmapId,
                nodeId = nodeId,
                skillId = skillId
            )
            return nodeContentResult
        }

        override suspend fun startRoadmap(roadmapId: String): Result<Unit> {
            return Result.failure(UnsupportedOperationException())
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
            updateResult?.let { return it }
            return Result.success(
                NodeProgressUpdateResult(
                    nodeId = nodeId,
                    status = status,
                    unlockedNodeIds = emptyList()
                )
            )
        }
    }

    private data class ProgressUpdateRequest(
        val roadmapId: String,
        val nodeId: String,
        val status: LearningStatus
    )

    private data class NodeContentRequest(
        val roadmapId: String,
        val nodeId: String,
        val skillId: String
    )

    private companion object {
        val testContent = SkillLearningContent(
            skill = SkillDetail(
                id = "skill-api",
                name = "REST API",
                description = "Design and consume REST APIs.",
                category = "Backend",
                estimatedHours = 4
            ),
            resources = listOf(
                SkillResource(
                    id = "resource-http",
                    skillId = "skill-api",
                    title = "HTTP course",
                    url = "https://example.com/http",
                    platform = SkillResourcePlatform.Youtube,
                    isFree = true,
                    levelTag = SkillLevelTag.Fresher
                )
            )
        )

        val fallbackDetail = RoadmapDetail(
            id = "roadmap-1",
            title = "Backend Roadmap",
            completedLessons = 0,
            totalLessons = 1,
            sections = listOf(
                LearningModuleSection(
                    title = "API Foundations",
                    modules = listOf(
                        LearningModule(
                            title = "REST API fallback",
                            status = LearningStatus.NotStarted,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList(),
                            id = "node-api",
                            skillId = "skill-api",
                            estimatedHours = 4,
                            description = "Fallback node description."
                        )
                    )
                )
            ),
            aiTip = null,
            roleId = "role-backend",
            roleName = "Backend"
        )
    }
}
