package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.SportsEsports
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
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
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
    val modules = sections.flatMap { section -> section.modules }
    val nextActionModule = modules.firstOrNull { module -> module.status == LearningStatus.InProgress }
        ?: modules.firstOrNull { module -> module.status == LearningStatus.NotStarted }
    val nextUnlockModule = modules.firstOrNull { module -> module.status == LearningStatus.Locked }
    val requiredModules = modules.filter { module -> module.requirement == LearningRequirement.Required }

    return RoadmapDetailUiState(
        roadmapId = id,
        title = title,
        categoryLabel = categoryLabel,
        progressFraction = progressFraction,
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        completedRequiredNodes = requiredModules.count { module -> module.status == LearningStatus.Completed },
        totalRequiredNodes = requiredModules.size,
        nextActionTitle = nextActionModule?.title.orEmpty(),
        nextAction = nextActionModule?.toRoadmapNodeAction(),
        nextUnlockTitle = nextUnlockModule?.title.orEmpty(),
        groups = sections.toRoadmapDetailGroups(),
        milestones = milestones.map { milestone -> milestone.toRoadmapMilestoneUiModel() },
        isLoading = false,
        errorMessageResId = null
    )
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
        LearningTopicIcon.Game -> Icons.Outlined.SportsEsports
        LearningTopicIcon.Palette -> Icons.Outlined.Palette
        LearningTopicIcon.Science -> Icons.Outlined.Science
        LearningTopicIcon.Security -> Icons.Outlined.Security
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

private fun List<LearningModuleSection>.toRoadmapDetailGroups(): List<RoadmapGroupUiModel> {
    return mapIndexed { index, section ->
        val requiredModules = section.modules.filter { module ->
            module.requirement == LearningRequirement.Required
        }
        val completedRequiredNodes = requiredModules.count { module ->
            module.status == LearningStatus.Completed
        }
        val totalRequiredNodes = requiredModules.size
        val state = section.toRoadmapGroupState()
        val previousSectionTitle = getOrNull(index - 1)?.title ?: section.title
        val firstModuleTitle = section.modules.firstOrNull()?.title ?: section.title

        RoadmapGroupUiModel(
            id = "${section.title.toStableLearningId()}-$index",
            title = section.title,
            completedRequiredNodes = completedRequiredNodes,
            totalRequiredNodes = totalRequiredNodes,
            progressFraction = if (totalRequiredNodes == 0) {
                0f
            } else {
                completedRequiredNodes.toFloat() / totalRequiredNodes.toFloat()
            },
            state = state,
            nodes = if (state == RoadmapGroupState.Locked) {
                emptyList()
            } else {
                section.modules.map { module -> module.toRoadmapNodeUiModel() }
            },
            lockedDescriptionResId = if (state == RoadmapGroupState.Locked) {
                R.string.roadmap_detail_locked_group_description
            } else {
                null
            },
            lockedDescriptionArgs = if (state == RoadmapGroupState.Locked) {
                listOf(previousSectionTitle, firstModuleTitle)
            } else {
                emptyList()
            },
            lockedExpandedDescriptionResId = if (state == RoadmapGroupState.Locked) {
                R.string.roadmap_detail_framework_ecosystem_description
            } else {
                null
            }
        )
    }
}

private fun LearningModuleSection.toRoadmapGroupState(): RoadmapGroupState {
    return when {
        modules.isNotEmpty() && modules.all { module -> module.status == LearningStatus.Completed } ->
            RoadmapGroupState.Completed
        modules.isNotEmpty() && modules.all { module -> module.status == LearningStatus.Locked } ->
            RoadmapGroupState.Locked
        else -> RoadmapGroupState.Expanded
    }
}

private fun LearningModule.toRoadmapNodeUiModel(): RoadmapNodeUiModel {
    return RoadmapNodeUiModel(
        id = id,
        title = title,
        icon = icon.toImageVector(),
        status = status.toRoadmapNodeStatus(),
        requirement = requirement.toRoadmapNodeRequirement(),
        descriptionResId = status.toNodeDescriptionResId(),
        descriptionText = description,
        descriptionArgs = emptyList(),
        action = toRoadmapNodeAction()
    )
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

private fun LearningStatus.toNodeDescriptionResId(): Int {
    return when (this) {
        LearningStatus.Completed -> R.string.roadmap_detail_completed_recently
        LearningStatus.InProgress -> R.string.roadmap_detail_async_description
        LearningStatus.NotStarted -> R.string.roadmap_detail_ready_to_start_description
        LearningStatus.Locked -> R.string.roadmap_detail_locked_node_description
    }
}

private fun LearningModule.toRoadmapNodeAction(): RoadmapNodeAction? {
    return when (status) {
        LearningStatus.Completed -> RoadmapNodeAction.Review
        LearningStatus.InProgress -> if (quizScorePercent == null) {
            RoadmapNodeAction.StartLearning
        } else {
            RoadmapNodeAction.Continue
        }
        LearningStatus.NotStarted -> RoadmapNodeAction.StartLearning
        LearningStatus.Locked -> null
    }
}

private fun RoadmapMilestone.toRoadmapMilestoneUiModel(): RoadmapMilestoneUiModel {
    return RoadmapMilestoneUiModel(
        id = id,
        title = title,
        description = description.orEmpty(),
        state = if (status == LearningStatus.Locked) {
            RoadmapMilestoneState.Locked
        } else {
            RoadmapMilestoneState.Available
        }
    )
}
