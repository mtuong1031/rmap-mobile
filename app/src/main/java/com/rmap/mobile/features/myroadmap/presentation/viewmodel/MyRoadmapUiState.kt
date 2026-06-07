package com.rmap.mobile.features.myroadmap.presentation.viewmodel

data class MyRoadmapUiState(
    val userName: String = "",
    val filters: List<MyRoadmapFilterUiModel> = MyRoadmapFilter.entries.map { filter ->
        MyRoadmapFilterUiModel(filter = filter, count = 0)
    },
    val selectedFilter: MyRoadmapFilter = MyRoadmapFilter.Active,
    val roadmaps: List<MyRoadmapCardUiModel> = emptyList(),
    val achievements: List<MyRoadmapAchievementUiModel> = emptyList(),
    val completedSkills: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val visibleRoadmaps: List<MyRoadmapCardUiModel>
        get() = roadmaps.filter { roadmap ->
            when (selectedFilter) {
                MyRoadmapFilter.Active -> roadmap.startedAt != null && roadmap.completionPercent < 100
                MyRoadmapFilter.All -> true
                MyRoadmapFilter.Completed -> roadmap.completionPercent >= 100
                MyRoadmapFilter.Behind -> roadmap.isBehind
            }
        }
}

enum class MyRoadmapFilter {
    Active,
    All,
    Completed,
    Behind
}

data class MyRoadmapFilterUiModel(
    val filter: MyRoadmapFilter,
    val count: Int
)

data class MyRoadmapCardUiModel(
    val id: String,
    val title: String,
    val categoryKey: String,
    val categoryLabel: String,
    val isTemplate: Boolean,
    val completionPercent: Int,
    val nodesCompleted: Int,
    val nodesTotal: Int,
    val deadlineDate: String?,
    val estimatedWeeks: Int?,
    val startedAt: String?,
    val isBehind: Boolean
)

data class MyRoadmapAchievementUiModel(
    val categoryKey: String,
    val label: String,
    val totalSkills: Int
)
