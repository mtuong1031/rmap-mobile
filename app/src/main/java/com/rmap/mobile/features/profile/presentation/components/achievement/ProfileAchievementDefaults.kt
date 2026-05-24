package com.rmap.mobile.features.profile.presentation.components.achievement

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal fun defaultAchievementBrushes(): List<Brush> {
    return listOf(
        Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFF59E0B))),
        Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
    )
}

internal fun defaultAchievementIcons(): List<ImageVector> {
    return listOf(Icons.Outlined.EmojiEvents, Icons.Outlined.Code)
}
