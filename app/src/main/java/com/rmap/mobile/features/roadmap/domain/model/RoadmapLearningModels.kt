package com.rmap.mobile.features.roadmap.domain.model

data class LearningNodeDetail(
    val roadmapId: String,
    val nodeId: String,
    val title: String,
    val description: String?,
    val skillName: String?,
    val skillDescription: String?,
    val estimatedHours: Int?,
    val status: LearningStatus,
    val requirement: LearningRequirement,
    val resources: List<LearningResource>,
    val prerequisites: List<LearningPrerequisite>
)

data class LearningResource(
    val id: String,
    val title: String,
    val url: String,
    val type: String,
    val isFree: Boolean,
    val isPrimary: Boolean
)

data class LearningPrerequisite(
    val skillId: String,
    val skillName: String
)

data class NodeQuiz(
    val nodeId: String,
    val skillId: String,
    val questions: List<NodeQuizQuestion>
)

data class NodeQuizQuestion(
    val id: String,
    val text: String,
    val options: List<NodeQuizOption>
)

data class NodeQuizOption(
    val key: String,
    val text: String
)

data class NodeQuizAnswer(
    val questionId: String,
    val selectedOption: String
)

data class NodeQuizSubmissionResult(
    val scorePercent: Int,
    val passed: Boolean,
    val correctCount: Int,
    val totalQuestions: Int,
    val suggestion: String?,
    val unlockedNodeIds: List<String>,
    val questionResults: List<NodeQuizQuestionResult>
)

data class NodeQuizQuestionResult(
    val questionId: String,
    val selectedOption: String,
    val correctOption: String,
    val isCorrect: Boolean
)
