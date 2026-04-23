package com.rmap.mobile.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.navigation.RMapNavigationBar
import com.rmap.mobile.presentation.ui.theme.RMapTheme

object ProfileNavigationRoute {
    const val HOME = "home"
    const val BOOKMARKS = "bookmarks"
    const val EXPLORE = "explore"
    const val AI = "ai"
    const val MORE = "more"
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RMapNavigationBar(
        selectedDestination = currentRoute.toDestination(),
        onDestinationSelected = { destination ->
            onNavigate(destination.toRoute())
        },
        modifier = modifier.fillMaxWidth()
    )
}

private fun String.toDestination(): NavBarDestination {
    return when (this) {
        ProfileNavigationRoute.HOME -> NavBarDestination.Home
        ProfileNavigationRoute.BOOKMARKS -> NavBarDestination.Bookmarks
        ProfileNavigationRoute.EXPLORE -> NavBarDestination.Explore
        ProfileNavigationRoute.AI -> NavBarDestination.Ai
        else -> NavBarDestination.More
    }
}

private fun NavBarDestination.toRoute(): String {
    return when (this) {
        NavBarDestination.Home -> ProfileNavigationRoute.HOME
        NavBarDestination.Bookmarks -> ProfileNavigationRoute.BOOKMARKS
        NavBarDestination.Explore -> ProfileNavigationRoute.EXPLORE
        NavBarDestination.Ai -> ProfileNavigationRoute.AI
        NavBarDestination.More -> ProfileNavigationRoute.MORE
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 100)
@Composable
private fun BottomNavBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BottomNavBar(
            currentRoute = ProfileNavigationRoute.MORE,
            onNavigate = {}
        )
    }
}
