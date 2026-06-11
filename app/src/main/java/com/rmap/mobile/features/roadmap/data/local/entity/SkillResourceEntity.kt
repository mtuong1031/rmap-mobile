package com.rmap.mobile.features.roadmap.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "skill_resources",
    foreignKeys = [
        ForeignKey(
            entity = SkillEntity::class,
            parentColumns = ["id"],
            childColumns = ["skillId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("skillId")]
)
data class SkillResourceEntity(
    @PrimaryKey val id: String,
    val skillId: String,
    val title: String,
    val url: String,
    val platform: String?,
    val isFree: Boolean,
    val levelTag: String?,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)
