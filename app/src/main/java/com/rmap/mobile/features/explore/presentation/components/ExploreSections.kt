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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCard
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.RoadmapCard
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.CardDividerColor
import com.rmap.mobile.core.ui.theme.CardShadowSubtleColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.explore.presentation.CategoryUiModel
import com.rmap.mobile.features.explore.presentation.RecommendedCardUiModel

private val ExploreSearchBarShape = AppShapes.searchBar
private val RecommendedCardShape = AppShapes.card

@Composable
fun ExploreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.exploreSearchBarHeight),
        shape = ExploreSearchBarShape,
        border = AppCardDefaults.border(color = CardDividerColor),
        shadowColor = CardShadowSubtleColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconLg)
            )
            
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(R.string.explore_search_placeholder),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )

            Box(
                modifier = Modifier
                    .size(Dimens.controlSm)
                    .clip(AppShapes.iconContainer)
                    .clickable(onClick = onFilterClick)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    categories: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingScreenHorizontalWide),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.explore_categories_title),
                style = AppTextStyles.sectionTitle.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = stringResource(R.string.explore_view_all),
                modifier = Modifier.clickable(onClick = onViewAllClick),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.spacingLg))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.spacingScreenHorizontalWide),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            items(categories) { category ->
                CategoryItem(category = category, onClick = { onCategoryClick(category) })
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.categoryIconContainerSize)
                .background(category.backgroundColor, CircleShape)
                .border(Dimens.borderThin, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconXl)
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun RecommendedSection(
    items: List<RecommendedCardUiModel>,
    onItemClick: (RecommendedCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.explore_recommended_title),
            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontalWide),
            style = AppTextStyles.sectionTitle.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingLg))

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.spacingScreenHorizontalWide),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            items(items) { item ->
                RecommendedCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun RecommendedCard(
    item: RecommendedCardUiModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(Dimens.recommendedCardWidth)
            .height(Dimens.recommendedCardHeight)
            .appCardShadow(
                elevation = AppCardDefaults.shadowElevation,
                shape = RecommendedCardShape,
                ambientColor = item.accentColor.copy(alpha = 0.3f),
                spotColor = item.accentColor.copy(alpha = 0.3f)
            )
            .clip(RecommendedCardShape)
            .clickable(onClick = onClick)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        item.accentColor.copy(alpha = 0.9f),
                        item.accentColor
                    )
                )
            )
    ) {
        // Decorative glow
        Box(
            modifier = Modifier
                .size(Dimens.recommendedCardGlowSize)
                .align(Alignment.TopEnd)
                .offset(x = Dimens.recommendedCardGlowOffset, y = -Dimens.recommendedCardGlowOffset)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                shape = AppShapes.chip
            ) {
                Text(
                    text = item.badgeText,
                    modifier = Modifier.padding(horizontal = Dimens.spacingSmPlus, vertical = Dimens.spacingXsPlus),
                    style = AppTextStyles.tag.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)) {
                Text(
                    text = item.title,
                    style = AppTextStyles.recommendedCardTitle.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        modifier = Modifier.size(Dimens.iconXs)
                    )
                    Spacer(modifier = Modifier.width(Dimens.spacingXsPlus))
                    Text(
                        text = stringResource(R.string.explore_card_stats, item.skillNodesCount, item.level),
                        style = AppTextStyles.metadata.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PopularRoadmapsSection(
    roadmaps: List<RoadmapCardUiModel>,
    onRoadmapClick: (RoadmapCardUiModel) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.explore_popular_title),
                style = AppTextStyles.sectionTitle.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = stringResource(R.string.roadmap_see_all),
                modifier = Modifier.clickable(onClick = onSeeAllClick),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        roadmaps.forEach { roadmap ->
            RoadmapCard(
                item = roadmap,
                onClick = { onRoadmapClick(roadmap) }
            )
        }
    }
}


@Preview(showBackground = true, name = "Explore Search Bar")
@Composable
private fun ExploreSearchBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            ExploreSearchBar(query = "", onQueryChange = {}, onFilterClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, name = "Recommended Card")
@Composable
private fun RecommendedCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            RecommendedCard(
                item = RecommendedCardUiModel(
                    id = "frontend",
                    title = "Frontend Development",
                    badgeText = "Recommended",
                    skillNodesCount = 24,
                    level = "Intermediate",
                    imageUrl = "",
                    accentColor = PrimaryLight
                ),
                onClick = {}
            )
        }
    }
}
