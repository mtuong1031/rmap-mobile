package com.rmap.mobile.features.profile.presentation.components.progress

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class ProfileRoadmapProgressUiModel(
    val id: String,
    val title: String,
    val remainingTime: String,
    val progressPercent: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val accentContainerColor: Color
)
