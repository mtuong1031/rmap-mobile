package com.rmap.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rmap.mobile.presentation.home.HomeScreen
import com.rmap.mobile.presentation.bookmarks.BookmarksScreen
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.ui.theme.RMapTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rmap.mobile.presentation.roadmapdetail.RoadmapDetailScreen

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

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                userName = "Thinh",
                                selectedDestination = NavBarDestination.Home,
                                onDestinationSelected = { dest ->
                                    if (dest == NavBarDestination.Bookmarks) {
                                        navController.navigate("bookmarks") {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
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
                                onDestinationSelected = { dest ->
                                    if (dest == NavBarDestination.Home) {
                                        navController.navigate("home") {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                        composable("roadmap_detail") {
                            RoadmapDetailScreen(
                                navController = navController,
                                selectedDestination = NavBarDestination.Home,
                                onDestinationSelected = { dest ->
                                    if (dest == NavBarDestination.Home) {
                                        navController.popBackStack("home", false)
                                    } else if (dest == NavBarDestination.Bookmarks) {
                                        navController.navigate("bookmarks") {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
