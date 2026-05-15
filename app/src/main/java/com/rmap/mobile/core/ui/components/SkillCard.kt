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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.CardDividerColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowBareColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowFaintColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowMediumColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowSoftColor
import com.rmap.mobile.core.ui.theme.NeutralDisabledColor
import com.rmap.mobile.core.ui.theme.NeutralDisabledTextColor
import com.rmap.mobile.core.ui.theme.NeutralTextMutedColor
import com.rmap.mobile.core.ui.theme.NeutralSoftSurfaceColor
import com.rmap.mobile.core.ui.theme.OnPrimaryLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

private val SkillCardShape = AppCardDefaults.shape
private val SkillIconFrameShape = RoundedCornerShape(24.dp)
private val SkillIconContainerShape = RoundedCornerShape(18.dp)

enum class SkillStatus(
    val backgroundColor: Color,
    val textColor: Color
) {
    IN_PROGRESS(
        backgroundColor = PrimaryLight,
        textColor = OnPrimaryLight
    ),
    NOT_STARTED(
        backgroundColor = NeutralSoftSurfaceColor,
        textColor = NeutralDisabledTextColor
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(shape = SkillCardShape),
        color = MaterialTheme.colorScheme.surface,
        shape = SkillCardShape,
        border = AppCardDefaults.border()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkillIconFrame(icon = item.icon)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 2
                        )

                        PartOfText(parentPathName = item.parentPathName)
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = CardDividerColor
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
                        tint = NeutralDisabledColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = 225.dp, y = (-63).dp)
                    .size(180.dp)
                    .blur(radius = 48.dp)
                    .background(
                        brush = Brush.radialGradient(
                        colors = listOf(
                                CardPrimaryGlowSoftColor,
                                CardPrimaryGlowFaintColor,
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
            .size(72.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = SkillIconFrameShape
            )
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CardPrimaryGlowMediumColor,
                            CardPrimaryGlowBareColor
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
                modifier = Modifier.size(32.dp)
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
                fontSize = 14.sp,
                color = NeutralTextMutedColor
            )
        ) {
            append("$partOfPrefix ")
        }
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
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
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 19.5.sp,
                letterSpacing = 0.325.sp,
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
