package com.rmap.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rmap.mobile.features.bookmarks.presentation.BookmarksScreen
import com.rmap.mobile.features.bookmarks.presentation.BookmarksViewModel
import com.rmap.mobile.features.explore.presentation.ExploreScreen
import com.rmap.mobile.features.explore.presentation.ExploreViewModel
import com.rmap.mobile.features.home.presentation.HomeScreen
import com.rmap.mobile.features.home.presentation.HomeViewModel
import com.rmap.mobile.features.profile.presentation.ProfileScreen
import com.rmap.mobile.features.profile.presentation.ProfileViewModel
import com.rmap.mobile.features.roadmap.presentation.detail.RoadmapDetailScreen
import com.rmap.mobile.features.roadmap.presentation.detail.RoadmapDetailViewModel

@Composable
fun RMapNavHost(navController: NavHostController) {
    fun navigateFromBottomBar(destination: NavBarDestination) {
        val route = when (destination) {
            NavBarDestination.Home -> AppRoutes.HOME
            NavBarDestination.Bookmarks -> AppRoutes.BOOKMARKS
            NavBarDestination.Explore -> AppRoutes.EXPLORE
            NavBarDestination.More -> AppRoutes.PROFILE
            NavBarDestination.Ai -> return
        }

        navController.navigate(route) {
            popUpTo(AppRoutes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            HomeScreen(
                userName = uiState.userName,
                progressFraction = uiState.progressFraction,
                selectedDestination = NavBarDestination.Home,
                onDestinationSelected = ::navigateFromBottomBar,
                onRoadmapClick = { item ->
                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                        navController.navigate(AppRoutes.ROADMAP_DETAIL)
                    }
                }
            )
        }

        composable(AppRoutes.EXPLORE) {
            val viewModel: ExploreViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            ExploreScreen(
                userName = uiState.userName,
                searchQuery = uiState.searchQuery,
                selectedDestination = NavBarDestination.Explore,
                onDestinationSelected = ::navigateFromBottomBar,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onRoadmapClick = { item ->
                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                        navController.navigate(AppRoutes.ROADMAP_DETAIL)
                    }
                }
            )
        }

        composable(AppRoutes.BOOKMARKS) {
            val viewModel: BookmarksViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            BookmarksScreen(
                userName = uiState.userName,
                selectedTabIndex = uiState.selectedTabIndex,
                selectedDestination = NavBarDestination.Bookmarks,
                onDestinationSelected = ::navigateFromBottomBar,
                onTabSelected = viewModel::onTabSelected,
                onRoadmapActionClick = { item ->
                    if (item.title.contains("Frontend Pro", ignoreCase = true)) {
                        navController.navigate(AppRoutes.ROADMAP_DETAIL)
                    }
                }
            )
        }

        composable(AppRoutes.PROFILE) {
            val viewModel: ProfileViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            ProfileScreen(
                uiState = uiState,
                selectedDestination = NavBarDestination.More,
                onDestinationSelected = ::navigateFromBottomBar,
                onEditProfile = {},
                onSettingClick = {}
            )
        }

        composable(AppRoutes.ROADMAP_DETAIL) {
            val viewModel: RoadmapDetailViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            RoadmapDetailScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                selectedDestination = NavBarDestination.Home,
                onDestinationSelected = { destination ->
                    if (destination == NavBarDestination.Home) {
                        navController.popBackStack(AppRoutes.HOME, false)
                    } else {
                        navigateFromBottomBar(destination)
                    }
                }
            )
        }
    }
}
