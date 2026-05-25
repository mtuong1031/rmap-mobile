package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.rmap.mobile.core.ui.theme.AppTextStyles

enum class BookmarkCategoryStyle {
    WebDevelopment,
    Design,
    DevOps
}

@Immutable
data class BookmarkCategoryColors(
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun BookmarkCategoryStyle.colors(): BookmarkCategoryColors {
    return when (this) {
        BookmarkCategoryStyle.WebDevelopment -> BookmarkCategoryColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        BookmarkCategoryStyle.Design -> BookmarkCategoryColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiary
        )

        BookmarkCategoryStyle.DevOps -> BookmarkCategoryColors(
            containerColor = Color(0xFFFFF7ED),
            contentColor = Color(0xFFF54900)
        )
    }
}

object BookmarkTextStyles {
    val tabLabel: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

    val filterChipLabel: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

    val badge: TextStyle
        @Composable get() = AppTextStyles.badgeSmall.copy(
            textAlign = TextAlign.Center
        )

    val cardTitle: TextStyle
        @Composable get() = AppTextStyles.compactCardTitle.copy(
            color = MaterialTheme.colorScheme.onSurface
        )

    val metadata: TextStyle
        @Composable get() = AppTextStyles.metadata

    val progressLabel: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold
        )

    val emptyTitle: TextStyle
        @Composable get() = AppTextStyles.sectionTitle.copy(
            textAlign = TextAlign.Center
        )

    val emptyBody: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center
        )

    val emptyDomainEyebrow: TextStyle
        @Composable get() = AppTextStyles.eyebrow.copy(
            textAlign = TextAlign.Center
        )

    val domainChip: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
}
