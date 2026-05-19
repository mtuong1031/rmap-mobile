package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.navigation.NavBarDestination

private object AppNavigationBarDefaults {
    val HorizontalPadding: Dp = Dimens.spacingXxl
    val TopPadding: Dp = Dimens.spacingXl
    val BottomPadding: Dp = Dimens.spacingXl
    val ItemHeight: Dp = 55.dp
    val IconSize: Dp = Dimens.iconLg
    val ItemSpacing: Dp = Dimens.spacingXsPlus
    val MinItemWidth: Dp = Dimens.controlLg
    val SelectedIndicatorSize: Dp = Dimens.spacingXs
    val UnselectedBottomSpacer: Dp = Dimens.spacingSmPlus
}

@Composable
fun AppNavigationBar(
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = AppShapes.navigationBar,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimens.cardElevationMd
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.borderThin)
                    .background(Color(0xFFF3F4F6))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(
                        start = AppNavigationBarDefaults.HorizontalPadding,
                        top = AppNavigationBarDefaults.TopPadding,
                        end = AppNavigationBarDefaults.HorizontalPadding,
                        bottom = AppNavigationBarDefaults.BottomPadding
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarDestination.entries.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        isSelected = destination == selectedDestination,
                        onSelected = {
                            onDestinationSelected(destination)
                        }
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
    val interactionSource = remember { MutableInteractionSource() }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(0xFF99A1AF)
    }

    Column(
        modifier = Modifier
            .height(AppNavigationBarDefaults.ItemHeight)
            .widthIn(min = AppNavigationBarDefaults.MinItemWidth)
            .clip(AppShapes.small)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = stringResource(id = destination.labelRes),
            tint = contentColor,
            modifier = Modifier.size(AppNavigationBarDefaults.IconSize)
        )

        Spacer(modifier = Modifier.height(AppNavigationBarDefaults.ItemSpacing))

        Text(
            text = stringResource(id = destination.labelRes),
            color = contentColor,
            style = AppTextStyles.navigationLabel.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(AppNavigationBarDefaults.ItemSpacing))
            Box(
                modifier = Modifier
                    .size(AppNavigationBarDefaults.SelectedIndicatorSize)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = AppShapes.pill
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(AppNavigationBarDefaults.UnselectedBottomSpacer))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 120)
@Composable
private fun RMapNavigationBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AppNavigationBar(
            selectedDestination = NavBarDestination.Home,
            onDestinationSelected = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
