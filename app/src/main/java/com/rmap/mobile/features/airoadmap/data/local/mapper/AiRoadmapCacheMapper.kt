package com.rmap.mobile.features.airoadmap.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rmap.mobile.features.airoadmap.data.local.entity.AiRoadmapCacheEntity
import com.rmap.mobile.features.airoadmap.domain.model.AiGeneratedRoadmap

fun List<AiGeneratedRoadmap>.toAiRoadmapCacheEntity(gson: Gson): AiRoadmapCacheEntity {
    return AiRoadmapCacheEntity(dataJson = gson.toJson(this))
}

fun AiRoadmapCacheEntity.toAiGeneratedRoadmaps(gson: Gson): List<AiGeneratedRoadmap>? {
    return runCatching {
        val type = object : TypeToken<List<AiGeneratedRoadmap>>() {}.type
        gson.fromJson<List<AiGeneratedRoadmap>>(dataJson, type)
    }.getOrNull()
}
