package com.rmap.mobile.features.roadmap.presentation.components.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmber
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBorder
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberDark
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberText
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapInk
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneIconBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneLockedBorder
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneSoftBg
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapSearchCard
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel

@Composable
fun RoadmapMilestoneCard(
    milestone: RoadmapMilestoneUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = milestone.state == RoadmapMilestoneState.Locked
    val isDarkTheme = isSystemInDarkTheme()
    val colors = if (isDarkTheme) DarkMilestoneColors else LightMilestoneColors

    RoadmapDecoratedCard(
        modifier = modifier,
        borderColor = colors.border,
        useHeroBackground = true,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                colors.gradientStart,
                colors.gradientEnd
            )
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = colors.decorativeIcon,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.spacingSm)
                .size(MilestoneDecorIconSize)
                .rotate(12f)
        )

        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RoadmapPill(
                text = stringResource(R.string.roadmap_detail_milestone_label),
                containerColor = colors.pillContainer,
                contentColor = colors.accent,
                borderColor = colors.border,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(Dimens.iconXxs)
                    )
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = colors.title,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.body),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            RMapButton(
                text = stringResource(
                    if (isLocked) R.string.roadmap_detail_locked else R.string.roadmap_detail_action_view_project
                ),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Small,
                leadingIcon = if (isLocked) {
                    {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(RMapButtonSize.Small.iconSize)
                        )
                    }
                } else {
                    null
                },
                trailingIcon = if (!isLocked) {
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colors.body,
                            modifier = Modifier.size(RMapButtonSize.Small.iconSize)
                        )
                    }
                } else {
                    null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.buttonContainer,
                    contentColor = colors.body,
                    disabledContainerColor = colors.disabledButtonContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.cardElevationNone),
                border = if (isLocked) BorderStroke(Dimens.borderThin, colors.lockedBorder) else null,
                enabled = !isLocked
            )
        }
    }
}

@Composable
fun RoadmapMilestoneCompactCard(
    milestone: RoadmapMilestoneUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(Dimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconXxl)
                .background(roadmapMilestoneIconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = roadmapAmber,
                modifier = Modifier.size(Dimens.iconXs)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
        ) {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else roadmapInk,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.roadmap_search_recent_milestone_project),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDarkTheme) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        OnSurfacePlaceholderLight
                    }
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val MilestoneDecorIconSize = Dimens.iconFrameSize + Dimens.spacingMd

private data class MilestoneColors(
    val gradientStart: Color,
    val gradientEnd: Color,
    val border: Color,
    val pillContainer: Color,
    val accent: Color,
    val title: Color,
    val body: Color,
    val decorativeIcon: Color,
    val buttonContainer: Color,
    val disabledButtonContainer: Color,
    val lockedBorder: Color
)

private val LightMilestoneColors = MilestoneColors(
    gradientStart = roadmapMilestoneSoftBg.copy(alpha = 0.96f),
    gradientEnd = roadmapAmberBg.copy(alpha = 0.98f),
    border = roadmapAmberBorder,
    pillContainer = roadmapAmberBg,
    accent = roadmapAmber,
    title = roadmapAmberDark,
    body = roadmapAmberText,
    decorativeIcon = roadmapAmber.copy(alpha = 0.18f),
    buttonContainer = Color.White.copy(alpha = 0.68f),
    disabledButtonContainer = Color.White.copy(alpha = 0.86f),
    lockedBorder = roadmapMilestoneLockedBorder
)

private val DarkMilestoneColors = MilestoneColors(
    gradientStart = Color(0xFF35290F),
    gradientEnd = Color(0xFF292314),
    border = Color(0xFF80621E),
    pillContainer = Color(0xFF49370F),
    accent = Color(0xFFFBBF24),
    title = Color(0xFFFDE68A),
    body = Color(0xFFF5C75B),
    decorativeIcon = Color(0xFFFBBF24).copy(alpha = 0.16f),
    buttonContainer = Color(0xFF3B321E),
    disabledButtonContainer = Color(0xFF302C23),
    lockedBorder = Color(0xFF514936)
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCardAvailablePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapMilestoneCard(
                milestone = RoadmapMilestoneUiModel(
                    id = "landing-page",
                    title = "Build your first landing page",
                    description = "Apply HTML, CSS, and JavaScript basics in a portfolio-ready project.",
                    state = RoadmapMilestoneState.Available
                ),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCompactCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapSearchCard {
                RoadmapMilestoneCompactCard(
                    milestone = RoadmapMilestoneUiModel(
                        id = "landing-page-compact",
                        title = "Build your first landing page",
                        description = "Apply HTML, CSS, and JavaScript basics in a portfolio-ready project.",
                        state = RoadmapMilestoneState.Available
                    ),
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCardLockedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapMilestoneCard(
                milestone = RoadmapMilestoneUiModel(
                    id = "landing-page-locked",
                    title = "Build your first landing page",
                    description = "Apply HTML, CSS, and JavaScript basics in a portfolio-ready project.",
                    state = RoadmapMilestoneState.Locked
                ),
                onClick = {}
            )
        }
    }
}
