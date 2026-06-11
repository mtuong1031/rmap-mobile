package com.rmap.mobile.core.database.sync

import com.google.gson.annotations.SerializedName

data class SyncVersionDto(
    @SerializedName("roadmaps") val roadmaps: String?,
    @SerializedName("skills") val skills: String?,
    @SerializedName("resources") val resources: String?
)
