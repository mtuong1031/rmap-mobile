package com.rmap.mobile.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapProgressBanner
import com.rmap.mobile.features.airoadmap.presentation.screen.AiRoadmapScreen
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapEvent
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapViewModel
import com.rmap.mobile.features.bookmarks.presentation.screen.BookmarkWindowSizeClass
import com.rmap.mobile.features.auth.presentation.screen.AuthScreen
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthEvent
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthViewModel
import com.rmap.mobile.features.bookmarks.presentation.screen.BookmarksScreen
import com.rmap.mobile.features.bookmarks.presentation.viewmodel.BookmarksViewModel
import com.rmap.mobile.features.explore.presentation.screen.ExploreScreen
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreViewModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchAiSuggestionUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemDefaults
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillStatusDefaults
import com.rmap.mobile.features.home.presentation.screen.HomeScreen
import com.rmap.mobile.features.home.presentation.screen.HomeSearchScreen
import com.rmap.mobile.features.home.presentation.viewmodel.HomeViewModel
import com.rmap.mobile.features.profile.presentation.screen.NotificationSettingsScreen
import com.rmap.mobile.features.profile.presentation.screen.ProfileScreen
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsViewModel
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileViewModel
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapDetailScreen
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun RMapNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val isAuthenticated by RMapAppGraph.sessionRepository.isAuthenticated.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMessage = stringResource(R.string.coming_soon_message)
    val debugNotificationSentMessage = stringResource(R.string.notification_debug_sent_snackbar)
    val startDestination = if (isAuthenticated) AppRoutes.HOME else AppRoutes.AUTH
    val isDebugBuild = remember(context) {
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun navigateFromBottomBar(destination: NavBarDestination) {
        val route = when (destination) {
            NavBarDestination.Home -> AppRoutes.HOME
            NavBarDestination.Bookmarks -> AppRoutes.BOOKMARKS
            NavBarDestination.Explore -> AppRoutes.EXPLORE
            NavBarDestination.More -> AppRoutes.PROFILE
            NavBarDestination.AiAssistant -> AppRoutes.AI_ROADMAP
        }

        navController.navigate(route) {
            popUpTo(AppRoutes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun handleDestinationSelected(destination: NavBarDestination) {
        navigateFromBottomBar(destination)
    }

    fun navigateBackFromRoadmapDetail() {
        if (!navController.popBackStack()) {
            val currentRoute = navController.currentDestination?.route

            navController.navigate(startDestination) {
                currentRoute?.let { route ->
                    popUpTo(route) { inclusive = true }
                }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val aiGenerationStatus by RMapAppGraph.aiRoadmapRepository.generationStatus.collectAsStateWithLifecycle()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val bookmarkWindowSizeClass = BookmarkWindowSizeClass.fromWidth(maxWidth)

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
                    onSearchClick = {
                        navController.navigate(AppRoutes.HOME_SEARCH) {
                            launchSingleTop = true
                        }
                    },
                    onRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
                    onContinueLearningPlanClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
                    onCreateRoadmapWithAiClick = {
                        navController.navigate(AppRoutes.AI_ROADMAP) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.HOME_SEARCH) {
                var searchQuery by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }

                HomeSearchScreen(
                    query = searchQuery,
                    suggestions = listOf("Frontend", "Backend", "React", "AI", "DevOps"),
                    recentSearches = listOf(
                        "React roadmap",
                        "CSS Grid",
                        "Backend developer",
                        "DevOps",
                        "AI Engineer"
                    ),
                    popularSearches = listOf(
                        "Frontend",
                        "React",
                        "Backend",
                        "DevOps",
                        "Data Analyst",
                        "AI Engineer"
                    ),
                    recommendedRoadmaps = listOf(
                        HomeSearchRoadmapItemUiModel(
                            id = "react-fundamentals",
                            title = "React Fundamentals",
                            categoryLabel = "Web Development",
                            metadataText = "4 weeks",
                            leadingIcon = Icons.Outlined.TrackChanges,
                            style = HomeSearchRoadmapItemDefaults.reactStyle()
                        ),
                        HomeSearchRoadmapItemUiModel(
                            id = "frontend-starter",
                            title = "Frontend Interview Prep",
                            categoryLabel = "Web Development",
                            metadataText = "3 weeks",
                            leadingText = "FI",
                            style = HomeSearchRoadmapItemDefaults.starterStyle()
                        )
                    ),
                    skills = listOf(
                        HomeSearchSkillItemUiModel(
                            id = "frontend-react",
                            title = "Frontend",
                            parentText = "Part of: React Fundamentals",
                            statusText = "Not started",
                            statusStyle = HomeSearchSkillStatusDefaults.notStartedStyle()
                        ),
                        HomeSearchSkillItemUiModel(
                            id = "frontend-pro",
                            title = "Frontend",
                            parentText = "Part of: Frontend Pro",
                            statusText = "In progress",
                            statusStyle = HomeSearchSkillStatusDefaults.inProgressStyle()
                        )
                    ),
                    aiSuggestion = HomeSearchAiSuggestionUiModel(
                        id = "react-roadmap",
                        title = "Create a personalized React roadmap",
                        description = "Generate a roadmap based on your goal, current skills, and timeline.",
                        actionText = "Create with AI"
                    ),
                    onQueryChange = { searchQuery = it },
                    onBackClick = { navController.popBackStack() },
                    onFilterClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onSuggestionClick = { searchQuery = it },
                    onClearRecentSearchesClick = {},
                    onRecentSearchClick = { searchQuery = it },
                    onRemoveRecentSearchClick = {},
                    onPopularSearchClick = { searchQuery = it },
                    onRecommendedRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
                    onRecommendedRoadmapBookmarkClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onSkillClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onCreateWithAiClick = {
                        navController.navigate(AppRoutes.AI_ROADMAP) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.AI_ROADMAP) {
                val viewModel: AiRoadmapViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is AiRoadmapEvent.NavigateToRoadmapDetail -> {
                                navController.navigate(AppRoutes.roadmapDetail(event.roadmapId))
                            }
                        }
                    }
                }

                AiRoadmapScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.AiAssistant,
                    onDestinationSelected = ::handleDestinationSelected,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onCreateRoadmapClick = viewModel::onCreateRoadmapClick,
                    onSeeMoreGeneratedRoadmaps = viewModel::onSeeMoreGeneratedRoadmaps,
                    onSeeAllGeneratedRoadmaps = viewModel::onSeeAllGeneratedRoadmaps,
                    onSeeLessGeneratedRoadmaps = viewModel::onSeeLessGeneratedRoadmaps,
                    onBackToLibrary = viewModel::onBackToLibrary,
                    onTopicChange = viewModel::onTopicChange,
                    onDeadlineSelected = viewModel::onDeadlineSelected,
                    onDailyStudyHoursChange = viewModel::onDailyStudyHoursChange,
                    onSubmitSetup = viewModel::onSubmitSetup,
                    onOptionSelected = viewModel::onOptionSelected,
                    onCustomAnswerChange = viewModel::onCustomAnswerChange,
                    onPreviousQuestion = viewModel::onPreviousQuestion,
                    onNextQuestion = viewModel::onNextQuestion,
                    onSubmitAnswers = viewModel::onSubmitAnswers,
                    onCancelGeneration = viewModel::onCancelGeneration,
                    onExploreClick = {
                        navController.navigate(AppRoutes.HOME) {
                            launchSingleTop = true
                        }
                    },
                    onExploreRoadmapsClick = {
                        navigateFromBottomBar(NavBarDestination.Explore)
                    },
                    onRoadmapSelected = viewModel::onRoadmapSelected
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
                    onCategoryClick = viewModel::onCategorySelected,
                    onViewAllCategoriesClick = viewModel::onViewAllCategories,
                    onSeeMoreRoadmapsClick = viewModel::onSeeMoreRoadmaps,
                    onSeeAllRoadmapsClick = viewModel::onSeeAllRoadmaps,
                    onSeeLessRoadmapsClick = viewModel::onSeeLessRoadmaps,
                    onPopularRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
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
                    windowSizeClass = bookmarkWindowSizeClass,
                    selectedDestination = NavBarDestination.Bookmarks,
                    onDestinationSelected = ::handleDestinationSelected,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onTabSelected = viewModel::onTabSelected,
                    onStatusFilterSelected = viewModel::onStatusFilterSelected,
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
                            ProfileEvent.NavigateToNotificationSettings -> navController.navigate(AppRoutes.NOTIFICATION_SETTINGS)
                            ProfileEvent.ShowComingSoon -> snackbarHostState.showSnackbar(profileComingSoonMessage)
                            ProfileEvent.SignedOut -> navController.navigate(AppRoutes.AUTH) {
                                popUpTo(AppRoutes.HOME) { inclusive = true }
                            }
                        }
                    }
                }

                ProfileScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.More,
                    onDestinationSelected = ::handleDestinationSelected,
                    onEditProfile = viewModel::onEditProfile,
                    onSettingClick = viewModel::onSettingClick
                )
            }

            composable(AppRoutes.NOTIFICATION_SETTINGS) {
                val viewModel: NotificationSettingsViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val permissionRequiredMessage = stringResource(R.string.notification_permission_required_snackbar)

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            NotificationSettingsEvent.ShowNotificationPermissionRequired -> {
                                snackbarHostState.showSnackbar(permissionRequiredMessage)
                            }
                        }
                    }
                }

                NotificationSettingsScreen(
                    uiState = uiState,
                    selectedDestination = NavBarDestination.More,
                    onBackClick = { navController.popBackStack() },
                    onNotificationPermissionStateChanged = viewModel::onNotificationPermissionStateChanged,
                    onNotificationPermissionDenied = viewModel::onNotificationPermissionDenied,
                    onAllowNotificationsChange = viewModel::onAllowNotificationsChange,
                    onReminderTimeSelected = viewModel::onReminderTimeSelected,
                    onReminderFrequencySelected = viewModel::onReminderFrequencySelected,
                    isDebugNotificationTestVisible = isDebugBuild,
                    onSendTestNotificationClick = {
                        RMapAppGraph.learningNotificationNotifier.showLearningReminder()
                        coroutineScope.launch { snackbarHostState.showSnackbar(debugNotificationSentMessage) }
                    },
                    onDestinationSelected = { destination ->
                        navigateFromBottomBar(destination)
                    }
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
                    onBackClick = ::navigateBackFromRoadmapDetail,
                    onMoreClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onContinueClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSearchFocus = viewModel::onSearchFocus,
                    onSearchFocusChange = viewModel::onSearchFocusChange,
                    onSearchClearClick = viewModel::onSearchClearClick,
                    onSearchBackClick = viewModel::onSearchBackClick,
                    onNodeActionClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onGroupClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onMilestoneClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    }
                )
            }
            }
        }

        if (aiGenerationStatus.isActive && currentRoute != AppRoutes.AI_ROADMAP) {
            AiRoadmapProgressBanner(
                status = aiGenerationStatus,
                title = stringResource(R.string.ai_roadmap_banner_title),
                actionText = stringResource(R.string.ai_roadmap_banner_action),
                onClick = {
                    navController.navigate(AppRoutes.AI_ROADMAP) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = Dimens.spacingScreenHorizontal, vertical = Dimens.spacingMd)
                    .fillMaxWidth()
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
