package com.rmap.mobile.presentation.ui.components

import androidx.compose.foundation.BorderStroke
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
import com.rmap.mobile.presentation.ui.theme.RMapTheme

private val SkillCardShape = RoundedCornerShape(32.dp)
private val SkillIconFrameShape = RoundedCornerShape(24.dp)
private val SkillIconContainerShape = RoundedCornerShape(18.dp)

enum class SkillStatus(
    val backgroundColor: Color,
    val textColor: Color
) {
    IN_PROGRESS(
        backgroundColor = Color(0xFF298CF7),
        textColor = Color.White
    ),
    NOT_STARTED(
        backgroundColor = Color(0xFFF3F4F6),
        textColor = Color(0xFF6A7282)
    )
}

data class BookmarkSkillCardUiModel(
    val title: String,
    val parentPathName: String,
    val status: SkillStatus,
    val statusLabel: String,
    val icon: ImageVector
)

@Composable
fun BookmarkSkillCard(
    item: BookmarkSkillCardUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = SkillCardShape,
        border = BorderStroke(1.dp, Color(0x80F9FAFB)),
        shadowElevation = 8.dp
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
                    color = Color(0xFFF3F4F6)
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
                        tint = Color(0xFF9CA3AF),
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
                                Color(0x14298CF7),
                                Color(0x0A298CF7),
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
                            Color(0x26298CF7),
                            Color(0x0D298CF7)
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
    val partOfPrefix = stringResource(R.string.bookmarks_part_of_format, "").trimEnd()
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
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
private fun BookmarkSkillCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BookmarkSkillCard(
                item = BookmarkSkillCardUiModel(
                    title = "Advanced CSS Layouts",
                    parentPathName = "Frontend Dev",
                    status = SkillStatus.IN_PROGRESS,
                    statusLabel = "In Progress",
                    icon = Icons.Outlined.Code
                )
            )
            BookmarkSkillCard(
                item = BookmarkSkillCardUiModel(
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
