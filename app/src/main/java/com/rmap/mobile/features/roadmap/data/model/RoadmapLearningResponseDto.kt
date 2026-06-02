package com.rmap.mobile.features.roadmap.data.model

import com.google.gson.annotations.SerializedName

data class NodeDetailResponseDto(
    @SerializedName("node") val node: RoadmapNodeWithUserProgressDto,
    @SerializedName("skill") val skill: SkillDetailDto?,
    @SerializedName("resources") val resources: List<ResourceDto>?,
    @SerializedName("prerequisites") val prerequisites: List<PrerequisiteDto>,
    @SerializedName("latestSubmission") val latestSubmission: MilestoneSubmissionDto?
)

data class SkillDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName(value = "defaultEstimatedHours", alternate = ["default_estimated_hours"])
    val defaultEstimatedHours: Double?,
    @SerializedName(value = "roleCategory", alternate = ["role_category"])
    val roleCategory: String?
)

data class ResourceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName(value = "resourceType", alternate = ["resource_type"])
    val resourceType: String,
    @SerializedName(value = "isFree", alternate = ["is_free"])
    val isFree: Boolean,
    @SerializedName(value = "isPrimary", alternate = ["is_primary"])
    val isPrimary: Boolean
)

data class PrerequisiteDto(
    @SerializedName(value = "skillId", alternate = ["skill_id"])
    val skillId: String,
    @SerializedName(value = "skillName", alternate = ["skill_name"])
    val skillName: String
)

data class MilestoneSubmissionDto(
    @SerializedName("id") val id: String,
    @SerializedName(value = "repoUrl", alternate = ["repo_url"])
    val repoUrl: String,
    @SerializedName(value = "testCommand", alternate = ["test_command"])
    val testCommand: String,
    @SerializedName("status") val status: String,
    @SerializedName(value = "outputLog", alternate = ["output_log"])
    val outputLog: String?,
    @SerializedName(value = "attemptNumber", alternate = ["attempt_number"])
    val attemptNumber: Int,
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String,
    @SerializedName(value = "completedAt", alternate = ["completed_at"])
    val completedAt: String?
)

data class RoadmapNodeQuizResponseDto(
    @SerializedName("nodeId") val nodeId: String,
    @SerializedName("skillId") val skillId: String,
    @SerializedName("questions") val questions: List<QuizQuestionDto>
)

data class QuizQuestionDto(
    @SerializedName("id") val id: String,
    @SerializedName("questionText") val questionText: String,
    @SerializedName("optionA") val optionA: String,
    @SerializedName("optionB") val optionB: String,
    @SerializedName("optionC") val optionC: String,
    @SerializedName("optionD") val optionD: String
)

data class SubmitQuizRequestDto(
    @SerializedName("answers") val answers: List<QuizAnswerRequestDto>
)

data class QuizAnswerRequestDto(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("selectedOption") val selectedOption: String
)

data class SubmitQuizResponseDto(
    @SerializedName("scorePct") val scorePct: Double,
    @SerializedName("passed") val passed: Boolean,
    @SerializedName("correctCount") val correctCount: Int,
    @SerializedName("totalQuestions") val totalQuestions: Int,
    @SerializedName("results") val results: List<SubmitQuizQuestionResultDto>,
    @SerializedName("nodeProgress") val nodeProgress: UserNodeProgressDto,
    @SerializedName("unlockedNodes") val unlockedNodes: List<String>,
    @SerializedName("suggestion") val suggestion: String?
)

data class SubmitQuizQuestionResultDto(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("selectedOption") val selectedOption: String,
    @SerializedName("correctOption") val correctOption: String,
    @SerializedName("isCorrect") val isCorrect: Boolean
)
