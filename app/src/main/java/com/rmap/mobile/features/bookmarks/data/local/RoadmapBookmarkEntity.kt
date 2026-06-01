package com.rmap.mobile.features.bookmarks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmap_bookmarks")
data class RoadmapBookmarkEntity(
    @PrimaryKey val roadmapId: String,
    val savedAtMillis: Long,
    val updatedAtMillis: Long,
    val title: String? = null,
    val categoryId: String? = null,
    val categoryLabel: String? = null,
    val nodesTotal: Int? = null,
    val durationLabel: String? = null,
    val iconKey: String? = null
)
