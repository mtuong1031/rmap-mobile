package com.rmap.mobile.features.airoadmap.presentation.viewmodel

import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus

enum class AiRoadmapStep {
    Library,
    Setup,
    Questions,
    Generating
}

enum class AiRoadmapFormError {
    TopicRequired,
    DeadlineRequired,
    DeadlineInPast,
    QuestionsLoadFailed,
    AnswerAllQuestions,
    GenerationFailed
}

data class AiRoadmapQuestionOptionUiModel(
    val id: String,
    val numberText: String,
    val label: String
)

data class AiRoadmapQuestionUiModel(
    val id: String,
    val skillName: String,
    val prompt: String,
    val options: List<AiRoadmapQuestionOptionUiModel>,
    val selectedOptionId: String? = null,
    val customAnswer: String = ""
) {
    val requiresCustomAnswer: Boolean
        get() = options.isEmpty()

    val hasAnswer: Boolean
        get() = if (requiresCustomAnswer) {
            customAnswer.isNotBlank()
        } else {
            selectedOptionId != null
        }

    val answerText: String
        get() = if (requiresCustomAnswer) {
            customAnswer.trim()
        } else {
            options.firstOrNull { it.id == selectedOptionId }?.label.orEmpty()
        }
}

data class AiGeneratedRoadmapUiModel(
    val id: String,
    val title: String,
    val lessonsCount: Int,
    val durationWeeks: Int,
    val createdDaysAgo: Int
)

data class AiRoadmapUiState(
    val step: AiRoadmapStep = AiRoadmapStep.Library,
    val searchQuery: String = "",
    val generatedRoadmaps: List<AiGeneratedRoadmapUiModel> = emptyList(),
    val visibleGeneratedRoadmapCount: Int = GENERATED_ROADMAP_PAGE_SIZE,
    val topic: String = "",
    val roleCategory: String? = null,
    val deadlineEpochMillis: Long? = null,
    val dailyStudyHours: Float = DEFAULT_DAILY_STUDY_HOURS,
    val questions: List<AiRoadmapQuestionUiModel> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val isLoadingGeneratedRoadmaps: Boolean = false,
    val isLoadingQuestions: Boolean = false,
    val formError: AiRoadmapFormError? = null,
    val generationStatus: AiRoadmapGenerationStatus = AiRoadmapGenerationStatus()
) {
    val filteredGeneratedRoadmaps: List<AiGeneratedRoadmapUiModel>
        get() {
            val normalizedQuery = searchQuery.trim()
            return if (normalizedQuery.isBlank()) {
                generatedRoadmaps
            } else {
                generatedRoadmaps.filter { roadmap ->
                    roadmap.title.contains(normalizedQuery, ignoreCase = true)
                }
            }
        }

    val visibleGeneratedRoadmaps: List<AiGeneratedRoadmapUiModel>
        get() = filteredGeneratedRoadmaps.take(visibleGeneratedRoadmapCount)

    val totalGeneratedRoadmapCount: Int
        get() = filteredGeneratedRoadmaps.size

    val hasAnyGeneratedRoadmaps: Boolean
        get() = generatedRoadmaps.isNotEmpty()

    val isSearchingGeneratedRoadmaps: Boolean
        get() = searchQuery.isNotBlank()

    val hasMoreGeneratedRoadmaps: Boolean
        get() = visibleGeneratedRoadmaps.size < totalGeneratedRoadmapCount

    val canToggleAllGeneratedRoadmaps: Boolean
        get() = totalGeneratedRoadmapCount > GENERATED_ROADMAP_PAGE_SIZE

    val isShowingAllGeneratedRoadmaps: Boolean
        get() = canToggleAllGeneratedRoadmaps && !hasMoreGeneratedRoadmaps

    val isSetupSubmitEnabled: Boolean
        get() = topic.trim().length >= MIN_TOPIC_LENGTH &&
            deadlineEpochMillis != null &&
            !isLoadingQuestions

    val currentQuestion: AiRoadmapQuestionUiModel?
        get() = questions.getOrNull(currentQuestionIndex)

    val answeredQuestionCount: Int
        get() = questions.count { it.hasAnswer }

    val isCurrentQuestionAnswered: Boolean
        get() = currentQuestion?.hasAnswer == true

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.lastIndex

    val isReadyToGenerate: Boolean
        get() = questions.isNotEmpty() && answeredQuestionCount == questions.size

    companion object {
        const val MIN_TOPIC_LENGTH = 10
        const val DEFAULT_DAILY_STUDY_HOURS = 2f
        const val GENERATED_ROADMAP_PAGE_SIZE = 5
    }
}
