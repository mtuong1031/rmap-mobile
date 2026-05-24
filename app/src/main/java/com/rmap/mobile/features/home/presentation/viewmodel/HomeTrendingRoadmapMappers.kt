package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardStyle
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector

fun RoadmapSummary.toTrendingRoadmapCardUiModel(rank: Int): TrendingRoadmapCardUiModel {
    return TrendingRoadmapCardUiModel(
        id = id,
        rankText = "#$rank",
        categoryLabel = toTrendingCategoryLabel(),
        title = title,
        metadataText = "$skillNodesCount nodes • $durationLabel",
        trendText = toTrendText(rank),
        leadingIcon = icon.toImageVector(),
        trendIcon = toTrendIcon(rank),
        style = toTrendingStyle()
    )
}

private fun RoadmapSummary.toTrendingCategoryLabel(): String {
    return when {
        icon == LearningTopicIcon.Palette -> "Design"
        id.contains("data", ignoreCase = true) || title.contains("Data", ignoreCase = true) -> "Data"
        categoryId.equals("devops", ignoreCase = true) -> "DevOps"
        categoryId.equals("ai", ignoreCase = true) -> "AI"
        categoryId.equals("frontend", ignoreCase = true) -> "Frontend"
        categoryId.equals("backend", ignoreCase = true) -> "Backend"
        else -> categoryId.replaceFirstChar { it.uppercase() }
    }
}

private fun RoadmapSummary.toTrendingStyle(): TrendingRoadmapCardStyle {
    return when {
        id.contains("data", ignoreCase = true) || title.contains("Data", ignoreCase = true) ->
            TrendingRoadmapCardDefaults.indigoStyle()
        categoryId.equals("devops", ignoreCase = true) ->
            TrendingRoadmapCardDefaults.neutralStyle()
        else ->
            TrendingRoadmapCardDefaults.primaryStyle()
    }
}

private fun RoadmapSummary.toTrendText(rank: Int): String {
    return when {
        rank == 2 -> "2.4k learners"
        id.contains("data", ignoreCase = true) || title.contains("Data", ignoreCase = true) -> "Trending globally"
        else -> "Popular this week"
    }
}

private fun toTrendIcon(rank: Int): ImageVector {
    return if (rank == 2) {
        Icons.Outlined.Groups
    } else {
        Icons.AutoMirrored.Outlined.TrendingUp
    }
}
