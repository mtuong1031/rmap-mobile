package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.RMapTheme

private val RoadmapCardShape = RoundedCornerShape(32.dp)
private val RoadmapIconFrameShape = RoundedCornerShape(24.dp)
private val RoadmapIconContainerShape = RoundedCornerShape(18.dp)

enum class RoadmapDifficulty(
    val backgroundColor: Color,
    val textColor: Color
) {
    Expert(
        backgroundColor = Color(0xFFF3E8FF),
        textColor = Color(0xFF9810FA)
    ),
    Beginner(
        backgroundColor = Color(0xFFD0FBDC),
        textColor = Color(0xFF0F8330)
    ),
    Intermediate(
        backgroundColor = Color(0xFFDBEAFA),
        textColor = Color(0xFF298CF7)
    ),
    Hard(
        backgroundColor = Color(0xFFFFEDD4),
        textColor = Color(0xFFF54900)
    )
}

data class RoadmapCardUiModel(
    val title: String,
    val lessonsCount: Int,
    val difficultyLabel: String,
    val difficulty: RoadmapDifficulty,
    val durationLabel: String,
    val icon: ImageVector
)

@Composable
fun RoadmapCard(
    item: RoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 30.dp,
                spotColor = Color(0x0F000000),
                ambientColor = Color(0x0F000000)
            )
            .then(clickModifier),
        color = MaterialTheme.colorScheme.surface,
        shape = RoadmapCardShape,
        border = BorderStroke(1.dp, Color(0x80F9FAFB)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoadmapIconFrame(icon = item.icon)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.roadmap_lessons_count,
                                    item.lessonsCount
                                ),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                HorizontalDivider(Modifier, thickness = 1.dp, color = Color(0xCCF3F4F6))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DifficultyBadge(
                        text = item.difficultyLabel,
                        difficulty = item.difficulty
                    )

                    Text(
                        text = item.durationLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1
                    )
                }
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
                            Color(0x4D298CF7),
                            Color(0x26298CF7),
                            Color(0x10298CF7),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun RoadmapIconFrame(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoadmapIconFrameShape
            )
            .padding(horizontal = 5.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x26007AFF),
                            Color(0x0D007AFF)
                        )
                    ),
                    shape = RoadmapIconContainerShape
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
private fun DifficultyBadge(
    text: String,
    difficulty: RoadmapDifficulty
) {
    Box(
        modifier = Modifier
            .background(
                color = difficulty.backgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = difficulty.textColor,
                letterSpacing = 0.325.sp
            ),
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun TrendingRoadmapsSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapCard(
            item = RoadmapCardUiModel(
                title = "UI/UX Master",
                lessonsCount = 96,
                difficultyLabel = "Intermediate",
                difficulty = RoadmapDifficulty.Intermediate,
                durationLabel = "2 months",
                icon = Icons.Outlined.Search
            ),
        )
    }
}


