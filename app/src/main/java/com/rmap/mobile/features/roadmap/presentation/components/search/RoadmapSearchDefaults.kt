package com.rmap.mobile.features.roadmap.presentation.components.search

import com.rmap.mobile.R

fun defaultRoadmapQuickFilters(): List<RoadmapQuickFilterUiModel> {
    return listOf(
        RoadmapQuickFilterUiModel(id = "required", labelResId = R.string.roadmap_detail_status_required),
        RoadmapQuickFilterUiModel(id = "optional", labelResId = R.string.roadmap_detail_status_optional),
        RoadmapQuickFilterUiModel(id = "completed", labelResId = R.string.roadmap_detail_status_completed),
        RoadmapQuickFilterUiModel(id = "locked", labelResId = R.string.roadmap_detail_locked),
        RoadmapQuickFilterUiModel(id = "milestone", labelResId = R.string.roadmap_detail_milestone_label),
        RoadmapQuickFilterUiModel(id = "chapter", labelResId = R.string.roadmap_detail_chapter_tag)
    )
}
