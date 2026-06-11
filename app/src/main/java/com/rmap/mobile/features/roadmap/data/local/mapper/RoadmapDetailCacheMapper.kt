package com.rmap.mobile.features.roadmap.data.local.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.roadmap.data.local.entity.RoadmapDetailCacheEntity
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail

fun RoadmapDetail.toRoadmapDetailCacheEntity(gson: Gson): RoadmapDetailCacheEntity {
    return RoadmapDetailCacheEntity(
        roadmapId = id,
        dataJson = gson.toJson(copy(contentItems = emptyList()))
    )
}

fun RoadmapDetailCacheEntity.toRoadmapDetail(gson: Gson): RoadmapDetail? {
    return runCatching {
        gson.fromJson(dataJson, RoadmapDetail::class.java)
    }.getOrNull()
}
