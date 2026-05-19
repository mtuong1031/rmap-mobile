package com.rmap.mobile.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.icons.RMapIcons

enum class NavBarDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(
        labelRes = R.string.nav_home,
        icon = RMapIcons.Map,
    ),
    Bookmarks(
        labelRes = R.string.nav_bookmarks,
        icon = Icons.Outlined.BookmarkBorder,
    ),
    Explore(
        labelRes = R.string.nav_explore,
        icon = Icons.Outlined.Explore,
    ),
    AiAssistant(
        labelRes = R.string.nav_ai,
        icon = Icons.Outlined.AutoAwesome,
    ),
    Profile(
        labelRes = R.string.nav_more,
        icon = Icons.Outlined.Menu,
    )
}
