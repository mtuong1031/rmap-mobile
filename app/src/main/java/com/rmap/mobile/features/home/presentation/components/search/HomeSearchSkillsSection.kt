package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchSkillsSection(
    title: String,
    skills: List<HomeSearchSkillItemUiModel>,
    seeMoreText: String,
    resultCountText: String,
    canSeeMore: Boolean,
    isLoadingMore: Boolean,
    onSkillClick: (HomeSearchSkillItemUiModel) -> Unit,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HomeSearchSectionHeader(
            title = title,
            resultCountText = resultCountText
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            skills.forEach { item ->
                HomeSearchSkillCard(
                    item = item,
                    onClick = { onSkillClick(item) }
                )
            }

            if (canSeeMore) {
                RMapButton(
                    text = seeMoreText,
                    onClick = onSeeMoreClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = RMapButtonVariant.Neutral,
                    size = RMapButtonSize.Medium,
                    enabled = !isLoadingMore
                )
            }
        }
    }
}

@Composable
private fun HomeSearchSkillCard(
    item: HomeSearchSkillItemUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cardShadow(shape = AppShapes.searchBar)
            .clip(AppShapes.searchBar)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = Color(0x80F9FAFB),
                shape = AppShapes.searchBar
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(Dimens.spacingLgPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = item.parentText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }

        Text(
            text = item.statusText,
            maxLines = 1,
            modifier = Modifier
                .clip(AppShapes.small)
                .background(item.statusStyle.containerColor)
                .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = item.statusStyle.contentColor
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun HomeSearchSkillsSectionPreview() {
    RMapTheme {
        HomeSearchSkillsSection(
            title = "Skills",
            skills = listOf(
                HomeSearchSkillItemUiModel(
                    id = "frontend-react",
                    title = "Frontend Super Star",
                    parentText = "Part of: React Fundamentals",
                    statusText = "Not started",
                    statusStyle = HomeSearchSkillStatusDefaults.notStartedStyle()
                ),
                HomeSearchSkillItemUiModel(
                    id = "frontend-pro",
                    title = "Frontend",
                    parentText = "Part of: Frontend Pro",
                    statusText = "In progress",
                    statusStyle = HomeSearchSkillStatusDefaults.inProgressStyle()
                )
            ),
            seeMoreText = "SEE MORE",
            resultCountText = "2 skills founds",
            canSeeMore = true,
            isLoadingMore = false,
            onSkillClick = {},
            onSeeMoreClick = {},
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
