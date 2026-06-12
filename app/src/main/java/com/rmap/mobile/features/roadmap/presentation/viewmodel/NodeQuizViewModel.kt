package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationManager
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.R
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NodeQuizViewModel(
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val notificationManager: AppNotificationManager = RMapAppGraph.appNotificationManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(NodeQuizUiState())
    val uiState: StateFlow<NodeQuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(
        roadmapId: String,
        nodeId: String
    ) {
        if (
            _uiState.value.roadmapId == roadmapId &&
            _uiState.value.nodeId == nodeId &&
            !_uiState.value.isLoading
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    roadmapId = roadmapId,
                    nodeId = nodeId,
                    isLoading = true,
                    errorMessage = null,
                    result = null,
                    currentQuestionIndex = 0
                )
            }

            repository.getNodeQuiz(roadmapId, nodeId)
                .onSuccess { quiz ->
                    _uiState.update { current ->
                        quiz.toNodeQuizUiState(
                            roadmapId = current.roadmapId,
                            nodeId = current.nodeId
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                    notificationManager.enqueue(
                        AppNotification(
                            titleResId = R.string.snackbar_title_error,
                            message = error.message ?: "Unable to load quiz",
                            variant = AppNotificationVariant.Error
                        )
                    )
                }
        }
    }

    fun onOptionSelected(
        questionId: String,
        optionKey: String
    ) {
        _uiState.update { state ->
            val updatedAnswers = state.selectedAnswers.toMutableMap()
            updatedAnswers[questionId] = optionKey
            state.copy(
                selectedAnswers = updatedAnswers,
                errorMessage = null
            )
        }
    }

    fun onPreviousQuestion() {
        _uiState.update { state ->
            state.copy(
                currentQuestionIndex = (state.currentQuestionIndex - 1).coerceAtLeast(0),
                errorMessage = null
            )
        }
    }

    fun onNextQuestion() {
        _uiState.update { state ->
            if (!state.isCurrentQuestionAnswered) {
                state.copy(errorMessage = QUIZ_ANSWER_CURRENT_QUESTION_ERROR)
            } else {
                state.copy(
                    currentQuestionIndex = (state.currentQuestionIndex + 1).coerceAtMost(state.questions.size - 1),
                    errorMessage = null
                )
            }
        }
    }

    fun onSubmitClick() {
        val state = _uiState.value
        if (!state.isCurrentQuestionAnswered) {
            _uiState.update { it.copy(errorMessage = QUIZ_ANSWER_CURRENT_QUESTION_ERROR) }
            return
        }
        if (!state.canSubmit) {
            _uiState.update { it.copy(errorMessage = QUIZ_ANSWER_ALL_QUESTIONS_ERROR) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val answers = state.questions.map { question ->
                NodeQuizAnswer(
                    questionId = question.id,
                    selectedOption = state.selectedAnswers[question.id].orEmpty()
                )
            }

            repository.submitNodeQuiz(
                roadmapId = state.roadmapId,
                nodeId = state.nodeId,
                answers = answers
            ).onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        result = result.toNodeQuizResultUiModel()
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message
                    )
                }
                notificationManager.enqueue(
                    AppNotification(
                        titleResId = R.string.snackbar_title_error,
                        message = error.message ?: "Unable to submit quiz",
                        variant = AppNotificationVariant.Error
                    )
                )
            }
        }
    }
}

data class NodeQuizUiState(
    val roadmapId: String = "",
    val nodeId: String = "",
    val questions: List<NodeQuizQuestionUiModel> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val result: NodeQuizResultUiModel? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: NodeQuizQuestionUiModel?
        get() = questions.getOrNull(currentQuestionIndex)

    val answeredQuestionCount: Int
        get() = selectedAnswers.size

    val isCurrentQuestionAnswered: Boolean
        get() = currentQuestion?.let { selectedAnswers.containsKey(it.id) } ?: false

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = questions.isNotEmpty() && currentQuestionIndex == questions.size - 1

    val progressFraction: Float
        get() = if (questions.isEmpty()) 0f else (currentQuestionIndex + 1).toFloat() / questions.size

    val canSubmit: Boolean
        get() = questions.isNotEmpty() && selectedAnswers.size == questions.size
}

data class NodeQuizQuestionUiModel(
    val id: String,
    val text: String,
    val options: List<NodeQuizOptionUiModel>
)

data class NodeQuizOptionUiModel(
    val key: String,
    val text: String
)

data class NodeQuizResultUiModel(
    val scorePercent: Int,
    val passed: Boolean,
    val correctCount: Int,
    val totalQuestions: Int,
    val suggestion: String?,
    val questionResults: List<NodeQuizQuestionResultUiModel>
)

data class NodeQuizQuestionResultUiModel(
    val questionId: String,
    val selectedOption: String,
    val correctOption: String,
    val isCorrect: Boolean
)

private fun NodeQuiz.toNodeQuizUiState(
    roadmapId: String,
    nodeId: String
): NodeQuizUiState {
    return NodeQuizUiState(
        roadmapId = roadmapId,
        nodeId = nodeId,
        questions = questions.map { it.toNodeQuizQuestionUiModel() },
        isLoading = false,
        errorMessage = null
    )
}

private fun com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion.toNodeQuizQuestionUiModel(): NodeQuizQuestionUiModel {
    return NodeQuizQuestionUiModel(
        id = id,
        text = text,
        options = options.map { it.toNodeQuizOptionUiModel() }
    )
}

private fun com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption.toNodeQuizOptionUiModel(): NodeQuizOptionUiModel {
    return NodeQuizOptionUiModel(
        key = key,
        text = text
    )
}

private fun com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult.toNodeQuizResultUiModel(): NodeQuizResultUiModel {
    return NodeQuizResultUiModel(
        scorePercent = scorePercent,
        passed = passed,
        correctCount = correctCount,
        totalQuestions = totalQuestions,
        suggestion = suggestion,
        questionResults = questionResults.map { result ->
            NodeQuizQuestionResultUiModel(
                questionId = result.questionId,
                selectedOption = result.selectedOption,
                correctOption = result.correctOption,
                isCorrect = result.isCorrect
            )
        }
    )
}

const val QUIZ_ANSWER_CURRENT_QUESTION_ERROR = "Please select an option before continuing"
const val QUIZ_ANSWER_ALL_QUESTIONS_ERROR = "Please answer all questions before submitting"
