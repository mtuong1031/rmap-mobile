package com.rmap.mobile.features.profile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Immutable
data class ProfileRoadmapProgressUiModel(
    val title: String,
    val remainingTime: String,
    val progressPercent: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val accentContainerColor: Color
)

@Immutable
data class ProfileManagedRoadmapUiModel(
    val title: String,
    val description: String,
    val progressPercent: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val accentContainerColor: Color
)

@Immutable
data class ProfileAchievementUiModel(
    val title: String,
    val status: String,
    val completedAt: String,
    val icon: ImageVector,
    val brush: Brush
)

enum class ProfileAchievementTab {
    Roadmaps,
    Skills
}

@Immutable
data class ProfileActivityDayUiModel(
    val label: String,
    val isComplete: Boolean
)

@Composable
fun ActiveRoadmapsCard(
    title: String,
    subtitle: String,
    manageLabel: String,
    collapseLabel: String,
    items: List<ProfileRoadmapProgressUiModel>,
    managedRoadmaps: List<ProfileManagedRoadmapUiModel>,
    isManagedRoadmapsVisible: Boolean,
    onManageRoadmapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileSectionCard(modifier = modifier) {
        ProfileSectionHeader(title = title, subtitle = subtitle)

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)) {
            items.forEach { item ->
                RoadmapProgressRow(item = item)
            }

            AnimatedVisibility(
                visible = isManagedRoadmapsVisible && managedRoadmaps.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)) {
                    managedRoadmaps.forEach { roadmap ->
                        ManagedRoadmapRow(item = roadmap)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.chip)
                .clickable(onClick = onManageRoadmapsClick)
                .padding(vertical = Dimens.spacingSm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isManagedRoadmapsVisible) collapseLabel else manageLabel,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.iconXs)
            )
        }
    }
}

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
fun WeeklyActivityCard(
    title: String,
    subtitle: String,
    bestLabel: String,
    days: List<ProfileActivityDayUiModel>,
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
                text = bestLabel,
                modifier = Modifier
                    .clip(AppShapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                ActivityDay(day = day)
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)),
        shadowElevation = Dimens.cardElevationSm,
        shadowColor = Color(0x10298CF7)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
            content = content
        )
    }
}

@Composable
private fun ProfileSectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        )
    }
}

@Composable
private fun RoadmapProgressRow(item: ProfileRoadmapProgressUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.controlSm)
                    .clip(AppShapes.iconContainerLarge)
                    .background(item.accentContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.accentColor,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = stringResource(id = R.string.profile_progress_percent_short, item.progressPercent),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = item.accentColor,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }
                Text(
                    text = item.remainingTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }
        }

        LinearProfileProgress(progress = item.progressPercent / 100f, color = item.accentColor)
    }
}

@Composable
private fun LinearProfileProgress(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(AppShapes.pill)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(AppShapes.pill)
                .background(
                    Brush.horizontalGradient(
                        listOf(color, color.copy(alpha = 0.75f))
                    )
                )
        )
    }
}

@Composable
private fun ManagedRoadmapRow(item: ProfileManagedRoadmapUiModel) {
    RoadmapProgressRow(
        item = ProfileRoadmapProgressUiModel(
            title = item.title,
            remainingTime = item.description,
            progressPercent = item.progressPercent,
            icon = item.icon,
            accentColor = item.accentColor,
            accentContainerColor = item.accentContainerColor
        )
    )
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

@Composable
private fun ActivityDay(day: ProfileActivityDayUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (day.isComplete) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.isComplete) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            }
        }

        Text(
            text = day.label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (day.isComplete) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ActiveRoadmapsCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ActiveRoadmapsCard(
            title = "Active Roadmaps",
            subtitle = "2 active paths",
            manageLabel = "Manage roadmaps",
            collapseLabel = "Show less",
            items = listOf(
                ProfileRoadmapProgressUiModel(
                    title = "Frontend Fresher",
                    remainingTime = "4 months left",
                    progressPercent = 75,
                    icon = Icons.Outlined.TrackChanges,
                    accentColor = Color(0xFF2B7FFF),
                    accentContainerColor = Color(0xFFF0F5FE)
                ),
                ProfileRoadmapProgressUiModel(
                    title = "UI/UX Master",
                    remainingTime = "2 months left",
                    progressPercent = 32,
                    icon = Icons.Outlined.Palette,
                    accentColor = Color(0xFF8B5CF6),
                    accentContainerColor = Color(0xFFF3F0FF)
                )
            ),
            managedRoadmaps = listOf(
                ProfileManagedRoadmapUiModel(
                    title = "Full Stack Development",
                    description = "Career path • 5 months left",
                    progressPercent = 48,
                    icon = Icons.Outlined.TrackChanges,
                    accentColor = Color(0xFF10B981),
                    accentContainerColor = Color(0xFFECFDF5)
                ),
                ProfileManagedRoadmapUiModel(
                    title = "Mobile App Development",
                    description = "Career path • 6 months left",
                    progressPercent = 18,
                    icon = Icons.Outlined.TrackChanges,
                    accentColor = Color(0xFFFB923C),
                    accentContainerColor = Color(0xFFFFF7ED)
                )
            ),
            isManagedRoadmapsVisible = true,
            onManageRoadmapsClick = {},
            modifier = Modifier.padding(Dimens.spacingXl)
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

internal fun defaultAchievementBrushes(): List<Brush> {
    return listOf(
        Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFF59E0B))),
        Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
    )
}

internal fun defaultProfileRoadmapIcons(): List<ImageVector> {
    return listOf(Icons.Outlined.TrackChanges, Icons.Outlined.Palette)
}

internal fun defaultAchievementIcons(): List<ImageVector> {
    return listOf(Icons.Outlined.EmojiEvents, Icons.Outlined.Code)
}
