package com.rmap.mobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.presentation.screen.AuthScreen
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthEvent
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthViewModel
import com.rmap.mobile.features.bookmarks.presentation.screen.BookmarksScreen
import com.rmap.mobile.features.bookmarks.presentation.viewmodel.BookmarksViewModel
import com.rmap.mobile.features.explore.presentation.screen.ExploreScreen
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreViewModel
import com.rmap.mobile.features.home.presentation.screen.HomeScreen
import com.rmap.mobile.features.home.presentation.viewmodel.HomeViewModel
import com.rmap.mobile.features.profile.presentation.screen.ProfileScreen
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileViewModel
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapDetailScreen
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun RMapNavHost(navController: NavHostController) {
    val isAuthenticated by RMapAppGraph.sessionRepository.isAuthenticated.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMessage = stringResource(R.string.coming_soon_message)
    val startDestination = if (isAuthenticated) AppRoutes.HOME else AppRoutes.AUTH

    fun navigateFromBottomBar(destination: NavBarDestination) {
        val route = when (destination) {
            NavBarDestination.Home -> AppRoutes.HOME
            NavBarDestination.Bookmarks -> AppRoutes.BOOKMARKS
            NavBarDestination.Explore -> AppRoutes.EXPLORE
            NavBarDestination.Profile -> AppRoutes.PROFILE
            NavBarDestination.AiAssistant -> return
        }

        navController.navigate(route) {
            popUpTo(AppRoutes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun handleDestinationSelected(destination: NavBarDestination) {
        if (destination == NavBarDestination.AiAssistant) {
            coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
        } else {
            navigateFromBottomBar(destination)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(AppRoutes.AUTH) {
                val viewModel: AuthViewModel = viewModel()
                val signInFailedMessage = stringResource(R.string.auth_sign_in_failed)

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            AuthEvent.NavigateToHome -> navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.AUTH) { inclusive = true }
                            }

                            AuthEvent.ShowSignInFailed -> snackbarHostState.showSnackbar(signInFailedMessage)
                        }
                    }
                }

                AuthScreen(
                    onContinueWithGoogle = viewModel::onContinueWithGoogle,
                    onContinueWithFacebook = viewModel::onContinueWithFacebook
                )
            }

            composable(AppRoutes.HOME) {
                val viewModel: HomeViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.Home,
                    onDestinationSelected = ::handleDestinationSelected,
                    onRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    }
                )
            }

            composable(AppRoutes.EXPLORE) {
                val viewModel: ExploreViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ExploreScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.Explore,
                    onDestinationSelected = ::handleDestinationSelected,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    }
                )
            }

            composable(AppRoutes.BOOKMARKS) {
                val viewModel: BookmarksViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                BookmarksScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.Bookmarks,
                    onDestinationSelected = ::handleDestinationSelected,
                    onTabSelected = viewModel::onTabSelected,
                    onRoadmapActionClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
                    onRoadmapShareClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                )
            }

            composable(AppRoutes.PROFILE) {
                val viewModel: ProfileViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val profileComingSoonMessage = stringResource(R.string.coming_soon_message)

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            ProfileEvent.ShowComingSoon -> snackbarHostState.showSnackbar(profileComingSoonMessage)
                            ProfileEvent.SignedOut -> navController.navigate(AppRoutes.AUTH) {
                                popUpTo(AppRoutes.HOME) { inclusive = true }
                            }
                        }
                    }
                }

                ProfileScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.Profile,
                    onDestinationSelected = ::handleDestinationSelected,
                    onEditProfile = viewModel::onEditProfile,
                    onSettingClick = viewModel::onSettingClick
                )
            }

            composable(
                route = AppRoutes.ROADMAP_DETAIL,
                arguments = listOf(navArgument(AppRoutes.ROADMAP_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val viewModel: RoadmapDetailViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val roadmapId = backStackEntry.arguments?.getString(AppRoutes.ROADMAP_ID_ARG).orEmpty()

                LaunchedEffect(roadmapId) {
                    viewModel.loadRoadmap(roadmapId)
                }

                RoadmapDetailScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    selectedDestination = NavBarDestination.Home,
                    onDestinationSelected = { destination ->
                        if (destination == NavBarDestination.Home) {
                            navController.popBackStack(AppRoutes.HOME, false)
                        } else if (destination == NavBarDestination.AiAssistant) {
                            coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                        } else {
                            navigateFromBottomBar(destination)
                        }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
