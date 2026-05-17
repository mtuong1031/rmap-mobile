package com.rmap.mobile.features.bookmarks.domain.repository

import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary

interface BookmarkRepository {
    suspend fun getSavedRoadmaps(): Result<List<RoadmapSummary>>
    suspend fun getSavedSkills(): Result<List<SkillBookmark>>
}
