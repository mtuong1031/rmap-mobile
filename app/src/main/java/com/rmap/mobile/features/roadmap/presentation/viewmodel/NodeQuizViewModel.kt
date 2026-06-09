package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NodeQuizViewModel(
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository
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
                }
        }
    }

    fun onOptionSelected(
        questionId: String,
        optionKey: String
    ) {
        _uiState.update { state ->
            val questionIndex = state.questions.indexOfFirst { it.id == questionId }
            val question = state.questions.getOrNull(questionIndex)

            if (
                state.result != null ||
                state.isSubmitting ||
                questionIndex == -1 ||
                question == null ||
                question.options.none { option -> option.key == optionKey }
            ) {
                state
            } else {
                state.copy(
                    selectedAnswers = state.selectedAnswers + (questionId to optionKey),
                    errorMessage = null
                )
            }
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
                    currentQuestionIndex = (state.currentQuestionIndex + 1).coerceAtMost(state.questions.lastIndex),
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

            val answers = state.questions.mapNotNull { question ->
                state.selectedAnswers[question.id]?.let { selectedOption ->
                    NodeQuizAnswer(
                        questionId = question.id,
                        selectedOption = selectedOption
                    )
                }
            }

            repository.submitNodeQuiz(
                roadmapId = state.roadmapId,
                nodeId = state.nodeId,
                answers = answers
            )
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            result = result.toNodeQuizResultUiModel(),
                            currentQuestionIndex = 0
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message
                        )
                    }
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
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: NodeQuizQuestionUiModel?
        get() = questions.getOrNull(currentQuestionIndex)

    val answeredQuestionCount: Int
        get() = questions.count { question -> selectedAnswers.containsKey(question.id) }

    val isCurrentQuestionAnswered: Boolean
        get() = currentQuestion?.let { question -> selectedAnswers.containsKey(question.id) } == true

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.lastIndex

    val progressFraction: Float
        get() = if (questions.isEmpty()) {
            0f
        } else {
            ((currentQuestionIndex + 1).toFloat() / questions.size.toFloat()).coerceIn(0f, 1f)
        }

    val canSubmit: Boolean
        get() = questions.isNotEmpty() &&
            selectedAnswers.size == questions.size &&
            !isSubmitting &&
            result == null
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
        questions = questions.map { question -> question.toNodeQuizQuestionUiModel() },
        currentQuestionIndex = 0,
        selectedAnswers = emptyMap(),
        result = null,
        isLoading = false,
        isSubmitting = false,
        errorMessage = null
    )
}

private fun NodeQuizQuestion.toNodeQuizQuestionUiModel(): NodeQuizQuestionUiModel {
    return NodeQuizQuestionUiModel(
        id = id,
        text = text,
        options = options.map { option -> option.toNodeQuizOptionUiModel() }
    )
}

private fun NodeQuizOption.toNodeQuizOptionUiModel(): NodeQuizOptionUiModel {
    return NodeQuizOptionUiModel(
        key = key,
        text = text
    )
}

private fun NodeQuizSubmissionResult.toNodeQuizResultUiModel(): NodeQuizResultUiModel {
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

private const val QUIZ_ANSWER_CURRENT_QUESTION_ERROR = "Answer this question before continuing."
private const val QUIZ_ANSWER_ALL_QUESTIONS_ERROR = "Answer every question before submitting."
