package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomeTemplateCategory
import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryDisplayLabel
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryIcon
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository = RMapAppGraph.homeRepository,
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadHome()
        observeSavedRoadmaps()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            homeRepository.getHomeContent()
                .onSuccess { content ->
                    _uiState.update {
                        content.toUiState(savedRoadmapIds = it.savedRoadmapIds)
                    }
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

    fun onRecommendedRoadmapBookmarkClick(item: HomeRecommendedRoadmapState) {
        viewModelScope.launch {
            val wasSaved = item.id in _uiState.value.savedRoadmapIds
            val result = if (wasSaved) {
                bookmarkRepository.deleteRoadmap(item.id)
            } else {
                bookmarkRepository.saveRoadmap(item.snapshot.toDomain())
            }

            result
                .onSuccess {
                    updateSavedRoadmapState(
                        roadmapId = item.id,
                        isSaved = !wasSaved
                    )
                    _events.emit(
                        if (wasSaved) {
                            HomeEvent.RoadmapBookmarkRemoved
                        } else {
                            HomeEvent.RoadmapBookmarkSaved
                        }
                    )
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to update bookmark") }
                    _events.emit(HomeEvent.BookmarkActionFailed)
                }
        }
    }

    private fun observeSavedRoadmaps() {
        viewModelScope.launch {
            bookmarkRepository.observeSavedRoadmaps()
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to load bookmarks") }
                }
                .collect { savedRoadmaps ->
                    _uiState.update {
                        it.copy(
                            savedRoadmapIds = savedRoadmaps
                                .map { bookmark -> bookmark.summary.id }
                                .toSet()
                        )
                    }
                }
        }
    }

    private fun updateSavedRoadmapState(
        roadmapId: String,
        isSaved: Boolean
    ) {
        _uiState.update {
            val nextIds = if (isSaved) {
                it.savedRoadmapIds + roadmapId
            } else {
                it.savedRoadmapIds - roadmapId
            }
            it.copy(savedRoadmapIds = nextIds)
        }
    }
}

private fun HomeContent.toUiState(savedRoadmapIds: Set<String>): HomeUiState {
    val learningPlans = activeRoadmaps.map { it.toLearningPlanState() }
    val firstPlan = learningPlans.firstOrNull()
    return HomeUiState(
        userName = "User",
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
        categories = categories.map { it.toCategoryState() },
        trendingRoadmaps = trendings.map { it.toTrendingRoadmapCardUiModel() },
        savedRoadmapIds = savedRoadmapIds,
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
        timeLeftText = planNode?.estimatedHours?.let { "${it}h left" },
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
    val displayLabel = roleCategory.toRoadmapCategoryDisplayLabel(categoryLabel)
    val nodeCount = when {
        nodesTotal > 0 -> nodesTotal
        requiredNodesTotal > 0 -> requiredNodesTotal
        else -> 0
    }
    val duration = toDurationText()
    return HomeRecommendedRoadmapState(
        id = roadmapId,
        categoryId = roleCategory,
        categoryLabel = displayLabel,
        title = title,
        nodesText = "$nodeCount nodes",
        durationText = duration,
        icon = icon,
        isBeginner = false,
        snapshot = HomeRoadmapBookmarkSnapshotState(
            roadmapId = roadmapId,
            title = title,
            categoryId = roleCategory,
            categoryLabel = displayLabel,
            nodesTotal = nodeCount,
            durationLabel = duration,
            iconKey = icon.name
        )
    )
}

private fun HomeTemplateCategory.toCategoryState(): HomeCategoryState {
    return HomeCategoryState(
        id = category,
        label = category.toRoadmapCategoryDisplayLabel(label),
        countText = templatesCount.toString(),
        icon = category.toRoadmapCategoryIcon()
    )
}

private fun HomeTrendingRoadmap.toTrendingRoadmapCardUiModel(): TrendingRoadmapCardUiModel {
    val icon = roleCategory.toRoadmapCategoryIcon()
    return TrendingRoadmapCardUiModel(
        id = roadmapId,
        rankText = "#$rank",
        categoryLabel = roleCategory.toRoadmapCategoryDisplayLabel(categoryLabel),
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

private fun HomeRoadmapBookmarkSnapshotState.toDomain(): RoadmapBookmarkSnapshot {
    return RoadmapBookmarkSnapshot(
        roadmapId = roadmapId,
        title = title,
        categoryId = categoryId,
        categoryLabel = categoryLabel,
        nodesTotal = nodesTotal,
        durationLabel = durationLabel,
        iconKey = iconKey
    )
}

sealed class HomeEvent {
    data object RoadmapBookmarkSaved : HomeEvent()
    data object RoadmapBookmarkRemoved : HomeEvent()
    data object BookmarkActionFailed : HomeEvent()
}
