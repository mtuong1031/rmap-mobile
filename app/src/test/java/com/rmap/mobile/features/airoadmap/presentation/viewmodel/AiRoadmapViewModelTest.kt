package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationRequest
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestionOption
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuizResult
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
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

        viewModel.onTopicChange("Android Developer")
        viewModel.onDeadlineSelected(futureDeadline)
        viewModel.onSubmitSetup()

        assertEquals(AiRoadmapStep.Questions, viewModel.uiState.value.step)
        assertEquals(7, viewModel.uiState.value.questions.size)
        assertEquals("MOBILE_DEVELOPMENT", viewModel.uiState.value.roleCategory)
    }

    @Test
    fun onSubmitAnswers_requiresEveryQuestionToBeAnswered() = runTest {
        val viewModel = preparedViewModel()

        viewModel.onOptionSelected("level", "level-1")
        viewModel.onSubmitAnswers()

        assertEquals(AiRoadmapFormError.AnswerAllQuestions, viewModel.uiState.value.formError)
    }

    @Test
    fun onOptionSelected_keepsCurrentQuestionUntilNextIsClicked() = runTest {
        val viewModel = preparedViewModel()

        viewModel.onOptionSelected("level", "level-1")

        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        assertEquals("level-1", viewModel.uiState.value.questions.first().selectedOptionId)

        viewModel.onNextQuestion()

        assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
    }

    @Test
    fun onCustomAnswerChange_acceptsCustomAnswerForQuestionWithOptions() = runTest {
        val viewModel = preparedViewModel()

        viewModel.onOptionSelected("level", "level-1")
        viewModel.onCustomAnswerChange("level", "I know Compose but need backend basics")

        val firstQuestion = viewModel.uiState.value.questions.first()
        assertTrue(firstQuestion.hasAnswer)
        assertTrue(firstQuestion.isCustomAnswerSelected)
        assertEquals(null, firstQuestion.selectedOptionId)
        assertEquals("I know Compose but need backend basics", firstQuestion.customAnswer)
        assertEquals("I know Compose but need backend basics", firstQuestion.answerText)
    }

    @Test
    fun onNextQuestion_requiresTextWhenCustomOptionIsSelected() = runTest {
        val viewModel = preparedViewModel()

        viewModel.onCustomAnswerChange("level", "")
        viewModel.onNextQuestion()

        val firstQuestion = viewModel.uiState.value.questions.first()
        assertTrue(firstQuestion.isCustomAnswerSelected)
        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        assertEquals(AiRoadmapFormError.CustomAnswerRequired, viewModel.uiState.value.formError)
    }

    @Test
    fun onSubmitAnswers_startsGenerationWithCustomAnswerForQuestionWithoutOptions() = runTest {
        val repository = FakeAiRoadmapRepository()
        val viewModel = preparedViewModel(repository)

        viewModel.onOptionSelected("level", "level-1")
        (2..6).forEach { index ->
            viewModel.onOptionSelected("question-$index", "question-$index-option-1")
        }
        viewModel.onCustomAnswerChange("question-7", "I can ship small Android apps")
        viewModel.onSubmitAnswers()

        val request = repository.startedRequest
        assertTrue(repository.wasGenerationStarted)
        assertEquals("New", request?.answers?.first()?.answer)
        assertEquals("How comfortable are you?", request?.answers?.first()?.question)
        assertEquals("I can ship small Android apps", request?.answers?.last()?.answer)
        assertEquals("Question 7?", request?.answers?.last()?.question)
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
        assertEquals("Frontend Pro Roadmap", viewModel.uiState.value.generatedRoadmaps.first().title)
    }

    private fun preparedViewModel(
        repository: FakeAiRoadmapRepository = FakeAiRoadmapRepository()
    ): AiRoadmapViewModel {
        return AiRoadmapViewModel(
            repository = repository,
            currentTimeMillis = { now }
        ).apply {
            onTopicChange("Android Developer")
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

    override suspend fun getGeneratedRoadmaps(): Result<List<AiGeneratedRoadmap>> {
        return Result.success(
            listOf(
                AiGeneratedRoadmap(
                    id = "frontend-pro",
                    title = "Frontend Pro Roadmap",
                    lessonsCount = 12,
                    durationWeeks = 8,
                    generatedAtEpochMillis = 1_000L
                )
            )
        )
    }

    override suspend fun getPersonalizedQuestions(draft: AiRoadmapDraft): Result<AiRoadmapQuizResult> {
        return Result.success(
            AiRoadmapQuizResult(
                roleCategory = "MOBILE_DEVELOPMENT",
                questions = listOf(
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
                    )
                ) + (2..6).map { index ->
                    AiRoadmapQuestion(
                        id = "question-$index",
                        skillName = draft.topic,
                        prompt = "Question $index?",
                        options = listOf(
                            AiRoadmapQuestionOption("question-$index-option-1", "Answer $index")
                        )
                    )
                } + AiRoadmapQuestion(
                    id = "question-7",
                    skillName = draft.topic,
                    prompt = "Question 7?",
                    options = emptyList()
                )
            )
        )
    }

    override suspend fun prepareGeneration(
        draft: AiRoadmapDraft,
        answers: List<AiRoadmapAnswer>
    ): Result<AiRoadmapGenerationRequest> {
        return if (answers.all { it.hasAnswer } && answers.size == 7 && draft.roleCategory != null) {
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
