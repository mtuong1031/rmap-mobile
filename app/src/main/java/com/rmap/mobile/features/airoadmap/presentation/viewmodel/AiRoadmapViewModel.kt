package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapAnswer
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapDraft
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapQuestion
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AiRoadmapViewModel(
    private val repository: AiRoadmapRepository = RMapAppGraph.aiRoadmapRepository,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiRoadmapUiState())
    val uiState: StateFlow<AiRoadmapUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AiRoadmapEvent>()
    val events: SharedFlow<AiRoadmapEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.generationStatus.collect { status ->
                _uiState.update { state ->
                    val nextStep = when (status.phase) {
                        AiRoadmapGenerationPhase.Queued,
                        AiRoadmapGenerationPhase.Running -> AiRoadmapStep.Generating
                        AiRoadmapGenerationPhase.Succeeded -> AiRoadmapStep.Library
                        AiRoadmapGenerationPhase.Failed,
                        AiRoadmapGenerationPhase.Cancelled -> {
                            if (state.questions.isNotEmpty()) AiRoadmapStep.Questions else AiRoadmapStep.Setup
                        }
                        AiRoadmapGenerationPhase.Idle -> state.step
                    }

                    state.copy(
                        step = nextStep,
                        generationStatus = status,
                        generatedRoadmaps = if (
                            status.phase == AiRoadmapGenerationPhase.Succeeded &&
                            status.generatedRoadmapId != null &&
                            state.generatedRoadmaps.none { it.id == status.generatedRoadmapId }
                        ) {
                            listOf(state.toGeneratedRoadmap(status.generatedRoadmapId)) + state.generatedRoadmaps
                        } else {
                            state.generatedRoadmaps
                        },
                        formError = if (status.phase == AiRoadmapGenerationPhase.Failed) {
                            AiRoadmapFormError.GenerationFailed
                        } else {
                            state.formError
                        }
                    )
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
                deadlineEpochMillis = null,
                dailyStudyHours = AiRoadmapUiState.DEFAULT_DAILY_STUDY_HOURS,
                questions = emptyList(),
                currentQuestionIndex = 0,
                formError = null
            )
        }
    }

    fun onBackToLibrary() {
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
                .onSuccess { questions ->
                    _uiState.update {
                        it.copy(
                            step = AiRoadmapStep.Questions,
                            questions = questions.map { question -> question.toUiModel() },
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
                }
        }
    }

    fun onOptionSelected(questionId: String, optionId: String) {
        updateQuestion(questionId) { question ->
            question.copy(selectedOptionId = optionId)
        }
    }

    fun onCustomAnswerChange(questionId: String, answer: String) {
        updateQuestion(questionId) { question ->
            question.copy(customAnswer = answer)
        }
    }

    fun onPreviousQuestion() {
        _uiState.update { state ->
            state.copy(currentQuestionIndex = (state.currentQuestionIndex - 1).coerceAtLeast(0))
        }
    }

    fun onNextQuestion() {
        _uiState.update { state ->
            if (!state.isCurrentQuestionAnswered) {
                state.copy(formError = AiRoadmapFormError.AnswerAllQuestions)
            } else {
                state.copy(
                    currentQuestionIndex = (state.currentQuestionIndex + 1).coerceAtMost(state.questions.lastIndex),
                    formError = null
                )
            }
        }
    }

    fun onSubmitAnswers() {
        val state = _uiState.value
        val draft = buildDraftOrSetError() ?: return

        if (!state.isReadyToGenerate) {
            _uiState.update { it.copy(formError = AiRoadmapFormError.AnswerAllQuestions) }
            return
        }

        viewModelScope.launch {
            repository.prepareGeneration(
                draft = draft,
                answers = state.questions.map { question ->
                    AiRoadmapAnswer(
                        questionId = question.id,
                        selectedOptionId = question.selectedOptionId,
                        customAnswer = question.customAnswer.trim()
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
            }
        }
    }

    fun onCancelGeneration() {
        repository.cancelGeneration()
        _uiState.update {
            it.copy(
                step = if (it.questions.isNotEmpty()) AiRoadmapStep.Questions else AiRoadmapStep.Setup
            )
        }
    }

    fun onRoadmapSelected(roadmapId: String) {
        viewModelScope.launch {
            _events.emit(AiRoadmapEvent.NavigateToRoadmapDetail(roadmapId))
        }
    }

    private fun buildDraftOrSetError(): AiRoadmapDraft? {
        val state = _uiState.value
        val trimmedTopic = state.topic.trim()
        val deadline = state.deadlineEpochMillis

        val error = when {
            trimmedTopic.length < AiRoadmapUiState.MIN_TOPIC_LENGTH -> AiRoadmapFormError.TopicRequired
            deadline == null -> AiRoadmapFormError.DeadlineRequired
            deadline <= currentTimeMillis() -> AiRoadmapFormError.DeadlineInPast
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(formError = error) }
            return null
        }

        val validDeadline = deadline ?: return null

        return AiRoadmapDraft(
            topic = trimmedTopic,
            deadlineEpochMillis = validDeadline,
            dailyStudyHours = state.dailyStudyHours
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

    private fun AiRoadmapUiState.toGeneratedRoadmap(roadmapId: String): AiGeneratedRoadmapUiModel {
        return AiGeneratedRoadmapUiModel(
            id = roadmapId,
            title = topic.trim(),
            lessonsCount = GENERATED_ROADMAP_LESSON_COUNT,
            durationWeeks = dailyStudyHours.toDurationWeeks(),
            createdDaysAgo = 0
        )
    }

    private fun Float.toDurationWeeks(): Int {
        return when {
            this < 1f -> 16
            this < 2f -> 12
            this < 4f -> 8
            else -> 4
        }
    }

    companion object {
        const val MIN_DAILY_STUDY_HOURS = 0.5f
        const val MAX_DAILY_STUDY_HOURS = 12f
        private const val HOURS_STEP_MULTIPLIER = 2f
        private const val GENERATED_ROADMAP_LESSON_COUNT = 24
    }
}
