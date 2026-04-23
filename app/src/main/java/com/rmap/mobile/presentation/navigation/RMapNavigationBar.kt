package com.rmap.mobile.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.presentation.ui.theme.RMapTheme

enum class NavBarDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(
        labelRes = R.string.nav_home,
        icon = Icons.Outlined.Home,
    ),
    Bookmarks(
        labelRes = R.string.nav_bookmarks,
        icon = Icons.Outlined.Bookmarks,
    ),
    Explore(
        labelRes = R.string.nav_explore,
        icon = Icons.Outlined.Explore,
    ),
    More(
        labelRes = R.string.nav_more,
        icon = Icons.Outlined.Menu,
    )
}

@Composable
fun RMapNavigationBar(
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
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
        MaterialTheme.colorScheme.onBackground
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            )
            .alpha(if (isSelected) 1f else 0.40f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = androidx.compose.ui.res.stringResource(id = destination.labelRes),
            color = contentColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 80)
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
