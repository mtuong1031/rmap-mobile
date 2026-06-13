package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.R
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestCase
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuite
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuiteStatus
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RoadmapMilestoneViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadMilestone success maps detail state`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapMilestoneViewModel(repository)

        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Basic API Server", state.title)
        assertEquals(RoadmapMilestoneDetailStatusUiModel.InProgress, state.status)
        assertEquals("Raw Node.js API Server Evaluation", state.testSuite?.title)
        assertEquals(80, state.testSuite?.passThresholdPercent)
        assertEquals(2, state.testSuite?.testCases?.size)
        assertEquals("Dependency Audit", state.testSuite?.testCases?.first()?.name)
        assertFalse(state.latestSubmission?.hasTestExecutionResult ?: true)
        assertEquals("https://github.com/example/rmap-test", state.repoUrl)
        assertFalse(state.isTestSuiteExpanded)
        assertTrue(state.canSubmit)
    }

    @Test
    fun `completed submission can be displayed as test execution result`() {
        val repository = FakeRoadmapRepository(
            detail = testDetail.copy(
                latestSubmission = testSubmission.copy(
                    status = MilestoneSubmissionStatus.Error,
                    outputLog = "[error]\nspawn docker ENOENT"
                )
            )
        )
        val viewModel = RoadmapMilestoneViewModel(repository)

        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        assertTrue(viewModel.uiState.value.latestSubmission?.hasTestExecutionResult ?: false)
    }

    @Test
    fun `test suite toggle updates expanded state`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapMilestoneViewModel(repository)
        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        assertFalse(viewModel.uiState.value.isTestSuiteExpanded)

        viewModel.onTestSuiteToggleClick()

        assertTrue(viewModel.uiState.value.isTestSuiteExpanded)

        viewModel.onTestSuiteToggleClick()

        assertFalse(viewModel.uiState.value.isTestSuiteExpanded)
    }

    @Test
    fun `submit with invalid url shows validation error without repository call`() {
        val repository = FakeRoadmapRepository()
        val viewModel = RoadmapMilestoneViewModel(repository)
        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        viewModel.onRepoUrlChanged("https://example.com/not-github")
        viewModel.onSubmitClick()

        assertEquals(R.string.roadmap_milestone_repo_url_error, viewModel.uiState.value.repoUrlErrorResId)
        assertTrue(repository.submissions.isEmpty())
    }

    @Test
    fun `submit success updates latest submission and reloads detail`() = runTest {
        val repository = FakeRoadmapRepository(
            detail = testDetail.copy(latestSubmission = null)
        )
        val viewModel = RoadmapMilestoneViewModel(repository)
        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )
        viewModel.onTestSuiteToggleClick()
        viewModel.onRepoUrlChanged("https://github.com/example/rmap-test")
        val event = async(start = CoroutineStart.UNDISPATCHED) { viewModel.events.first() }

        viewModel.onSubmitClick()

        assertEquals(
            listOf(MilestoneSubmitRequest("roadmap-1", "milestone-api", "https://github.com/example/rmap-test")),
            repository.submissions
        )
        assertEquals(2, repository.detailRequests.size)
        assertEquals(RoadmapMilestoneEvent.SubmissionQueued, event.await())
        assertEquals(RoadmapMilestoneSubmissionStatusUiModel.Running, viewModel.uiState.value.latestSubmission?.status)
        assertEquals("https://github.com/example/rmap-test", viewModel.uiState.value.repoUrl)
        assertTrue(viewModel.uiState.value.canSubmit)
        assertTrue(viewModel.uiState.value.isTestSuiteExpanded)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `locked milestone cannot submit`() {
        val repository = FakeRoadmapRepository(
            detail = testDetail.copy(status = LearningStatus.Locked)
        )
        val viewModel = RoadmapMilestoneViewModel(repository)
        viewModel.loadMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        viewModel.onRepoUrlChanged("https://github.com/example/rmap-test")
        viewModel.onSubmitClick()

        assertEquals(RoadmapMilestoneDetailStatusUiModel.Locked, viewModel.uiState.value.status)
        assertFalse(viewModel.uiState.value.canSubmit)
        assertTrue(repository.submissions.isEmpty())
    }

    private class FakeRoadmapRepository(
        private var detail: MilestoneDetail = testDetail,
        private val submitResult: Result<MilestoneSubmission> = Result.success(testSubmission)
    ) : RoadmapRepository {
        val detailRequests = mutableListOf<MilestoneDetailRequest>()
        val submissions = mutableListOf<MilestoneSubmitRequest>()

        override suspend fun getLearningProgress(): Result<LearningProgress> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun searchRoadmaps(
            query: String,
            categoryId: String?,
            page: Int,
            perPage: Int
        ): Result<Pair<List<RoadmapSummary>, Int>> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getLearningNode(
            roadmapId: String,
            nodeId: String
        ): Result<LearningNodeDetail> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getMilestoneDetail(
            roadmapId: String,
            milestoneId: String
        ): Result<MilestoneDetail> {
            detailRequests += MilestoneDetailRequest(roadmapId, milestoneId)
            return Result.success(detail)
        }

        override suspend fun submitMilestone(
            roadmapId: String,
            milestoneId: String,
            repoUrl: String
        ): Result<MilestoneSubmission> {
            submissions += MilestoneSubmitRequest(roadmapId, milestoneId, repoUrl)
            return submitResult.onSuccess { submission ->
                detail = detail.copy(latestSubmission = submission)
            }
        }

        override suspend fun getNodeQuiz(
            roadmapId: String,
            nodeId: String
        ): Result<NodeQuiz> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun submitNodeQuiz(
            roadmapId: String,
            nodeId: String,
            answers: List<NodeQuizAnswer>
        ): Result<NodeQuizSubmissionResult> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun getRoadmapNodeLearningContent(
            roadmapId: String,
            nodeId: String,
            skillId: String
        ): Result<SkillLearningContent> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun startRoadmap(roadmapId: String): Result<Unit> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun resetRoadmapProgress(roadmapId: String): Result<Unit> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> {
            return Result.failure(NotImplementedError())
        }

        override suspend fun updateNodeProgress(
            roadmapId: String,
            nodeId: String,
            status: LearningStatus
        ): Result<NodeProgressUpdateResult> {
            return Result.failure(NotImplementedError())
        }
    }

    private data class MilestoneDetailRequest(
        val roadmapId: String,
        val milestoneId: String
    )

    private data class MilestoneSubmitRequest(
        val roadmapId: String,
        val milestoneId: String,
        val repoUrl: String
    )

    private companion object {
        val testSubmission = MilestoneSubmission(
            id = "submission-1",
            repoUrl = "https://github.com/example/rmap-test",
            testSuiteId = "suite-1",
            status = MilestoneSubmissionStatus.Running,
            outputLog = null,
            passRatePercent = null,
            passedTests = null,
            totalTests = null,
            attemptNumber = 1,
            createdAt = "2026-06-01T00:00:00Z",
            completedAt = null,
            testResults = emptyList()
        )

        val testDetail = MilestoneDetail(
            roadmapId = "roadmap-1",
            nodeId = "milestone-api",
            title = "Basic API Server",
            description = "Construct a raw Node.js HTTP server.",
            status = LearningStatus.InProgress,
            testSuite = MilestoneTestSuite(
                id = "suite-1",
                title = "Raw Node.js API Server Evaluation",
                summary = "Verifies the implementation of a manual HTTP server.",
                passThresholdPercent = 80,
                status = MilestoneTestSuiteStatus.Ready,
                testCases = listOf(
                    MilestoneTestCase(
                        name = "Dependency Audit",
                        description = "Verifies that no high-level frameworks are listed."
                    ),
                    MilestoneTestCase(
                        name = "HTTP Module Integration",
                        description = "Checks for usage of the native node:http module."
                    )
                )
            ),
            latestSubmission = testSubmission
        )
    }
}
