package com.rmap.mobile.features.airoadmap.data.model

import com.google.gson.annotations.SerializedName

data class OnboardingQuizRequestDto(
    @SerializedName("topic") val topic: String
)

data class OnboardingQuizResponseDto(
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("questions") val questions: List<OnboardingQuizQuestionDto>
)

data class OnboardingQuizQuestionDto(
    @SerializedName("question") val question: String,
    @SerializedName("possibleAnswers") val possibleAnswers: List<String>
)

data class GenerateRoadmapRequestDto(
    @SerializedName("goal") val goal: String,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("hoursPerDay") val hoursPerDay: Double,
    @SerializedName("deadlineDate") val deadlineDate: String,
    @SerializedName("quizAnswers") val quizAnswers: List<AssessmentAnswerDto>
)

data class AssessmentAnswerDto(
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String
)

data class GenerateRoadmapResponseDto(
    @SerializedName("roadmap") val roadmap: RoadmapResponseDto,
    @SerializedName("timelineWarning") val timelineWarning: TimelineWarningDto?
)

data class TimelineWarningDto(
    @SerializedName("paceDeficitPct") val paceDeficitPct: Double?,
    @SerializedName("estimatedDelayDays") val estimatedDelayDays: Int?,
    @SerializedName("message") val message: String?
)

data class PaginatedRoadmapsResponseDto(
    @SerializedName("data") val data: List<RoadmapResponseDto>,
    @SerializedName("meta") val meta: PaginationMetaDto?
)

data class PaginationMetaDto(
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class RoadmapResponseDto(
    @SerializedName("deadlineDate") val deadlineDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int?,
    @SerializedName("generatedAt") val generatedAt: String,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("hoursPerDay") val hoursPerDay: Double?,
    @SerializedName("id") val id: String,
    @SerializedName("isTemplate") val isTemplate: Boolean,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("title") val title: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("userId") val userId: String?
)

data class RoadmapNodesResponseDto(
    @SerializedName("nodes") val nodes: List<RoadmapNodeResponseDto>
)

data class RoadmapNodeResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("roadmapId") val roadmapId: String,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("skillId") val skillId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("nodeType") val nodeType: String,
    @SerializedName("estimatedHours") val estimatedHours: Double?,
    @SerializedName("posX") val posX: Double,
    @SerializedName("posY") val posY: Double
)

data class TemplateRoadmapNodesResponseDto(
    @SerializedName("nodes") val nodes: List<TemplateRoadmapNodeDto>
)

data class TemplateRoadmapNodeDto(
    @SerializedName("description") val description: String?,
    @SerializedName("estimatedHours") val estimatedHours: Double?,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nodeType") val nodeType: String,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("posX") val posX: Double,
    @SerializedName("posY") val posY: Double,
    @SerializedName("roadmapId") val roadmapId: String,
    @SerializedName("skillId") val skillId: String?
)
