package com.rmap.mobile.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val dataType: String,
    val serverTimestamp: String,
    val syncedAtEpoch: Long = System.currentTimeMillis()
)

object SyncDataType {
    const val TEMPLATE_ROADMAPS = "template_roadmaps"
    const val TEMPLATE_CATEGORIES = "template_categories"
    const val TEMPLATE_TRENDINGS = "template_trendings"

    fun skill(skillId: String): String = "skills:${skillId.trim()}"

    fun resources(skillId: String): String = "resources:${skillId.trim()}"
}
