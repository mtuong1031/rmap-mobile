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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

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
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        if (compact && onBackClick != null) {
            CompactAiRoadmapHeaderBar(
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

            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = description,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "AI Step Header", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapStepHeaderPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapStepHeader(
            eyebrow = "AI Roadmap",
            title = "Create a personalized roadmap",
            description = "Tell RMap your goal, timeline, and learning pace."
        )
    }
}

@Preview(showBackground = true, name = "AI Step Header - Back", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapStepHeaderCompactPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapStepHeader(
            eyebrow = "AI Roadmap",
            title = "Create roadmap",
            description = "Set the goal, deadline, and daily pace.",
            compact = true,
            backContentDescription = "Back",
            onBackClick = {}
        )
    }
}

@Composable
private fun CompactAiRoadmapHeaderBar(
    backContentDescription: String?,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.controlXl),
        horizontalArrangement = Arrangement.Start,
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
                modifier = Modifier.size(Dimens.iconLg)
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
