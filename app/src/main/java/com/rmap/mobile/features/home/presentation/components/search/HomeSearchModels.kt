package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight

@Immutable
data class HomeSearchRoadmapItemUiModel(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val metadataText: String,
    val style: HomeSearchRoadmapItemStyle,
    val leadingIcon: ImageVector? = null,
    val leadingText: String? = null
)

@Immutable
data class HomeSearchRoadmapItemStyle(
    val iconContainerColor: Color,
    val iconContentColor: Color,
    val categoryColor: Color
)

object HomeSearchRoadmapItemDefaults {
    @Composable
    fun reactStyle(): HomeSearchRoadmapItemStyle {
        return HomeSearchRoadmapItemStyle(
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconContentColor = MaterialTheme.colorScheme.primary,
            categoryColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    @Composable
    fun starterStyle(): HomeSearchRoadmapItemStyle {
        return HomeSearchRoadmapItemStyle(
            iconContainerColor = Color(0xFFEEF2FF),
            iconContentColor = Color(0xFF615FFF),
            categoryColor = Color(0xFF4F39F6)
        )
    }
}

@Immutable
data class HomeSearchSkillItemUiModel(
    val id: String,
    val title: String,
    val parentText: String
)

@Immutable
data class HomeSearchSkillStatusStyle(
    val containerColor: Color,
    val contentColor: Color
)

object HomeSearchSkillStatusDefaults {
    @Composable
    fun notStartedStyle(): HomeSearchSkillStatusStyle {
        return HomeSearchSkillStatusStyle(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = OnSurfacePlaceholderLight
        )
    }

    @Composable
    fun inProgressStyle(): HomeSearchSkillStatusStyle {
        return HomeSearchSkillStatusStyle(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Immutable
data class HomeSearchAiSuggestionUiModel(
    val id: String,
    val title: String,
    val description: String,
    val actionText: String
)
