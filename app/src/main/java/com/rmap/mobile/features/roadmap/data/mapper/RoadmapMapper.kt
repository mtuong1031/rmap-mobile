package com.rmap.mobile.features.roadmap.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.rmap.mobile.features.roadmap.data.remote.model.NodeProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionEnvelopeDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionTestResultDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneTestCaseDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneTestSuiteDto
import com.rmap.mobile.features.roadmap.data.remote.model.QuizAnswerRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.QuizQuestionDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapWithNodesDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourceDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizQuestionResultDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressResponseDto
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
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionTestResult
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestCase
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuite
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuiteStatus
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapContentItem
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId
import kotlin.math.roundToInt

fun RoadmapWithNodesDto.toDomain(progress: RoadmapProgressDto): RoadmapDetail {
    val roadmapId = id.requiredApiField("id")
    val roadmapTitle = title.requiredApiField("title")
    val treeNodes = nodes.orEmpty().toTreeNodes()
    val allNodes = treeNodes.flattenNodes()
    val progressByNodeId = (
        progress.nodes.orEmpty() +
            allNodes.mapNotNull { node ->
                val nodeId = node.id?.takeIf { it.isNotBlank() }
                val nodeProgress = node.progress
                if (nodeId != null && nodeProgress != null && nodeProgress.roadmapNodeId.isNullOrBlank()) {
                    nodeProgress.copy(roadmapNodeId = nodeId)
                } else {
                    nodeProgress
                }
            }
        )
        .mapNotNull { nodeProgress ->
            nodeProgress.roadmapNodeId?.takeIf { it.isNotBlank() }?.let { nodeId ->
                nodeId to nodeProgress
            }
        }
        .toMap()
    val sortedNodes = treeNodes.sortedByRoadmapOrder()
    val leafNodes = allNodes.filter { node -> node.isLeafNode() }
    val completedLessons = progress.completedNodes ?: leafNodes.count { node ->
        progressByNodeId[node.id.orEmpty()].toLearningStatus() == LearningStatus.Completed
    }
    val totalLessons = progress.totalNodes ?: allNodes.size

    val rawContentItems = sortedNodes.map { node ->
        if (node.isMilestoneNode()) {
            RoadmapContentItem.Milestone(node.toRoadmapMilestone(progressByNodeId))
        } else {
            RoadmapContentItem.Group(node.toLearningModuleSection(progressByNodeId))
        }
    }
    val lockedSections = rawContentItems
        .filterIsInstance<RoadmapContentItem.Group>()
        .map { item -> item.section }
        .withSequentialLocks()
    var sectionIndex = 0
    val contentItems = rawContentItems.map { item ->
        when (item) {
            is RoadmapContentItem.Group -> RoadmapContentItem.Group(lockedSections[sectionIndex++])
            is RoadmapContentItem.Milestone -> item
        }
    }
    val sections = contentItems
        .filterIsInstance<RoadmapContentItem.Group>()
        .map { item -> item.section }
    val milestones = contentItems
        .filterIsInstance<RoadmapContentItem.Milestone>()
        .map { item -> item.milestone }

    return RoadmapDetail(
        id = roadmapId,
        title = roadmapTitle,
        categoryLabel = displayRoleName(),
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        sections = sections,
        milestones = milestones,
        contentItems = contentItems,
        aiTip = null,
        roleId = roleId.orEmpty(),
        roleName = displayRoleName(),
        description = description,
        isTemplate = isTemplate == true
    )
}

fun RoadmapWithNodesDto.toSummary(progress: RoadmapProgressDto? = null): RoadmapSummary {
    val nodeCount = progress?.totalNodes ?: nodes.orEmpty().toTreeNodes().flattenNodes().size
    val completedCount = progress?.completedNodes ?: 0
    val categoryId = roleId?.takeIf { it.isNotBlank() }
        ?: roleCategory?.toStableLearningId()
        ?: roleName.orStableCategoryId()
    val categoryLabel = displayRoleName()

    return RoadmapSummary(
        id = id.requiredApiField("id"),
        title = title.requiredApiField("title"),
        totalLessonsCount = nodeCount,
        completedLessonsCount = completedCount,
        difficulty = nodeCount.toDifficulty(),
        durationLabel = categoryLabel,
        icon = inferIcon(categoryLabel, title, description),
        categoryId = categoryId,
        skillNodesCount = nodeCount
    )
}

fun RoadmapDto.toSummary(nodes: List<RoadmapNodeDto> = emptyList()): RoadmapSummary {
    return toRoadmapWithNodes(nodes).toSummary()
}

fun RoadmapDto.toRoadmapWithNodes(nodes: List<RoadmapNodeDto>): RoadmapWithNodesDto {
    return RoadmapWithNodesDto(
        id = id,
        userId = userId,
        roleId = roleId,
        roleName = roleName,
        roleCategory = roleCategory,
        goalName = goalName,
        title = title,
        description = description,
        isTemplate = isTemplate,
        createdAt = createdAt,
        generatedAt = generatedAt,
        updatedAt = updatedAt,
        deadlineDate = deadlineDate,
        estimatedWeeks = estimatedWeeks,
        hoursPerDay = hoursPerDay,
        nodes = nodes
    )
}

fun RoadmapProgressDto.toLearningProgress(): LearningProgress {
    return LearningProgress(
        completedLessons = completedNodes ?: 0,
        totalLessons = totalNodes ?: nodes.orEmpty().size,
        streakDays = streakDays ?: 0,
        todayGoalCompleted = 0,
        todayGoalTotal = 0,
        completedRoadmaps = if ((totalNodes ?: 0) > 0 && completedNodes == totalNodes) 1 else 0
    )
}

fun UpdateNodeProgressResponseDto.toDomain(): NodeProgressUpdateResult {
    return (progress ?: toNodeProgressDto()).toNodeProgressUpdateResult(unlockedNodes.orEmpty())
}

fun RoadmapNodeDetailResponseDto.toSkillLearningContent(
    fallbackSkillId: String
): SkillLearningContent {
    val detail = toDetailDto()
    val node = detail.node
    val progress = detail.progress ?: node?.progress
    val resolvedSkillId = detail.skill?.id
        ?: node?.skillId
        ?: fallbackSkillId
    val skill = (
        detail.skill ?: SkillDetailDto(
            id = resolvedSkillId,
            name = node?.skillName ?: node?.name,
            description = node?.description,
            estimatedHours = node?.estimatedHoursAsInt()
        )
        ).toDomain()
    val resources = detail.resources
        .toSkillResourceDtos()
        .map { resource -> resource.toDomain(defaultSkillId = skill.id) }

    return SkillLearningContent(
        skill = skill,
        resources = resources,
        status = progress.toLearningStatus(),
        quizPassed = progress?.quizPassed == true
    )
}

fun RoadmapNodeDetailResponseDto.toLearningNodeDetail(): LearningNodeDetail {
    val detail = toDetailDto()
    val node = detail.node ?: error("Missing roadmap node detail")
    val progress = detail.progress ?: node.progress
    val skill = detail.skill
    val resources = detail.resources
        .toSkillResourceDtos()
        .map { resource -> resource.toLearningResource() }

    return LearningNodeDetail(
        roadmapId = node.roadmapId.requiredApiField("node.roadmapId"),
        nodeId = node.id.requiredApiField("node.id"),
        title = node.displayName().requiredApiField("node.name"),
        description = node.description ?: skill?.description,
        skillName = skill?.name,
        skillDescription = skill?.description,
        estimatedHours = node.estimatedHoursAsInt() ?: skill?.estimatedHours,
        status = progress.toLearningStatus(),
        requirement = node.learningRequirement(),
        resources = resources,
        prerequisites = detail.prerequisites.toLearningPrerequisites()
    )
}

fun RoadmapNodeDetailResponseDto.toMilestoneDetail(): MilestoneDetail {
    val detail = toDetailDto()
    val node = detail.node ?: error("Missing milestone node detail")
    val progress = detail.progress ?: node.progress

    return MilestoneDetail(
        roadmapId = node.roadmapId.requiredApiField("milestone.node.roadmapId"),
        nodeId = node.id.requiredApiField("milestone.node.id"),
        title = node.displayName().requiredApiField("milestone.node.name"),
        description = node.description,
        status = progress.toLearningStatus(),
        testSuite = detail.milestoneTestSuite?.toDomain(),
        latestSubmission = detail.latestSubmission?.toDomain()
    )
}

fun RoadmapNodeQuizResponseDto.toDomain(): NodeQuiz {
    return NodeQuiz(
        nodeId = nodeId.requiredApiField("quiz.nodeId"),
        skillId = skillId.requiredApiField("quiz.skillId"),
        questions = questions.orEmpty().map { question -> question.toDomain() }
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
        scorePercent = scorePct?.toInt()?.coerceIn(0, 100) ?: 0,
        passed = passed == true,
        correctCount = correctCount ?: 0,
        totalQuestions = totalQuestions ?: results.orEmpty().size,
        suggestion = suggestion,
        unlockedNodeIds = unlockedNodes.orEmpty(),
        questionResults = results.orEmpty().map { result -> result.toDomain() }
    )
}

fun MilestoneSubmissionEnvelopeDto.toDomain(): MilestoneSubmission {
    return submission?.toDomain() ?: error("Missing milestone submission response")
}

fun LearningStatus.toNodeStatusRequestValue(): String {
    return when (this) {
        LearningStatus.Completed -> "COMPLETED"
        LearningStatus.InProgress -> "IN_PROGRESS"
        LearningStatus.Locked -> "LOCKED"
        LearningStatus.NotStarted -> "LOCKED"
    }
}

fun List<RoadmapSummary>.toCategories(): List<RoadmapCategory> {
    return filter { it.categoryId.isNotBlank() }
        .distinctBy { it.categoryId }
        .map { summary ->
            RoadmapCategory(
                id = summary.categoryId,
                name = summary.durationLabel.ifBlank { summary.categoryId },
                icon = summary.icon
            )
        }
}

private fun RoadmapNodeDetailResponseDto.toDetailDto(): RoadmapNodeDetailDto {
    return data?.copy(
        latestSubmission = data.latestSubmission ?: latestSubmission,
        milestoneTestSuite = data.milestoneTestSuite ?: milestoneTestSuite
    ) ?: RoadmapNodeDetailDto(
        node = node,
        progress = progress,
        skill = skill,
        resources = resources,
        prerequisites = prerequisites,
        latestSubmission = latestSubmission,
        milestoneTestSuite = milestoneTestSuite
    )
}

private fun MilestoneTestSuiteDto.toDomain(): MilestoneTestSuite {
    return MilestoneTestSuite(
        id = id.requiredApiField("milestoneTestSuite.id"),
        title = title.requiredApiField("milestoneTestSuite.title"),
        summary = summary.requiredApiField("milestoneTestSuite.summary"),
        passThresholdPercent = (passThresholdPct ?: 0).coerceIn(0, 100),
        status = status.toMilestoneTestSuiteStatus(),
        testCases = testCases.orEmpty().map { testCase -> testCase.toDomain() }
    )
}

private fun MilestoneTestCaseDto.toDomain(): MilestoneTestCase {
    return MilestoneTestCase(
        name = name.requiredApiField("milestoneTestSuite.testCases.name"),
        description = description.requiredApiField("milestoneTestSuite.testCases.description")
    )
}

private fun MilestoneSubmissionDto.toDomain(): MilestoneSubmission {
    return MilestoneSubmission(
        id = id.requiredApiField("milestoneSubmission.id"),
        repoUrl = repoUrl.requiredApiField("milestoneSubmission.repoUrl"),
        testSuiteId = testSuiteId,
        status = status.toMilestoneSubmissionStatus(),
        outputLog = outputLog,
        passRatePercent = passRatePct?.roundToInt()?.coerceIn(0, 100),
        passedTests = passedTests,
        totalTests = totalTests,
        attemptNumber = attemptNumber ?: 0,
        createdAt = createdAt.requiredApiField("milestoneSubmission.createdAt"),
        completedAt = completedAt,
        testResults = testResults.orEmpty().map { result -> result.toDomain() }
    )
}

private fun MilestoneSubmissionTestResultDto.toDomain(): MilestoneSubmissionTestResult {
    return MilestoneSubmissionTestResult(
        name = name.requiredApiField("milestoneSubmission.testResults.name"),
        message = message.orEmpty(),
        passed = passed == true
    )
}

private fun String?.toMilestoneSubmissionStatus(): MilestoneSubmissionStatus {
    return when (this?.lowercase()) {
        "running" -> MilestoneSubmissionStatus.Running
        "passed" -> MilestoneSubmissionStatus.Passed
        "failed" -> MilestoneSubmissionStatus.Failed
        "error" -> MilestoneSubmissionStatus.Error
        else -> MilestoneSubmissionStatus.Unknown
    }
}

private fun String?.toMilestoneTestSuiteStatus(): MilestoneTestSuiteStatus {
    return when (this?.lowercase()) {
        "ready" -> MilestoneTestSuiteStatus.Ready
        "generating" -> MilestoneTestSuiteStatus.Generating
        "failed" -> MilestoneTestSuiteStatus.Failed
        "not_generated" -> MilestoneTestSuiteStatus.NotGenerated
        else -> MilestoneTestSuiteStatus.Unknown
    }
}

private fun JsonElement?.toSkillResourceDtos(): List<SkillResourceDto> {
    if (this == null || isJsonNull) return emptyList()

    val array = when {
        isJsonArray -> asJsonArray
        isJsonObject -> asJsonObject.get("data")?.takeIf { it.isJsonArray }?.asJsonArray
        else -> null
    } ?: return emptyList()

    return array.mapNotNull { element ->
        element.takeIf { it.isJsonObject }
            ?.asJsonObject
            ?.toSkillResourceDto()
    }
}

private fun JsonObject.toSkillResourceDto(): SkillResourceDto {
    return SkillResourceDto(
        id = stringValue("id"),
        skillId = stringValue("skill_id", "skillId"),
        title = stringValue("title"),
        url = stringValue("url"),
        platform = stringValue("platform"),
        isFree = booleanValue("is_free", "isFree"),
        levelTag = stringValue("level_tag", "levelTag")
    )
}

private fun JsonObject.stringValue(vararg fieldNames: String): String? {
    return fieldNames.firstNotNullOfOrNull { fieldName ->
        get(fieldName)
            ?.takeIf { !it.isJsonNull }
            ?.asString
            ?.takeIf { it.isNotBlank() }
    }
}

private fun JsonObject.booleanValue(vararg fieldNames: String): Boolean? {
    return fieldNames.firstNotNullOfOrNull { fieldName ->
        get(fieldName)
            ?.takeIf { !it.isJsonNull }
            ?.asBoolean
    }
}

private fun JsonElement?.toLearningPrerequisites(): List<LearningPrerequisite> {
    if (this == null || isJsonNull) return emptyList()

    val array = when {
        isJsonArray -> asJsonArray
        isJsonObject -> asJsonObject.get("data")?.takeIf { it.isJsonArray }?.asJsonArray
        else -> null
    } ?: return emptyList()

    return array.mapNotNull { element ->
        val value = element.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
        val skillId = value.stringValue("skillId", "skill_id") ?: return@mapNotNull null
        val skillName = value.stringValue("skillName", "skill_name") ?: return@mapNotNull null
        LearningPrerequisite(
            skillId = skillId,
            skillName = skillName
        )
    }
}

private fun List<LearningModuleSection>.withSequentialLocks(): List<LearningModuleSection> {
    var previousRequiredNodesCompleted = true

    return mapIndexed { index, section ->
        val sectionUnlocked = index == 0 || previousRequiredNodesCompleted
        val updatedSection = if (sectionUnlocked) {
            section
        } else {
            section.copy(
                modules = section.modules.map { module -> module.lockIncomplete() }
            )
        }
        previousRequiredNodesCompleted = updatedSection.requiredStatuses()
            .let { statuses -> statuses.isEmpty() || statuses.all { it == LearningStatus.Completed } }
        updatedSection
    }
}

private fun LearningModuleSection.requiredStatuses(): List<LearningStatus> {
    return modules.flatMap { module -> module.requiredStatuses() }
}

private fun LearningModule.requiredStatuses(): List<LearningStatus> {
    val requiredSubLessons = subLessons
        .filter { subLesson -> subLesson.requirement == LearningRequirement.Required }
        .map { subLesson -> subLesson.status }

    return if (requiredSubLessons.isNotEmpty()) {
        requiredSubLessons
    } else if (requirement == LearningRequirement.Required) {
        listOf(status)
    } else {
        emptyList()
    }
}

private fun LearningModule.lockIncomplete(): LearningModule {
    return if (status == LearningStatus.Completed) {
        copy(
            subLessons = subLessons.map { subLesson -> subLesson.lockIncomplete() }
        )
    } else {
        copy(
            status = LearningStatus.Locked,
            progressPercent = 0,
            subLessons = subLessons.map { subLesson -> subLesson.lockIncomplete() }
        )
    }
}

private fun SubLesson.lockIncomplete(): SubLesson {
    return if (status == LearningStatus.Completed) {
        this
    } else {
        copy(status = LearningStatus.Locked)
    }
}

private fun RoadmapNodeDto.toLearningModuleSection(
    progressByNodeId: Map<String, NodeProgressDto>
): LearningModuleSection {
    val sortedChildren = children.orEmpty().sortedByRoadmapOrder()
    val sectionTitle = displayName().requiredApiField("name")
    val modules = if (isGroupNode() && sortedChildren.isNotEmpty()) {
        sortedChildren.map { child ->
            child.toLearningModule(progressByNodeId, includeChildSubLessons = true)
        }
    } else {
        listOf(toLearningModule(progressByNodeId, includeChildSubLessons = false)) +
            sortedChildren.map { child ->
                child.toLearningModule(progressByNodeId, includeChildSubLessons = true)
            }
    }

    return LearningModuleSection(
        title = sectionTitle,
        modules = modules
    )
}

private fun RoadmapNodeDto.toLearningModule(
    progressByNodeId: Map<String, NodeProgressDto>,
    includeChildSubLessons: Boolean
): LearningModule {
    val nodeId = id.requiredApiField("id")
    val status = progressByNodeId[nodeId].toLearningStatus()
    val subLessons = if (includeChildSubLessons) {
        children.orEmpty()
            .sortedByRoadmapOrder()
            .flatMap { child -> child.toSubLessons(progressByNodeId) }
    } else {
        emptyList()
    }

    return LearningModule(
        title = displayName().requiredApiField("name"),
        status = status,
        progressPercent = subLessons.toProgressPercent(status),
        icon = inferIcon(displayName(), description),
        subLessons = subLessons,
        id = nodeId,
        skillId = skillId.orNodeSkillId(nodeId),
        requirement = learningRequirement(),
        estimatedHours = estimatedHoursAsInt(),
        resourcesCount = resourcesCountValue(),
        description = description
    )
}

private fun RoadmapNodeDto.toSubLessons(
    progressByNodeId: Map<String, NodeProgressDto>
): List<SubLesson> {
    val nodeId = id.requiredApiField("id")
    val subLesson = SubLesson(
        title = displayName().requiredApiField("name"),
        status = progressByNodeId[nodeId].toLearningStatus(),
        id = nodeId,
        skillId = skillId.orNodeSkillId(nodeId),
        requirement = learningRequirement(),
        estimatedHours = estimatedHoursAsInt(),
        resourcesCount = resourcesCountValue(),
        description = description
    )
    val descendants = children.orEmpty()
        .sortedByRoadmapOrder()
        .flatMap { child -> child.toSubLessons(progressByNodeId) }

    return listOf(subLesson) + descendants
}

private fun RoadmapNodeDto.toRoadmapMilestone(
    progressByNodeId: Map<String, NodeProgressDto>
): RoadmapMilestone {
    val nodeId = id.requiredApiField("milestone.id")
    return RoadmapMilestone(
        id = nodeId,
        title = displayName().requiredApiField("milestone.name"),
        description = description,
        status = progressByNodeId[nodeId].toLearningStatus()
    )
}

private fun NodeProgressDto?.toNodeProgressUpdateResult(
    unlockedNodeIds: List<String>
): NodeProgressUpdateResult {
    val progress = this ?: error("Missing node progress response")
    return NodeProgressUpdateResult(
        nodeId = progress.roadmapNodeId.requiredApiField("progress.roadmapNodeId"),
        status = progress.toLearningStatus(),
        unlockedNodeIds = unlockedNodeIds.filter { it.isNotBlank() }
    )
}

private fun NodeProgressDto?.toLearningStatus(): LearningStatus {
    return when (this?.status?.lowercase()) {
        "completed" -> LearningStatus.Completed
        "in_progress" -> LearningStatus.InProgress
        "locked" -> LearningStatus.Locked
        else -> LearningStatus.NotStarted
    }
}

private fun UpdateNodeProgressResponseDto.toNodeProgressDto(): NodeProgressDto {
    return NodeProgressDto(
        roadmapNodeId = roadmapNodeId,
        skillId = skillId,
        skillName = skillName,
        status = status,
        startedAt = startedAt,
        completedAt = completedAt
    )
}

private fun Map<String, NodeProgressDto>.countCompleted(): Int {
    return values.count { it.status?.lowercase() == "completed" }
}

private fun SkillResourceDto.toLearningResource(): LearningResource {
    return LearningResource(
        id = id.requiredApiField("resource.id"),
        title = title.requiredApiField("resource.title"),
        url = url.requiredApiField("resource.url"),
        type = platform ?: "OTHER",
        isFree = isFree == true,
        isPrimary = false
    )
}

private fun QuizQuestionDto.toDomain(): NodeQuizQuestion {
    return NodeQuizQuestion(
        id = id.requiredApiField("question.id"),
        text = questionText.requiredApiField("question.questionText"),
        options = listOf(
            NodeQuizOption(key = "A", text = optionA.requiredApiField("question.optionA")),
            NodeQuizOption(key = "B", text = optionB.requiredApiField("question.optionB")),
            NodeQuizOption(key = "C", text = optionC.requiredApiField("question.optionC")),
            NodeQuizOption(key = "D", text = optionD.requiredApiField("question.optionD"))
        )
    )
}

private fun SubmitQuizQuestionResultDto.toDomain(): NodeQuizQuestionResult {
    return NodeQuizQuestionResult(
        questionId = questionId.requiredApiField("result.questionId"),
        selectedOption = selectedOption.requiredApiField("result.selectedOption").uppercase(),
        correctOption = correctOption.requiredApiField("result.correctOption").uppercase(),
        isCorrect = isCorrect == true
    )
}

private fun List<SubLesson>.toProgressPercent(parentStatus: LearningStatus): Int {
    if (isEmpty()) return parentStatus.toProgressPercent()
    return (count { it.status == LearningStatus.Completed } * 100) / size
}

private fun LearningStatus.toProgressPercent(): Int {
    return when (this) {
        LearningStatus.Completed -> 100
        LearningStatus.InProgress -> 50
        LearningStatus.Locked,
        LearningStatus.NotStarted -> 0
    }
}

private fun Int.toDifficulty(): LearningDifficulty {
    return when {
        this >= 40 -> LearningDifficulty.Advanced
        this >= 20 -> LearningDifficulty.Intermediate
        else -> LearningDifficulty.Beginner
    }
}

private fun inferIcon(vararg values: String?): LearningTopicIcon {
    val value = values.filterNotNull().joinToString(" ").lowercase()
    return when {
        listOf("android", "ios", "mobile", "kotlin", "swift").any { it in value } -> LearningTopicIcon.Devices
        listOf("design", "ui", "ux", "figma").any { it in value } -> LearningTopicIcon.Palette
        listOf("data", "machine learning", "ai", "artificial", "python").any { it in value } -> LearningTopicIcon.Science
        listOf("devops", "cloud", "ci", "cd", "docker", "kubernetes").any { it in value } -> LearningTopicIcon.Terminal
        listOf("backend", "database", "sql", "api", "server").any { it in value } -> LearningTopicIcon.Storage
        else -> LearningTopicIcon.Code
    }
}

private fun String?.orStableCategoryId(): String {
    return this?.toStableLearningId()?.takeIf { it.isNotBlank() } ?: "roadmap"
}

private fun String?.requiredApiField(fieldName: String): String {
    return this?.takeIf { it.isNotBlank() }
        ?: error("Missing roadmap API field: $fieldName")
}

private fun String?.orNodeSkillId(nodeId: String): String {
    return takeIf { !it.isNullOrBlank() } ?: nodeId
}

private fun RoadmapWithNodesDto.displayRoleName(): String {
    return roleName?.takeIf { it.isNotBlank() }
        ?: roleCategory?.toDisplayLabel()
        ?: goalName.orEmpty()
}

private fun RoadmapNodeDto.displayName(): String? {
    return skillName?.takeIf { it.isNotBlank() }
        ?: name?.takeIf { it.isNotBlank() }
}

private fun RoadmapNodeDto.estimatedHoursAsInt(): Int? {
    return skillEstimatedHours ?: estimatedHours?.toInt()
}

private fun RoadmapNodeDto.resourcesCountValue(): Int {
    return resourcesCount?.coerceAtLeast(0) ?: 0
}

private fun RoadmapNodeDto.isGroupNode(): Boolean {
    return nodeType.equals("GROUP", ignoreCase = true)
}

private fun RoadmapNodeDto.isMilestoneNode(): Boolean {
    return nodeType.equals("MILESTONE", ignoreCase = true)
}

private fun RoadmapNodeDto.isLeafNode(): Boolean {
    return nodeType.equals("REQUIRED", ignoreCase = true) ||
        nodeType.equals("OPTIONAL", ignoreCase = true)
}

private fun RoadmapNodeDto.learningRequirement(): LearningRequirement {
    return relationType.toLearningRequirement(
        nodeType = nodeType
    )
}

private fun String?.toLearningRequirement(nodeType: String? = null): LearningRequirement {
    return when {
        equals("optional", ignoreCase = true) -> LearningRequirement.Optional
        nodeType.equals("OPTIONAL", ignoreCase = true) -> LearningRequirement.Optional
        else -> LearningRequirement.Required
    }
}

private fun String.toDisplayLabel(): String {
    return split('_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { firstChar -> firstChar.uppercase() }
        }
}

private fun List<RoadmapNodeDto>.toTreeNodes(): List<RoadmapNodeDto> {
    val flatNodes = flatMap { it.flattenNodes() }
        .distinctBy { it.id ?: it.displayName().orEmpty() }
    if (flatNodes.isEmpty()) return emptyList()

    val childrenByParentId = flatNodes
        .filter { !it.parentNodeId.isNullOrBlank() }
        .groupBy { it.parentNodeId.orEmpty() }
    val rootNodes = flatNodes
        .filter { it.parentNodeId.isNullOrBlank() }
        .ifEmpty { flatNodes }

    return rootNodes.sortedByRoadmapOrder().map { node ->
        node.withNestedChildren(childrenByParentId)
    }
}

private fun RoadmapNodeDto.withNestedChildren(
    childrenByParentId: Map<String, List<RoadmapNodeDto>>
): RoadmapNodeDto {
    val nodeId = id.orEmpty()
    val nestedChildren = childrenByParentId[nodeId].orEmpty()
        .sortedByRoadmapOrder()
        .map { child -> child.withNestedChildren(childrenByParentId) }
    return copy(children = nestedChildren)
}

private fun RoadmapNodeDto.flattenNodes(): List<RoadmapNodeDto> {
    return listOf(this) + children.orEmpty().flatMap { child -> child.flattenNodes() }
}

private fun List<RoadmapNodeDto>.flattenNodes(): List<RoadmapNodeDto> {
    return flatMap { node -> node.flattenNodes() }
}

private fun List<RoadmapNodeDto>.sortedByRoadmapOrder(): List<RoadmapNodeDto> {
    return sortedWith(
        compareBy<RoadmapNodeDto> { it.sortOrder ?: Int.MAX_VALUE }
            .thenBy { it.posY ?: Float.MAX_VALUE }
            .thenBy { it.posX ?: Float.MAX_VALUE }
            .thenBy { it.displayName().orEmpty() }
    )
}
