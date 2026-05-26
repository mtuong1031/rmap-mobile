package com.rmap.mobile.features.airoadmap.domain.model

data class AiRoadmapDraft(
    val topic: String,
    val deadlineEpochMillis: Long,
    val dailyStudyHours: Float
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
    val questionId: String,
    val selectedOptionId: String?,
    val customAnswer: String
) {
    val hasAnswer: Boolean
        get() = selectedOptionId != null || customAnswer.isNotBlank()
}

data class AiRoadmapGenerationRequest(
    val draft: AiRoadmapDraft,
    val answers: List<AiRoadmapAnswer>
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
