package com.rmap.mobile.features.airoadmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_roadmaps_cache")
data class AiRoadmapCacheEntity(
    @PrimaryKey val id: Int = AI_ROADMAP_CACHE_ID,
    val dataJson: String,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)

const val AI_ROADMAP_CACHE_ID = 0
