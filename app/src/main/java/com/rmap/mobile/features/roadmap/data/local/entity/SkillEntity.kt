package com.rmap.mobile.features.roadmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val estimatedHours: Int?,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
