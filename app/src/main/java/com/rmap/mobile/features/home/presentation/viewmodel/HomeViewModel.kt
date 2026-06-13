package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.profile.domain.repository.LearningReminderContextRepository
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryIcon
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class HomeViewModel(
    private val homeRepository: HomeRepository = RMapAppGraph.homeRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository,
    private val learningReminderContextRepository: LearningReminderContextRepository =
        RMapAppGraph.learningReminderContextRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeContentUpdates()
        loadHome()
        observeAuthState()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            homeRepository.getHomeContent()
                .onSuccess { content ->
                    applyHomeContent(content)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to load home"
                        )
                    }
                }
        }
    }

    private fun observeHomeContentUpdates() {
        viewModelScope.launch {
            homeRepository.homeContentUpdates.collect { content ->
                applyHomeContent(content)
            }
        }
    }

    private suspend fun applyHomeContent(content: HomeContent) {
        learningReminderContextRepository.setActiveRoadmap(
            title = content.activeRoadmaps.firstOrNull()?.title
        )
        _uiState.update {
            content.toUiState(
                userName = it.userName,
                isAuthenticated = it.isAuthenticated,
                greetingPeriod = it.greetingPeriod
            )
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                _uiState.update {
                    val authenticated = authState as? AuthState.Authenticated
                    it.copy(
                        userName = authenticated?.user?.fullName?.toFirstName().orEmpty(),
                        isAuthenticated = authenticated != null,
                        greetingPeriod = currentVietnamGreetingPeriod()
                    )
                }
            }
        }
    }

}

private fun HomeContent.toUiState(
    userName: String,
    isAuthenticated: Boolean,
    greetingPeriod: HomeGreetingPeriod
): HomeUiState {
    val learningPlans = activeRoadmaps.map { it.toLearningPlanState() }
    val firstPlan = learningPlans.firstOrNull()
    return HomeUiState(
        userName = userName,
        isAuthenticated = isAuthenticated,
        greetingPeriod = greetingPeriod,
        progressFraction = (metrics.roadmapCompletionPct.toFloat() / 100f).coerceIn(0f, 1f),
        readinessFraction = (metrics.readinessPct.toFloat() / 100f).coerceIn(0f, 1f),
        completedLessons = firstPlan?.completedRequiredNodes ?: 0,
        totalLessons = firstPlan?.totalRequiredNodes ?: 0,
        streakDays = metrics.streakDays,
        todayGoalCompleted = firstPlan?.completedRequiredNodes ?: 0,
        todayGoalTotal = firstPlan?.totalRequiredNodes ?: 0,
        completedRoadmaps = 0,
        hasInProgressRoadmap = learningPlans.isNotEmpty(),
        learningPlans = learningPlans,
        recommendedRoadmaps = recommendations.map { it.toRecommendedRoadmapState() },
        beginnerRoadmaps = beginners.map { it.toBeginnerRoadmapState() },
        trendingRoadmaps = trendings.map { it.toTrendingRoadmapCardUiModel() },
        isLoading = false,
        errorMessage = null
    )
}

private fun HomeActiveRoadmap.toLearningPlanState(): HomeLearningPlanState {
    return HomeLearningPlanState(
        id = roadmapId,
        roadmapTitle = title,
        skillTitle = planNode?.name ?: currentGroup?.name ?: title,
        chapterText = chapter?.label,
        timeLeftText = planNode?.estimatedHours?.let { "${it.toDisplayHours()}h left" },
        completedRequiredNodes = progress.requiredNodesCompleted,
        totalRequiredNodes = progress.requiredNodesTotal,
        progressPercentage = progress.requiredCompletionPct.toInt().coerceIn(0, 100),
        nextUnlockText = nextUnlock?.name,
        currentNodeId = planNode?.id,
        startedAtMillis = null,
        paceWarning = paceWarning?.takeIf { it.isBehind }?.toState()
    )
}

private fun HomePaceWarning.toState(): HomePaceWarningState {
    return HomePaceWarningState(
        message = "$title\n$message",
        actionText = actionLabel
    )
}

private fun HomeTemplateRoadmap.toRecommendedRoadmapState(): HomeRecommendedRoadmapState {
    val icon = roleCategory.toRoadmapCategoryIcon()
    val nodeCount = when {
        nodesTotal > 0 -> nodesTotal
        requiredNodesTotal > 0 -> requiredNodesTotal
        else -> 0
    }
    val duration = toDurationText()
    return HomeRecommendedRoadmapState(
        id = roadmapId,
        categoryId = roleCategory,
        categoryLabel = categoryLabel,
        title = title,
        nodesText = "$nodeCount nodes",
        durationText = duration,
        icon = icon,
        isBeginner = false
    )
}

private fun HomeTrendingRoadmap.toTrendingRoadmapCardUiModel(): TrendingRoadmapCardUiModel {
    val icon = roleCategory.toRoadmapCategoryIcon()
    return TrendingRoadmapCardUiModel(
        id = roadmapId,
        rankText = "#$rank",
        categoryLabel = categoryLabel,
        title = title,
        metadataText = "$nodesTotal nodes • ${toDurationText()}",
        trendText = trendText,
        leadingIcon = icon.toImageVector(),
        trendIcon = trendText.toTrendingIcon(),
        style = when (icon) {
            LearningTopicIcon.Storage,
            LearningTopicIcon.Terminal -> TrendingRoadmapCardDefaults.neutralStyle()
            LearningTopicIcon.Science,
            LearningTopicIcon.Security,
            LearningTopicIcon.Game,
            LearningTopicIcon.SmartToy -> TrendingRoadmapCardDefaults.indigoStyle()
            else -> TrendingRoadmapCardDefaults.primaryStyle()
        }
    )
}

private fun HomeTrendingRoadmap.toBeginnerRoadmapState(): HomeRecommendedRoadmapState {
    val icon = roleCategory.toRoadmapCategoryIcon()
    val duration = toDurationText()
    return HomeRecommendedRoadmapState(
        id = roadmapId,
        categoryId = roleCategory,
        categoryLabel = categoryLabel,
        title = title,
        nodesText = "$nodesTotal nodes",
        durationText = duration,
        icon = icon,
        isBeginner = true
    )
}

private fun String.toTrendingIcon(): ImageVector {
    val normalizedText = lowercase()
    return when {
        "learner" in normalizedText -> Icons.Outlined.Groups
        "popular" in normalizedText -> Icons.Outlined.LocalFireDepartment
        "trend" in normalizedText -> Icons.AutoMirrored.Outlined.TrendingUp
        else -> Icons.AutoMirrored.Outlined.TrendingUp
    }
}

private fun HomeTemplateRoadmap.toDurationText(): String {
    return durationLabel?.takeIf { it.isNotBlank() }
        ?: estimatedWeeks?.let { "$it weeks" }
        ?: "Self-paced"
}

private fun HomeTrendingRoadmap.toDurationText(): String {
    return durationLabel?.takeIf { it.isNotBlank() }
        ?: estimatedWeeks?.let { "$it weeks" }
        ?: "Self-paced"
}

private fun Double.toDisplayHours(): String {
    return if (this % 1.0 == 0.0) toInt().toString() else toString()
}

internal fun String.toFirstName(): String {
    return trim()
        .split(Regex("\\s+"))
        .firstOrNull()
        .orEmpty()
}

internal fun currentVietnamGreetingPeriod(
    hourOfDay: Int = Calendar.getInstance(VIETNAM_TIME_ZONE).get(Calendar.HOUR_OF_DAY)
): HomeGreetingPeriod {
    return when (hourOfDay) {
        in 5..11 -> HomeGreetingPeriod.Morning
        in 12..16 -> HomeGreetingPeriod.Afternoon
        in 17..21 -> HomeGreetingPeriod.Evening
        else -> HomeGreetingPeriod.Night
    }
}

private val VIETNAM_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
