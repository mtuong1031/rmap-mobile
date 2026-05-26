package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus

@Composable
fun AiRoadmapGenerationContent(
    status: AiRoadmapGenerationStatus,
    title: String,
    body: String,
    progressText: String,
    exploreText: String,
    cancelText: String,
    onExploreClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.largeCard,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = status.stageLabel.ifBlank { progressText },
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                LinearProgressIndicator(
                    progress = { status.progressPercent.toFloat().coerceIn(0f, 100f) / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                RMapButton(
                    text = exploreText,
                    onClick = onExploreClick,
                    modifier = Modifier.weight(1f),
                    variant = RMapButtonVariant.Secondary,
                    size = RMapButtonSize.Medium
                )
                RMapButton(
                    text = cancelText,
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    variant = RMapButtonVariant.Outline,
                    size = RMapButtonSize.Medium
                )
            }
        }
    }
}
