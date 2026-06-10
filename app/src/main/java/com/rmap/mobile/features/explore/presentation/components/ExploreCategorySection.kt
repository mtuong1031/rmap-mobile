package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel

private const val CategoryCarouselPageSize = 8
private const val CategoryCarouselColumnCount = 4
private const val CategoryCarouselRowCount = 2
private val ExploreCategoryIconContainerSize = 68.dp
private val ExploreCategoryIconSize = 26.dp
private val ExploreCategoryCarouselDotHeight = 6.dp
private val ExploreCategoryCarouselActiveDotWidth = 18.dp
private val ExploreCategoryCarouselInactiveDotWidth = 6.dp

@Composable
fun ExploreCategorySection(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    onCategoryClick: (CategoryUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingScreenHorizontal)
        ) {
            RMapSectionTitle(
                text = stringResource(R.string.explore_categories_title),
                subtitle = stringResource(R.string.explore_categories_subtitle)
            )
        }

        CategoryCarousel(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategoryClick = onCategoryClick
        )
    }
}

@Composable
private fun CategoryCarousel(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    val pages = remember(categories) { categories.chunked(CategoryCarouselPageSize) }
    if (pages.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        HorizontalPager(
            state = pagerState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Dimens.spacingScreenHorizontal),
            pageSpacing = Dimens.spacingMd
        ) { page ->
            CategoryPage(
                categories = pages.getOrElse(page) { emptyList() },
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = onCategoryClick
            )
        }

        if (pages.size > 1) {
            CategoryCarouselDots(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
            )
        }
    }
}

@Composable
private fun CategoryPage(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    val rows = categories.chunked(CategoryCarouselColumnCount)
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        repeat(CategoryCarouselRowCount) { rowIndex ->
            val rowItems = rows.getOrElse(rowIndex) { emptyList() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)
            ) {
                repeat(CategoryCarouselColumnCount) { index ->
                    val category = rowItems.getOrNull(index)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (category != null) {
                            CategoryItem(
                                category = category,
                                selected = category.id == selectedCategoryId,
                                onClick = { onCategoryClick(category) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()

    val borderColor = when {
        isDarkTheme && selected -> MaterialTheme.colorScheme.primary
        isDarkTheme && !selected -> MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
        selected -> MaterialTheme.colorScheme.primary
        else -> category.backgroundColor
    }
    
    val iconContainerColor = when {
        isDarkTheme && selected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        isDarkTheme && !selected -> MaterialTheme.colorScheme.surfaceContainer
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val titleColor = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) else MaterialTheme.colorScheme.secondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(ExploreCategoryIconContainerSize)
                .cardShadow(shape = CircleShape)
                .background(iconContainerColor, CircleShape)
                .border(Dimens.borderMedium, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(ExploreCategoryIconSize)
            )
        }

        Text(
            text = category.name,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.explore_category_roadmap_count, category.roadmapCount),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(
                color = subtitleColor
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryCarouselDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val dotWidth = if (isSelected) {
                ExploreCategoryCarouselActiveDotWidth
            } else {
                ExploreCategoryCarouselInactiveDotWidth
            }
            val dotColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.spacingXs)
                    .width(dotWidth)
                    .height(ExploreCategoryCarouselDotHeight)
                    .clip(AppShapes.pill)
                    .background(dotColor)
            )
        }
    }
}
