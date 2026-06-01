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
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId

fun RoadmapCategory.toCategoryUiModel(roadmapCount: Int = 0): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        icon = icon.toImageVector(),
        backgroundColor = icon.toCategoryBackgroundColor(),
        roadmapCount = roadmapCount
    )
}

fun RoadmapDetail.toRoadmapDetailUiState(): RoadmapDetailUiState {
    val firstSectionTitle = sections.firstOrNull()?.title.orEmpty()
    val frameworkSectionTitle = sections.getOrNull(1)?.title.orEmpty()
    val nextNodeTitle = sections
        .flatMap { section -> section.modules }
        .flatMap { module -> module.subLessons }
        .firstOrNull { lesson -> lesson.status == LearningStatus.InProgress }
        ?.title
        ?: sections.firstOrNull()
            ?.modules
            ?.firstOrNull { module -> module.status == LearningStatus.InProgress }
            ?.title
            .orEmpty()
    val nextUnlockTitle = sections
        .flatMap { section -> section.modules }
        .flatMap { module -> module.subLessons }
        .firstOrNull { lesson -> lesson.status == LearningStatus.Locked }
        ?.title
        ?: ""

    return RoadmapDetailUiState(
        roadmapId = id,
        title = title,
        categoryLabel = "Web Development",
        progressFraction = progressFraction,
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        completedRequiredNodes = 6,
        totalRequiredNodes = 8,
        nextActionTitle = nextNodeTitle,
        nextUnlockTitle = nextUnlockTitle,
        groups = buildRoadmapDetailGroups(
            firstSectionTitle = firstSectionTitle,
            frameworkSectionTitle = frameworkSectionTitle,
            nextNodeTitle = nextNodeTitle,
            nextUnlockTitle = nextUnlockTitle
        ),
        milestones = buildRoadmapDetailMilestones(),
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

private fun buildRoadmapDetailGroups(
    firstSectionTitle: String,
    frameworkSectionTitle: String,
    nextNodeTitle: String,
    nextUnlockTitle: String
): List<RoadmapGroupUiModel> {
    val coreTitle = firstSectionTitle.ifBlank { "Core Web Fundamentals" }
    val frameworkTitle = frameworkSectionTitle.ifBlank { "Framework Ecosystem" }
    val activeNodeTitle = nextNodeTitle.ifBlank { "Asynchronous JS" }
    val lockedNodeTitle = nextUnlockTitle.ifBlank { "DOM Manipulation" }

    return listOf(
        RoadmapGroupUiModel(
            id = "core-web-fundamentals-expanded",
            title = coreTitle,
            completedRequiredNodes = 2,
            totalRequiredNodes = 3,
            progressFraction = 2f / 3f,
            state = RoadmapGroupState.Expanded,
            nodes = listOf(
                RoadmapNodeUiModel(
                    id = "HTML & CSS".toStableLearningId(),
                    title = "HTML & CSS",
                    icon = LearningTopicIcon.Code.toImageVector(),
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_completed_recently,
                    action = RoadmapNodeAction.Review
                ),
                RoadmapNodeUiModel(
                    id = activeNodeTitle.toStableLearningId(),
                    title = activeNodeTitle,
                    icon = LearningTopicIcon.DataObject.toImageVector(),
                    status = RoadmapNodeStatus.InProgress,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_async_description,
                    action = RoadmapNodeAction.Continue
                ),
                RoadmapNodeUiModel(
                    id = lockedNodeTitle.toStableLearningId(),
                    title = lockedNodeTitle,
                    icon = LearningTopicIcon.Code.toImageVector(),
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_unlock_by_completing,
                    descriptionArgs = listOf(activeNodeTitle)
                ),
                RoadmapNodeUiModel(
                    id = "CSS Animation".toStableLearningId(),
                    title = "CSS Animation",
                    icon = LearningTopicIcon.Devices.toImageVector(),
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Optional,
                    descriptionResId = R.string.roadmap_detail_css_animation_description
                )
            )
        ),
        RoadmapGroupUiModel(
            id = "core-web-fundamentals-completed",
            title = coreTitle,
            completedRequiredNodes = 3,
            totalRequiredNodes = 3,
            progressFraction = 1f,
            state = RoadmapGroupState.Completed,
            nodes = listOf(
                RoadmapNodeUiModel(
                    id = "HTML & CSS".toStableLearningId(),
                    title = "HTML & CSS",
                    icon = LearningTopicIcon.Code.toImageVector(),
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_completed_recently
                ),
                RoadmapNodeUiModel(
                    id = activeNodeTitle.toStableLearningId(),
                    title = activeNodeTitle,
                    icon = LearningTopicIcon.DataObject.toImageVector(),
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_completed_recently
                ),
                RoadmapNodeUiModel(
                    id = lockedNodeTitle.toStableLearningId(),
                    title = lockedNodeTitle,
                    icon = LearningTopicIcon.Code.toImageVector(),
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_completed_recently
                ),
                RoadmapNodeUiModel(
                    id = "CSS Animation".toStableLearningId(),
                    title = "CSS Animation",
                    icon = LearningTopicIcon.Devices.toImageVector(),
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Optional,
                    descriptionResId = R.string.roadmap_detail_css_animation_description
                )
            )
        ),
        RoadmapGroupUiModel(
            id = "framework-ecosystem-locked",
            title = frameworkTitle,
            completedRequiredNodes = 0,
            totalRequiredNodes = 1,
            progressFraction = 0f,
            state = RoadmapGroupState.Locked,
            lockedDescriptionResId = R.string.roadmap_detail_locked_group_description,
            lockedDescriptionArgs = listOf(coreTitle, "React Fundamentals"),
            lockedExpandedDescriptionResId = R.string.roadmap_detail_framework_ecosystem_description
        )
    )
}

private fun buildRoadmapDetailMilestones(): List<RoadmapMilestoneUiModel> {
    return listOf(
        RoadmapMilestoneUiModel(
            id = "first-landing-page",
            titleResId = R.string.roadmap_detail_milestone_landing_title,
            descriptionResId = R.string.roadmap_detail_milestone_landing_description,
            state = RoadmapMilestoneState.Available
        ),
        RoadmapMilestoneUiModel(
            id = "first-landing-page-locked",
            titleResId = R.string.roadmap_detail_milestone_landing_title,
            descriptionResId = R.string.roadmap_detail_milestone_landing_description,
            state = RoadmapMilestoneState.Locked
        )
    )
}
