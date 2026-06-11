package com.rmap.mobile.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rmap.mobile.core.database.dao.SyncMetadataDao
import com.rmap.mobile.core.database.entity.SyncMetadataEntity
import com.rmap.mobile.features.airoadmap.data.local.dao.AiRoadmapCacheDao
import com.rmap.mobile.features.airoadmap.data.local.entity.AiRoadmapCacheEntity
import com.rmap.mobile.features.dashboard.data.local.dao.DashboardCacheDao
import com.rmap.mobile.features.dashboard.data.local.entity.DashboardCacheEntity
import com.rmap.mobile.features.home.data.local.dao.HomeTrendingRoadmapDao
import com.rmap.mobile.features.home.data.local.entity.HomeTrendingRoadmapEntity
import com.rmap.mobile.features.roadmap.data.local.dao.RoadmapDetailCacheDao
import com.rmap.mobile.features.roadmap.data.local.dao.SkillDao
import com.rmap.mobile.features.roadmap.data.local.dao.TemplateCategoryDao
import com.rmap.mobile.features.roadmap.data.local.dao.TemplateRoadmapDao
import com.rmap.mobile.features.roadmap.data.local.entity.RoadmapDetailCacheEntity
import com.rmap.mobile.features.roadmap.data.local.entity.SkillEntity
import com.rmap.mobile.features.roadmap.data.local.entity.SkillResourceEntity
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateCategoryEntity
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateRoadmapEntity

@Database(
    entities = [
        SyncMetadataEntity::class,
        TemplateCategoryEntity::class,
        TemplateRoadmapEntity::class,
        HomeTrendingRoadmapEntity::class,
        SkillEntity::class,
        SkillResourceEntity::class,
        DashboardCacheEntity::class,
        RoadmapDetailCacheEntity::class,
        AiRoadmapCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class RMapDatabase : RoomDatabase() {
    abstract fun syncMetadataDao(): SyncMetadataDao
    abstract fun templateCategoryDao(): TemplateCategoryDao
    abstract fun templateRoadmapDao(): TemplateRoadmapDao
    abstract fun homeTrendingRoadmapDao(): HomeTrendingRoadmapDao
    abstract fun skillDao(): SkillDao
    abstract fun dashboardCacheDao(): DashboardCacheDao
    abstract fun roadmapDetailCacheDao(): RoadmapDetailCacheDao
    abstract fun aiRoadmapCacheDao(): AiRoadmapCacheDao
}
