package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

@Composable
fun AiRoadmapStepHeader(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    backContentDescription: String? = null,
    onBackClick: (() -> Unit)? = null,
    compact: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            if (compact) Dimens.spacingSm else Dimens.spacingMd
        )
    ) {
        if (compact && onBackClick != null) {
            CompactAiRoadmapHeaderBar(
                eyebrow = eyebrow,
                backContentDescription = backContentDescription,
                onBackClick = onBackClick
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AiRoadmapSparkIcon()

                Text(
                    text = eyebrow,
                    modifier = Modifier
                        .clip(AppShapes.pill)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style = if (compact) {
                MaterialTheme.typography.headlineSmall
            } else {
                MaterialTheme.typography.headlineMedium
            }.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = description,
            modifier = Modifier.fillMaxWidth(),
            style = if (compact) {
                MaterialTheme.typography.bodyMedium
            } else {
                MaterialTheme.typography.bodyLarge
            }.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun CompactAiRoadmapHeaderBar(
    eyebrow: String,
    backContentDescription: String?,
    onBackClick: () -> Unit
) {
    Surface(
        shape = AppShapes.pill,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.cardElevationNone,
        shadowElevation = Dimens.cardElevationNone
    ) {
        Row(
            modifier = Modifier.padding(
                start = Dimens.spacingXs,
                top = Dimens.spacingXs,
                end = Dimens.spacingMd,
                bottom = Dimens.spacingXs
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(Dimens.controlSm)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(Dimens.iconLg)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )

            AiRoadmapSparkIcon()

            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun AiRoadmapSparkIcon() {
    Box(
        modifier = Modifier
            .size(Dimens.aiIconContainerSize)
            .clip(AppShapes.iconContainer)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconMd)
        )
    }
}
