package com.rmap.mobile.features.profile.presentation.components.achievement

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class ProfileAchievementUiModel(
    val title: String,
    val status: String,
    val completedAt: String,
    val icon: ImageVector,
    val brush: Brush
)

enum class ProfileAchievementTab {
    Roadmaps,
    Skills
}

@Immutable
data class ProfileActivityDayUiModel(
    val label: String,
    val isComplete: Boolean
)
