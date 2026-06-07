package com.rmap.mobile.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.navigation.NavBarDestination

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
            Row(
                modifier = Modifier.padding(Dimens.spacingXsPlus),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarDestination.entries.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        isSelected = destination == selectedDestination,
                        onSelected = { onDestinationSelected(destination) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    destination: NavBarDestination,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else OnSurfacePlaceholderLight,
        label = "contentColor"
    )

    Row(
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected
            )
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
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
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
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
