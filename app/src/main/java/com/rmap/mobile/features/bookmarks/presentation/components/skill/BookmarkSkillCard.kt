package com.rmap.mobile.features.bookmarks.presentation.components.skill

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapCardDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnPrimaryLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

private val BookmarkSkillCardShape = RMapCardDefaults.shape
private val BookmarkSkillIconFrameShape = AppShapes.card
private val BookmarkSkillIconContainerShape = AppShapes.iconFrameInner

enum class BookmarkSkillStatus(
    val backgroundColor: Color,
    val textColor: Color
) {
    IN_PROGRESS(
        backgroundColor = PrimaryLight,
        textColor = OnPrimaryLight
    ),
    COMPLETED(
        backgroundColor = Color(0xFFDCFCE7),
        textColor = Color(0xFF008236)
    ),
    NOT_STARTED(
        backgroundColor = Color(0xFFF3F4F6),
        textColor = Color(0xFF6A7282)
    )
}

data class BookmarkSkillCardUiModel(
    val title: String,
    val parentPathName: String,
    val status: BookmarkSkillStatus,
    val statusLabel: String,
    val icon: ImageVector
)

@Composable
fun BookmarkSkillCard(
    item: BookmarkSkillCardUiModel,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle? = null,
    titleMaxLines: Int = 2,
    headerContentEndPadding: Dp = 0.dp
) {
    val resolvedTitleStyle = titleStyle ?: AppTextStyles.compactCardTitle.copy(
        color = MaterialTheme.colorScheme.onSurface
    )

    RMapCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = BookmarkSkillCardShape,
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
                    BookmarkSkillIconFrame(icon = item.icon)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = headerContentEndPadding),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                    ) {
                        Text(
                            text = item.title,
                            style = resolvedTitleStyle,
                            maxLines = titleMaxLines,
                            overflow = TextOverflow.Ellipsis
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
                    BookmarkSkillStatusBadge(
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
private fun BookmarkSkillIconFrame(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(Dimens.iconFrameSize)
            .border(
                width = Dimens.borderMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = BookmarkSkillIconFrameShape
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
                    shape = BookmarkSkillIconContainerShape
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
private fun BookmarkSkillStatusBadge(
    text: String,
    status: BookmarkSkillStatus
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
private fun BookmarkSkillCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            BookmarkSkillCard(
                item = BookmarkSkillCardUiModel(
                    title = "Advanced CSS Layouts",
                    parentPathName = "Frontend Dev",
                    status = BookmarkSkillStatus.IN_PROGRESS,
                    statusLabel = "In Progress",
                    icon = Icons.Outlined.Code
                )
            )
            BookmarkSkillCard(
                item = BookmarkSkillCardUiModel(
                    title = "NoSQL Data Modeling",
                    parentPathName = "Backend Systems",
                    status = BookmarkSkillStatus.NOT_STARTED,
                    statusLabel = "Not Started",
                    icon = Icons.Outlined.DataObject
                )
            )
        }
    }
}
