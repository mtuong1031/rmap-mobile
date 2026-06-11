package com.rmap.mobile.features.home.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_trending_roadmaps")
data class HomeTrendingRoadmapEntity(
    @PrimaryKey val roadmapId: String,
    val rank: Int,
    val title: String,
    val roleCategory: String,
    val categoryLabel: String,
    val estimatedWeeks: Int?,
    val durationLabel: String?,
    val nodesTotal: Int,
    val trendText: String,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
