package com.rmap.mobile.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.R

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
    AiAssistant(
        labelRes = R.string.nav_ai,
        icon = Icons.Outlined.AutoAwesome,
    ),
    Explore(
        labelRes = R.string.nav_explore,
        icon = Icons.Outlined.Explore,
    ),
    Profile(
        labelRes = R.string.nav_profile,
        icon = Icons.Outlined.Person,
    )
}
