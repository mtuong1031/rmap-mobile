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
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.RoadmapDifficulty
import com.rmap.mobile.core.ui.theme.DifficultyExpertContentColor
import com.rmap.mobile.core.ui.theme.ExploreBlueContainerColor
import com.rmap.mobile.core.ui.theme.ExploreGreenContainerColor
import com.rmap.mobile.core.ui.theme.ExplorePurpleContainerColor
import com.rmap.mobile.core.ui.theme.ExploreRoseContainerColor
import com.rmap.mobile.core.ui.theme.NeutralSoftSurfaceColor
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.RecommendedCardUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.presentation.components.ModuleCardUiModel
import com.rmap.mobile.features.roadmap.presentation.components.ModuleStatus
import com.rmap.mobile.features.roadmap.presentation.components.SubLessonUiModel

fun RoadmapSummary.toRoadmapCardUiModel(): RoadmapCardUiModel {
    return RoadmapCardUiModel(
        id = id,
        title = title,
        totalLessonsCount = totalLessonsCount,
        difficultyLabel = difficulty.toLabel(),
        difficulty = difficulty.toRoadmapDifficulty(),
        durationLabel = durationLabel,
        icon = icon.toImageVector()
    )
}

fun RoadmapCategory.toCategoryUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        icon = icon.toImageVector(),
        backgroundColor = icon.toCategoryBackgroundColor()
    )
}

fun RoadmapSummary.toRecommendedCardUiModel(): RecommendedCardUiModel {
    return RecommendedCardUiModel(
        id = id,
        title = title,
        badgeText = recommendationBadge.orEmpty(),
        skillNodesCount = skillNodesCount,
        level = difficulty.toLabel(),
        coverImageUrl = "",
        accentColor = toRecommendedAccentColor()
    )
}

fun RoadmapDetail.toRoadmapDetailUiState(): RoadmapDetailUiState {
    return RoadmapDetailUiState(
        roadmapId = id,
        title = title,
        progressFraction = progressFraction,
        completedLessons = completedLessons,
        totalLessons = totalLessons,
        sections = sections.map { section ->
            RoadmapModuleSectionUiModel(
                title = section.title,
                modules = section.modules.map(LearningModule::toModuleCardUiModel)
            )
        },
        aiTip = aiTip?.let {
            AiScholarTipUiModel(
                currentModule = it.currentModule,
                recommendedTopic = it.recommendedTopic,
                nextModule = it.nextModule
            )
        },
        isLoading = false,
        errorMessage = null
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
        LearningTopicIcon.Palette -> Icons.Outlined.Palette
        LearningTopicIcon.Science -> Icons.Outlined.Science
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
        LearningTopicIcon.Code -> ExploreBlueContainerColor
        LearningTopicIcon.Devices -> ExploreRoseContainerColor
        LearningTopicIcon.SmartToy -> ExplorePurpleContainerColor
        LearningTopicIcon.Terminal -> ExploreGreenContainerColor
        else -> NeutralSoftSurfaceColor
    }
}

private fun RoadmapSummary.toRecommendedAccentColor(): Color {
    return when (icon) {
        LearningTopicIcon.SmartToy -> DifficultyExpertContentColor
        else -> PrimaryLight
    }
}

private fun LearningModule.toModuleCardUiModel(): ModuleCardUiModel {
    return ModuleCardUiModel(
        title = title,
        status = status.toModuleStatus(),
        progressPercent = progressPercent,
        icon = icon.toImageVector(),
        subLessons = subLessons.map(SubLesson::toSubLessonUiModel)
    )
}

private fun SubLesson.toSubLessonUiModel(): SubLessonUiModel {
    return SubLessonUiModel(title = title, status = status.toModuleStatus())
}

fun LearningStatus.toModuleStatus(): ModuleStatus {
    return when (this) {
        LearningStatus.Completed -> ModuleStatus.COMPLETED
        LearningStatus.InProgress -> ModuleStatus.IN_PROGRESS
        LearningStatus.Locked -> ModuleStatus.LOCKED
        LearningStatus.NotStarted -> ModuleStatus.LOCKED
    }
}
