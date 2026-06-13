package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.auth.PendingProtectedAction
import com.rmap.mobile.core.auth.ProtectedActionGate
import com.rmap.mobile.core.datarefresh.DynamicDataChange
import com.rmap.mobile.core.datarefresh.DynamicDataRefreshCoordinator
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import com.rmap.mobile.features.auth.domain.model.AuthState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AiRoadmapViewModel(
    private val repository: AiRoadmapRepository = RMapAppGraph.aiRoadmapRepository,
    private val protectedActionGate: ProtectedActionGate = RMapAppGraph.authGuard,
    private val dynamicDataRefreshCoordinator: DynamicDataRefreshCoordinator? = null,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiRoadmapUiState())
    val uiState: StateFlow<AiRoadmapUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AiRoadmapEvent>()
    val events: SharedFlow<AiRoadmapEvent> = _events.asSharedFlow()

    private var lastLoadedGeneratedRoadmapId: String? = null
    private val activeDynamicDataRefreshCoordinator: DynamicDataRefreshCoordinator?
        get() = dynamicDataRefreshCoordinator ?: runCatching {
            RMapAppGraph.dynamicDataRefreshCoordinator
        }.getOrNull()

    init {
        loadGeneratedRoadmaps()

        viewModelScope.launch {
            repository.generationStatus.collect { status ->
                if (status.phase == AiRoadmapGenerationPhase.Failed) {
                    _events.emit(AiRoadmapEvent.ShowError(AiRoadmapFormError.GenerationFailed))
                }
                _uiState.update { state ->
                    val nextStep = when (status.phase) {
                        AiRoadmapGenerationPhase.Queued,
                        AiRoadmapGenerationPhase.Running -> AiRoadmapStep.Generating
                        AiRoadmapGenerationPhase.Succeeded -> {
                            // If we were already in Generating, stay there to show the Success UI
                            // Otherwise (e.g. app reopened), go to Library
                            if (state.step == AiRoadmapStep.Generating) {
                                AiRoadmapStep.Generating
                            } else {
                                AiRoadmapStep.Library
                            }
                        }
                        AiRoadmapGenerationPhase.Failed,
                        AiRoadmapGenerationPhase.Cancelled -> {
                            if (state.questions.isNotEmpty()) AiRoadmapStep.Questions else AiRoadmapStep.Setup
                        }
                        AiRoadmapGenerationPhase.Idle -> state.step
                    }

                    state.copy(
                        step = nextStep,
                        generationStatus = status,
                        formError = if (status.phase == AiRoadmapGenerationPhase.Failed) {
                            AiRoadmapFormError.GenerationFailed
                        } else {
                            state.formError
                        }
                    )
                }

                val generatedRoadmapId = status.generatedRoadmapId
                if (
                    status.phase == AiRoadmapGenerationPhase.Succeeded &&
                    generatedRoadmapId != null &&
                    generatedRoadmapId != lastLoadedGeneratedRoadmapId
                ) {
                    lastLoadedGeneratedRoadmapId = generatedRoadmapId
                    notifyDynamicDataChanged(DynamicDataChange.AiRoadmapGenerated(generatedRoadmapId))
                    loadGeneratedRoadmaps()
                }
            }
        }

        viewModelScope.launch {
            combine(
                protectedActionGate.authState,
                protectedActionGate.pendingAction
            ) { authState, pendingAction -> authState to pendingAction }
                .collect { (authState, pendingAction) ->
                    if (
                        authState is AuthState.Authenticated &&
                        pendingAction == PendingProtectedAction.GenerateAiRoadmap &&
                        protectedActionGate.consumePendingAction(PendingProtectedAction.GenerateAiRoadmap)
                    ) {
                        executeGenerateRoadmap()
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                visibleGeneratedRoadmapCount = AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE
            )
        }
    }

    fun onSeeMoreGeneratedRoadmaps() {
        _uiState.update { state ->
            state.copy(
                visibleGeneratedRoadmapCount = (state.visibleGeneratedRoadmapCount +
                    AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE)
                    .coerceAtMost(state.totalGeneratedRoadmapCount)
            )
        }
    }

    fun onSeeAllGeneratedRoadmaps() {
        _uiState.update { state ->
            state.copy(visibleGeneratedRoadmapCount = state.totalGeneratedRoadmapCount)
        }
    }

    fun onSeeLessGeneratedRoadmaps() {
        _uiState.update {
            it.copy(visibleGeneratedRoadmapCount = AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE)
        }
    }

    fun onCreateRoadmapClick() {
        _uiState.update {
            it.copy(
                step = AiRoadmapStep.Setup,
                topic = "",
                roleCategory = null,
                deadlineEpochMillis = null,
                dailyStudyHours = AiRoadmapUiState.DEFAULT_DAILY_STUDY_HOURS,
                questions = emptyList(),
                currentQuestionIndex = 0,
                formError = null
            )
        }
    }

    fun onBackToLibrary() {
        protectedActionGate.clearPendingAction(PendingProtectedAction.GenerateAiRoadmap)
        _uiState.update {
            it.copy(
                step = AiRoadmapStep.Library,
                formError = null
            )
        }
    }

    fun onTopicChange(topic: String) {
        _uiState.update {
            it.copy(
                topic = topic,
                formError = null
            )
        }
    }

    fun onDeadlineSelected(deadlineEpochMillis: Long) {
        _uiState.update {
            it.copy(
                deadlineEpochMillis = deadlineEpochMillis,
                formError = null
            )
        }
    }

    fun onDailyStudyHoursChange(hours: Float) {
        val roundedHours = (hours * HOURS_STEP_MULTIPLIER).roundToInt() / HOURS_STEP_MULTIPLIER
        _uiState.update {
            it.copy(
                dailyStudyHours = roundedHours.coerceIn(MIN_DAILY_STUDY_HOURS, MAX_DAILY_STUDY_HOURS),
                formError = null
            )
        }
    }

    fun onSubmitSetup() {
        val draft = buildDraftOrSetError() ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingQuestions = true,
                    formError = null
                )
            }

            repository.getPersonalizedQuestions(draft)
                .onSuccess { quizResult ->
                    _uiState.update {
                        it.copy(
                            step = AiRoadmapStep.Questions,
                            roleCategory = quizResult.roleCategory,
                            questions = quizResult.questions.map { question -> question.toUiModel() },
                            currentQuestionIndex = 0,
                            isLoadingQuestions = false,
                            formError = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isLoadingQuestions = false,
                            formError = AiRoadmapFormError.QuestionsLoadFailed
                        )
                    }
                    viewModelScope.launch {
                        _events.emit(AiRoadmapEvent.ShowError(AiRoadmapFormError.QuestionsLoadFailed))
                    }
                }
        }
    }

    fun onOptionSelected(questionId: String, optionId: String) {
        _uiState.update { state ->
            val questionIndex = state.questions.indexOfFirst { it.id == questionId }
            val question = state.questions.getOrNull(questionIndex)

            if (questionIndex == -1 || question == null || question.options.none { it.id == optionId }) {
                state
            } else {
                val questions = state.questions.map {
                    if (it.id == questionId) {
                        it.copy(
                            selectedOptionId = optionId,
                            isCustomAnswerSelected = false,
                            customAnswer = ""
                        )
                    } else {
                        it
                    }
                }

                state.copy(
                    questions = questions,
                    formError = null
                )
            }
        }
    }

    fun onCustomAnswerChange(questionId: String, answer: String) {
        updateQuestion(questionId) { question ->
            question.copy(
                selectedOptionId = null,
                isCustomAnswerSelected = true,
                customAnswer = answer
            )
        }
    }

    fun onPreviousQuestion() {
        _uiState.update { state ->
            state.copy(currentQuestionIndex = (state.currentQuestionIndex - 1).coerceAtLeast(0))
        }
    }

    fun onNextQuestion() {
        var errorToEmit: AiRoadmapFormError? = null
        _uiState.update { state ->
            val question = state.currentQuestion

            when {
                question == null -> state
                !question.hasSelection -> {
                    errorToEmit = AiRoadmapFormError.AnswerAllQuestions
                    state.copy(formError = AiRoadmapFormError.AnswerAllQuestions)
                }
                question.isCustomAnswerSelected && question.customAnswer.isBlank() -> {
                    errorToEmit = AiRoadmapFormError.CustomAnswerRequired
                    state.copy(formError = AiRoadmapFormError.CustomAnswerRequired)
                }
                else -> {
                    state.copy(
                        currentQuestionIndex = (state.currentQuestionIndex + 1).coerceAtMost(state.questions.lastIndex),
                        formError = null
                    )
                }
            }
        }
        errorToEmit?.let { error ->
            viewModelScope.launch {
                _events.emit(AiRoadmapEvent.ShowError(error))
            }
        }
    }

    fun onSubmitAnswers() {
        if (!validateAnswersForGeneration()) return

        viewModelScope.launch {
            protectedActionGate.runOrRequestAuth(PendingProtectedAction.GenerateAiRoadmap) {
                executeGenerateRoadmap()
            }
        }
    }

    fun onCancelGeneration() {
        protectedActionGate.clearPendingAction(PendingProtectedAction.GenerateAiRoadmap)
        repository.cancelGeneration()
        _uiState.update {
            it.copy(
                step = AiRoadmapStep.Setup,
                topic = "",
                roleCategory = null,
                deadlineEpochMillis = null,
                dailyStudyHours = AiRoadmapUiState.DEFAULT_DAILY_STUDY_HOURS,
                questions = emptyList(),
                currentQuestionIndex = 0,
                formError = null
            )
        }
    }

    private fun validateAnswersForGeneration(): Boolean {
        val state = _uiState.value
        buildDraftOrSetError(requireRoleCategory = true) ?: return false

        val hasBlankCustomAnswer = state.questions.any {
            it.isCustomAnswerSelected && it.customAnswer.isBlank()
        }
        if (hasBlankCustomAnswer) {
            _uiState.update { it.copy(formError = AiRoadmapFormError.CustomAnswerRequired) }
            viewModelScope.launch {
                _events.emit(AiRoadmapEvent.ShowError(AiRoadmapFormError.CustomAnswerRequired))
            }
            return false
        }

        if (!state.isReadyToGenerate) {
            _uiState.update { it.copy(formError = AiRoadmapFormError.AnswerAllQuestions) }
            viewModelScope.launch {
                _events.emit(AiRoadmapEvent.ShowError(AiRoadmapFormError.AnswerAllQuestions))
            }
            return false
        }

        return true
    }

    private suspend fun executeGenerateRoadmap() {
        if (_uiState.value.step == AiRoadmapStep.Generating) return
        if (!validateAnswersForGeneration()) return

        val state = _uiState.value
        val draft = buildDraftOrSetError(requireRoleCategory = true) ?: return

        repository.prepareGeneration(
            draft = draft,
            answers = state.questions.map { question ->
                AiRoadmapAnswer(
                    question = question.prompt,
                    answer = question.answerText
                )
            }
        ).onSuccess { request ->
            _uiState.update {
                it.copy(
                    step = AiRoadmapStep.Generating,
                    formError = null
                )
            }
            repository.startGeneration(request)
        }.onFailure {
            _uiState.update { it.copy(formError = AiRoadmapFormError.AnswerAllQuestions) }
            _events.emit(AiRoadmapEvent.ShowError(AiRoadmapFormError.AnswerAllQuestions))
        }
    }

    fun onViewGeneratedRoadmap() {
        val roadmapId = _uiState.value.generationStatus.generatedRoadmapId ?: return
        viewModelScope.launch {
            _events.emit(AiRoadmapEvent.NavigateToRoadmapDetail(roadmapId))
            // Reset step to library after navigating away so that if they come back, it's clean
            _uiState.update { it.copy(step = AiRoadmapStep.Library) }
        }
    }

    fun onRoadmapSelected(roadmapId: String) {
        viewModelScope.launch {
            _events.emit(AiRoadmapEvent.NavigateToRoadmapDetail(roadmapId))
        }
    }

    private fun loadGeneratedRoadmaps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGeneratedRoadmaps = true) }
            repository.getGeneratedRoadmaps()
                .onSuccess { roadmaps ->
                    _uiState.update {
                        it.copy(
                            generatedRoadmaps = roadmaps.map { roadmap -> roadmap.toUiModel() },
                            visibleGeneratedRoadmapCount = AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE
                                .coerceAtLeast(it.visibleGeneratedRoadmapCount),
                            isLoadingGeneratedRoadmaps = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingGeneratedRoadmaps = false) }
                }
        }
    }

    private fun notifyDynamicDataChanged(change: DynamicDataChange) {
        val coordinator = activeDynamicDataRefreshCoordinator ?: return
        viewModelScope.launch {
            runCatching { coordinator.notifyChange(change) }
        }
    }

    private fun buildDraftOrSetError(requireRoleCategory: Boolean = false): AiRoadmapDraft? {
        val state = _uiState.value
        val trimmedTopic = state.topic.trim()
        val deadline = state.deadlineEpochMillis

        val error = when {
            trimmedTopic.length < AiRoadmapUiState.MIN_TOPIC_LENGTH -> AiRoadmapFormError.TopicRequired
            deadline == null -> AiRoadmapFormError.DeadlineRequired
            deadline <= currentTimeMillis() -> AiRoadmapFormError.DeadlineInPast
            requireRoleCategory && state.roleCategory.isNullOrBlank() -> AiRoadmapFormError.QuestionsLoadFailed
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(formError = error) }
            viewModelScope.launch {
                _events.emit(AiRoadmapEvent.ShowError(error))
            }
            return null
        }

        val validDeadline = deadline ?: return null

        return AiRoadmapDraft(
            topic = trimmedTopic,
            deadlineEpochMillis = validDeadline,
            dailyStudyHours = state.dailyStudyHours,
            roleCategory = state.roleCategory
        )
    }

    private fun updateQuestion(
        questionId: String,
        transform: (AiRoadmapQuestionUiModel) -> AiRoadmapQuestionUiModel
    ) {
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { question ->
                    if (question.id == questionId) transform(question) else question
                },
                formError = null
            )
        }
    }

    private fun AiRoadmapQuestion.toUiModel(): AiRoadmapQuestionUiModel {
        return AiRoadmapQuestionUiModel(
            id = id,
            skillName = skillName,
            prompt = prompt,
            options = options.mapIndexed { index, option ->
                AiRoadmapQuestionOptionUiModel(
                    id = option.id,
                    numberText = (index + 1).toString(),
                    label = option.label
                )
            }
        )
    }

    private fun AiGeneratedRoadmap.toUiModel(): AiGeneratedRoadmapUiModel {
        return AiGeneratedRoadmapUiModel(
            id = id,
            title = title,
            lessonsCount = lessonsCount,
            durationWeeks = durationWeeks,
            createdDaysAgo = generatedAtEpochMillis?.toCreatedDaysAgo() ?: 0
        )
    }

    private fun Long.toCreatedDaysAgo(): Int {
        return ((currentTimeMillis() - this) / MILLIS_PER_DAY).coerceAtLeast(0L).toInt()
    }

    companion object {
        const val MIN_DAILY_STUDY_HOURS = 0.5f
        const val MAX_DAILY_STUDY_HOURS = 12f
        private const val HOURS_STEP_MULTIPLIER = 2f
        private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
