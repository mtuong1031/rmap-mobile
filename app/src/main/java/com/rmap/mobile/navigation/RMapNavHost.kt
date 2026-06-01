package com.rmap.mobile.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.rmap.mobile.core.session.SessionEvent
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapProgressBanner
import com.rmap.mobile.features.airoadmap.presentation.screen.AiRoadmapScreen
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapEvent
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapViewModel
import com.rmap.mobile.features.bookmarks.presentation.screen.BookmarkWindowSizeClass
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.presentation.screen.AuthScreen
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthEvent
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthViewModel
import com.rmap.mobile.features.bookmarks.presentation.screen.BookmarksScreen
import com.rmap.mobile.features.bookmarks.presentation.viewmodel.BookmarksViewModel
import com.rmap.mobile.features.explore.presentation.screen.ExploreScreen
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreViewModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchAiSuggestionUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillStatusDefaults
import com.rmap.mobile.features.home.presentation.screen.HomeScreen
import com.rmap.mobile.features.home.presentation.screen.HomeSearchScreen
import com.rmap.mobile.features.home.presentation.viewmodel.HomeEvent
import com.rmap.mobile.features.home.presentation.viewmodel.HomeSearchEvent
import com.rmap.mobile.features.home.presentation.viewmodel.HomeSearchViewModel
import com.rmap.mobile.features.home.presentation.viewmodel.HomeViewModel
import com.rmap.mobile.features.profile.presentation.screen.NotificationSettingsScreen
import com.rmap.mobile.features.profile.presentation.screen.ProfileScreen
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsViewModel
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileViewModel
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapDetailScreen
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailEvent
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun RMapNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val authState by RMapAppGraph.authRepository.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMessage = stringResource(R.string.coming_soon_message)
    val debugNotificationSentMessage = stringResource(R.string.notification_debug_sent_snackbar)
    val roadmapSavedMessage = stringResource(R.string.bookmarks_roadmap_saved)
    val roadmapRemovedMessage = stringResource(R.string.bookmarks_roadmap_removed)
    val skillSavedMessage = stringResource(R.string.bookmarks_skill_saved)
    val skillRemovedMessage = stringResource(R.string.bookmarks_skill_removed)
    val bookmarkFailedMessage = stringResource(R.string.bookmarks_action_failed)
    val startDestination = if (authState is AuthState.Authenticated) AppRoutes.HOME else AppRoutes.AUTH
    val isDebugBuild = remember(context) {
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    LaunchedEffect(Unit) {
        RMapAppGraph.getCurrentUserUseCase()
    }

    if (authState == AuthState.Checking) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
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

    LaunchedEffect(Unit) {
        RMapAppGraph.sessionManager.events.collect { event ->
            when (event) {
                SessionEvent.SessionExpired -> navController.navigate(AppRoutes.AUTH) {
                    popUpTo(AppRoutes.HOME) { inclusive = true }
                    launchSingleTop = true
                }
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
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            AuthEvent.NavigateToHome -> navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.AUTH) { inclusive = true }
                            }
                        }
                    }
                }

                AuthScreen(
                    uiState = uiState,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onFullNameChange = viewModel::onFullNameChange,
                    onToggleMode = viewModel::onToggleMode,
                    onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                    onSubmit = viewModel::onSubmit
                )
            }

            composable(AppRoutes.HOME) {
                val viewModel: HomeViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            HomeEvent.RoadmapBookmarkSaved -> snackbarHostState.showSnackbar(roadmapSavedMessage)
                            HomeEvent.RoadmapBookmarkRemoved -> snackbarHostState.showSnackbar(roadmapRemovedMessage)
                            HomeEvent.BookmarkActionFailed -> snackbarHostState.showSnackbar(bookmarkFailedMessage)
                        }
                    }
                }

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
                    onRecommendedRoadmapBookmarkClick = viewModel::onRecommendedRoadmapBookmarkClick,
                    onCategoryItemClick = { _, item ->
                        navController.navigate(AppRoutes.explore(item.id)) {
                            launchSingleTop = true
                        }
                    },
                    onCreateRoadmapWithAiClick = {
                        navController.navigate(AppRoutes.AI_ROADMAP) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.HOME_SEARCH) {
                val viewModel: HomeSearchViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            HomeSearchEvent.RoadmapBookmarkSaved -> snackbarHostState.showSnackbar(roadmapSavedMessage)
                            HomeSearchEvent.RoadmapBookmarkRemoved -> snackbarHostState.showSnackbar(roadmapRemovedMessage)
                            HomeSearchEvent.BookmarkActionFailed -> snackbarHostState.showSnackbar(bookmarkFailedMessage)
                        }
                    }
                }

                HomeSearchScreen(
                    query = uiState.query,
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
                    recommendedRoadmaps = uiState.recommendedRoadmaps,
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
                    isLoading = uiState.isLoading,
                    onQueryChange = viewModel::onQueryChange,
                    onBackClick = { navController.popBackStack() },
                    onFilterClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onSuggestionClick = viewModel::onQueryChange,
                    onClearRecentSearchesClick = {},
                    onRecentSearchClick = viewModel::onQueryChange,
                    onRemoveRecentSearchClick = {},
                    onPopularSearchClick = viewModel::onQueryChange,
                    onRecommendedRoadmapClick = { item ->
                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                    },
                    onRecommendedRoadmapBookmarkClick = viewModel::onRecommendedRoadmapBookmarkClick,
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

            composable(
                route = AppRoutes.EXPLORE_WITH_CATEGORY,
                arguments = listOf(
                    navArgument(AppRoutes.EXPLORE_CATEGORY_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val viewModel: ExploreViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val categoryId = backStackEntry.arguments?.getString(AppRoutes.EXPLORE_CATEGORY_ARG)

                LaunchedEffect(categoryId) {
                    categoryId?.let(viewModel::selectCategoryById)
                }

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
                    onRoadmapBookmarkClick = viewModel::onRoadmapBookmarkClick,
                    onSkillClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                    },
                    onSkillBookmarkClick = viewModel::onSkillBookmarkClick
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

                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            RoadmapDetailEvent.RoadmapBookmarkSaved -> snackbarHostState.showSnackbar(roadmapSavedMessage)
                            RoadmapDetailEvent.RoadmapBookmarkRemoved -> snackbarHostState.showSnackbar(roadmapRemovedMessage)
                            RoadmapDetailEvent.SkillBookmarkSaved -> snackbarHostState.showSnackbar(skillSavedMessage)
                            RoadmapDetailEvent.SkillBookmarkRemoved -> snackbarHostState.showSnackbar(skillRemovedMessage)
                            RoadmapDetailEvent.BookmarkActionFailed -> snackbarHostState.showSnackbar(bookmarkFailedMessage)
                        }
                    }
                }

                RoadmapDetailScreen(
                    uiState = uiState,
                    onBackClick = ::navigateBackFromRoadmapDetail,
                    onBookmarkClick = viewModel::onBookmarkClick,
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
                    onNodeBookmarkClick = viewModel::onNodeBookmarkClick,
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
