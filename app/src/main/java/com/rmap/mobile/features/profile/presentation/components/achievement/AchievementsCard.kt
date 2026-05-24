package com.rmap.mobile.features.profile.presentation.components.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionCard
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionHeader

@Composable
fun AchievementsCard(
    title: String,
    subtitle: String,
    seeAllLabel: String,
    roadmapsTabLabel: String,
    skillsTabLabel: String,
    selectedTab: ProfileAchievementTab,
    achievements: List<ProfileAchievementUiModel>,
    onTabSelected: (ProfileAchievementTab) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileSectionCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ProfileSectionHeader(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
            Text(
                text = seeAllLabel,
                modifier = Modifier
                    .clip(AppShapes.chip)
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = Dimens.spacingXs, vertical = Dimens.spacingXs),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        ProfileSegmentedTabs(
            selectedLabel = roadmapsTabLabel,
            unselectedLabel = skillsTabLabel,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            achievements.forEach { achievement ->
                AchievementRow(item = achievement)
            }
        }
    }
}

@Composable
private fun ProfileSegmentedTabs(
    selectedLabel: String,
    unselectedLabel: String,
    selectedTab: ProfileAchievementTab,
    onTabSelected: (ProfileAchievementTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.iconContainerLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.iconContainerLarge)
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
    ) {
        ProfileSegmentedTab(
            label = selectedLabel,
            isSelected = selectedTab == ProfileAchievementTab.Roadmaps,
            onClick = { onTabSelected(ProfileAchievementTab.Roadmaps) },
            modifier = Modifier.weight(1f)
        )
        ProfileSegmentedTab(
            label = unselectedLabel,
            isSelected = selectedTab == ProfileAchievementTab.Skills,
            onClick = { onTabSelected(ProfileAchievementTab.Skills) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileSegmentedTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(AppShapes.chip)
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
private fun AchievementRow(item: ProfileAchievementUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.button)
            .padding(Dimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.controlLg)
                .clip(AppShapes.iconContainerLarge)
                .background(item.brush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(Dimens.iconLg)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )
            Text(
                text = item.completedAt,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                ),
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(Dimens.iconSm)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AchievementsCardPreview() {
    val icons = defaultAchievementIcons()
    val brushes = defaultAchievementBrushes()
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AchievementsCard(
            title = "Achievements",
            subtitle = "Completed roadmaps and skills",
            seeAllLabel = "See All",
            roadmapsTabLabel = "Roadmaps",
            skillsTabLabel = "Skills",
            selectedTab = ProfileAchievementTab.Roadmaps,
            achievements = listOf(
                ProfileAchievementUiModel(
                    title = "Frontend Starter",
                    status = "Roadmap completed",
                    completedAt = "Completed 2 days ago",
                    icon = icons[0],
                    brush = brushes[0]
                ),
                ProfileAchievementUiModel(
                    title = "Web Design Basics",
                    status = "Roadmap completed",
                    completedAt = "Completed 1 month ago",
                    icon = icons[1],
                    brush = brushes[1]
                )
            ),
            onTabSelected = {},
            onSeeAllClick = {},
            modifier = Modifier.padding(Dimens.spacingXl)
        )
    }
}

