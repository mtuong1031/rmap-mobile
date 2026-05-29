package com.rmap.mobile.features.bookmarks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmap_bookmarks")
data class RoadmapBookmarkEntity(
    @PrimaryKey val roadmapId: String,
    val savedAtMillis: Long,
    val updatedAtMillis: Long
)
