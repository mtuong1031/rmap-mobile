package com.rmap.mobile.features.airoadmap.domain.model

data class AiRoadmapDraft(
    val topic: String,
    val deadlineEpochMillis: Long,
    val dailyStudyHours: Float,
    val roleCategory: String? = null
)

data class AiRoadmapQuizResult(
    val roleCategory: String,
    val questions: List<AiRoadmapQuestion>
)

data class AiRoadmapQuestion(
    val id: String,
    val skillName: String,
    val prompt: String,
    val options: List<AiRoadmapQuestionOption>
)

data class AiRoadmapQuestionOption(
    val id: String,
    val label: String
)

data class AiRoadmapAnswer(
    val question: String,
    val answer: String
) {
    val hasAnswer: Boolean
        get() = question.isNotBlank() && answer.isNotBlank()
}

data class AiRoadmapGenerationRequest(
    val draft: AiRoadmapDraft,
    val answers: List<AiRoadmapAnswer>
)

data class AiGeneratedRoadmap(
    val id: String,
    val title: String,
    val lessonsCount: Int,
    val durationWeeks: Int,
    val generatedAtEpochMillis: Long?
)

enum class AiRoadmapGenerationPhase {
    Idle,
    Queued,
    Running,
    Succeeded,
    Failed,
    Cancelled
}

data class AiRoadmapGenerationStatus(
    val phase: AiRoadmapGenerationPhase = AiRoadmapGenerationPhase.Idle,
    val progressPercent: Int = 0,
    val stageLabel: String = "",
    val generatedRoadmapId: String? = null,
    val errorMessage: String? = null
) {
    val isActive: Boolean
        get() = phase == AiRoadmapGenerationPhase.Queued || phase == AiRoadmapGenerationPhase.Running
}
