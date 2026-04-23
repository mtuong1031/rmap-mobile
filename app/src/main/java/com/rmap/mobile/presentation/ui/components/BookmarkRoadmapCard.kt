package com.rmap.mobile.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.presentation.ui.theme.RMapTheme

private val BookmarkRoadmapCardShape = RoundedCornerShape(32.dp)
private val CoverHeight = 150.dp

data class BookmarkRoadmapCardUiModel(
    val title: String,
    val difficultyLabel: String,
    val difficulty: RoadmapDifficulty,
    val durationLabel: String,
    val actionLabel: String,
    @DrawableRes val coverPlaceholderRes: Int? = null
)

@Composable
fun BookmarkRoadmapCard(
    item: BookmarkRoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 30.dp,
                spotColor = Color(0x0F000000),
                ambientColor = Color(0x0F000000)
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = BookmarkRoadmapCardShape,
        border = BorderStroke(1.dp, Color(0x80F9FAFB))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CoverImageSection(
                coverPlaceholderRes = item.coverPlaceholderRes,
                onBookmarkClick = onBookmarkClick
            )

            ContentSection(
                item = item,
                onActionClick = onActionClick,
                onShareClick = onShareClick
            )
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

@Composable
private fun CoverImageSection(
    @DrawableRes coverPlaceholderRes: Int?,
    onBookmarkClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CoverHeight)
            .background(Color(0xFF0F172B))
    ) {
        if (coverPlaceholderRes != null) {
            val isPreview = LocalInspectionMode.current
            if (!isPreview) {
                Image(
                    painter = painterResource(id = coverPlaceholderRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E3A5F).copy(alpha = 0.6f))
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        if (onBookmarkClick != null) {
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(44.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Color(0x1A000000),
                        ambientColor = Color(0x1A000000)
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ContentSection(
    item: BookmarkRoadmapCardUiModel,
    onActionClick: (() -> Unit)?,
    onShareClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    lineHeight = 30.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = item.difficulty.textColor,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = item.difficultyLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280)
                        ),
                        maxLines = 1
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = Color(0xFFD1D5DB),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = item.durationLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        ),
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledButton(
                    text = item.actionLabel,
                    onClick = onActionClick ?: {},
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xCCF4F8FF),
                            ambientColor = Color(0xCCF4F8FF)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF4F8FF),
                    border = BorderStroke(1.dp, Color(0x1A298CF7))
                ) {
                    IconButton(
                        onClick = onShareClick ?: {},
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkRoadmapCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BookmarkRoadmapCard(
                item = BookmarkRoadmapCardUiModel(
                    title = "Full Stack Development",
                    difficultyLabel = "Intermediate",
                    difficulty = RoadmapDifficulty.Intermediate,
                    durationLabel = "8 months",
                    actionLabel = "Continue Path",
                    coverPlaceholderRes = R.drawable.bg_placeholder_fullstack
                ),
                onActionClick = {},
                onShareClick = {},
                onBookmarkClick = {}
            )

            BookmarkRoadmapCard(
                item = BookmarkRoadmapCardUiModel(
                    title = "UI/UX Masterclass",
                    difficultyLabel = "Beginner",
                    difficulty = RoadmapDifficulty.Beginner,
                    durationLabel = "4 months",
                    actionLabel = "Join Now",
                    coverPlaceholderRes = R.drawable.bg_placeholder_uiux
                ),
                onActionClick = {},
                onShareClick = {},
                onBookmarkClick = {}
            )
        }
    }
}
