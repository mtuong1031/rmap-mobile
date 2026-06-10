package com.rmap.mobile.features.myroadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapAchievementUiModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapCardUiModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapFilter
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapFilterUiModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapUiState
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryIcon
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import com.rmap.mobile.navigation.NavBarDestination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect

@Composable
fun MyRoadmapScreen(
    uiState: MyRoadmapUiState,
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    reselectEvent: Flow<NavBarDestination> = emptyFlow(),
    onFilterSelected: (MyRoadmapFilter) -> Unit,
    onRoadmapClick: (String) -> Unit,
    onCreateWithAiClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(reselectEvent) {
        reselectEvent.collectLatest {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = Dimens.spacingScreenTopCompact,
                bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
        ) {
            item {
                RMapHeader(
                    greetingText = stringResource(R.string.my_roadmap_header_eyebrow),
                    headingText = stringResource(R.string.my_roadmap_header_title),
                    greetingIcon = Icons.Outlined.Route,
                    actionIcon = Icons.Outlined.Map,
                    modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                )
            }

            when {
                uiState.isLoading -> item { MyRoadmapLoading() }
                uiState.errorMessage != null -> item { MyRoadmapError(message = uiState.errorMessage) }
                uiState.roadmaps.isEmpty() -> item {
                    MyRoadmapFullEmptyState(
                        onCreateWithAiClick = onCreateWithAiClick,
                        onExploreRoadmapsClick = onExploreRoadmapsClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    )
                }
                else -> {
                    item {
                        MyRoadmapFilters(
                            filters = uiState.filters,
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelected = onFilterSelected
                        )
                    }

                    item {
                        MyRoadmapListSection(
                            selectedFilter = uiState.selectedFilter,
                            roadmaps = uiState.visibleRoadmaps,
                            onRoadmapClick = onRoadmapClick,
                            onShowAllClick = { onFilterSelected(MyRoadmapFilter.All) },
                            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                        )
                    }

                    item {
                        MyRoadmapAchievementsSection(
                            completedSkills = uiState.completedSkills,
                            achievements = uiState.achievements,
                            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyRoadmapFilters(
    filters: List<MyRoadmapFilterUiModel>,
    selectedFilter: MyRoadmapFilter,
    onFilterSelected: (MyRoadmapFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.spacingScreenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        items(filters, key = { it.filter.name }) { item ->
            MyRoadmapFilterChip(
                item = item,
                isSelected = item.filter == selectedFilter,
                onClick = { onFilterSelected(item.filter) }
            )
        }
    }
}

@Composable
private fun MyRoadmapFilterChip(
    item: MyRoadmapFilterUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isBehind = item.filter == MyRoadmapFilter.Behind && item.count > 0
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isBehind -> Color(0xFFFFF7ED)
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isBehind -> Color(0xFFEA580C)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(containerColor)
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.filter.label(),
            style = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = item.count.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun MyRoadmapListSection(
    selectedFilter: MyRoadmapFilter,
    roadmaps: List<MyRoadmapCardUiModel>,
    onRoadmapClick: (String) -> Unit,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionHeader(
            title = stringResource(R.string.my_roadmap_list_title),
            subtitle = selectedFilter.subtitle()
        )

        if (roadmaps.isEmpty()) {
            FilterEmptyState(
                selectedFilter = selectedFilter,
                onShowAllClick = onShowAllClick
            )
        } else {
            roadmaps.forEach { roadmap ->
                MyRoadmapCard(
                    roadmap = roadmap,
                    onClick = { onRoadmapClick(roadmap.id) }
                )
            }
        }
    }
}

@Composable
private fun MyRoadmapCard(
    roadmap: MyRoadmapCardUiModel,
    onClick: () -> Unit
) {
    RMapCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.largeCard)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.Top
            ) {
                CategoryIconTile(
                    icon = roadmap.categoryKey.toRoadmapCategoryIcon().toImageVector()
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = roadmap.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = roadmap.categoryLabel,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        TypeBadge(isTemplate = roadmap.isTemplate)
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }

            LinearRoadmapProgress(progress = roadmap.completionPercent / 100f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.my_roadmap_progress_meta,
                        roadmap.completionPercent,
                        roadmap.nodesCompleted,
                        roadmap.nodesTotal
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = roadmap.deadlineLabel(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            StatusPill(roadmap = roadmap)
        }
    }
}

@Composable
private fun CategoryIconTile(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(Dimens.controlLg)
            .clip(AppShapes.iconContainerLarge)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconMd)
        )
    }
}

@Composable
private fun TypeBadge(isTemplate: Boolean) {
    val text = if (isTemplate) {
        stringResource(R.string.my_roadmap_badge_template)
    } else {
        stringResource(R.string.my_roadmap_badge_ai)
    }
    Text(
        text = text,
        maxLines = 1,
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(
                if (isTemplate) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
            .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
        style = MaterialTheme.typography.labelSmall.copy(
            color = if (isTemplate) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            },
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun LinearRoadmapProgress(progress: Float) {
    Box(
        modifier = Modifier
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
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun StatusPill(roadmap: MyRoadmapCardUiModel) {
    val status = roadmap.statusLabel()
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(status.containerColor)
            .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs)
    ) {
        Icon(
            imageVector = status.icon,
            contentDescription = null,
            tint = status.contentColor,
            modifier = Modifier.size(Dimens.iconXs)
        )
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = status.contentColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun MyRoadmapAchievementsSection(
    completedSkills: Int,
    achievements: List<MyRoadmapAchievementUiModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        SectionHeader(
            title = stringResource(R.string.my_roadmap_achievements_title),
            subtitle = stringResource(R.string.my_roadmap_achievements_subtitle, completedSkills)
        )
        achievements.forEach { item ->
            AchievementCategoryRow(item = item)
        }
    }
}

@Composable
private fun AchievementCategoryRow(item: MyRoadmapAchievementUiModel) {
    RMapCard(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = Dimens.cardElevationXs
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIconTile(icon = item.categoryKey.toRoadmapCategoryIcon().toImageVector())
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
            ) {
                Text(
                    text = item.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(R.string.my_roadmap_achievement_category_skills, item.totalSkills),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun MyRoadmapFullEmptyState(
    onCreateWithAiClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            CategoryIconTile(icon = Icons.Outlined.Map)
            Text(
                text = stringResource(R.string.my_roadmap_empty_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.my_roadmap_empty_body),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
            Button(onClick = onCreateWithAiClick, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.size(Dimens.spacingSm))
                Text(text = stringResource(R.string.my_roadmap_empty_create_ai))
            }
            OutlinedButton(onClick = onExploreRoadmapsClick, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Outlined.Explore, contentDescription = null)
                Spacer(modifier = Modifier.size(Dimens.spacingSm))
                Text(text = stringResource(R.string.my_roadmap_empty_explore))
            }
        }
    }
}

@Composable
private fun FilterEmptyState(
    selectedFilter: MyRoadmapFilter,
    onShowAllClick: () -> Unit
) {
    RMapCard(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = Dimens.cardElevationXs
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Text(
                text = selectedFilter.emptyTitle(),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            OutlinedButton(onClick = onShowAllClick) {
                Text(text = stringResource(R.string.my_roadmap_filter_empty_show_all))
            }
        }
    }
}

@Composable
private fun MyRoadmapLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacingXxl),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MyRoadmapError(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(Dimens.spacingScreenHorizontal),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun MyRoadmapFilter.label(): String {
    val resId = when (this) {
        MyRoadmapFilter.Active -> R.string.my_roadmap_filter_active
        MyRoadmapFilter.All -> R.string.my_roadmap_filter_all
        MyRoadmapFilter.Completed -> R.string.my_roadmap_filter_completed
        MyRoadmapFilter.Behind -> R.string.my_roadmap_filter_behind
    }
    return stringResource(resId)
}

@Composable
private fun MyRoadmapFilter.subtitle(): String {
    val resId = when (this) {
        MyRoadmapFilter.Active -> R.string.my_roadmap_list_subtitle_active
        MyRoadmapFilter.All -> R.string.my_roadmap_list_subtitle_all
        MyRoadmapFilter.Completed -> R.string.my_roadmap_list_subtitle_completed
        MyRoadmapFilter.Behind -> R.string.my_roadmap_list_subtitle_behind
    }
    return stringResource(resId)
}

@Composable
private fun MyRoadmapFilter.emptyTitle(): String {
    val resId = when (this) {
        MyRoadmapFilter.Active -> R.string.my_roadmap_filter_empty_active
        MyRoadmapFilter.All -> R.string.my_roadmap_filter_empty_all
        MyRoadmapFilter.Completed -> R.string.my_roadmap_filter_empty_completed
        MyRoadmapFilter.Behind -> R.string.my_roadmap_filter_empty_behind
    }
    return stringResource(resId)
}

@Composable
private fun MyRoadmapCardUiModel.deadlineLabel(): String {
    val daysLeft = deadlineDate?.toDaysLeftFromToday()
    return when {
        daysLeft == null -> estimatedWeeks?.let {
            stringResource(R.string.my_roadmap_estimated_weeks, it)
        } ?: stringResource(R.string.my_roadmap_no_deadline)
        daysLeft <= 0 -> stringResource(R.string.my_roadmap_due_today)
        daysLeft >= DAYS_PER_MONTH -> stringResource(
            R.string.my_roadmap_months_left,
            daysLeft.toRoundedUnits(DAYS_PER_MONTH)
        )
        daysLeft >= DAYS_PER_WEEK -> stringResource(
            R.string.my_roadmap_weeks_left,
            daysLeft.toRoundedUnits(DAYS_PER_WEEK)
        )
        else -> stringResource(R.string.my_roadmap_days_left, daysLeft)
    }
}

@Composable
private fun MyRoadmapCardUiModel.statusLabel(): RoadmapStatusVisual {
    return when {
        completionPercent >= 100 -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_completed),
            containerColor = Color(0xFFECFDF5),
            contentColor = Color(0xFF059669),
            icon = Icons.Outlined.CheckCircle
        )
        isBehind -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_behind),
            containerColor = Color(0xFFFFF7ED),
            contentColor = Color(0xFFEA580C),
            icon = Icons.Outlined.Route
        )
        startedAt == null -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_not_started),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = Icons.Outlined.Route
        )
        else -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_on_track),
            containerColor = Color(0xFFEFF6FF),
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Outlined.CheckCircle
        )
    }
}

private data class RoadmapStatusVisual(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector
)

private fun String.toDaysLeftFromToday(): Int? {
    val deadlineMillis = toUtcDateMillisOrNull() ?: return null
    val todayUtc = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE)).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return TimeUnit.MILLISECONDS.toDays(deadlineMillis - todayUtc.timeInMillis).toInt()
}

private fun String.toUtcDateMillisOrNull(): Long? {
    val parser = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone(UTC_TIME_ZONE)
    }
    return runCatching { parser.parse(this)?.time }.getOrNull()
}

private fun Int.toRoundedUnits(unitSize: Int): Int {
    return ((this + unitSize - 1) / unitSize).coerceAtLeast(1)
}

private const val DAYS_PER_WEEK = 7
private const val DAYS_PER_MONTH = 30
private const val DATE_ONLY_PATTERN = "yyyy-MM-dd"
private const val UTC_TIME_ZONE = "UTC"

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 900)
@Composable
private fun MyRoadmapScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        MyRoadmapScreen(
            uiState = MyRoadmapUiState(
                filters = listOf(
                    MyRoadmapFilterUiModel(MyRoadmapFilter.Active, 2),
                    MyRoadmapFilterUiModel(MyRoadmapFilter.All, 3),
                    MyRoadmapFilterUiModel(MyRoadmapFilter.Completed, 0),
                    MyRoadmapFilterUiModel(MyRoadmapFilter.Behind, 0)
                ),
                roadmaps = listOf(
                    MyRoadmapCardUiModel(
                        id = "backend",
                        title = "Backend Intern (Node.js) Roadmap",
                        categoryKey = "WEB_DEVELOPMENT",
                        categoryLabel = "Web Development",
                        isTemplate = false,
                        completionPercent = 10,
                        nodesCompleted = 7,
                        nodesTotal = 70,
                        deadlineDate = "2026-07-31",
                        estimatedWeeks = 18,
                        startedAt = "2026-06-01T03:22:18.055Z",
                        isBehind = false
                    )
                ),
                achievements = listOf(
                    MyRoadmapAchievementUiModel("WEB_DEVELOPMENT", "Web Development", 432),
                    MyRoadmapAchievementUiModel("DESIGN", "Design", 187)
                ),
                completedSkills = 21,
                isLoading = false
            ),
            selectedDestination = NavBarDestination.MyRoadmap,
            onDestinationSelected = {},
            onFilterSelected = {},
            onRoadmapClick = {},
            onCreateWithAiClick = {},
            onExploreRoadmapsClick = {}
        )
    }
}
