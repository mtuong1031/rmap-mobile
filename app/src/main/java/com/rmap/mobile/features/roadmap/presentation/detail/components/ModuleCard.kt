package com.rmap.mobile.features.roadmap.presentation.detail.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.theme.RMapTheme

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
    val cardBg = if (isLocked) Color.White.copy(alpha = 0.6f) else Color.White
    val titleColor = if (isLocked) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.onSurface
    val iconBg = if (isLocked) Color(0xFFF3F4F6) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val iconTint = if (isLocked) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        border = AppCardDefaults.border(
            color = if (isLocked) AppCardDefaults.borderColor.copy(alpha = 0.5f) else AppCardDefaults.borderColor
        ),
        shadowElevation = if (isLocked) 0.dp else AppCardDefaults.shadowElevation,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Box
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(iconBg, RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (isLocked) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Texts
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                            ModuleStatus.COMPLETED -> Color(0xFF0F8330)
                            ModuleStatus.IN_PROGRESS -> Color(0xFF6B7280)
                            ModuleStatus.LOCKED -> Color(0xFF9CA3AF)
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = statusColor
                            )
                        )
                    }
                }

                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotation)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded && !isLocked) {
                Column(
                    modifier = Modifier.padding(top = 16.dp, start = 6.dp)
                ) {
                    item.subLessons.forEachIndexed { index, lesson ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timeline connection
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(20.dp)
                            ) {
                                if (index > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(16.dp)
                                            .background(Color(0xFFF4F8FF))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // Status Indicator
                                Box(contentAlignment = Alignment.Center) {
                                    when (lesson.status) {
                                        ModuleStatus.COMPLETED -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                        ModuleStatus.IN_PROGRESS -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary)
                                                )
                                            }
                                        }
                                        ModuleStatus.LOCKED -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFF3F4F6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = Color(0xFF9CA3AF),
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (index < item.subLessons.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(16.dp)
                                            .background(Color(0xFFF4F8FF))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (lesson.status == ModuleStatus.IN_PROGRESS) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (lesson.status == ModuleStatus.LOCKED) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.onSurface
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
        Column(modifier = Modifier.padding(16.dp)) {
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
