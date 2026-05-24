package com.rmap.mobile.features.profile.presentation.components.progress

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class ProfileRoadmapProgressUiModel(
    val title: String,
    val remainingTime: String,
    val progressPercent: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val accentContainerColor: Color
)

@Immutable
data class ProfileManagedRoadmapUiModel(
    val title: String,
    val description: String,
    val progressPercent: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val accentContainerColor: Color
)
