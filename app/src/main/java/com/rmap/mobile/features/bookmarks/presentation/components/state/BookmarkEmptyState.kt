package com.rmap.mobile.features.bookmarks.presentation.components.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTextStyles

private val BookmarkEmptyContentMaxWidth = 278.dp
private val BookmarkEmptyTopDecorMinSize = 80.dp
private val BookmarkEmptyTopDecorMaxSize = 120.dp
private val BookmarkEmptyBottomDecorMinSize = 72.dp
private val BookmarkEmptyBottomDecorMaxSize = 100.dp
private val BookmarkEmptyIconContainerShape = RoundedCornerShape(24.dp)
private val BookmarkEmptyMinHeight = 460.dp
private val BookmarkEmptyMaxHeight = 547.dp
private const val BookmarkEmptyHeightRatio = 1.6f

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmptyBookmarkState(
    modifier: Modifier = Modifier,
    onExploreRoadmapsClick: (() -> Unit)? = null,
    onBrowseCategoriesClick: (() -> Unit)? = null
) {
    BookmarkStateContainer(
        modifier = modifier,
        showBorder = false,
        shadowElevation = Dimens.cardElevationNone
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val topDecorSize = (maxWidth * 0.35f).coerceIn(
                minimumValue = BookmarkEmptyTopDecorMinSize,
                maximumValue = BookmarkEmptyTopDecorMaxSize
            )
            val bottomDecorSize = (maxWidth * 0.292f).coerceIn(
                minimumValue = BookmarkEmptyBottomDecorMinSize,
                maximumValue = BookmarkEmptyBottomDecorMaxSize
            )
            val horizontalPadding = if (maxWidth < 320.dp) {
                Dimens.spacingXxl
            } else {
                Dimens.spacingHuge
            }
            val cardMinHeight = (maxWidth * BookmarkEmptyHeightRatio).coerceIn(
                minimumValue = BookmarkEmptyMinHeight,
                maximumValue = BookmarkEmptyMaxHeight
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = cardMinHeight)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(topDecorSize)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = topDecorSize
                            )
                        )
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(bottomDecorSize)
                        .clip(
                            RoundedCornerShape(
                                topEnd = bottomDecorSize
                            )
                        )
                        .background(Color(0xFFF0FDFA).copy(alpha = 0.8f))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = Dimens.spacingHuge
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = BookmarkEmptyContentMaxWidth)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .sizeIn(
                                    minWidth = Dimens.controlXl + Dimens.spacingLg,
                                    minHeight = Dimens.controlXl + Dimens.spacingLg
                                )
                                .clip(BookmarkEmptyIconContainerShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimens.iconXxl)
                            )
                        }

                        Text(
                            text = stringResource(R.string.bookmarks_empty_title),
                            style = BookmarkTextStyles.emptyTitle,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = Dimens.spacingXxl)
                        )

                        Text(
                            text = stringResource(R.string.bookmarks_empty_description),
                            style = BookmarkTextStyles.emptyBody,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(top = Dimens.spacingSm)
                                .fillMaxWidth()
                        )

                        Column(
                            modifier = Modifier
                                .padding(top = Dimens.spacingHuge)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                        ) {
                            RMapButton(
                                text = stringResource(R.string.bookmarks_empty_explore_roadmaps),
                                onClick = { onExploreRoadmapsClick?.invoke() },
                                modifier = Modifier.fillMaxWidth(),
                                variant = RMapButtonVariant.Primary,
                                size = RMapButtonSize.Large
                            )
                            RMapButton(
                                text = stringResource(R.string.bookmarks_empty_browse_categories),
                                onClick = { onBrowseCategoriesClick?.invoke() },
                                modifier = Modifier.fillMaxWidth(),
                                variant = RMapButtonVariant.Secondary,
                                size = RMapButtonSize.Large
                            )
                        }

                        BookmarkSuggestedDomains(
                            modifier = Modifier.padding(top = Dimens.spacingHuge)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkEmptyState(
    modifier: Modifier = Modifier,
    onExploreRoadmapsClick: (() -> Unit)? = null,
    onBrowseCategoriesClick: (() -> Unit)? = null
) {
    EmptyBookmarkState(
        modifier = modifier,
        onExploreRoadmapsClick = onExploreRoadmapsClick,
        onBrowseCategoriesClick = onBrowseCategoriesClick
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 620)
@Composable
private fun BookmarkEmptyStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        EmptyBookmarkState(modifier = Modifier.padding(Dimens.spacingXxl))
    }
}
