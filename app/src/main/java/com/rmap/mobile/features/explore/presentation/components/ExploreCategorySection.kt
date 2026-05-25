package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel

private const val CategoryGridColumnCount = 4
private val ExploreCategoryIconContainerSize = 72.dp
private val ExploreCategoryIconSize = 28.dp

@Composable
fun ExploreCategorySection(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSectionTitle(
                text = stringResource(R.string.explore_categories_title),
                subtitle = stringResource(R.string.explore_categories_subtitle),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(R.string.explore_view_all),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onViewAllClick
                ),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        CategoryGrid(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategoryClick = onCategoryClick
        )
    }
}

@Composable
private fun CategoryGrid(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        categories.chunked(CategoryGridColumnCount).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(CategoryGridColumnCount) { index ->
                    val category = rowItems.getOrNull(index)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (category != null) {
                            CategoryItem(
                                category = category,
                                selected = category.id == selectedCategoryId,
                                onClick = { onCategoryClick(category) }
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
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        category.backgroundColor
    }
    val iconContainerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus),
        modifier = Modifier.clickable(
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
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(R.string.explore_category_roadmap_count, category.roadmapCount),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.secondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
