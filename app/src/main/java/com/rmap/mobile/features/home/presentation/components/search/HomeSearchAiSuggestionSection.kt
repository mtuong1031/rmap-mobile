package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchAiSuggestionSection(
    title: String,
    suggestion: HomeSearchAiSuggestionUiModel,
    onCreateWithAiClick: (HomeSearchAiSuggestionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HomeSearchSectionHeader(title = title)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .cardShadow(shape = HomeSearchAiSuggestionShape)
                .clip(HomeSearchAiSuggestionShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(
                    width = Dimens.borderThin,
                    color = Color(0xFFDBEAFE),
                    shape = HomeSearchAiSuggestionShape
                )
                .padding(Dimens.spacingXl)
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = Dimens.spacingMd, y = (-Dimens.spacingSm))
                    .size(80.dp)
                    .graphicsLayer(alpha = 0.10f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = suggestion.description,
                    modifier = Modifier.fillMaxWidth(0.86f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )

                Row(
                    modifier = Modifier
                        .padding(top = Dimens.spacingSm)
                        .clip(AppShapes.chip)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = { onCreateWithAiClick(suggestion) }
                        )
                        .padding(horizontal = Dimens.spacingMdPlus, vertical = Dimens.spacingSm),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(Dimens.iconXs)
                    )

                    Text(
                        text = suggestion.actionText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

private val HomeSearchAiSuggestionShape = AppShapes.card

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun HomeSearchAiSuggestionSectionPreview() {
    RMapTheme {
        HomeSearchAiSuggestionSection(
            title = "AI suggestion",
            suggestion = HomeSearchAiSuggestionUiModel(
                id = "react-roadmap",
                title = "Create a personalized React roadmap",
                description = "Generate a roadmap based on your goal, current skills, and timeline.",
                actionText = "Create with AI"
            ),
            onCreateWithAiClick = {},
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
