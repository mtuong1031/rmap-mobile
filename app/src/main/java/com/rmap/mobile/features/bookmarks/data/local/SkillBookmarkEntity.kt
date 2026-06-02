package com.rmap.mobile.features.bookmarks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_bookmarks")
data class SkillBookmarkEntity(
    @PrimaryKey val skillId: String,
    val roadmapId: String,
    val savedAtMillis: Long,
    val updatedAtMillis: Long,
    val title: String? = null,
    val parentPathName: String? = null,
    val statusKey: String? = null,
    val iconKey: String? = null
)
