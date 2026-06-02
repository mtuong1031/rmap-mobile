package com.rmap.mobile.features.roadmap.data.mapper

import com.rmap.mobile.features.roadmap.data.model.NodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.model.PrerequisiteDto
import com.rmap.mobile.features.roadmap.data.model.QuizAnswerRequestDto
import com.rmap.mobile.features.roadmap.data.model.QuizQuestionDto
import com.rmap.mobile.features.roadmap.data.model.ResourceDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodeWithUserProgressDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapProgressSummaryDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapResponseDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizQuestionResultDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizResponseDto
import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningPrerequisite
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningResource
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson

fun RoadmapResponseDto.toSummary(
    progress: RoadmapProgressSummaryDto? = null
): RoadmapSummary {
    val icon = roleCategory.toLearningTopicIcon()
    val nodeCount = progress?.nodesTotal ?: 0
    return RoadmapSummary(
        id = id,
        title = title,
        totalLessonsCount = nodeCount,
        completedLessonsCount = progress?.nodesCompleted ?: 0,
        difficulty = estimatedWeeks.toLearningDifficulty(),
        durationLabel = estimatedWeeks.toDurationLabel(),
        icon = icon,
        categoryId = roleCategory.toCategoryId(),
        skillNodesCount = nodeCount,
        coverPlaceholder = title.toCoverPlaceholder()
    )
}

fun List<RoadmapSummary>.toCategories(): List<RoadmapCategory> {
    return distinctBy { it.categoryId }
        .map { roadmap ->
            RoadmapCategory(
                id = roadmap.categoryId,
                name = roadmap.categoryId.toCategoryName(),
                icon = roadmap.icon
            )
        }
}

fun RoadmapResponseDto.toDetail(
    nodes: List<RoadmapNodeWithUserProgressDto>
): RoadmapDetail {
    val leafNodes = nodes.filter { node -> node.isLessonNode() }
    val milestones = nodes
        .filter { node -> node.nodeType.equals(NODE_TYPE_MILESTONE, ignoreCase = true) }
        .sortedWith(compareBy<RoadmapNodeWithUserProgressDto> { it.posY }.thenBy { it.posX })
        .map { node -> node.toRoadmapMilestone() }
    val completedLessons = leafNodes.count { node -> node.toLearningStatus() == LearningStatus.Completed }
    val sections = nodes
        .filter { node -> node.nodeType.equals(NODE_TYPE_GROUP, ignoreCase = true) }
        .sortedWith(compareBy<RoadmapNodeWithUserProgressDto> { it.posY }.thenBy { it.posX })
        .mapNotNull { group ->
            val modules = leafNodes
                .filter { node -> node.parentId == group.id }
                .sortedWith(compareBy<RoadmapNodeWithUserProgressDto> { it.posY }.thenBy { it.posX })
                .map { node -> node.toLearningModule(roleCategory) }

            if (modules.isEmpty()) {
                null
            } else {
                LearningModuleSection(
                    title = group.name,
                    modules = modules
                )
            }
        }
        .ifEmpty {
            listOf(
                LearningModuleSection(
                    title = roleCategory.toCategoryName(),
                    modules = leafNodes
                        .sortedWith(compareBy<RoadmapNodeWithUserProgressDto> { it.posY }.thenBy { it.posX })
                        .map { node -> node.toLearningModule(roleCategory) }
                )
            )
        }

    return RoadmapDetail(
        id = id,
        title = title,
        categoryLabel = roleCategory.toCategoryName(),
        completedLessons = completedLessons,
        totalLessons = leafNodes.size,
        sections = sections,
        milestones = milestones,
        aiTip = leafNodes.toAiScholarTip()
    )
}

fun List<RoadmapProgressSummaryDto>.toLearningProgress(): LearningProgress {
    val totalLessons = sumOf { it.nodesTotal }
    val completedLessons = sumOf { it.nodesCompleted }
    return LearningProgress(
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        streakDays = maxOfOrNull { it.streakDays } ?: 0,
        todayGoalCompleted = 0,
        todayGoalTotal = DEFAULT_DAILY_GOAL,
        completedRoadmaps = count { progress ->
            progress.nodesTotal > 0 && progress.nodesCompleted >= progress.nodesTotal
        }
    )
}

fun NodeDetailResponseDto.toDomain(): LearningNodeDetail {
    return LearningNodeDetail(
        roadmapId = node.roadmapId,
        nodeId = node.id,
        title = node.name,
        description = node.description ?: skill?.description,
        skillName = skill?.name,
        skillDescription = skill?.description,
        estimatedHours = node.estimatedHours.toEstimatedHours()
            ?: skill?.defaultEstimatedHours.toEstimatedHours(),
        status = node.toLearningStatus(),
        requirement = node.toLearningRequirement(),
        resources = resources.orEmpty().map { resource -> resource.toDomain() },
        prerequisites = prerequisites.map { prerequisite -> prerequisite.toDomain() }
    )
}

fun RoadmapNodeQuizResponseDto.toDomain(): NodeQuiz {
    return NodeQuiz(
        nodeId = nodeId,
        skillId = skillId,
        questions = questions.map { question -> question.toDomain() }
    )
}

fun List<NodeQuizAnswer>.toSubmitQuizRequestDto(): SubmitQuizRequestDto {
    return SubmitQuizRequestDto(
        answers = map { answer ->
            QuizAnswerRequestDto(
                questionId = answer.questionId,
                selectedOption = answer.selectedOption.uppercase()
            )
        }
    )
}

fun SubmitQuizResponseDto.toDomain(): NodeQuizSubmissionResult {
    return NodeQuizSubmissionResult(
        scorePercent = scorePct.toInt(),
        passed = passed,
        correctCount = correctCount,
        totalQuestions = totalQuestions,
        suggestion = suggestion,
        unlockedNodeIds = unlockedNodes,
        questionResults = results.map { result -> result.toDomain() }
    )
}

fun String.toCategoryId(): String {
    return trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

fun String.toCategoryName(): String {
    return trim()
        .replace("_", " ")
        .replace("-", " ")
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }
}

private fun RoadmapNodeWithUserProgressDto.toLearningModule(
    roleCategory: String
): LearningModule {
    val status = toLearningStatus()
    return LearningModule(
        id = id,
        title = name,
        status = status,
        progressPercent = status.toProgressPercent(),
        icon = roleCategory.toLearningTopicIcon(),
        subLessons = emptyList(),
        description = description,
        requirement = toLearningRequirement(),
        quizScorePercent = progress?.quizScorePct.toIntPercent(),
        quizPassed = progress?.quizPassed
    )
}

private fun RoadmapNodeWithUserProgressDto.toRoadmapMilestone(): RoadmapMilestone {
    return RoadmapMilestone(
        id = id,
        title = name,
        description = description,
        status = toLearningStatus()
    )
}

private fun RoadmapNodeWithUserProgressDto.toLearningStatus(): LearningStatus {
    return when (progress?.status?.uppercase()) {
        NODE_STATUS_COMPLETED -> LearningStatus.Completed
        NODE_STATUS_IN_PROGRESS -> LearningStatus.InProgress
        NODE_STATUS_LOCKED -> LearningStatus.Locked
        null -> LearningStatus.NotStarted
        else -> LearningStatus.NotStarted
    }
}

private fun RoadmapNodeWithUserProgressDto.toLearningRequirement(): LearningRequirement {
    return if (nodeType.equals(NODE_TYPE_OPTIONAL, ignoreCase = true)) {
        LearningRequirement.Optional
    } else {
        LearningRequirement.Required
    }
}

private fun LearningStatus.toProgressPercent(): Int {
    return when (this) {
        LearningStatus.Completed -> 100
        LearningStatus.InProgress -> 50
        LearningStatus.Locked,
        LearningStatus.NotStarted -> 0
    }
}

private fun RoadmapNodeWithUserProgressDto.isLessonNode(): Boolean {
    return nodeType.equals(NODE_TYPE_REQUIRED, ignoreCase = true) ||
        nodeType.equals(NODE_TYPE_OPTIONAL, ignoreCase = true)
}

private fun List<RoadmapNodeWithUserProgressDto>.toAiScholarTip(): AiScholarTip? {
    val currentModule = firstOrNull { node -> node.toLearningStatus() == LearningStatus.InProgress }
    val nextModule = firstOrNull { node ->
        node.toLearningStatus() == LearningStatus.NotStarted ||
            node.toLearningStatus() == LearningStatus.Locked
    }
    return if (currentModule == null && nextModule == null) {
        null
    } else {
        AiScholarTip(
            currentModule = currentModule?.name.orEmpty(),
            recommendedTopic = nextModule?.name ?: currentModule?.name.orEmpty(),
            nextModule = nextModule?.name.orEmpty()
        )
    }
}

private fun Int?.toLearningDifficulty(): LearningDifficulty {
    return when {
        this == null -> LearningDifficulty.Intermediate
        this <= 4 -> LearningDifficulty.Beginner
        this <= 8 -> LearningDifficulty.Intermediate
        this <= 12 -> LearningDifficulty.Advanced
        else -> LearningDifficulty.Expert
    }
}

private fun Int?.toDurationLabel(): String {
    return this?.let { weeks -> "$weeks weeks" } ?: "Self-paced"
}

private fun String.toLearningTopicIcon(): LearningTopicIcon {
    return when (uppercase()) {
        "WEB_DEVELOPMENT",
        "FRAMEWORKS",
        "BEST_PRACTICES",
        "GAME_DEVELOPMENT" -> LearningTopicIcon.Code
        "MOBILE_DEVELOPMENT" -> LearningTopicIcon.Devices
        "DESIGN" -> LearningTopicIcon.Palette
        "AI_AND_MACHINE_LEARNING" -> LearningTopicIcon.SmartToy
        "DATA_ANALYSIS" -> LearningTopicIcon.Science
        "DEVOPS" -> LearningTopicIcon.Terminal
        "DATABASES",
        "BLOCKCHAIN",
        "CYBER_SECURITY" -> LearningTopicIcon.Storage
        else -> LearningTopicIcon.DataObject
    }
}

private fun String.toCoverPlaceholder(): RoadmapCoverPlaceholder? {
    val normalizedTitle = lowercase()
    return when {
        "full stack" in normalizedTitle -> RoadmapCoverPlaceholder.FullStack
        "ui/ux" in normalizedTitle || "ui ux" in normalizedTitle -> RoadmapCoverPlaceholder.UiUx
        else -> null
    }
}

private fun ResourceDto.toDomain(): LearningResource {
    return LearningResource(
        id = id.toString(),
        title = title,
        url = url,
        type = resourceType,
        isFree = isFree,
        isPrimary = isPrimary
    )
}

private fun PrerequisiteDto.toDomain(): LearningPrerequisite {
    return LearningPrerequisite(
        skillId = skillId,
        skillName = skillName
    )
}

private fun QuizQuestionDto.toDomain(): NodeQuizQuestion {
    return NodeQuizQuestion(
        id = id,
        text = questionText,
        options = listOf(
            NodeQuizOption(key = "A", text = optionA),
            NodeQuizOption(key = "B", text = optionB),
            NodeQuizOption(key = "C", text = optionC),
            NodeQuizOption(key = "D", text = optionD)
        )
    )
}

private fun SubmitQuizQuestionResultDto.toDomain(): NodeQuizQuestionResult {
    return NodeQuizQuestionResult(
        questionId = questionId,
        selectedOption = selectedOption.uppercase(),
        correctOption = correctOption.uppercase(),
        isCorrect = isCorrect
    )
}

private fun Double?.toEstimatedHours(): Int? {
    return this?.toInt()?.takeIf { hours -> hours > 0 }
}

private fun Double?.toIntPercent(): Int? {
    return this?.toInt()?.coerceIn(0, 100)
}

private const val DEFAULT_DAILY_GOAL = 3
private const val NODE_TYPE_GROUP = "GROUP"
private const val NODE_TYPE_MILESTONE = "MILESTONE"
private const val NODE_TYPE_OPTIONAL = "OPTIONAL"
private const val NODE_TYPE_REQUIRED = "REQUIRED"
private const val NODE_STATUS_COMPLETED = "COMPLETED"
private const val NODE_STATUS_IN_PROGRESS = "IN_PROGRESS"
private const val NODE_STATUS_LOCKED = "LOCKED"
