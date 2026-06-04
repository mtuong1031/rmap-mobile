package com.rmap.mobile.features.roadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NodeQuizViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `loadQuiz starts on first question with answered progress`() = runTest {
        val viewModel = NodeQuizViewModel(FakeRoadmapRepository())

        viewModel.loadQuiz(roadmapId = "roadmap-1", nodeId = "node-1")
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("question-1", state.currentQuestion?.id)
        assertEquals(0, state.currentQuestionIndex)
        assertEquals(0, state.answeredQuestionCount)
        assertEquals(1f / 3f, state.progressFraction)
        assertNull(state.errorMessage)
    }

    @Test
    fun `selecting option auto advances to next question`() = runTest {
        val viewModel = NodeQuizViewModel(FakeRoadmapRepository())
        viewModel.loadQuiz(roadmapId = "roadmap-1", nodeId = "node-1")
        runCurrent()

        viewModel.onOptionSelected(questionId = "question-1", optionKey = "A")
        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)

        advanceTimeBy(321L)
        runCurrent()

        assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        assertEquals("question-2", viewModel.uiState.value.currentQuestion?.id)
    }

    @Test
    fun `next question requires current question answer`() = runTest {
        val viewModel = NodeQuizViewModel(FakeRoadmapRepository())
        viewModel.loadQuiz(roadmapId = "roadmap-1", nodeId = "node-1")
        runCurrent()

        viewModel.onNextQuestion()

        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `submit sends answers after all questions are answered`() = runTest {
        val repository = FakeRoadmapRepository()
        val viewModel = NodeQuizViewModel(repository)
        viewModel.loadQuiz(roadmapId = "roadmap-1", nodeId = "node-1")
        runCurrent()

        viewModel.onOptionSelected(questionId = "question-1", optionKey = "A")
        viewModel.onNextQuestion()
        viewModel.onOptionSelected(questionId = "question-2", optionKey = "B")
        viewModel.onNextQuestion()
        viewModel.onOptionSelected(questionId = "question-3", optionKey = "C")
        viewModel.onSubmitClick()
        runCurrent()

        assertEquals(
            listOf(
                NodeQuizAnswer(questionId = "question-1", selectedOption = "A"),
                NodeQuizAnswer(questionId = "question-2", selectedOption = "B"),
                NodeQuizAnswer(questionId = "question-3", selectedOption = "C")
            ),
            repository.submittedAnswers
        )
        assertNotNull(viewModel.uiState.value.result)
        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
    }

    private class FakeRoadmapRepository : RoadmapRepository {
        var submittedAnswers: List<NodeQuizAnswer> = emptyList()
            private set

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

        override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
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

        override suspend fun getNodeQuiz(
            roadmapId: String,
            nodeId: String
        ): Result<NodeQuiz> {
            return Result.success(testQuiz)
        }

        override suspend fun submitNodeQuiz(
            roadmapId: String,
            nodeId: String,
            answers: List<NodeQuizAnswer>
        ): Result<NodeQuizSubmissionResult> {
            submittedAnswers = answers
            return Result.success(
                NodeQuizSubmissionResult(
                    scorePercent = 100,
                    passed = true,
                    correctCount = answers.size,
                    totalQuestions = answers.size,
                    suggestion = null,
                    unlockedNodeIds = emptyList(),
                    questionResults = answers.map { answer ->
                        NodeQuizQuestionResult(
                            questionId = answer.questionId,
                            selectedOption = answer.selectedOption,
                            correctOption = answer.selectedOption,
                            isCorrect = true
                        )
                    }
                )
            )
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

        override suspend fun updateNodeProgress(
            roadmapId: String,
            nodeId: String,
            status: LearningStatus
        ): Result<NodeProgressUpdateResult> {
            return Result.failure(NotImplementedError())
        }
    }

    private companion object {
        val testQuiz = NodeQuiz(
            nodeId = "node-1",
            skillId = "skill-1",
            questions = listOf(
                question("question-1"),
                question("question-2"),
                question("question-3")
            )
        )

        fun question(id: String): NodeQuizQuestion {
            return NodeQuizQuestion(
                id = id,
                text = "Question $id",
                options = listOf(
                    NodeQuizOption(key = "A", text = "Option A"),
                    NodeQuizOption(key = "B", text = "Option B"),
                    NodeQuizOption(key = "C", text = "Option C")
                )
            )
        }
    }
}
