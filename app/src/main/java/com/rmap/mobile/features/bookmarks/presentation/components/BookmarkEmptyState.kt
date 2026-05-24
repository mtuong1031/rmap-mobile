package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSecondaryContainerLight
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SecondaryLight
import com.rmap.mobile.core.ui.theme.SurfaceContainerHighLight
import com.rmap.mobile.core.ui.theme.SurfaceVariantLight
import com.rmap.mobile.core.ui.theme.cardShadow

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
        border = null
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
                        .background(PrimaryContainerLight.copy(alpha = 0.8f))
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
                                .background(PrimaryContainerLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.size(Dimens.iconXxl)
                            )
                        }

                        Text(
                            text = stringResource(R.string.bookmarks_empty_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.sp,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = Dimens.spacingXxl)
                        )

                        Text(
                            text = stringResource(R.string.bookmarks_empty_description),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 22.75.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.sp,
                                textAlign = TextAlign.Center
                            ),
                            color = SecondaryLight,
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

                        SuggestedDomains(
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

@Composable
fun BookmarkLoadingState(modifier: Modifier = Modifier) {
    BookmarkStateContainer(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMassive),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BookmarkErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    BookmarkStateContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingHuge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Dimens.iconXxl)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BookmarkStateContainer(
    modifier: Modifier = Modifier,
    border: BorderStroke? = BorderStroke(
        width = Dimens.borderThin,
        color = SurfaceContainerHighLight
    ),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(shape = AppShapes.largeCard)
            .clip(AppShapes.largeCard),
        shape = AppShapes.largeCard,
        color = MaterialTheme.colorScheme.surface,
        border = border,
        content = content
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestedDomains(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Text(
            text = stringResource(R.string.bookmarks_empty_suggested_domains),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                lineHeight = 16.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.55.sp,
                textAlign = TextAlign.Center
            ),
            color = OnSurfacePlaceholderLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DomainChip(text = stringResource(R.string.bookmarks_empty_domain_web_development))
                DomainChip(text = stringResource(R.string.bookmarks_empty_domain_data))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DomainChip(text = stringResource(R.string.bookmarks_empty_domain_devops))
                DomainChip(text = stringResource(R.string.bookmarks_empty_domain_design))
            }
        }
    }
}

@Composable
private fun DomainChip(text: String) {
    Box(
        modifier = Modifier
            .clip(AppShapes.iconContainerLarge)
            .background(SurfaceVariantLight)
            .border(
                width = Dimens.borderThin,
                color = SurfaceContainerHighLight,
                shape = AppShapes.iconContainerLarge
            )
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 19.5.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            ),
            color = OnSecondaryContainerLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 620)
@Composable
private fun BookmarkEmptyStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        EmptyBookmarkState(modifier = Modifier.padding(Dimens.spacingXxl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkLoadingStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkLoadingState(modifier = Modifier.padding(Dimens.spacingXxl))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkErrorStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkErrorState(
            message = "Unable to load bookmarks",
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
