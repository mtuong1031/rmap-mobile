package com.rmap.mobile.features.bookmarks.domain.model

import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary

enum class BookmarkTab {
    Roadmaps,
    Skills
}

enum class BookmarkStatusFilter {
    All,
    InProgress,
    NotStarted,
    Completed
}

data class SkillBookmark(
    val title: String,
    val parentPathName: String,
    val status: LearningStatus,
    val icon: LearningTopicIcon,
    val skillId: String,
    val roadmapId: String,
    val savedAtMillis: Long = 0L,
    val updatedAtMillis: Long = 0L
)

data class RoadmapBookmark(
    val summary: RoadmapSummary,
    val status: LearningStatus,
    val savedAtMillis: Long,
    val updatedAtMillis: Long
)

data class RoadmapBookmarkSnapshot(
    val roadmapId: String,
    val title: String,
    val categoryId: String,
    val categoryLabel: String,
    val nodesTotal: Int,
    val durationLabel: String,
    val iconKey: String
)
