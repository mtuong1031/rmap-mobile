package com.rmap.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rmap.mobile.presentation.bookmarks.BookmarksScreen
import com.rmap.mobile.presentation.explore.ExploreScreen
import com.rmap.mobile.presentation.home.HomeScreen
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.profile.ProfileScreen
import com.rmap.mobile.presentation.profile.ProfileUiState
import com.rmap.mobile.presentation.roadmapdetail.RoadmapDetailScreen
import com.rmap.mobile.presentation.ui.components.ProfileNavigationRoute
import com.rmap.mobile.presentation.ui.theme.RMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    fun navigateFromBottomBar(destination: NavBarDestination) {
                        val route = when (destination) {
                            NavBarDestination.Home -> "home"
                            NavBarDestination.Bookmarks -> "bookmarks"
                            NavBarDestination.Explore -> "explore"
                            NavBarDestination.More -> "profile"
                            NavBarDestination.Ai -> return
                        }

                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                userName = "Thinh",
                                selectedDestination = NavBarDestination.Home,
                                onDestinationSelected = ::navigateFromBottomBar,
                                onRoadmapClick = { item ->
                                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                                        navController.navigate("roadmap_detail")
                                    }
                                }
                            )
                        }
                        composable("explore") {
                            ExploreScreen(
                                userName = "Thinh",
                                selectedDestination = NavBarDestination.Explore,
                                onDestinationSelected = ::navigateFromBottomBar,
                                onRoadmapClick = { item ->
                                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                                        navController.navigate("roadmap_detail")
                                    }
                                }
                            )
                        }
                        composable("bookmarks") {
                            BookmarksScreen(
                                userName = "Thinh",
                                selectedDestination = NavBarDestination.Bookmarks,
                                onDestinationSelected = ::navigateFromBottomBar,
                                onRoadmapActionClick = { item ->
                                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                                        navController.navigate("roadmap_detail")
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                uiState = ProfileUiState(
                                    name = "Thinh Duy",
                                    role = "Aspiring Frontend Developer",
                                    avatarUrl = "",
                                    xp = 450,
                                    streak = 5,
                                    certificates = 2
                                ),
                                onEditProfile = {},
                                onSettingClick = {},
                                onNavigate = { route ->
                                    when (route) {
                                        ProfileNavigationRoute.HOME -> navigateFromBottomBar(NavBarDestination.Home)
                                        ProfileNavigationRoute.BOOKMARKS -> navigateFromBottomBar(NavBarDestination.Bookmarks)
                                        ProfileNavigationRoute.MORE -> navigateFromBottomBar(NavBarDestination.More)
                                        else -> Unit
                                    }
                                },
                                currentRoute = ProfileNavigationRoute.MORE
                            )
                        }
                        composable("roadmap_detail") {
                            RoadmapDetailScreen(
                                navController = navController,
                                selectedDestination = NavBarDestination.Home,
                                onDestinationSelected = { dest ->
                                    if (dest == NavBarDestination.Home) {
                                        navController.popBackStack("home", false)
                                    } else {
                                        navigateFromBottomBar(dest)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
