package com.rmap.mobile.features.roadmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_categories")
data class TemplateCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val roadmapCount: Int,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
