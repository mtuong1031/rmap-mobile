package com.rmap.mobile.features.bookmarks.domain.model

import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon

enum class BookmarkTab {
    Roadmaps,
    Skills
}

data class SkillBookmark(
    val title: String,
    val parentPathName: String,
    val status: LearningStatus,
    val icon: LearningTopicIcon
)
