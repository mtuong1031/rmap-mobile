package com.rmap.mobile.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.navigation.NavBarDestination
import kotlin.math.roundToInt

@Composable
fun RMapNavigationBar(
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = Dimens.spacingLg),
        contentAlignment = Alignment.BottomCenter
    ) {
        RMapCard(
            shape = AppShapes.pill,
            color = MaterialTheme.colorScheme.surface,
            border = null,
            shadowElevation = Dimens.cardElevationXxl
        ) {
            var itemLayoutInfo by remember { mutableStateOf<Map<NavBarDestination, Pair<Float, Float>>>(emptyMap()) }
            val selectedLayoutInfo = itemLayoutInfo[selectedDestination]
            val indicatorOffset = selectedLayoutInfo?.first ?: 0f
            val indicatorWidth = selectedLayoutInfo?.second ?: 0f
            var hasInitializedIndicator by remember { mutableStateOf(false) }

            val animatedOffset by animateFloatAsState(
                targetValue = indicatorOffset,
                animationSpec = tween(250, easing = FastOutSlowInEasing),
                label = "indicatorOffset"
            )
            val animatedWidth by animateFloatAsState(
                targetValue = indicatorWidth,
                animationSpec = tween(250, easing = FastOutSlowInEasing),
                label = "indicatorWidth"
            )
            val displayedOffset = if (hasInitializedIndicator) animatedOffset else indicatorOffset
            val displayedWidth = if (hasInitializedIndicator) animatedWidth else indicatorWidth

            LaunchedEffect(selectedLayoutInfo) {
                if (selectedLayoutInfo != null && !hasInitializedIndicator) {
                    hasInitializedIndicator = true
                }
            }

            Box(modifier = Modifier.padding(Dimens.spacingXsPlus).height(IntrinsicSize.Min)) {
                if (indicatorWidth > 0f) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(displayedOffset.roundToInt(), 0) }
                            .width(with(LocalDensity.current) { displayedWidth.toDp() })
                            .fillMaxHeight()
                            .clip(AppShapes.pill)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarDestination.entries.forEach { destination ->
                        NavBarItem(
                            destination = destination,
                            isSelected = destination == selectedDestination,
                            onSelected = { onDestinationSelected(destination) },
                            onPlaced = { offset, width ->
                                itemLayoutInfo = itemLayoutInfo.toMutableMap().apply {
                                    put(destination, offset to width)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    destination: NavBarDestination,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onPlaced: (Float, Float) -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else OnSurfacePlaceholderLight,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "contentColor"
    )

    Row(
        modifier = Modifier
            .clip(AppShapes.pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected
            )
            .onGloballyPositioned { layoutCoordinates ->
                onPlaced(layoutCoordinates.positionInParent().x, layoutCoordinates.size.width.toFloat())
            }
            .animateContentSize(animationSpec = tween(250, easing = FastOutSlowInEasing))
            .padding(
                horizontal = if (isSelected) Dimens.spacingLg else Dimens.spacingMd,
                vertical = Dimens.spacingMd
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = stringResource(id = destination.labelRes),
            tint = contentColor,
            modifier = Modifier.size(Dimens.iconLg)
        )

        AnimatedVisibility(
            visible = isSelected,
            enter = expandHorizontally(animationSpec = tween(250, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)),
            exit = shrinkHorizontally(animationSpec = tween(250, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(250, easing = FastOutSlowInEasing))
        ) {
            Row {
                Spacer(modifier = Modifier.width(Dimens.spacingXsPlus))
                Text(
                    text = stringResource(id = destination.labelRes),
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390, heightDp = 100)
@Composable
private fun RMapNavigationBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapNavigationBar(
            selectedDestination = NavBarDestination.Home,
            onDestinationSelected = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390, heightDp = 100)
@Composable
private fun RMapNavigationBarAiSelectedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapNavigationBar(
            selectedDestination = NavBarDestination.AiAssistant,
            onDestinationSelected = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
