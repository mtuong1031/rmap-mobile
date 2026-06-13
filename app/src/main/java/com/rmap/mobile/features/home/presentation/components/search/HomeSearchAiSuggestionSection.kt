package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeSearchAiSuggestionShape = AppShapes.card
private val HomeSearchAiSuggestionDecorativeIconSize = 96.dp

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
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.16f),
                    shape = HomeSearchAiSuggestionShape
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = Dimens.spacingXl, y = (-26).dp)
                    .size(HomeSearchAiSuggestionDecorativeIconSize)
                    .alpha(0.2f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Text(
                    text = suggestion.description,
                    modifier = Modifier.fillMaxWidth(0.86f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                )

                RMapButton(
                    text = suggestion.actionText,
                    onClick = { onCreateWithAiClick(suggestion) },
                    variant = RMapButtonVariant.Primary,
                    size = RMapButtonSize.XSmall,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.padding(top = Dimens.spacingSm)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun HomeSearchAiSuggestionSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
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

@Preview(showBackground = true, backgroundColor = 0xFF15151B)
@Composable
private fun HomeSearchAiSuggestionSectionDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
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
