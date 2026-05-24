package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnPrimaryLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

private val SkillCardShape = RMapCardDefaults.shape
private val SkillIconFrameShape = AppShapes.card
private val SkillIconContainerShape = AppShapes.iconFrameInner

enum class SkillStatus(
    val backgroundColor: Color,
    val textColor: Color
) {
    IN_PROGRESS(
        backgroundColor = PrimaryLight,
        textColor = OnPrimaryLight
    ),
    NOT_STARTED(
        backgroundColor = Color(0xFFF3F4F6),
        textColor = Color(0xFF6A7282)
    )
}

data class SkillCardUiModel(
    val title: String,
    val parentPathName: String,
    val status: SkillStatus,
    val statusLabel: String,
    val icon: ImageVector
)

@Composable
fun SkillCard(
    item: SkillCardUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    RMapCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = SkillCardShape,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLgPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkillIconFrame(icon = item.icon)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                    ) {
                        Text(
                            text = item.title,
                            style = AppTextStyles.compactCardTitle.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 2
                        )

                        PartOfText(parentPathName = item.parentPathName)
                    }
                }

                HorizontalDivider(
                    thickness = Dimens.borderThin,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = item.statusLabel,
                        status = item.status
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF99A1AF),
                        modifier = Modifier.size(Dimens.iconMdPlus)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = Dimens.cardGlowOffsetX, y = Dimens.cardGlowOffsetY)
                    .size(Dimens.cardGlowSize)
                    .blur(radius = Dimens.cardGlowBlur)
                    .background(
                        brush = Brush.radialGradient(
                        colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun SkillIconFrame(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(Dimens.iconFrameSize)
            .border(
                width = Dimens.borderMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = SkillIconFrameShape
            )
            .padding(Dimens.iconFramePadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    ),
                    shape = SkillIconContainerShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconXxl)
            )
        }
    }
}

@Composable
private fun PartOfText(parentPathName: String) {
    val partOfPrefix = stringResource(R.string.skill_part_of_format, "").trimEnd()
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6A7282)
            )
        ) {
            append("$partOfPrefix ")
        }
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append(parentPathName)
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1
    )
}

@Composable
private fun StatusBadge(
    text: String,
    status: SkillStatus
) {
    Box(
        modifier = Modifier
            .background(
                color = status.backgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = Dimens.spacingMdPlus, vertical = Dimens.spacingXsPlus),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTextStyles.badgeSmall.copy(
                color = status.textColor
            ),
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun SkillCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            SkillCard(
                item = SkillCardUiModel(
                    title = "Advanced CSS Layouts",
                    parentPathName = "Frontend Dev",
                    status = SkillStatus.IN_PROGRESS,
                    statusLabel = "In Progress",
                    icon = Icons.Outlined.Code
                )
            )
            SkillCard(
                item = SkillCardUiModel(
                    title = "NoSQL Data Modeling",
                    parentPathName = "Backend Systems",
                    status = SkillStatus.NOT_STARTED,
                    statusLabel = "Not Started",
                    icon = Icons.Outlined.DataObject
                )
            )
        }
    }
}

