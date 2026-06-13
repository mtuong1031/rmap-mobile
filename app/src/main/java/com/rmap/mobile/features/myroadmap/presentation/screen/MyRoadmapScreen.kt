package com.rmap.mobile.features.myroadmap.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.components.RMapSearchBar
import com.rmap.mobile.core.ui.components.RMapSkeletonBlock
import com.rmap.mobile.core.ui.components.RMapSkeletonCard
import com.rmap.mobile.core.ui.components.rememberRMapSkeletonBrush
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.LocalRMapSemanticColors
import com.rmap.mobile.core.ui.theme.RMapTheme
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoadmapScreen(
    uiState: MyRoadmapUiState,
    isAuthenticated: Boolean,
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchClick: () -> Unit,
    onFilterSelected: (MyRoadmapFilter) -> Unit,
    onRoadmapClick: (String) -> Unit,
    onRoadmapCtaClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    onCreateWithAiClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    onHeaderActionClick: () -> Unit = {},
    avatarUrl: String = "",
    modifier: Modifier = Modifier,
    reselectEvent: Flow<NavBarDestination> = emptyFlow()
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
                bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact + Dimens.floatingNavBarHeight
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            item {
                RMapHeader(
                    greetingText = stringResource(R.string.my_roadmap_header_eyebrow),
                    headingText = stringResource(R.string.my_roadmap_header_title),
                    greetingIcon = Icons.Outlined.Route,
                    actionImageUrl = avatarUrl,
                    onActionClick = onHeaderActionClick,
                    modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                )
            }

            item {
                RMapSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = stringResource(R.string.my_roadmap_search_placeholder),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacingScreenHorizontal)
                )
            }

            when {
                !isAuthenticated -> item {
                    MyRoadmapFullEmptyState(
                        onCreateWithAiClick = onCreateWithAiClick,
                        onExploreRoadmapsClick = onExploreRoadmapsClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    )
                }
                uiState.isLoading -> item { MyRoadmapLoading() }
                uiState.errorMessage != null -> item {
                    MyRoadmapError(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    )
                }
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

                    if (uiState.visibleRoadmaps.isEmpty()) {
                        item {
                            if (uiState.isSearching) {
                                SearchEmptyState(
                                    query = uiState.searchQuery,
                                    onClearSearchClick = onClearSearchClick,
                                    modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                                )
                            } else {
                                FilterEmptyState(
                                    selectedFilter = uiState.selectedFilter,
                                    onShowAllClick = { onFilterSelected(MyRoadmapFilter.All) },
                                    modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                                )
                            }
                        }
                    } else {
                        items(
                            items = uiState.visibleRoadmaps,
                            key = { roadmap -> "roadmap-${roadmap.id}" }
                        ) { roadmap ->
                            MyRoadmapCompactCard(
                                roadmap = roadmap,
                                onClick = { onRoadmapClick(roadmap.id) },
                                onCtaClick = { onRoadmapCtaClick(roadmap.id) },
                                modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                            )
                        }
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
    val semanticColors = LocalRMapSemanticColors.current
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isBehind -> semanticColors.warning.container
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isBehind -> semanticColors.warning.content
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
        isBehind -> semanticColors.warning.border
        else -> MaterialTheme.colorScheme.outline
    }

    Row(
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(containerColor)
            .border(Dimens.borderThin, borderColor, AppShapes.pill)
            .selectable(
                selected = isSelected,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { selected = isSelected }
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
private fun MyRoadmapCompactCard(
    roadmap: MyRoadmapCardUiModel,
    onClick: () -> Unit,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardDescription = stringResource(R.string.my_roadmap_card_content_description, roadmap.title)
    RMapCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.heroCard)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = cardDescription },
        shape = AppShapes.heroCard,
        border = BorderStroke(
            width = Dimens.borderThin,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shadowElevation = Dimens.cardElevationXs
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            // Upper Row: Icon + (Title & Metadata Column) + Trailing Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                verticalAlignment = Alignment.Top
            ) {
                // Circular Progress around Category Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(52.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        strokeWidth = 3.dp
                    )
                    CircularProgressIndicator(
                        progress = { roadmap.completionPercent / 100f },
                        modifier = Modifier.size(52.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = roadmap.categoryKey.toRoadmapCategoryIcon().toImageVector(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Title & Metadata Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = roadmap.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = AppTextStyles.compactCardTitle.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            lineHeight = 22.sp
                        )
                    )

                    Text(
                        text = "${roadmap.categoryLabel} • ${roadmap.deadlineLabel()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Trailing Chevron
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = Dimens.spacingXs)
                )
            }

            // Lower Row: Status + Badge (left) and Progress % (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusTextWithDot(roadmap = roadmap)
                    TypeBadge(isTemplate = roadmap.isTemplate)
                }

                Text(
                    text = stringResource(R.string.my_roadmap_progress_percent, roadmap.completionPercent),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            RMapButton(
                text = roadmap.ctaLabel(),
                onClick = onCtaClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Medium
            )
        }
    }
}

@Composable
private fun StatusTextWithDot(roadmap: MyRoadmapCardUiModel) {
    val status = roadmap.statusLabel()
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(AppShapes.pill)
                .background(status.contentColor)
        )
        Text(
            text = status.label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
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

private val MyRoadmapEmptyContentMaxWidth = 282.dp
private val MyRoadmapEmptyIconContainerShape = RoundedCornerShape(24.dp)
private val MyRoadmapEmptyMinHeight = 420.dp
private val MyRoadmapEmptyMaxHeight = 520.dp
private val MyRoadmapFilterEmptyMinHeight = 320.dp
private const val MyRoadmapEmptyHeightRatio = 1.42f

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyRoadmapFullEmptyState(
    onCreateWithAiClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val horizontalPadding = if (maxWidth < 320.dp) {
            Dimens.spacingXxl
        } else {
            Dimens.spacingHuge
        }
        val cardMinHeight = (maxWidth * MyRoadmapEmptyHeightRatio).coerceIn(
            minimumValue = MyRoadmapEmptyMinHeight,
            maximumValue = MyRoadmapEmptyMaxHeight
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = cardMinHeight)
        ) {
            RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = horizontalPadding, vertical = Dimens.spacingHuge)
                    .widthIn(max = MyRoadmapEmptyContentMaxWidth)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .sizeIn(
                            minWidth = Dimens.controlXl + Dimens.spacingLg,
                            minHeight = Dimens.controlXl + Dimens.spacingLg
                        )
                        .clip(MyRoadmapEmptyIconContainerShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconXxl)
                    )
                }

                Text(
                    text = stringResource(R.string.my_roadmap_empty_title),
                    modifier = Modifier
                        .padding(top = Dimens.spacingXxl)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.my_roadmap_empty_body),
                    modifier = Modifier
                        .padding(top = Dimens.spacingSm)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .padding(top = Dimens.spacingHuge)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    RMapButton(
                        text = stringResource(R.string.my_roadmap_empty_create_ai),
                        onClick = onCreateWithAiClick,
                        modifier = Modifier.fillMaxWidth(),
                        variant = RMapButtonVariant.Primary,
                        size = RMapButtonSize.Large,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null
                            )
                        }
                    )
                    RMapButton(
                        text = stringResource(R.string.my_roadmap_empty_explore),
                        onClick = onExploreRoadmapsClick,
                        modifier = Modifier.fillMaxWidth(),
                        variant = RMapButtonVariant.Secondary,
                        size = RMapButtonSize.Large
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterEmptyState(
    selectedFilter: MyRoadmapFilter,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = selectedFilter.emptyVisual()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = MyRoadmapFilterEmptyMinHeight)
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = Dimens.spacingHuge, vertical = Dimens.spacingXxl)
                .widthIn(max = MyRoadmapEmptyContentMaxWidth)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.controlXl + Dimens.spacingLg)
                    .clip(MyRoadmapEmptyIconContainerShape)
                    .background(visual.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.contentColor,
                    modifier = Modifier.size(Dimens.iconXxl)
                )
            }

            Text(
                text = selectedFilter.emptyTitle(),
                modifier = Modifier
                    .padding(top = Dimens.spacingXl)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = selectedFilter.emptyBody(),
                modifier = Modifier
                    .padding(top = Dimens.spacingSm)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
                textAlign = TextAlign.Center
            )

            RMapButton(
                text = stringResource(R.string.my_roadmap_filter_empty_show_all),
                onClick = onShowAllClick,
                modifier = Modifier
                    .padding(top = Dimens.spacingXxl)
                    .fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large
            )
        }
    }
}

@Composable
private fun MyRoadmapLoading() {
    val brush = rememberRMapSkeletonBrush()
    Column(
        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
            repeat(4) {
                RMapSkeletonBlock(
                    modifier = Modifier.size(width = 76.dp, height = 40.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
            }
        }
        repeat(2) {
            RMapSkeletonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = AppShapes.largeCard
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacingLg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(Dimens.spacingXxl),
                        brush = brush
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth(0.42f)
                            .height(Dimens.spacingLg),
                        brush = brush
                    )
                    Spacer(modifier = Modifier.height(Dimens.spacingMassive))
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.spacingSm),
                        brush = brush
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.controlMd),
                        shape = AppShapes.button,
                        brush = brush
                    )
                }
            }
        }
    }
}

@Composable
private fun MyRoadmapError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(modifier = modifier.fillMaxWidth(), shadowElevation = Dimens.cardElevationXs) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Dimens.iconXl)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
            RMapButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetryClick,
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Medium
            )
        }
    }
}

@Composable
private fun SearchEmptyState(
    query: String,
    onClearSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(modifier = modifier.fillMaxWidth(), shadowElevation = Dimens.cardElevationXs) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Text(
                text = stringResource(R.string.my_roadmap_search_empty_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.my_roadmap_search_empty_body, query),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
            RMapButton(
                text = stringResource(R.string.my_roadmap_search_clear),
                onClick = onClearSearchClick,
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Medium
            )
        }
    }
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
private fun MyRoadmapFilter.emptyBody(): String {
    val resId = when (this) {
        MyRoadmapFilter.Active -> R.string.my_roadmap_filter_empty_active_body
        MyRoadmapFilter.All -> R.string.my_roadmap_filter_empty_all_body
        MyRoadmapFilter.Completed -> R.string.my_roadmap_filter_empty_completed_body
        MyRoadmapFilter.Behind -> R.string.my_roadmap_filter_empty_behind_body
    }
    return stringResource(resId)
}

@Composable
private fun MyRoadmapFilter.emptyVisual(): MyRoadmapEmptyVisual {
    val semanticColors = LocalRMapSemanticColors.current
    return when (this) {
        MyRoadmapFilter.Active -> MyRoadmapEmptyVisual(
            icon = Icons.Outlined.PlayArrow,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
        MyRoadmapFilter.All -> MyRoadmapEmptyVisual(
            icon = Icons.Outlined.Map,
            containerColor = semanticColors.info.container,
            contentColor = semanticColors.info.content
        )
        MyRoadmapFilter.Completed -> MyRoadmapEmptyVisual(
            icon = Icons.Outlined.CheckCircle,
            containerColor = semanticColors.success.container,
            contentColor = semanticColors.success.content
        )
        MyRoadmapFilter.Behind -> MyRoadmapEmptyVisual(
            icon = Icons.Outlined.Route,
            containerColor = semanticColors.success.container,
            contentColor = semanticColors.success.content
        )
    }
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
    val semanticColors = LocalRMapSemanticColors.current
    return when {
        completionPercent >= 100 -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_completed),
            containerColor = semanticColors.success.container,
            contentColor = semanticColors.success.content,
            icon = Icons.Outlined.CheckCircle
        )
        isBehind -> RoadmapStatusVisual(
            label = stringResource(R.string.my_roadmap_status_behind),
            containerColor = semanticColors.warning.container,
            contentColor = semanticColors.warning.content,
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
            containerColor = semanticColors.info.container,
            contentColor = semanticColors.info.content,
            icon = Icons.Outlined.CheckCircle
        )
    }
}

@Composable
private fun MyRoadmapCardUiModel.ctaLabel(): String {
    return when {
        completionPercent >= 100 -> stringResource(R.string.my_roadmap_cta_view_completed)
        startedAt == null -> stringResource(R.string.my_roadmap_cta_review)
        else -> stringResource(R.string.my_roadmap_cta_continue)
    }
}

private data class RoadmapStatusVisual(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector
)

private data class MyRoadmapEmptyVisual(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
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
                isLoading = false
            ),
            isAuthenticated = true,
            selectedDestination = NavBarDestination.MyRoadmap,
            onDestinationSelected = {},
            onSearchQueryChange = {},
            onClearSearchClick = {},
            onFilterSelected = {},
            onRoadmapClick = {},
            onRoadmapCtaClick = {},
            onRetryClick = {},
            onCreateWithAiClick = {},
            onExploreRoadmapsClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun MyRoadmapCompactCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            MyRoadmapCompactCard(
                roadmap = MyRoadmapCardUiModel(
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
                ),
                onClick = {},
                onCtaClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390)
@Composable
private fun MyRoadmapFilterEmptyDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingScreenHorizontal)) {
            FilterEmptyState(
                selectedFilter = MyRoadmapFilter.Completed,
                onShowAllClick = {}
            )
        }
    }
}
