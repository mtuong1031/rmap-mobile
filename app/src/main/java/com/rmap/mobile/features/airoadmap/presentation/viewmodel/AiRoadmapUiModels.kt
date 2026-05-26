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
    val hasAnswer: Boolean
        get() = selectedOptionId != null || customAnswer.isNotBlank()
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
    val generatedRoadmaps: List<AiGeneratedRoadmapUiModel> = mockGeneratedRoadmaps,
    val visibleGeneratedRoadmapCount: Int = GENERATED_ROADMAP_PAGE_SIZE,
    val topic: String = "",
    val deadlineEpochMillis: Long? = null,
    val dailyStudyHours: Float = DEFAULT_DAILY_STUDY_HOURS,
    val questions: List<AiRoadmapQuestionUiModel> = emptyList(),
    val currentQuestionIndex: Int = 0,
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
        const val MIN_TOPIC_LENGTH = 2
        const val DEFAULT_DAILY_STUDY_HOURS = 2f
        const val GENERATED_ROADMAP_PAGE_SIZE = 5
    }
}

private val mockGeneratedRoadmaps = listOf(
    AiGeneratedRoadmapUiModel(
        id = "ai-android-compose",
        title = "Android Compose Specialist",
        lessonsCount = 28,
        durationWeeks = 8,
        createdDaysAgo = 0
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-react-production",
        title = "React Production Roadmap",
        lessonsCount = 24,
        durationWeeks = 6,
        createdDaysAgo = 1
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-backend-api",
        title = "Backend API Engineer",
        lessonsCount = 32,
        durationWeeks = 10,
        createdDaysAgo = 2
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-devops-foundations",
        title = "DevOps Foundations",
        lessonsCount = 22,
        durationWeeks = 7,
        createdDaysAgo = 3
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-uiux-mobile",
        title = "Mobile UI/UX Mastery",
        lessonsCount = 18,
        durationWeeks = 5,
        createdDaysAgo = 5
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-data-analytics",
        title = "Data Analytics Starter",
        lessonsCount = 26,
        durationWeeks = 9,
        createdDaysAgo = 6
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-flutter-cross-platform",
        title = "Flutter Cross-platform",
        lessonsCount = 21,
        durationWeeks = 6,
        createdDaysAgo = 8
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-system-design",
        title = "System Design Interview",
        lessonsCount = 30,
        durationWeeks = 8,
        createdDaysAgo = 10
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-ios-swift",
        title = "iOS Swift Starter",
        lessonsCount = 20,
        durationWeeks = 6,
        createdDaysAgo = 12
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-fullstack-product",
        title = "Full Stack Product Builder",
        lessonsCount = 36,
        durationWeeks = 12,
        createdDaysAgo = 14
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-ai-engineering",
        title = "AI Engineering Path",
        lessonsCount = 34,
        durationWeeks = 11,
        createdDaysAgo = 16
    ),
    AiGeneratedRoadmapUiModel(
        id = "ai-security-basics",
        title = "Security Basics",
        lessonsCount = 19,
        durationWeeks = 5,
        createdDaysAgo = 20
    )
)
