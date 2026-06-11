package com.rmap.mobile.features.dashboard.data.local.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.dashboard.data.local.entity.DashboardCacheEntity
import com.rmap.mobile.features.dashboard.domain.model.Dashboard

fun Dashboard.toDashboardCacheEntity(gson: Gson): DashboardCacheEntity {
    return DashboardCacheEntity(dataJson = gson.toJson(this))
}

fun DashboardCacheEntity.toDashboard(gson: Gson): Dashboard? {
    return runCatching {
        gson.fromJson(dataJson, Dashboard::class.java)
    }.getOrNull()
}
