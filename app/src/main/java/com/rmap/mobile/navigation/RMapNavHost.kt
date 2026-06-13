package com.rmap.mobile.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rmap.mobile.features.myroadmap.presentation.screen.MyRoadmapScreen
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.channels.BufferOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationAction
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.core.notification.showRMapSnackbar
import com.rmap.mobile.core.ui.components.RMapSnackbar
import com.rmap.mobile.core.session.SessionEvent
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapProgressBanner
import com.rmap.mobile.features.airoadmap.presentation.screen.AiRoadmapScreen
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapEvent
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapFormError
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapStep
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapViewModel
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.presentation.screen.AuthScreen
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthEvent
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthViewModel
import com.rmap.mobile.features.explore.presentation.screen.ExploreScreen
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreViewModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchAiSuggestionUiModel
import com.rmap.mobile.features.home.presentation.screen.HomeScreen
import com.rmap.mobile.features.home.presentation.screen.HomeSearchScreen
import com.rmap.mobile.features.home.presentation.viewmodel.HomeSearchViewModel
import com.rmap.mobile.features.home.presentation.viewmodel.HomeViewModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapViewModel
import com.rmap.mobile.features.myroadmap.presentation.viewmodel.MyRoadmapEvent
import com.rmap.mobile.features.profile.presentation.screen.NotificationSettingsScreen
import com.rmap.mobile.features.profile.presentation.screen.PersonalInformationScreen
import com.rmap.mobile.features.profile.presentation.screen.PrivacySecurityScreen
import com.rmap.mobile.features.profile.presentation.screen.ProfileScreen
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.PersonalInformationEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.PersonalInformationViewModel
import com.rmap.mobile.features.profile.presentation.viewmodel.PrivacySecurityEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.PrivacySecurityViewModel
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileEvent
import com.rmap.mobile.features.profile.presentation.viewmodel.NotificationSettingsViewModel
import com.rmap.mobile.features.profile.presentation.viewmodel.ProfileViewModel
import com.rmap.mobile.features.roadmap.presentation.screen.NodeQuizScreen
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapDetailScreen
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapDetailScrollTarget
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapLearningScreen
import com.rmap.mobile.features.roadmap.presentation.screen.RoadmapMilestoneScreen
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizViewModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailEvent
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailViewModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapLearningEvent
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapLearningViewModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneEvent
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneViewModel
import kotlinx.coroutines.launch

@Composable
fun RMapNavHost(
    navController: NavHostController,
    initialNotificationRoute: String? = null
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val authState by RMapAppGraph.authRepository.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMessage = stringResource(R.string.coming_soon_message)

    val nodeProgressUpdatedMessage = stringResource(R.string.roadmap_detail_progress_updated)
    val nodeProgressUpdateFailedMessage = stringResource(R.string.roadmap_detail_progress_update_failed)
    val learningCompletedMessage = stringResource(R.string.roadmap_learning_completed_snackbar)
    val learningCompletionRequiresQuizMessage = stringResource(R.string.roadmap_learning_completion_requires_quiz)
    val learningLockedQuizMessage = stringResource(R.string.roadmap_learning_locked_quiz_snackbar)
    val resourceOpenFailedMessage = stringResource(R.string.roadmap_learning_resource_open_failed)
    val milestoneSubmissionQueuedMessage = stringResource(R.string.roadmap_milestone_submission_queued)
    val milestoneSubmissionFailedMessage = stringResource(R.string.roadmap_milestone_error_submit_failed)
    val profileUpdatedMessage = stringResource(R.string.personal_information_profile_updated)
    val passwordChangedMessage = stringResource(R.string.privacy_security_password_changed)
    val myRoadmapRefreshFailedMessage = stringResource(R.string.my_roadmap_refresh_failed)
    val debugNotificationSentMessage = stringResource(R.string.notification_debug_sent_snackbar)
    val snackbarSuccessTitle = stringResource(R.string.snackbar_title_success)
    val snackbarErrorTitle = stringResource(R.string.snackbar_title_error)
    val snackbarWarningTitle = stringResource(R.string.snackbar_title_warning)
    val snackbarInfoTitle = stringResource(R.string.snackbar_title_info)
    val startDestination = AppRoutes.MAIN_TABS
    val pagerState = rememberPagerState(pageCount = { 5 })
    var isAiRoadmapSubScreen by remember { mutableStateOf(false) }
    var isAiRoadmapGenerating by remember { mutableStateOf(false) }
    var handledNotificationRoute by remember { mutableStateOf<String?>(null) }
    val currentUserAvatarUrl = (authState as? AuthState.Authenticated)?.user?.avatarUrl.orEmpty()
    var isAiRoadmapQuestionsStep by remember { mutableStateOf(false) }
    val isDebugBuild = remember(context) {
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
    
    val tabReselectEvent = remember { 
        MutableSharedFlow<NavBarDestination>(
            extraBufferCapacity = 1, 
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        ) 
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

    fun handleDestinationSelected(destination: NavBarDestination) {
        val targetPage = when (destination) {
            NavBarDestination.Home -> 0
            NavBarDestination.MyRoadmap -> 1
            NavBarDestination.AiAssistant -> 2
            NavBarDestination.Explore -> 3
        }
        val currentRoute = navController.currentDestination?.route

        if (currentRoute == AppRoutes.HOME_SEARCH) {
            navController.popBackStack(AppRoutes.MAIN_TABS, inclusive = false)
        }

        if (pagerState.currentPage == targetPage) {
            coroutineScope.launch { tabReselectEvent.emit(destination) }
        } else {
            coroutineScope.launch { pagerState.animateScrollToPage(targetPage) }
        }
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
                SessionEvent.SessionExpired -> {
                    RMapAppGraph.appNotificationManager.enqueue(
                        AppNotification(
                            titleResId = R.string.auth_required_title,
                            messageResId = R.string.session_expired_message,
                            variant = AppNotificationVariant.Warning,
                            actionLabelResId = R.string.action_login,
                            action = AppNotificationAction.Login
                        )
                    )
                    navController.navigate(AppRoutes.MAIN_TABS) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        RMapAppGraph.appNotificationManager.notifications.collect { notification ->
            val message = notification.message
                ?: notification.messageResId?.let { context.getString(it) }
                ?: ""

            val result = snackbarHostState.showRMapSnackbar(
                title = context.getString(notification.titleResId),
                message = message,
                variant = notification.variant,
                action = notification.action,
                actionLabel = notification.actionLabelResId?.let(context::getString),
                duration = notification.duration
            )
            if (
                result == SnackbarResult.ActionPerformed &&
                notification.action == AppNotificationAction.Login
            ) {
                navController.navigate(AppRoutes.AUTH) {
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(authState, initialNotificationRoute) {
        val route = initialNotificationRoute?.takeIf { it != handledNotificationRoute } ?: return@LaunchedEffect
        when (authState) {
            is AuthState.Authenticated -> {
                handledNotificationRoute = route
                navController.navigate(route) {
                    launchSingleTop = true
                }
            }
            AuthState.Checking -> Unit
            else -> {
                handledNotificationRoute = route
            }
        }
    }

    val aiGenerationStatus by RMapAppGraph.aiRoadmapRepository.generationStatus.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                RMapSnackbar(snackbarData = snackbarData)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            NavHost(
                navController = navController, 
                startDestination = startDestination,
                enterTransition = {
                    val targetRoute = targetState.destination.route
                    val initialRoute = initialState.destination.route
                    val isHomeAndSearch = (targetRoute == AppRoutes.HOME_SEARCH && initialRoute == AppRoutes.MAIN_TABS) || (initialRoute == AppRoutes.HOME_SEARCH && targetRoute == AppRoutes.MAIN_TABS)
                    if (isHomeAndSearch) {
                        androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    } else if (targetRoute == AppRoutes.MAIN_TABS) {
                        androidx.compose.animation.slideInHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            initialOffsetX = { -it / 4 }
                        ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    } else {
                        androidx.compose.animation.slideInHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            initialOffsetX = { it }
                        ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    }
                },
                exitTransition = {
                    val targetRoute = targetState.destination.route
                    val initialRoute = initialState.destination.route
                    val isHomeAndSearch = (targetRoute == AppRoutes.HOME_SEARCH && initialRoute == AppRoutes.MAIN_TABS) || (initialRoute == AppRoutes.HOME_SEARCH && targetRoute == AppRoutes.MAIN_TABS)
                    if (isHomeAndSearch) {
                        androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    } else if (targetRoute == AppRoutes.MAIN_TABS) {
                        androidx.compose.animation.slideOutHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            targetOffsetX = { -it / 4 }
                        ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    } else {
                        androidx.compose.animation.slideOutHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            targetOffsetX = { -it / 4 }
                        ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    }
                },
                popEnterTransition = {
                    val targetRoute = targetState.destination.route
                    val initialRoute = initialState.destination.route
                    val isHomeAndSearch = (targetRoute == AppRoutes.HOME_SEARCH && initialRoute == AppRoutes.MAIN_TABS) || (initialRoute == AppRoutes.HOME_SEARCH && targetRoute == AppRoutes.MAIN_TABS)
                    if (isHomeAndSearch) {
                        androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    } else if (targetRoute == AppRoutes.MAIN_TABS) {
                        androidx.compose.animation.slideInHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            initialOffsetX = { -it / 4 }
                        ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    } else {
                        androidx.compose.animation.slideInHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            initialOffsetX = { -it / 4 }
                        ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
                    }
                },
                popExitTransition = {
                    val targetRoute = targetState.destination.route
                    val initialRoute = initialState.destination.route
                    val isHomeAndSearch = (targetRoute == AppRoutes.HOME_SEARCH && initialRoute == AppRoutes.MAIN_TABS) || (initialRoute == AppRoutes.HOME_SEARCH && targetRoute == AppRoutes.MAIN_TABS)
                    if (isHomeAndSearch) {
                        androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    } else if (targetRoute == AppRoutes.MAIN_TABS) {
                        androidx.compose.animation.slideOutHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            targetOffsetX = { it }
                        ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    } else {
                        androidx.compose.animation.slideOutHorizontally(
                            animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                            targetOffsetX = { it }
                        ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                    }
                }
            ) {
                navigation(startDestination = AppRoutes.AUTH, route = AppRoutes.AUTH_GRAPH) {
                    composable(
                        route = AppRoutes.AUTH,
                        deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "rmap://oauth/callback?code={code}" })
                    ) { backStackEntry ->
                        val viewModel: AuthViewModel = viewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        val code = backStackEntry.arguments?.getString("code")
                        val context = LocalContext.current

                        LaunchedEffect(code) {
                            if (!code.isNullOrBlank()) {
                                viewModel.onGithubCodeReceived(code)
                            } else {
                                val intent = (context as? androidx.activity.ComponentActivity)?.intent
                                if (intent?.data?.scheme == "rmap" && intent.data?.host == "oauth") {
                                    val codeFromIntent = intent.data?.getQueryParameter("code")
                                    if (!codeFromIntent.isNullOrBlank()) {
                                        intent.data = null
                                        viewModel.onGithubCodeReceived(codeFromIntent)
                                    }
                                }
                            }
                        }

                        DisposableEffect(context) {
                            val listener = androidx.core.util.Consumer<android.content.Intent> { intent ->
                                if (intent.data?.scheme == "rmap" && intent.data?.host == "oauth") {
                                    val newCode = intent.data?.getQueryParameter("code")
                                    if (!newCode.isNullOrBlank()) {
                                        intent.data = null
                                        viewModel.onGithubCodeReceived(newCode)
                                    }
                                }
                            }
                            val activity = context as? androidx.activity.ComponentActivity
                            activity?.addOnNewIntentListener(listener)
                            onDispose {
                                activity?.removeOnNewIntentListener(listener)
                            }
                        }

                        LaunchedEffect(viewModel) {
                            viewModel.events.collect { event ->
                                when (event) {
                                    AuthEvent.NavigateToHome -> navController.navigate(AppRoutes.MAIN_TABS) {
                                        popUpTo(AppRoutes.AUTH_GRAPH) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    AuthEvent.NavigateBack -> {
                                        if (!navController.popBackStack()) {
                                            navController.navigate(AppRoutes.MAIN_TABS) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AuthScreen(
                            uiState = uiState,
                            onGoogleIdTokenReceived = viewModel::onGoogleIdTokenReceived,
                            onLoginError = viewModel::onLoginError,
                            onGithubLoginClick = { viewModel.onGithubLoginClick(context) },
                            onBackClick = viewModel::onBackClick
                        )
                    }
                }

                composable(AppRoutes.MAIN_TABS) {
                    val aiRoadmapViewModel: AiRoadmapViewModel = viewModel()
                    androidx.activity.compose.BackHandler(enabled = pagerState.currentPage != 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> {
                                val viewModel: HomeViewModel = viewModel()
                                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                                val reselectEvent = remember { tabReselectEvent.filter { it == NavBarDestination.Home } }

                                HomeScreen(
                                    uiState = uiState,
                                    avatarUrl = currentUserAvatarUrl,
                                    selectedDestination = NavBarDestination.Home,
                                    onDestinationSelected = ::handleDestinationSelected,
                                    onHeaderActionClick = {
                                        navController.navigate(AppRoutes.PROFILE) {
                                            launchSingleTop = true
                                        }
                                    },
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
                                    onRecommendedRoadmapClick = { item ->
                                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                                    },
                                    onCreateRoadmapWithAiClick = {
                                        aiRoadmapViewModel.onCreateRoadmapClick()
                                        handleDestinationSelected(NavBarDestination.AiAssistant)
                                    },
                                    onExploreReadyMadeClick = {
                                        handleDestinationSelected(NavBarDestination.Explore)
                                    },
                                    reselectEvent = reselectEvent
                                )
                            }
                            1 -> {
                                val viewModel: MyRoadmapViewModel = viewModel()
                                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                                val reselectEvent = remember { tabReselectEvent.filter { it == NavBarDestination.MyRoadmap } }

                                LaunchedEffect(viewModel) {
                                    viewModel.events.collect { event ->
                                        when (event) {
                                            MyRoadmapEvent.DashboardRefreshFailed -> {
                                                snackbarHostState.showRMapSnackbar(
                                                    title = snackbarWarningTitle,
                                                    message = myRoadmapRefreshFailedMessage,
                                                    variant = AppNotificationVariant.Warning
                                                )
                                            }
                                        }
                                    }
                                }

                                MyRoadmapScreen(
                                    uiState = uiState,
                                    isAuthenticated = authState is AuthState.Authenticated,
                                    avatarUrl = currentUserAvatarUrl,
                                    selectedDestination = NavBarDestination.MyRoadmap,
                                    onDestinationSelected = ::handleDestinationSelected,
                                    onHeaderActionClick = {
                                        navController.navigate(AppRoutes.PROFILE) {
                                            launchSingleTop = true
                                        }
                                    },
                                    onSearchQueryChange = viewModel::onSearchQueryChange,
                                    onClearSearchClick = viewModel::clearSearch,
                                    onFilterSelected = viewModel::onFilterSelected,
                                    onRoadmapClick = { roadmapId ->
                                        navController.navigate(AppRoutes.roadmapDetail(roadmapId))
                                    },
                                    onRoadmapCtaClick = { roadmapId ->
                                        navController.navigate(AppRoutes.roadmapDetail(roadmapId))
                                    },
                                    onRetryClick = viewModel::loadDashboard,
                                    onCreateWithAiClick = {
                                        aiRoadmapViewModel.onCreateRoadmapClick()
                                        handleDestinationSelected(NavBarDestination.AiAssistant)
                                    },
                                    onExploreRoadmapsClick = {
                                        handleDestinationSelected(NavBarDestination.Explore)
                                    },
                                    reselectEvent = reselectEvent
                                )
                            }
                            2 -> {
                                val viewModel: AiRoadmapViewModel = aiRoadmapViewModel
                                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                                val reselectEvent = remember { tabReselectEvent.filter { it == NavBarDestination.AiAssistant } }

                                LaunchedEffect(uiState.step) {
                                    isAiRoadmapSubScreen = uiState.step != AiRoadmapStep.Library
                                    isAiRoadmapGenerating = uiState.step == AiRoadmapStep.Generating
                                }

                                LaunchedEffect(viewModel) {
                                    viewModel.events.collect { event ->
                                        when (event) {
                                            is AiRoadmapEvent.NavigateToRoadmapDetail -> {
                                                navController.navigate(AppRoutes.roadmapDetail(event.roadmapId))
                                            }
                                            is AiRoadmapEvent.ShowError -> {
                                                val message = when (event.error) {
                                                    AiRoadmapFormError.AnswerAllQuestions -> context.getString(R.string.ai_roadmap_error_answer_all)
                                                    AiRoadmapFormError.CustomAnswerRequired -> context.getString(R.string.ai_roadmap_error_custom_answer_required)
                                                    AiRoadmapFormError.GenerationFailed -> context.getString(R.string.ai_roadmap_error_generation)
                                                    AiRoadmapFormError.TopicRequired -> context.getString(R.string.ai_roadmap_error_topic_required)
                                                    AiRoadmapFormError.DeadlineRequired -> context.getString(R.string.ai_roadmap_error_deadline_required)
                                                    AiRoadmapFormError.DeadlineInPast -> context.getString(R.string.ai_roadmap_error_deadline_past)
                                                    AiRoadmapFormError.QuestionsLoadFailed -> context.getString(R.string.ai_roadmap_error_questions)
                                                }
                                                snackbarHostState.showRMapSnackbar(
                                                    title = snackbarErrorTitle,
                                                    message = message,
                                                    variant = AppNotificationVariant.Error
                                                )
                                            }
                                        }
                                    }
                                }

                                AiRoadmapScreen(
                                    uiState = uiState,
                                    avatarUrl = currentUserAvatarUrl,
                                    selectedDestination = NavBarDestination.AiAssistant,
                                    onDestinationSelected = ::handleDestinationSelected,
                                    onHeaderActionClick = {
                                        navController.navigate(AppRoutes.PROFILE) {
                                            launchSingleTop = true
                                        }
                                    },
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
                                    onViewGeneratedRoadmap = viewModel::onViewGeneratedRoadmap,
                                    onExploreClick = {
                                        handleDestinationSelected(NavBarDestination.Home)
                                    },
                                    onExploreRoadmapsClick = {
                                        handleDestinationSelected(NavBarDestination.Explore)
                                    },
                                    onRoadmapSelected = viewModel::onRoadmapSelected,
                                    reselectEvent = reselectEvent
                                )
                            }
                            3 -> {
                                val viewModel: ExploreViewModel = viewModel()
                                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                                val reselectEvent = remember { tabReselectEvent.filter { it == NavBarDestination.Explore } }

                                ExploreScreen(
                                    uiState = uiState,
                                    avatarUrl = currentUserAvatarUrl,
                                    selectedDestination = NavBarDestination.Explore,
                                    onDestinationSelected = ::handleDestinationSelected,
                                    onHeaderActionClick = {
                                        navController.navigate(AppRoutes.PROFILE) {
                                            launchSingleTop = true
                                        }
                                    },
                                    onSearchQueryChange = viewModel::onSearchQueryChange,
                                    onCategoryClick = viewModel::onCategorySelected,
                                    onSeeMoreRoadmapsClick = viewModel::onSeeMoreRoadmaps,
                                    onSeeAllRoadmapsClick = viewModel::onSeeAllRoadmaps,
                                    onSeeLessRoadmapsClick = viewModel::onSeeLessRoadmaps,
                                    onRoadmapClick = { item ->
                                        navController.navigate(AppRoutes.roadmapDetail(item.id))
                                    },
                                    reselectEvent = reselectEvent
                                )
                            }
                        }
                    }
                }

                composable(AppRoutes.PROFILE) {
                    val viewModel: ProfileViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val profileComingSoonMessage = stringResource(R.string.coming_soon_message)

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                ProfileEvent.NavigateToPersonalInformation -> navController.navigate(AppRoutes.PERSONAL_INFORMATION)
                                ProfileEvent.NavigateToNotificationSettings -> navController.navigate(AppRoutes.NOTIFICATION_SETTINGS)
                                ProfileEvent.NavigateToPrivacySecurity -> navController.navigate(AppRoutes.PRIVACY_SECURITY)
                                ProfileEvent.NavigateToConnectedAccounts -> navController.navigate(AppRoutes.CONNECTED_ACCOUNTS)
                                ProfileEvent.ShowComingSoon -> snackbarHostState.showRMapSnackbar(
                                    title = snackbarInfoTitle,
                                    message = profileComingSoonMessage,
                                    variant = AppNotificationVariant.Info
                                )
                                ProfileEvent.SignedOut -> {
                                    coroutineScope.launch {
                                        snackbarHostState.showRMapSnackbar(
                                            title = snackbarSuccessTitle,
                                            message = context.getString(R.string.profile_sign_out_success),
                                            variant = AppNotificationVariant.Success
                                        )
                                    }
                                    navController.navigate(AppRoutes.AUTH) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                                ProfileEvent.NavigateToAuth -> navController.navigate(AppRoutes.AUTH) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                    ProfileScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onEditProfile = viewModel::onEditProfile,
                        onSettingClick = viewModel::onSettingClick,
                        onLanguageSelected = viewModel::onLanguageSelected,
                        onDismissLanguageSheet = viewModel::onDismissLanguageSheet
                    )
                }

                composable(AppRoutes.HOME_SEARCH) {
                    val viewModel: HomeSearchViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    HomeSearchScreen(
                        query = uiState.query,
                        recentSearches = uiState.recentSearches,
                        popularSearches = listOf(
                            stringResource(R.string.home_search_popular_frontend),
                            stringResource(R.string.home_search_popular_react),
                            stringResource(R.string.home_search_popular_backend),
                            stringResource(R.string.home_search_popular_devops),
                            stringResource(R.string.home_search_popular_data_analyst),
                            stringResource(R.string.home_search_popular_ai_engineer)
                        ),
                        roadmaps = uiState.roadmaps,
                        skills = uiState.skills,
                        roadmapTotal = uiState.roadmapTotal,
                        skillTotal = uiState.skillTotal,
                        aiSuggestion = HomeSearchAiSuggestionUiModel(
                            id = "react-roadmap",
                            title = stringResource(R.string.home_search_ai_suggestion_card_title),
                            description = stringResource(R.string.home_search_ai_suggestion_card_description),
                            actionText = stringResource(R.string.home_search_ai_suggestion_card_action)
                        ),
                        isLoading = uiState.isLoading,
                        hasMoreRoadmaps = uiState.hasMoreRoadmaps,
                        hasMoreSkills = uiState.hasMoreSkills,
                        isLoadingMoreRoadmaps = uiState.isLoadingMoreRoadmaps,
                        isLoadingMoreSkills = uiState.isLoadingMoreSkills,
                        onQueryChange = viewModel::onQueryChange,
                        onBackClick = { navController.popBackStack() },
                        onClearRecentSearchesClick = viewModel::onClearRecentSearchesClick,
                        onRecentSearchClick = viewModel::onQueryChange,
                        onRemoveRecentSearchClick = viewModel::onRemoveRecentSearchClick,
                        onPopularSearchClick = viewModel::onQueryChange,
                        onRoadmapClick = { item ->
                            navController.navigate(AppRoutes.roadmapDetail(item.id))
                        },
                        onSeeMoreRoadmapsClick = viewModel::onSeeMoreRoadmapsClick,
                        onSkillClick = {
                            coroutineScope.launch {
                                snackbarHostState.showRMapSnackbar(
                                    title = snackbarInfoTitle,
                                    message = comingSoonMessage,
                                    variant = AppNotificationVariant.Info
                                )
                            }
                        },
                        onSeeMoreSkillsClick = viewModel::onSeeMoreSkillsClick,
                        onCreateWithAiClick = {
                            handleDestinationSelected(NavBarDestination.AiAssistant)
                        }
                    )
                }

                composable(AppRoutes.PERSONAL_INFORMATION) {
                    val viewModel: PersonalInformationViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                PersonalInformationEvent.ProfileUpdated -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarSuccessTitle,
                                        message = profileUpdatedMessage,
                                        variant = AppNotificationVariant.Success
                                    )
                                }
                            }
                        }
                    }

                    PersonalInformationScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onFullNameChanged = viewModel::onFullNameChanged,
                        onStartEditingDetails = viewModel::onStartEditingDetails,
                        onCancelEditDetails = viewModel::onCancelEditDetails,
                        onOpenAvatarPicker = viewModel::onOpenAvatarPicker,
                        onCancelAvatarPicker = viewModel::onCancelAvatarPicker,
                        onAvatarSelected = viewModel::onAvatarSelected,
                        onResetSelectedAvatar = viewModel::onResetSelectedAvatar,
                        onRegenerateAvatars = viewModel::onRegenerateAvatars,
                        onSaveClick = viewModel::onSaveClick
                    )
                }

                composable(AppRoutes.PRIVACY_SECURITY) {
                    val viewModel: PrivacySecurityViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                PrivacySecurityEvent.PasswordChanged -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarSuccessTitle,
                                        message = passwordChangedMessage,
                                        variant = AppNotificationVariant.Success
                                    )
                                    navController.navigate(AppRoutes.AUTH) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    PrivacySecurityScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onCurrentPasswordChanged = viewModel::onCurrentPasswordChanged,
                        onNewPasswordChanged = viewModel::onNewPasswordChanged,
                        onConfirmNewPasswordChanged = viewModel::onConfirmNewPasswordChanged,
                        onToggleCurrentPasswordVisibility = viewModel::onToggleCurrentPasswordVisibility,
                        onToggleNewPasswordVisibility = viewModel::onToggleNewPasswordVisibility,
                        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
                        onChangePasswordClick = viewModel::onChangePasswordClick
                    )
                }

                composable(AppRoutes.CONNECTED_ACCOUNTS) {
                    val viewModel: com.rmap.mobile.features.profile.presentation.viewmodel.ConnectedAccountsViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(viewModel) {
                        viewModel.eventFlow.collect { event ->
                            when (event) {
                                is com.rmap.mobile.features.profile.presentation.viewmodel.ConnectedAccountsEvent.NavigateBack -> {
                                    navController.popBackStack()
                                }
                                is com.rmap.mobile.features.profile.presentation.viewmodel.ConnectedAccountsEvent.ShowSnackbar -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarInfoTitle,
                                        message = event.message,
                                        variant = AppNotificationVariant.Info
                                    )
                                }
                            }
                        }
                    }

                    com.rmap.mobile.features.profile.presentation.screen.ConnectedAccountsScreen(
                        uiState = uiState,
                        onBackClick = viewModel::onBackClick,
                        onConnectClick = {
                            coroutineScope.launch {
                                snackbarHostState.showRMapSnackbar(
                                    title = snackbarInfoTitle,
                                    message = comingSoonMessage,
                                    variant = AppNotificationVariant.Info
                                )
                            }
                        },
                        onDisconnectClick = viewModel::disconnectIntegration
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
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarWarningTitle,
                                        message = permissionRequiredMessage,
                                        variant = AppNotificationVariant.Warning
                                    )
                                }
                            }
                        }
                    }

                    NotificationSettingsScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onNotificationPermissionStateChanged = viewModel::onNotificationPermissionStateChanged,
                        onNotificationPermissionDenied = viewModel::onNotificationPermissionDenied,
                        onAllowNotificationsChange = viewModel::onAllowNotificationsChange,
                        onLearningRemindersEnabledChange = viewModel::onLearningRemindersEnabledChange,
                        onStreakProtectionEnabledChange = viewModel::onStreakProtectionEnabledChange,
                        onAiRoadmapUpdatesEnabledChange = viewModel::onAiRoadmapUpdatesEnabledChange,
                        onReminderTimeSelected = viewModel::onReminderTimeSelected,
                        onReminderFrequencySelected = viewModel::onReminderFrequencySelected,
                        isDebugNotificationTestVisible = isDebugBuild,
                        onSendTestNotificationClick = {
                            RMapAppGraph.learningNotificationNotifier.showLearningReminder()
                            coroutineScope.launch {
                                snackbarHostState.showRMapSnackbar(
                                    title = snackbarSuccessTitle,
                                    message = debugNotificationSentMessage,
                                    variant = AppNotificationVariant.Success
                                )
                            }
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
                    val shouldRefreshRoadmap by remember(backStackEntry) {
                        backStackEntry.savedStateHandle.getStateFlow(
                            AppRoutes.ROADMAP_DETAIL_REFRESH_RESULT,
                            false
                        )
                    }.collectAsStateWithLifecycle()
                    val detailScrollRequest by remember(backStackEntry) {
                        backStackEntry.savedStateHandle.getStateFlow(
                            AppRoutes.ROADMAP_DETAIL_SCROLL_REQUEST,
                            ""
                        )
                    }.collectAsStateWithLifecycle()
                    val detailScrollTarget = when (detailScrollRequest) {
                        AppRoutes.ROADMAP_DETAIL_SCROLL_HERO -> RoadmapDetailScrollTarget.Hero
                        AppRoutes.ROADMAP_DETAIL_SCROLL_IN_PROGRESS_GROUP -> RoadmapDetailScrollTarget.InProgressGroup
                        else -> null
                    }

                    LaunchedEffect(roadmapId) {
                        viewModel.loadRoadmap(roadmapId)
                    }

                    LaunchedEffect(shouldRefreshRoadmap) {
                        if (shouldRefreshRoadmap) {
                            backStackEntry.savedStateHandle[AppRoutes.ROADMAP_DETAIL_REFRESH_RESULT] = false
                            viewModel.refreshRoadmap()
                        }
                    }

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                is RoadmapDetailEvent.NodeProgressUpdated -> {
                                    val message = if (event.unlockedNodeCount > 0) {
                                        context.getString(
                                            R.string.roadmap_detail_progress_unlocked,
                                            event.unlockedNodeCount
                                        )
                                    } else {
                                        nodeProgressUpdatedMessage
                                    }
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarSuccessTitle,
                                        message = message,
                                        variant = AppNotificationVariant.Success
                                    )
                                }
                                is RoadmapDetailEvent.NodeProgressUpdateFailed -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarErrorTitle,
                                        message = event.message ?: nodeProgressUpdateFailedMessage,
                                        variant = AppNotificationVariant.Error
                                    )
                                }
                                RoadmapDetailEvent.NodeActionUnavailable -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarInfoTitle,
                                        message = comingSoonMessage,
                                        variant = AppNotificationVariant.Info
                                    )
                                }
                                is RoadmapDetailEvent.MilestoneSelected -> {
                                    navController.navigate(
                                        AppRoutes.roadmapMilestone(
                                            roadmapId = roadmapId,
                                            milestoneId = event.milestoneId
                                        )
                                    )
                                }
                                is RoadmapDetailEvent.NavigateToLearning -> {
                                    navController.navigate(
                                        AppRoutes.roadmapLearning(
                                            roadmapId = event.roadmapId,
                                            nodeId = event.nodeId,
                                            skillId = event.skillId,
                                            isCompleted = event.isCompleted,
                                            groupTitle = event.groupTitle
                                        )
                                    )
                                }
                            }
                        }
                    }

                    RoadmapDetailScreen(
                        uiState = uiState,
                        onBackClick = ::navigateBackFromRoadmapDetail,
                        scrollTarget = detailScrollTarget,
                        onScrollTargetHandled = {
                            backStackEntry.savedStateHandle[AppRoutes.ROADMAP_DETAIL_SCROLL_REQUEST] = ""
                        },
                        onContinueClick = viewModel::onContinueClick,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearchFocus = viewModel::onSearchFocus,
                        onSearchFocusChange = viewModel::onSearchFocusChange,
                        onSearchClearClick = viewModel::onSearchClearClick,
                        onSearchBackClick = viewModel::onSearchBackClick,
                        onRetryClick = viewModel::retryLoadRoadmap,
                        onNodeActionClick = viewModel::onNodeActionClick,
                        onGroupClick = {
                            coroutineScope.launch {
                                snackbarHostState.showRMapSnackbar(
                                    title = snackbarInfoTitle,
                                    message = comingSoonMessage,
                                    variant = AppNotificationVariant.Info
                                )
                            }
                        },
                        onMilestoneClick = { milestone ->
                            navController.navigate(
                                AppRoutes.roadmapMilestone(
                                    roadmapId = roadmapId,
                                    milestoneId = milestone.id
                                )
                            )
                        }
                    )
                }

                composable(
                    route = AppRoutes.ROADMAP_MILESTONE,
                    arguments = listOf(
                        navArgument(AppRoutes.ROADMAP_ID_ARG) { type = NavType.StringType },
                        navArgument(AppRoutes.MILESTONE_ID_ARG) { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val viewModel: RoadmapMilestoneViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val roadmapId = backStackEntry.arguments?.getString(AppRoutes.ROADMAP_ID_ARG).orEmpty()
                    val milestoneId = backStackEntry.arguments?.getString(AppRoutes.MILESTONE_ID_ARG).orEmpty()

                    LaunchedEffect(roadmapId, milestoneId) {
                        viewModel.loadMilestone(
                            roadmapId = roadmapId,
                            milestoneId = milestoneId
                        )
                    }

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                RoadmapMilestoneEvent.SubmissionQueued -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarSuccessTitle,
                                        message = milestoneSubmissionQueuedMessage,
                                        variant = AppNotificationVariant.Success
                                    )
                                }
                                RoadmapMilestoneEvent.SubmissionFailed -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarErrorTitle,
                                        message = milestoneSubmissionFailedMessage,
                                        variant = AppNotificationVariant.Error
                                    )
                                }
                            }
                        }
                    }

                    RoadmapMilestoneScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onRetryClick = viewModel::retryLoadMilestone,
                        onRepoUrlChanged = viewModel::onRepoUrlChanged,
                        onTestSuiteToggleClick = viewModel::onTestSuiteToggleClick,
                        onSubmitClick = viewModel::onSubmitClick
                    )
                }

                composable(
                    route = AppRoutes.ROADMAP_LEARNING,
                    arguments = listOf(
                        navArgument(AppRoutes.ROADMAP_ID_ARG) { type = NavType.StringType },
                        navArgument(AppRoutes.NODE_ID_ARG) { type = NavType.StringType },
                        navArgument(AppRoutes.SKILL_ID_ARG) { type = NavType.StringType },
                        navArgument(AppRoutes.NODE_COMPLETED_ARG) { type = NavType.BoolType }
                    )
                ) { backStackEntry ->
                    val viewModel: RoadmapLearningViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val roadmapId = backStackEntry.arguments?.getString(AppRoutes.ROADMAP_ID_ARG).orEmpty()
                    val nodeId = backStackEntry.arguments?.getString(AppRoutes.NODE_ID_ARG).orEmpty()
                    val skillId = backStackEntry.arguments?.getString(AppRoutes.SKILL_ID_ARG).orEmpty()
                    val isCompleted = backStackEntry.arguments?.getBoolean(AppRoutes.NODE_COMPLETED_ARG) == true
                    val groupTitle = backStackEntry.arguments?.getString(AppRoutes.GROUP_TITLE_ARG)
                    val shouldRefreshLearning by remember(backStackEntry) {
                        backStackEntry.savedStateHandle.getStateFlow(
                            AppRoutes.LEARNING_NODE_REFRESH_RESULT,
                            false
                        )
                    }.collectAsStateWithLifecycle()

                    fun markRoadmapDetailForRefresh() {
                        runCatching {
                            navController.getBackStackEntry(AppRoutes.ROADMAP_DETAIL)
                                .savedStateHandle[AppRoutes.ROADMAP_DETAIL_REFRESH_RESULT] = true
                        }.onFailure {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(AppRoutes.ROADMAP_DETAIL_REFRESH_RESULT, true)
                        }
                    }

                    fun navigateBackToRoadmapDetail(scrollRequest: String) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(AppRoutes.ROADMAP_DETAIL_SCROLL_REQUEST, scrollRequest)

                        if (!navController.popBackStack() && uiState.roadmapId.isNotBlank()) {
                            navController.navigate(AppRoutes.roadmapDetail(uiState.roadmapId)) {
                                launchSingleTop = true
                            }
                        }
                    }

                    LaunchedEffect(roadmapId, nodeId, skillId, isCompleted, groupTitle) {
                        viewModel.loadLearningContent(
                            roadmapId = roadmapId,
                            nodeId = nodeId,
                            skillId = skillId,
                            isCompleted = isCompleted,
                            groupTitle = groupTitle
                        )
                    }

                    LaunchedEffect(shouldRefreshLearning) {
                        if (shouldRefreshLearning) {
                            backStackEntry.savedStateHandle[AppRoutes.LEARNING_NODE_REFRESH_RESULT] = false
                            viewModel.refreshLearningContent()
                            markRoadmapDetailForRefresh()
                        }
                    }

                    LaunchedEffect(viewModel) {
                        viewModel.events.collect { event ->
                            when (event) {
                                RoadmapLearningEvent.NodeCompleted -> {
                                    markRoadmapDetailForRefresh()
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarSuccessTitle,
                                        message = learningCompletedMessage,
                                        variant = AppNotificationVariant.Success
                                    )
                                }
                                RoadmapLearningEvent.NodeCompletionFailed -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarErrorTitle,
                                        message = nodeProgressUpdateFailedMessage,
                                        variant = AppNotificationVariant.Error
                                    )
                                }
                                RoadmapLearningEvent.NodeCompletionRequiresQuiz -> {
                                    snackbarHostState.showRMapSnackbar(
                                        title = snackbarWarningTitle,
                                        message = learningCompletionRequiresQuizMessage,
                                        variant = AppNotificationVariant.Warning
                                    )
                                }
                                is RoadmapLearningEvent.NavigateToQuiz -> {
                                    markRoadmapDetailForRefresh()
                                    navController.navigate(
                                        AppRoutes.roadmapNodeQuiz(
                                            roadmapId = event.roadmapId,
                                            nodeId = event.nodeId
                                        )
                                    )
                                }
                            }
                        }
                    }

                    RoadmapLearningScreen(
                        uiState = uiState,
                        onBackClick = {
                            if (uiState.isCompleted) {
                                markRoadmapDetailForRefresh()
                            }
                            navController.popBackStack()
                        },
                        onRetryClick = viewModel::retryLoadLearningContent,
                        onResourceClick = { resource ->
                            runCatching { uriHandler.openUri(resource.url) }
                                .onFailure {
                                    coroutineScope.launch {
                                        snackbarHostState.showRMapSnackbar(
                                            title = snackbarErrorTitle,
                                            message = resourceOpenFailedMessage,
                                            variant = AppNotificationVariant.Error
                                        )
                                    }
                                }
                        },
                        onTakeQuizClick = {
                            when {
                                uiState.canTakeQuiz && !uiState.isNodeLocked -> {
                                    navController.navigate(
                                        AppRoutes.roadmapNodeQuiz(
                                            roadmapId = uiState.roadmapId,
                                            nodeId = uiState.nodeId
                                        )
                                    )
                                }

                                uiState.isNodeLocked -> {
                                    coroutineScope.launch {
                                        snackbarHostState.showRMapSnackbar(
                                            title = snackbarWarningTitle,
                                            message = learningLockedQuizMessage,
                                            variant = AppNotificationVariant.Warning
                                        )
                                    }
                                    navigateBackToRoadmapDetail(AppRoutes.ROADMAP_DETAIL_SCROLL_IN_PROGRESS_GROUP)
                                }
                            }
                        },
                        onStartRoadmapForQuizClick = viewModel::onStartRoadmapForQuizClick
                    )
                }

                composable(
                    route = AppRoutes.ROADMAP_NODE_QUIZ,
                    arguments = listOf(
                        navArgument(AppRoutes.ROADMAP_ID_ARG) { type = NavType.StringType },
                        navArgument(AppRoutes.NODE_ID_ARG) { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val viewModel: NodeQuizViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val roadmapId = backStackEntry.arguments?.getString(AppRoutes.ROADMAP_ID_ARG).orEmpty()
                    val nodeId = backStackEntry.arguments?.getString(AppRoutes.NODE_ID_ARG).orEmpty()

                    LaunchedEffect(roadmapId, nodeId) {
                        viewModel.loadQuiz(
                            roadmapId = roadmapId,
                            nodeId = nodeId
                        )
                    }

                    NodeQuizScreen(
                        uiState = uiState,
                        onBackClick = { navController.popBackStack() },
                        onOptionSelected = viewModel::onOptionSelected,
                        onPreviousQuestion = viewModel::onPreviousQuestion,
                        onNextQuestion = viewModel::onNextQuestion,
                        onSubmitClick = viewModel::onSubmitClick,
                        onDoneClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(AppRoutes.LEARNING_NODE_REFRESH_RESULT, true)
                            navController.popBackStack()
                        },
                        onRetryClick = {
                            viewModel.loadQuiz(
                                roadmapId = roadmapId,
                                nodeId = nodeId
                            )
                        }
                    )
                }
            }

            val isTopLevelRoute = currentRoute == AppRoutes.MAIN_TABS || currentRoute == AppRoutes.HOME_SEARCH
            val shouldHideBottomBarForAiQuiz =
                currentRoute == AppRoutes.MAIN_TABS &&
                        pagerState.currentPage == 2 &&
                        isAiRoadmapSubScreen

            androidx.compose.animation.AnimatedVisibility(
                visible = isTopLevelRoute && !shouldHideBottomBarForAiQuiz,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + androidx.compose.animation.fadeOut()
            ) {
                val selectedDestination = when {
                    currentRoute == AppRoutes.HOME_SEARCH -> NavBarDestination.Home
                    else -> when (pagerState.currentPage) {
                        0 -> NavBarDestination.Home
                        1 -> NavBarDestination.MyRoadmap
                        2 -> NavBarDestination.AiAssistant
                        3 -> NavBarDestination.Explore
                        else -> NavBarDestination.Home
                    }
                }
                com.rmap.mobile.core.ui.components.RMapNavigationBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = ::handleDestinationSelected
                )
            }

            val isAiAssistantGeneratingScreen = currentRoute == AppRoutes.MAIN_TABS &&
                pagerState.currentPage == 2 &&
                isAiRoadmapGenerating

            val shouldShowAiBanner = aiGenerationStatus.isActive && !isAiAssistantGeneratingScreen

            if (shouldShowAiBanner) {
                AiRoadmapProgressBanner(
                    status = aiGenerationStatus,
                    title = stringResource(R.string.ai_roadmap_banner_title),
                    actionText = stringResource(R.string.ai_roadmap_banner_action),
                    onClick = {
                        handleDestinationSelected(NavBarDestination.AiAssistant)
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(horizontal = Dimens.spacingScreenHorizontal, vertical = Dimens.spacingMd)
                        .fillMaxWidth()
                )
            }
        }
    }
}
