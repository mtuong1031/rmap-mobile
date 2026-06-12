package com.rmap.mobile.features.home.presentation.viewmodel

import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon

data class HomeUiState(
    val userName: String = "",
    val isAuthenticated: Boolean = false,
    val greetingPeriod: HomeGreetingPeriod = HomeGreetingPeriod.Morning,
    val progressFraction: Float = 0f,
    val readinessFraction: Float = 0f,
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val streakDays: Int = 0,
    val todayGoalCompleted: Int = 0,
    val todayGoalTotal: Int = 0,
    val completedRoadmaps: Int = 0,
    val hasInProgressRoadmap: Boolean = false,
    val learningPlans: List<HomeLearningPlanState> = emptyList(),
    val recommendedRoadmaps: List<HomeRecommendedRoadmapState> = emptyList(),
    val beginnerRoadmaps: List<HomeRecommendedRoadmapState> = emptyList(),
    val trendingRoadmaps: List<TrendingRoadmapCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

enum class HomeGreetingPeriod {
    Morning,
    Afternoon,
    Evening,
    Night
}

data class HomeLearningPlanState(
    val id: String,
    val roadmapTitle: String,
    val skillTitle: String,
    val chapterText: String?,
    val timeLeftText: String?,
    val completedRequiredNodes: Int,
    val totalRequiredNodes: Int,
    val progressPercentage: Int?,
    val nextUnlockText: String?,
    val currentNodeId: String?,
    val startedAtMillis: Long?,
    val paceWarning: HomePaceWarningState?
)

data class HomePaceWarningState(
    val message: String,
    val actionText: String
)

data class HomeRecommendedRoadmapState(
    val id: String,
    val categoryId: String,
    val categoryLabel: String,
    val title: String,
    val nodesText: String,
    val durationText: String,
    val icon: LearningTopicIcon,
    val isBeginner: Boolean
)
