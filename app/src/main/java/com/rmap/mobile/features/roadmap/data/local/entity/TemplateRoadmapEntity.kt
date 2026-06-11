package com.rmap.mobile.features.roadmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_roadmaps")
data class TemplateRoadmapEntity(
    @PrimaryKey val id: String,
    val title: String,
    val totalLessonsCount: Int,
    val completedLessonsCount: Int,
    val difficulty: String,
    val durationLabel: String,
    val icon: String,
    val categoryId: String,
    val recommendationBadge: String?,
    val skillNodesCount: Int,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
