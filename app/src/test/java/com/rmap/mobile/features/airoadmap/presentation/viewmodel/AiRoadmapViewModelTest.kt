package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.core.auth.PendingProtectedAction
import com.rmap.mobile.core.auth.ProtectedActionGate
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
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
            protectedActionGate = FakeProtectedActionGate.authenticated(),
            currentTimeMillis = { now }
        )

        viewModel.onSubmitSetup()

        assertEquals(AiRoadmapFormError.TopicRequired, viewModel.uiState.value.formError)
    }

    @Test
    fun onSubmitSetup_setsTopicRequiredError_whenTopicIsBlank_emitsShowErrorEvent() = runTest {
        val viewModel = AiRoadmapViewModel(
            repository = FakeAiRoadmapRepository(),
            protectedActionGate = FakeProtectedActionGate.authenticated(),
            currentTimeMillis = { now }
        )
        val events = mutableListOf<AiRoadmapEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        viewModel.onSubmitSetup()

        assertEquals(AiRoadmapFormError.TopicRequired, viewModel.uiState.value.formError)
        assertEquals(listOf(AiRoadmapEvent.ShowError(AiRoadmapFormError.TopicRequired)), events)
    }

    @Test
    fun onSubmitSetup_loadsQuestions_whenSetupIsValid() = runTest {
        val viewModel = AiRoadmapViewModel(
            repository = FakeAiRoadmapRepository(),
            protectedActionGate = FakeProtectedActionGate.authenticated(),
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
    fun onSubmitAnswers_requiresEveryQuestionToBeAnswered_emitsShowErrorEvent() = runTest {
        val viewModel = preparedViewModel()
        val events = mutableListOf<AiRoadmapEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        viewModel.onOptionSelected("level", "level-1")
        viewModel.onSubmitAnswers()

        assertEquals(AiRoadmapFormError.AnswerAllQuestions, viewModel.uiState.value.formError)
        assertEquals(listOf(AiRoadmapEvent.ShowError(AiRoadmapFormError.AnswerAllQuestions)), events)
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
    fun onNextQuestion_requiresTextWhenCustomOptionIsSelected_emitsShowErrorEvent() = runTest {
        val viewModel = preparedViewModel()
        val events = mutableListOf<AiRoadmapEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        viewModel.onCustomAnswerChange("level", "")
        viewModel.onNextQuestion()

        assertEquals(AiRoadmapFormError.CustomAnswerRequired, viewModel.uiState.value.formError)
        assertEquals(listOf(AiRoadmapEvent.ShowError(AiRoadmapFormError.CustomAnswerRequired)), events)
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
    fun onSubmitAnswers_requestsLoginAndKeepsQuestionnaire_whenGuestGenerates() = runTest {
        val repository = FakeAiRoadmapRepository()
        val protectedActionGate = FakeProtectedActionGate.guest()
        val viewModel = preparedViewModel(
            repository = repository,
            protectedActionGate = protectedActionGate
        )

        answerAllQuestions(viewModel)
        viewModel.onSubmitAnswers()

        assertEquals(PendingProtectedAction.GenerateAiRoadmap, protectedActionGate.pendingAction.value)
        assertEquals(AiRoadmapStep.Questions, viewModel.uiState.value.step)
        assertEquals(7, viewModel.uiState.value.questions.size)
        assertTrue(!repository.wasGenerationStarted)
    }

    @Test
    fun pendingGenerateRoadmap_resumesGenerationAfterLogin() = runTest {
        val repository = FakeAiRoadmapRepository()
        val protectedActionGate = FakeProtectedActionGate.guest()
        val viewModel = preparedViewModel(
            repository = repository,
            protectedActionGate = protectedActionGate
        )

        answerAllQuestions(viewModel)
        viewModel.onSubmitAnswers()
        protectedActionGate.authenticate()

        assertTrue(repository.wasGenerationStarted)
        assertEquals(null, protectedActionGate.pendingAction.value)
    }

    @Test
    fun generationSuccess_movesScreenToLibraryAndAddsGeneratedRoadmap() = runTest {
        val repository = FakeAiRoadmapRepository()
        val viewModel = AiRoadmapViewModel(
            repository = repository,
            protectedActionGate = FakeProtectedActionGate.authenticated(),
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

    @Test
    fun generationFailed_emitsShowErrorEvent() = runTest {
        val repository = FakeAiRoadmapRepository()
        val viewModel = AiRoadmapViewModel(
            repository = repository,
            protectedActionGate = FakeProtectedActionGate.authenticated(),
            currentTimeMillis = { now }
        )
        val events = mutableListOf<AiRoadmapEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event -> events.add(event) }
        }

        repository.status.value = AiRoadmapGenerationStatus(
            phase = AiRoadmapGenerationPhase.Failed
        )

        assertEquals(listOf(AiRoadmapEvent.ShowError(AiRoadmapFormError.GenerationFailed)), events)
    }

    private fun preparedViewModel(
        repository: FakeAiRoadmapRepository = FakeAiRoadmapRepository(),
        protectedActionGate: FakeProtectedActionGate = FakeProtectedActionGate.authenticated()
    ): AiRoadmapViewModel {
        return AiRoadmapViewModel(
            repository = repository,
            protectedActionGate = protectedActionGate,
            currentTimeMillis = { now }
        ).apply {
            onTopicChange("Android Developer")
            onDeadlineSelected(futureDeadline)
            onSubmitSetup()
        }
    }

    private fun answerAllQuestions(viewModel: AiRoadmapViewModel) {
        viewModel.onOptionSelected("level", "level-1")
        (2..6).forEach { index ->
            viewModel.onOptionSelected("question-$index", "question-$index-option-1")
        }
        viewModel.onCustomAnswerChange("question-7", "I can ship small Android apps")
    }
}

private class FakeProtectedActionGate(
    initialAuthState: AuthState
) : ProtectedActionGate {
    override val authState = MutableStateFlow(initialAuthState)
    override val pendingAction = MutableStateFlow<PendingProtectedAction?>(null)

    override suspend fun runOrRequestAuth(
        action: PendingProtectedAction,
        onAuthenticated: suspend () -> Unit
    ): Boolean {
        return if (authState.value is AuthState.Authenticated) {
            onAuthenticated()
            true
        } else {
            pendingAction.value = action
            false
        }
    }

    override fun consumePendingAction(action: PendingProtectedAction): Boolean {
        return if (pendingAction.value == action) {
            pendingAction.value = null
            true
        } else {
            false
        }
    }

    override fun clearPendingAction(action: PendingProtectedAction) {
        if (pendingAction.value == action) {
            pendingAction.value = null
        }
    }

    fun authenticate() {
        authState.value = AuthState.Authenticated(testUser)
    }

    companion object {
        fun authenticated(): FakeProtectedActionGate {
            return FakeProtectedActionGate(AuthState.Authenticated(testUser))
        }

        fun guest(): FakeProtectedActionGate {
            return FakeProtectedActionGate(AuthState.Unauthenticated)
        }

        private val testUser = User(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
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
