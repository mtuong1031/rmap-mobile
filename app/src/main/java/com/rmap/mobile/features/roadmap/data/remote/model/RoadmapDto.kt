package com.rmap.mobile.features.roadmap.data.remote.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class RoadmapDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "user_id", alternate = ["userId"]) val userId: String? = null,
    @SerializedName(value = "role_id", alternate = ["roleId"]) val roleId: String? = null,
    @SerializedName(value = "role_name", alternate = ["roleName"]) val roleName: String? = null,
    @SerializedName("roleCategory") val roleCategory: String? = null,
    @SerializedName("goalName") val goalName: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName(value = "is_template", alternate = ["isTemplate"]) val isTemplate: Boolean? = null,
    @SerializedName(value = "created_at", alternate = ["createdAt"]) val createdAt: String? = null,
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("deadlineDate") val deadlineDate: String? = null,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int? = null,
    @SerializedName("hoursPerDay") val hoursPerDay: Float? = null
)

data class RoadmapWithNodesDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "user_id", alternate = ["userId"]) val userId: String? = null,
    @SerializedName(value = "role_id", alternate = ["roleId"]) val roleId: String? = null,
    @SerializedName(value = "role_name", alternate = ["roleName"]) val roleName: String? = null,
    @SerializedName("roleCategory") val roleCategory: String? = null,
    @SerializedName("goalName") val goalName: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName(value = "is_template", alternate = ["isTemplate"]) val isTemplate: Boolean? = null,
    @SerializedName(value = "created_at", alternate = ["createdAt"]) val createdAt: String? = null,
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("deadlineDate") val deadlineDate: String? = null,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int? = null,
    @SerializedName("hoursPerDay") val hoursPerDay: Float? = null,
    @SerializedName("nodes") val nodes: List<RoadmapNodeDto>? = null
)

data class RoadmapNodeDto(
    @SerializedName(value = "roadmap_node_id", alternate = ["id", "roadmapNodeId"]) val id: String? = null,
    @SerializedName(value = "roadmap_id", alternate = ["roadmapId"]) val roadmapId: String? = null,
    @SerializedName(value = "skill_id", alternate = ["skillId"]) val skillId: String? = null,
    @SerializedName(value = "skill_name", alternate = ["skillName"]) val skillName: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("nodeType") val nodeType: String? = null,
    @SerializedName(value = "skill_estimated_hours", alternate = ["skillEstimatedHours"]) val skillEstimatedHours: Int? = null,
    @SerializedName("estimatedHours") val estimatedHours: Float? = null,
    @SerializedName(value = "parent_node_id", alternate = ["parentNodeId", "parentId"]) val parentNodeId: String? = null,
    @SerializedName(value = "relation_type", alternate = ["relationType"]) val relationType: String? = null,
    @SerializedName(value = "sort_order", alternate = ["sortOrder"]) val sortOrder: Int? = null,
    @SerializedName("posX") val posX: Float? = null,
    @SerializedName("posY") val posY: Float? = null,
    @SerializedName("resourcesCount")
    val resourcesCount: Int? = null,
    @SerializedName("progress") val progress: NodeProgressDto? = null,
    @SerializedName("children") val children: List<RoadmapNodeDto>? = null
)

data class RoadmapProgressDto(
    @SerializedName(value = "roadmap_id", alternate = ["roadmapId"]) val roadmapId: String? = null,
    @SerializedName(value = "total_nodes", alternate = ["totalNodes", "nodesTotal"]) val totalNodes: Int? = null,
    @SerializedName(value = "completed_nodes", alternate = ["completedNodes", "nodesCompleted"]) val completedNodes: Int? = null,
    @SerializedName(value = "in_progress_nodes", alternate = ["inProgressNodes"]) val inProgressNodes: Int? = null,
    @SerializedName(value = "completion_percentage", alternate = ["completionPercentage", "completionPct"]) val completionPercentage: Float? = null,
    @SerializedName("streakDays") val streakDays: Int? = null,
    @SerializedName("skillReadinessPct") val skillReadinessPct: Float? = null,
    @SerializedName("timelineWarning") val timelineWarning: TimelineWarningDto? = null,
    @SerializedName("nodes") val nodes: List<NodeProgressDto>? = null
)

data class NodeProgressDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "roadmap_node_id", alternate = ["roadmapNodeId"]) val roadmapNodeId: String? = null,
    @SerializedName(value = "skill_id", alternate = ["skillId"]) val skillId: String? = null,
    @SerializedName(value = "skill_name", alternate = ["skillName"]) val skillName: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName(value = "started_at", alternate = ["startedAt"]) val startedAt: String? = null,
    @SerializedName(value = "completed_at", alternate = ["completedAt"]) val completedAt: String? = null,
    @SerializedName("quizScorePct") val quizScorePct: Float? = null,
    @SerializedName("quizPassed") val quizPassed: Boolean? = null
)

data class UpdateNodeProgressRequestDto(
    @SerializedName("status") val status: String
)

data class UpdateNodeProgressResponseDto(
    @SerializedName("progress") val progress: NodeProgressDto? = null,
    @SerializedName(value = "unlocked_nodes", alternate = ["unlockedNodes"]) val unlockedNodes: List<String>? = null,
    @SerializedName(value = "roadmap_node_id", alternate = ["roadmapNodeId"]) val roadmapNodeId: String? = null,
    @SerializedName(value = "skill_id", alternate = ["skillId"]) val skillId: String? = null,
    @SerializedName(value = "skill_name", alternate = ["skillName"]) val skillName: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName(value = "started_at", alternate = ["startedAt"]) val startedAt: String? = null,
    @SerializedName(value = "completed_at", alternate = ["completedAt"]) val completedAt: String? = null
)

data class TimelineWarningDto(
    @SerializedName("isBehind") val isBehind: Boolean? = null,
    @SerializedName("paceDeficitPct") val paceDeficitPct: Float? = null,
    @SerializedName("estimatedDelayDays") val estimatedDelayDays: Int? = null,
    @SerializedName("message") val message: String? = null
)

data class RoadmapNodesResponseDto(
    @SerializedName(value = "nodes", alternate = ["data"]) val nodes: List<RoadmapNodeDto>? = null
)

data class RoadmapNodeDetailResponseDto(
    @SerializedName("data") val data: RoadmapNodeDetailDto? = null,
    @SerializedName("node") val node: RoadmapNodeDto? = null,
    @SerializedName("progress") val progress: NodeProgressDto? = null,
    @SerializedName("skill") val skill: SkillDetailDto? = null,
    @SerializedName("resources") val resources: JsonElement? = null,
    @SerializedName("prerequisites") val prerequisites: JsonElement? = null,
    @SerializedName("latestSubmission") val latestSubmission: MilestoneSubmissionDto? = null,
    @SerializedName("milestoneTestSuite") val milestoneTestSuite: MilestoneTestSuiteDto? = null
)

data class RoadmapNodeDetailDto(
    @SerializedName("node") val node: RoadmapNodeDto? = null,
    @SerializedName("progress") val progress: NodeProgressDto? = null,
    @SerializedName("skill") val skill: SkillDetailDto? = null,
    @SerializedName("resources") val resources: JsonElement? = null,
    @SerializedName("prerequisites") val prerequisites: JsonElement? = null,
    @SerializedName("latestSubmission") val latestSubmission: MilestoneSubmissionDto? = null,
    @SerializedName("milestoneTestSuite") val milestoneTestSuite: MilestoneTestSuiteDto? = null
)

data class SubmitMilestoneSubmissionRequestDto(
    @SerializedName("repoUrl") val repoUrl: String
)

data class MilestoneSubmissionEnvelopeDto(
    @SerializedName("submission") val submission: MilestoneSubmissionDto? = null
)

data class MilestoneSubmissionDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("repoUrl") val repoUrl: String? = null,
    @SerializedName("testSuiteId") val testSuiteId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("outputLog") val outputLog: String? = null,
    @SerializedName("passRatePct") val passRatePct: Float? = null,
    @SerializedName("passedTests") val passedTests: Int? = null,
    @SerializedName("testResults") val testResults: List<MilestoneSubmissionTestResultDto>? = null,
    @SerializedName("totalTests") val totalTests: Int? = null,
    @SerializedName("attemptNumber") val attemptNumber: Int? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null
)

data class MilestoneSubmissionTestResultDto(
    @SerializedName("message") val message: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("passed") val passed: Boolean? = null
)

data class MilestoneTestSuiteDto(
    @SerializedName("generatedAt") val generatedAt: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("passThresholdPct") val passThresholdPct: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("testCases") val testCases: List<MilestoneTestCaseDto>? = null,
    @SerializedName("title") val title: String? = null
)

data class MilestoneTestCaseDto(
    @SerializedName("description") val description: String? = null,
    @SerializedName("name") val name: String? = null
)

data class RoadmapNodeQuizResponseDto(
    @SerializedName("nodeId") val nodeId: String? = null,
    @SerializedName("skillId") val skillId: String? = null,
    @SerializedName("questions") val questions: List<QuizQuestionDto>? = null
)

data class QuizQuestionDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("questionText") val questionText: String? = null,
    @SerializedName("optionA") val optionA: String? = null,
    @SerializedName("optionB") val optionB: String? = null,
    @SerializedName("optionC") val optionC: String? = null,
    @SerializedName("optionD") val optionD: String? = null
)

data class SubmitQuizRequestDto(
    @SerializedName("answers") val answers: List<QuizAnswerRequestDto>
)

data class QuizAnswerRequestDto(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("selectedOption") val selectedOption: String
)

data class SubmitQuizResponseDto(
    @SerializedName("scorePct") val scorePct: Float? = null,
    @SerializedName("passed") val passed: Boolean? = null,
    @SerializedName("correctCount") val correctCount: Int? = null,
    @SerializedName("totalQuestions") val totalQuestions: Int? = null,
    @SerializedName("results") val results: List<SubmitQuizQuestionResultDto>? = null,
    @SerializedName("nodeProgress") val nodeProgress: NodeProgressDto? = null,
    @SerializedName("unlockedNodes") val unlockedNodes: List<String>? = null,
    @SerializedName("suggestion") val suggestion: String? = null
)

data class SubmitQuizQuestionResultDto(
    @SerializedName("questionId") val questionId: String? = null,
    @SerializedName("selectedOption") val selectedOption: String? = null,
    @SerializedName("correctOption") val correctOption: String? = null,
    @SerializedName("isCorrect") val isCorrect: Boolean? = null
)

data class RoadmapsResponseDto(
    @SerializedName("data") val data: List<RoadmapDto>? = null,
    @SerializedName("meta") val meta: PaginationMetaDto? = null
)

data class PaginationMetaDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName(value = "per_page", alternate = ["perPage"]) val perPage: Int? = null,
    @SerializedName(value = "total_pages", alternate = ["totalPages"]) val totalPages: Int? = null
)

data class TemplateCategoryDto(
    @SerializedName("category") val category: String,
    @SerializedName("label") val label: String,
    @SerializedName("templatesCount") val templatesCount: Int,
    @SerializedName("shortLabel") val shortLabel: String = label
)

data class TemplateCategoriesResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("categories") val categories: List<TemplateCategoryDto>
)

