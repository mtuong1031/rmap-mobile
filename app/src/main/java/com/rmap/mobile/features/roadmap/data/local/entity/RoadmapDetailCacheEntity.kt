package com.rmap.mobile.features.roadmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmap_detail_cache")
data class RoadmapDetailCacheEntity(
    @PrimaryKey val roadmapId: String,
    val dataJson: String,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
