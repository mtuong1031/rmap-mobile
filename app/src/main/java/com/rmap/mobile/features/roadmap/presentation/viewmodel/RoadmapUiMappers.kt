package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RoadmapDifficulty
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.SecondaryContainerLight
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapContentItem
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId

fun RoadmapCategory.toCategoryUiModel(roadmapCount: Int = this.roadmapCount): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = shortName,
        icon = icon.toImageVector(),
        backgroundColor = icon.toCategoryBackgroundColor(),
        roadmapCount = roadmapCount
    )
}

fun RoadmapDetail.toRoadmapDetailUiState(): RoadmapDetailUiState {
    val groups = sections.mapIndexed { index, section ->
        section.toRoadmapGroupUiModel(
            index = index,
            hasStartedLearning = hasStartedLearning
        )
    }
    val milestones = milestones.map { milestone -> milestone.toRoadmapMilestoneUiModel() }
    val contentItems = toRoadmapDetailContentUiItems(
        groups = groups,
        milestones = milestones
    )
    val nodes = groups.flatMap { group -> group.nodes }
    val hasStartedRoadmap = completedLessons > 0 ||
        hasStartedLearning ||
        progressFraction > 0f ||
        nodes.any { node ->
            node.status == RoadmapNodeStatus.Completed || node.status == RoadmapNodeStatus.InProgress
        }
    val nextAction = contentItems.nextActionCandidate(fallbackTitle = title)
    val nextUnlockTitle = when (nextAction.target) {
        is RoadmapNextActionTarget.Milestone -> nextAction.title
        RoadmapNextActionTarget.None,
        is RoadmapNextActionTarget.Node -> nodes.firstOrNull { node -> node.status == RoadmapNodeStatus.Locked }
            ?.title
            .orEmpty()
    }
    val requiredNodes = nodes.filter { node -> node.requirement == RoadmapNodeRequirement.Required }
        .ifEmpty { nodes }
    val completedRequiredNodes = requiredNodes.count { node -> node.status == RoadmapNodeStatus.Completed }

    return RoadmapDetailUiState(
        roadmapId = id,
        title = title,
        categoryLabel = roleName.ifBlank { title },
        progressFraction = progressFraction,
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        completedRequiredNodes = completedRequiredNodes,
        totalRequiredNodes = requiredNodes.size,
        nextActionTitle = nextAction.title,
        nextActionTarget = nextAction.target,
        primaryAction = if (hasStartedRoadmap) {
            RoadmapPrimaryAction.ContinueLearning
        } else {
            RoadmapPrimaryAction.StartLearning
        },
        nextUnlockTitle = nextUnlockTitle,
        groups = groups,
        milestones = milestones,
        contentItems = contentItems,
        isEmpty = contentItems.isEmpty(),
        isLoading = false,
        errorMessageResId = null
    )
}

private fun List<RoadmapDetailContentUiItem>.nextActionCandidate(
    fallbackTitle: String
): NextActionCandidate {
    val availableCandidate = firstNotNullOfOrNull { item ->
        when (item) {
            is RoadmapDetailContentUiItem.Group -> item.group
                .takeUnless { group -> group.state == RoadmapGroupState.Locked }
                ?.nodes
                ?.firstOrNull { node ->
                    node.status == RoadmapNodeStatus.InProgress ||
                        node.status == RoadmapNodeStatus.NotStarted
                }
                ?.toNextActionCandidate()

            is RoadmapDetailContentUiItem.Milestone -> item.milestone
                .takeIf { milestone -> milestone.state == RoadmapMilestoneState.Available }
                ?.toNextActionCandidate()
        }
    }

    val lockedCandidate = firstNotNullOfOrNull { item ->
        when (item) {
            is RoadmapDetailContentUiItem.Group -> item.group.nodes
                .firstOrNull { node -> node.status == RoadmapNodeStatus.Locked }
                ?.toNextActionCandidate()

            is RoadmapDetailContentUiItem.Milestone -> item.milestone
                .takeIf { milestone -> milestone.state == RoadmapMilestoneState.Locked }
                ?.toNextActionCandidate()
        }
    }

    val completedCandidate = firstNotNullOfOrNull { item ->
        when (item) {
            is RoadmapDetailContentUiItem.Group -> item.group.nodes
                .firstOrNull { node -> node.status == RoadmapNodeStatus.Completed }
                ?.toNextActionCandidate()

            is RoadmapDetailContentUiItem.Milestone -> null
        }
    }

    return availableCandidate
        ?: lockedCandidate
        ?: completedCandidate
        ?: NextActionCandidate(
            title = fallbackTitle,
            target = RoadmapNextActionTarget.None
        )
}

private fun RoadmapNodeUiModel.toNextActionCandidate(): NextActionCandidate {
    return NextActionCandidate(
        title = title,
        target = RoadmapNextActionTarget.Node(id)
    )
}

private fun RoadmapMilestoneUiModel.toNextActionCandidate(): NextActionCandidate {
    return NextActionCandidate(
        title = title,
        target = RoadmapNextActionTarget.Milestone(id)
    )
}

private fun RoadmapDetail.toRoadmapDetailContentUiItems(
    groups: List<RoadmapGroupUiModel>,
    milestones: List<RoadmapMilestoneUiModel>
): List<RoadmapDetailContentUiItem> {
    val orderedItems = contentItems.ifEmpty {
        sections.map { section -> RoadmapContentItem.Group(section) } +
            this.milestones.map { milestone -> RoadmapContentItem.Milestone(milestone) }
    }
    var groupIndex = 0
    var milestoneIndex = 0

    return orderedItems.mapNotNull { item ->
        when (item) {
            is RoadmapContentItem.Group -> groups.getOrNull(groupIndex++)
                ?.let { group -> RoadmapDetailContentUiItem.Group(group) }
            is RoadmapContentItem.Milestone -> milestones.getOrNull(milestoneIndex++)
                ?.let { milestone -> RoadmapDetailContentUiItem.Milestone(milestone) }
        }
    }
}

fun RoadmapCoverPlaceholder?.toDrawableRes(): Int? {
    return when (this) {
        RoadmapCoverPlaceholder.FullStack -> R.drawable.bg_placeholder_fullstack
        RoadmapCoverPlaceholder.UiUx -> R.drawable.bg_placeholder_uiux
        null -> null
    }
}

fun LearningTopicIcon.toImageVector(): ImageVector {
    return when (this) {
        LearningTopicIcon.Code -> Icons.Outlined.Code
        LearningTopicIcon.DataObject -> Icons.Outlined.DataObject
        LearningTopicIcon.Devices -> Icons.Outlined.Devices
        LearningTopicIcon.Game -> Icons.Outlined.Code
        LearningTopicIcon.Palette -> Icons.Outlined.Palette
        LearningTopicIcon.Science -> Icons.Outlined.Science
        LearningTopicIcon.Security -> Icons.Outlined.Storage
        LearningTopicIcon.SmartToy -> Icons.Outlined.SmartToy
        LearningTopicIcon.Storage -> Icons.Outlined.Storage
        LearningTopicIcon.Terminal -> Icons.Outlined.Terminal
    }
}

fun LearningDifficulty.toRoadmapDifficulty(): RoadmapDifficulty {
    return when (this) {
        LearningDifficulty.Beginner -> RoadmapDifficulty.Beginner
        LearningDifficulty.Intermediate -> RoadmapDifficulty.Intermediate
        LearningDifficulty.Advanced -> RoadmapDifficulty.Expert
        LearningDifficulty.Expert -> RoadmapDifficulty.Expert
        LearningDifficulty.Hard -> RoadmapDifficulty.Hard
    }
}

fun LearningDifficulty.toLabel(): String {
    return when (this) {
        LearningDifficulty.Beginner -> "Beginner"
        LearningDifficulty.Intermediate -> "Intermediate"
        LearningDifficulty.Advanced -> "Advanced"
        LearningDifficulty.Expert -> "Expert"
        LearningDifficulty.Hard -> "Hard"
    }
}

private fun RoadmapMilestone.toRoadmapMilestoneUiModel(): RoadmapMilestoneUiModel {
    return RoadmapMilestoneUiModel(
        id = id,
        title = title,
        description = description.orEmpty(),
        state = when (status) {
            LearningStatus.Completed,
            LearningStatus.InProgress,
            LearningStatus.NotStarted -> RoadmapMilestoneState.Available
            LearningStatus.Locked -> RoadmapMilestoneState.Locked
        }
    )
}

private fun LearningModuleSection.toRoadmapGroupUiModel(
    index: Int,
    hasStartedLearning: Boolean
): RoadmapGroupUiModel {
    val nodes = modules.flatMap { module ->
        module.toRoadmapNodeUiModels(
            hasStartedLearning = hasStartedLearning
        )
    }
    val requiredNodes = nodes.filter { node -> node.requirement == RoadmapNodeRequirement.Required }
        .ifEmpty { nodes }
    val completedRequiredNodes = requiredNodes.count { node -> node.status == RoadmapNodeStatus.Completed }
    val totalRequiredNodes = requiredNodes.size

    return RoadmapGroupUiModel(
        id = "${index}-${title.toStableLearningId()}",
        title = title,
        completedRequiredNodes = completedRequiredNodes,
        totalRequiredNodes = totalRequiredNodes,
        progressFraction = if (totalRequiredNodes == 0) {
            0f
        } else {
            completedRequiredNodes.toFloat() / totalRequiredNodes.toFloat()
        },
        state = nodes.toRoadmapGroupState(index),
        nodes = nodes
    )
}

private fun LearningModule.toRoadmapNodeUiModels(
    hasStartedLearning: Boolean
): List<RoadmapNodeUiModel> {
    return listOf(
        toRoadmapNodeUiModel(
            hasStartedLearning = hasStartedLearning
        )
    ) +
        subLessons.map { subLesson ->
            subLesson.toRoadmapNodeUiModel(
                parentIcon = icon,
                hasStartedLearning = hasStartedLearning
            )
        }
}

private fun LearningModule.toRoadmapNodeUiModel(
    hasStartedLearning: Boolean
): RoadmapNodeUiModel {
    val displayStatus = status.toStartedRoadmapStatus(hasStartedLearning)
    val description = nodeDescription(
        estimatedHours = estimatedHours,
        resourcesCount = resourcesCount,
        status = displayStatus
    )
    return RoadmapNodeUiModel(
        id = id,
        skillId = skillId,
        title = title,
        icon = icon.toImageVector(),
        status = displayStatus.toRoadmapNodeStatus(),
        requirement = requirement.toRoadmapNodeRequirement(),
        descriptionResId = description.resId,
        descriptionArgs = description.args,
        resourcesCount = resourcesCount,
        action = displayStatus.toRoadmapNodeAction()
    )
}

private fun SubLesson.toRoadmapNodeUiModel(
    parentIcon: LearningTopicIcon,
    hasStartedLearning: Boolean
): RoadmapNodeUiModel {
    val displayStatus = status.toStartedRoadmapStatus(hasStartedLearning)
    val description = nodeDescription(
        estimatedHours = estimatedHours,
        resourcesCount = resourcesCount,
        status = displayStatus
    )
    return RoadmapNodeUiModel(
        id = id,
        skillId = skillId,
        title = title,
        icon = parentIcon.toImageVector(),
        status = displayStatus.toRoadmapNodeStatus(),
        requirement = requirement.toRoadmapNodeRequirement(),
        descriptionResId = description.resId,
        descriptionArgs = description.args,
        resourcesCount = resourcesCount,
        action = displayStatus.toRoadmapNodeAction()
    )
}

private fun LearningStatus.toStartedRoadmapStatus(hasStartedLearning: Boolean): LearningStatus {
    return if (hasStartedLearning && this == LearningStatus.NotStarted) {
        LearningStatus.InProgress
    } else {
        this
    }
}

private fun List<RoadmapNodeUiModel>.toRoadmapGroupState(index: Int): RoadmapGroupState {
    val requiredNodes = filter { node -> node.requirement == RoadmapNodeRequirement.Required }
        .ifEmpty { this }

    return when {
        requiredNodes.isNotEmpty() && requiredNodes.all { node -> node.status == RoadmapNodeStatus.Completed } ->
            RoadmapGroupState.Completed
        index == 0 || any { node -> node.status != RoadmapNodeStatus.Locked } -> RoadmapGroupState.Expanded
        else -> RoadmapGroupState.Locked
    }
}

private fun LearningStatus.toRoadmapNodeStatus(): RoadmapNodeStatus {
    return when (this) {
        LearningStatus.Completed -> RoadmapNodeStatus.Completed
        LearningStatus.InProgress -> RoadmapNodeStatus.InProgress
        LearningStatus.NotStarted -> RoadmapNodeStatus.NotStarted
        LearningStatus.Locked -> RoadmapNodeStatus.Locked
    }
}

private fun LearningRequirement.toRoadmapNodeRequirement(): RoadmapNodeRequirement {
    return when (this) {
        LearningRequirement.Required -> RoadmapNodeRequirement.Required
        LearningRequirement.Optional -> RoadmapNodeRequirement.Optional
    }
}

private fun LearningStatus.toRoadmapNodeAction(): RoadmapNodeAction? {
    return when (this) {
        LearningStatus.Completed -> RoadmapNodeAction.Review
        LearningStatus.InProgress -> RoadmapNodeAction.Continue
        LearningStatus.NotStarted -> RoadmapNodeAction.Start
        LearningStatus.Locked -> null
    }
}

private fun nodeDescription(
    estimatedHours: Int?,
    resourcesCount: Int,
    status: LearningStatus
): NodeDescription {
    return if (estimatedHours != null && estimatedHours > 0) {
        NodeDescription(
            resId = R.string.roadmap_detail_node_estimated_hours,
            args = listOf(estimatedHours.toString())
        )
    } else if (resourcesCount > 0) {
        NodeDescription(
            resId = R.string.roadmap_detail_node_resources_available,
            args = listOf(resourcesCount.toString())
        )
    } else {
        when (status) {
            LearningStatus.Completed -> NodeDescription(R.string.roadmap_detail_node_completed_description)
            LearningStatus.InProgress -> NodeDescription(R.string.roadmap_detail_node_in_progress_description)
            LearningStatus.NotStarted -> NodeDescription(R.string.roadmap_detail_node_not_started_description)
            LearningStatus.Locked -> NodeDescription(R.string.roadmap_detail_node_locked_description)
        }
    }
}

private data class NodeDescription(
    val resId: Int,
    val args: List<String> = emptyList()
)

private data class NextActionCandidate(
    val title: String,
    val target: RoadmapNextActionTarget
)

private fun LearningTopicIcon.toCategoryBackgroundColor(): Color {
    return when (this) {
        LearningTopicIcon.Code -> PrimaryContainerLight
        LearningTopicIcon.Devices -> CategoryDevicesBackground
        LearningTopicIcon.SmartToy -> CategoryAiBackground
        LearningTopicIcon.Terminal -> CategoryTerminalBackground
        else -> SecondaryContainerLight
    }
}

private val CategoryDevicesBackground = Color(0xFFFDF2F8)
private val CategoryAiBackground = Color(0xFFEEF2FF)
private val CategoryTerminalBackground = Color(0xFFF0FDF4)
