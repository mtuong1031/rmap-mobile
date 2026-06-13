package com.rmap.mobile.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.icons.RMapIcons

enum class NavBarDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(
        labelRes = R.string.nav_home,
        icon = Icons.Outlined.Home,
    ),
    MyRoadmap(
        labelRes = R.string.nav_my_roadmap,
        icon = RMapIcons.Map,
    ),
    AiAssistant(
        labelRes = R.string.nav_ai,
        icon = Icons.Outlined.AutoAwesome,
    ),
    Explore(
        labelRes = R.string.nav_explore,
        icon = Icons.Outlined.Search,
    )
}
