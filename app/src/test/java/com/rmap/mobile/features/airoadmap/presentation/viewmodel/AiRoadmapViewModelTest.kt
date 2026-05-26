package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestionOption
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AiRoadmapViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val now = 1_000L
    private val futureDeadline = 90_000L

    @Test
    fun onSubmitSetup_setsTopicRequiredError_whenTopicIsBlank() = runTest {
        val viewModel = AiRoadmapViewModel(
            repository = FakeAiRoadmapRepository(),
            currentTimeMillis = { now }
        )

        viewModel.onSubmitSetup()

        assertEquals(AiRoadmapFormError.TopicRequired, viewModel.uiState.value.formError)
    }

    @Test
    fun onSubmitSetup_loadsQuestions_whenSetupIsValid() = runTest {
        val viewModel = AiRoadmapViewModel(
            repository = FakeAiRoadmapRepository(),
            currentTimeMillis = { now }
        )

        viewModel.onTopicChange("Android")
        viewModel.onDeadlineSelected(futureDeadline)
        viewModel.onSubmitSetup()

        assertEquals(AiRoadmapStep.Questions, viewModel.uiState.value.step)
        assertEquals(2, viewModel.uiState.value.questions.size)
    }

    @Test
    fun onSubmitAnswers_requiresEveryQuestionToBeAnswered() = runTest {
        val viewModel = preparedViewModel()

        viewModel.onOptionSelected("level", "level-1")
        viewModel.onSubmitAnswers()

        assertEquals(AiRoadmapFormError.AnswerAllQuestions, viewModel.uiState.value.formError)
    }

    @Test
    fun onSubmitAnswers_startsGenerationWithCustomAnswer() = runTest {
        val repository = FakeAiRoadmapRepository()
        val viewModel = preparedViewModel(repository)

        viewModel.onOptionSelected("level", "level-1")
        viewModel.onCustomAnswerChange("level", "I can ship small Android apps")
        viewModel.onOptionSelected("style", "style-2")
        viewModel.onSubmitAnswers()

        val request = repository.startedRequest
        assertTrue(repository.wasGenerationStarted)
        assertEquals("I can ship small Android apps", request?.answers?.first()?.customAnswer)
        assertEquals("level-1", request?.answers?.first()?.selectedOptionId)
    }

    @Test
    fun generationSuccess_movesScreenToLibraryAndAddsGeneratedRoadmap() = runTest {
        val repository = FakeAiRoadmapRepository()
        val viewModel = AiRoadmapViewModel(
            repository = repository,
            currentTimeMillis = { now }
        )

        viewModel.onTopicChange("Frontend Pro")
        repository.status.value = AiRoadmapGenerationStatus(
            phase = AiRoadmapGenerationPhase.Succeeded,
            progressPercent = 100,
            generatedRoadmapId = "frontend-pro"
        )

        assertEquals(AiRoadmapStep.Library, viewModel.uiState.value.step)
        assertEquals("frontend-pro", viewModel.uiState.value.generationStatus.generatedRoadmapId)
        assertEquals("frontend-pro", viewModel.uiState.value.generatedRoadmaps.first().id)
        assertEquals("Frontend Pro", viewModel.uiState.value.generatedRoadmaps.first().title)
    }

    private fun preparedViewModel(
        repository: FakeAiRoadmapRepository = FakeAiRoadmapRepository()
    ): AiRoadmapViewModel {
        return AiRoadmapViewModel(
            repository = repository,
            currentTimeMillis = { now }
        ).apply {
            onTopicChange("Android")
            onDeadlineSelected(futureDeadline)
            onSubmitSetup()
        }
    }
}

private class FakeAiRoadmapRepository : AiRoadmapRepository {
    val status = MutableStateFlow(AiRoadmapGenerationStatus())
    var wasGenerationStarted = false
        private set
    var startedRequest: AiRoadmapGenerationRequest? = null
        private set

    override val generationStatus: StateFlow<AiRoadmapGenerationStatus> = status

    override suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<List<AiRoadmapQuestion>> {
        return Result.success(
            listOf(
                AiRoadmapQuestion(
                    id = "level",
                    skillName = draft.topic,
                    prompt = "How comfortable are you?",
                    options = listOf(
                        AiRoadmapQuestionOption("level-1", "New"),
                        AiRoadmapQuestionOption("level-2", "Basic"),
                        AiRoadmapQuestionOption("level-3", "Intermediate"),
                        AiRoadmapQuestionOption("level-4", "Advanced")
                    )
                ),
                AiRoadmapQuestion(
                    id = "style",
                    skillName = "Learning style",
                    prompt = "How do you learn?",
                    options = listOf(
                        AiRoadmapQuestionOption("style-1", "Videos"),
                        AiRoadmapQuestionOption("style-2", "Projects"),
                        AiRoadmapQuestionOption("style-3", "Docs"),
                        AiRoadmapQuestionOption("style-4", "Quizzes")
                    )
                )
            )
        )
    }

    override suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest> {
        return if (answers.all { it.hasAnswer }) {
            Result.success(AiRoadmapGenerationRequest(draft, answers))
        } else {
            Result.failure(IllegalArgumentException("Missing answer"))
        }
    }

    override fun startGeneration(request: AiRoadmapGenerationRequest) {
        wasGenerationStarted = true
        startedRequest = request
    }

    override fun cancelGeneration() = Unit
}
