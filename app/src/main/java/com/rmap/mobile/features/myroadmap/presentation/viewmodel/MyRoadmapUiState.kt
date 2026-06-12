package com.rmap.mobile.features.myroadmap.presentation.viewmodel

data class MyRoadmapUiState(
    val userName: String = "",
    val selectedFilter: MyRoadmapFilter = MyRoadmapFilter.All,
    val searchQuery: String = "",
    val roadmaps: List<MyRoadmapCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filters: List<MyRoadmapFilterUiModel>
        get() = MyRoadmapFilter.entries.map { filter ->
            val count = roadmaps.count { roadmap ->
                when (filter) {
                    MyRoadmapFilter.All -> true
                    MyRoadmapFilter.Active -> roadmap.startedAt != null && roadmap.completionPercent < 100
                    MyRoadmapFilter.Completed -> roadmap.completionPercent >= 100
                    MyRoadmapFilter.Behind -> roadmap.isBehind
                }
            }
            MyRoadmapFilterUiModel(filter = filter, count = count)
        }
    val visibleRoadmaps: List<MyRoadmapCardUiModel>
        get() {
            val normalizedQuery = searchQuery.trim()
            val filteredRoadmaps = roadmaps.filter { roadmap ->
                val matchesFilter = when (selectedFilter) {
                    MyRoadmapFilter.Active -> roadmap.startedAt != null && roadmap.completionPercent < 100
                    MyRoadmapFilter.All -> true
                    MyRoadmapFilter.Completed -> roadmap.completionPercent >= 100
                    MyRoadmapFilter.Behind -> roadmap.isBehind
                }
                val matchesSearch = normalizedQuery.isBlank() ||
                    roadmap.title.contains(normalizedQuery, ignoreCase = true) ||
                    roadmap.categoryLabel.contains(normalizedQuery, ignoreCase = true)

                matchesFilter && matchesSearch
            }
            return if (selectedFilter == MyRoadmapFilter.Active || selectedFilter == MyRoadmapFilter.Behind) {
                filteredRoadmaps.sortedWith(
                    compareByDescending<MyRoadmapCardUiModel> { it.isBehind }
                        .thenBy { it.deadlineDate ?: SORT_LAST_DATE }
                        .thenByDescending { it.completionPercent }
                )
            } else {
                filteredRoadmaps
            }
        }

    val hasRoadmapSearchResults: Boolean
        get() = visibleRoadmaps.isNotEmpty()

    val isSearching: Boolean
        get() = searchQuery.isNotBlank()

    private companion object {
        const val SORT_LAST_DATE = "9999-12-31"
    }
}

enum class MyRoadmapFilter {
    All,
    Active,
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

sealed interface MyRoadmapEvent {
    data object DashboardRefreshFailed : MyRoadmapEvent
}
