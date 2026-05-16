package com.rmap.mobile.features.roadmap.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCard
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.BackgroundLight
import com.rmap.mobile.core.ui.theme.CardDividerColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralDisabledColor
import com.rmap.mobile.core.ui.theme.NeutralSoftSurfaceColor
import com.rmap.mobile.core.ui.theme.NeutralTextMutedColor
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.StatusCompletedContentColor

enum class ModuleStatus {
    COMPLETED, IN_PROGRESS, LOCKED
}

data class SubLessonUiModel(
    val title: String,
    val status: ModuleStatus
)

data class ModuleCardUiModel(
    val title: String,
    val status: ModuleStatus,
    val progressPercent: Int = 0,
    val icon: ImageVector,
    val subLessons: List<SubLessonUiModel> = emptyList()
)

@Composable
fun ModuleCard(
    item: ModuleCardUiModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "Chevron Rotation")
    val interactionSource = remember { MutableInteractionSource() }

    val isLocked = item.status == ModuleStatus.LOCKED
    val cardBg = if (isLocked) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val titleColor = if (isLocked) NeutralDisabledColor else MaterialTheme.colorScheme.onSurface
    val iconBg = if (isLocked) NeutralSoftSurfaceColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val iconTint = if (isLocked) NeutralDisabledColor else MaterialTheme.colorScheme.primary

    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        color = cardBg,
        border = AppCardDefaults.border(
            color = if (isLocked) AppCardDefaults.borderColor.copy(alpha = 0.5f) else AppCardDefaults.borderColor
        ),
        shadowElevation = if (isLocked) Dimens.cardElevationNone else AppCardDefaults.shadowElevation
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { if (!isLocked) isExpanded = !isExpanded }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Box
                    Box(
                        modifier = Modifier
                            .size(Dimens.controlLg)
                            .background(iconBg, AppShapes.button)
                            .border(
                                width = Dimens.borderThin,
                                color = if (isLocked) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = AppShapes.button
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(Dimens.iconLg)
                        )
                    }

                    // Texts
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = titleColor
                            )
                        )

                        val statusText = when (item.status) {
                            ModuleStatus.COMPLETED -> stringResource(R.string.roadmap_detail_completed, 100)
                            ModuleStatus.IN_PROGRESS -> stringResource(R.string.roadmap_detail_in_progress, item.progressPercent)
                            ModuleStatus.LOCKED -> stringResource(R.string.roadmap_detail_locked)
                        }

                        val statusColor = when (item.status) {
                            ModuleStatus.COMPLETED -> StatusCompletedContentColor
                            ModuleStatus.IN_PROGRESS -> NeutralTextMutedColor
                            ModuleStatus.LOCKED -> NeutralDisabledColor
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = statusColor
                            )
                        )
                    }
                }

                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeutralDisabledColor,
                        modifier = Modifier.size(Dimens.iconLg)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = NeutralDisabledColor,
                        modifier = Modifier
                            .size(Dimens.iconLg)
                            .rotate(rotation)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded && !isLocked) {
                Column(
                    modifier = Modifier.padding(top = Dimens.spacingLg, start = Dimens.spacingXsPlus)
                ) {
                    item.subLessons.forEachIndexed { index, lesson ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timeline connection
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(Dimens.iconMd)
                            ) {
                                if (index > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(Dimens.spacingXxs)
                                            .height(Dimens.spacingLg)
                                            .background(BackgroundLight)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(Dimens.spacingLg))
                                }

                                // Status Indicator
                                Box(contentAlignment = Alignment.Center) {
                                    when (lesson.status) {
                                        ModuleStatus.COMPLETED -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(Dimens.iconMd)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.onPrimary,
                                                        modifier = Modifier.size(Dimens.iconXxs)
                                                    )
                                            }
                                        }
                                        ModuleStatus.IN_PROGRESS -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(Dimens.iconMd)
                                                    .clip(CircleShape)
                                                    .border(Dimens.borderProgress, MaterialTheme.colorScheme.primary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(Dimens.spacingSmPlus)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary)
                                                )
                                            }
                                        }
                                        ModuleStatus.LOCKED -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(Dimens.iconMd)
                                                    .clip(CircleShape)
                                                    .background(CardDividerColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = NeutralDisabledColor,
                                                    modifier = Modifier.size(Dimens.spacingSmPlus)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (index < item.subLessons.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(Dimens.spacingXxs)
                                            .height(Dimens.spacingLg)
                                            .background(BackgroundLight)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(Dimens.spacingLg))
                                }
                            }

                            Spacer(modifier = Modifier.width(Dimens.spacingLg))

                            Text(
                                text = lesson.title,
                                style = AppTextStyles.badgeSmall.copy(
                                    fontWeight = if (lesson.status == ModuleStatus.IN_PROGRESS) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (lesson.status == ModuleStatus.LOCKED) NeutralDisabledColor else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ModuleCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(modifier = Modifier.padding(Dimens.spacingLg)) {
            ModuleCard(
                item = ModuleCardUiModel(
                    title = "JavaScript Basics",
                    status = ModuleStatus.IN_PROGRESS,
                    progressPercent = 45,
                    icon = Icons.Outlined.Code,
                    subLessons = listOf(
                        SubLessonUiModel("ES6+ Syntax", ModuleStatus.COMPLETED),
                        SubLessonUiModel("Asynchronous JS", ModuleStatus.IN_PROGRESS),
                        SubLessonUiModel("DOM Manipulation", ModuleStatus.LOCKED)
                    )
                )
            )
        }
    }
}
