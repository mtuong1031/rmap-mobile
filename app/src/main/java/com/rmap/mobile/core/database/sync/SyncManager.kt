package com.rmap.mobile.core.database.sync

import com.rmap.mobile.core.database.dao.SyncMetadataDao
import com.rmap.mobile.core.database.entity.SyncDataType
import com.rmap.mobile.core.database.entity.SyncMetadataEntity
import com.rmap.mobile.core.network.NetworkResult
import com.rmap.mobile.core.network.SafeApiCall

class SyncManager(
    private val syncApi: SyncApi,
    private val syncMetadataDao: SyncMetadataDao
) {
    suspend fun getServerVersions(): SyncVersionDto? {
        return when (val result = SafeApiCall.execute { syncApi.getVersions() }) {
            is NetworkResult.Success -> result.data
            is NetworkResult.Error -> null
        }
    }

    suspend fun isStale(dataType: String, serverVersions: SyncVersionDto?): Boolean {
        val serverTimestamp = serverVersions.timestampFor(dataType) ?: return false
        val local = syncMetadataDao.getByType(dataType)
        return local == null || serverTimestamp > local.serverTimestamp
    }

    suspend fun markSynced(dataType: String, serverVersions: SyncVersionDto?) {
        val serverTimestamp = serverVersions.timestampFor(dataType) ?: return
        syncMetadataDao.upsert(
            SyncMetadataEntity(
                dataType = dataType,
                serverTimestamp = serverTimestamp
            )
        )
    }

    suspend fun invalidateAll() {
        syncMetadataDao.clearAll()
    }

    private fun SyncVersionDto?.timestampFor(dataType: String): String? {
        if (this == null) return null
        return when {
            dataType == SyncDataType.TEMPLATE_ROADMAPS ||
                dataType == SyncDataType.TEMPLATE_CATEGORIES ||
                dataType == SyncDataType.TEMPLATE_TRENDINGS -> roadmaps

            dataType.startsWith("skills:") -> skills
            dataType.startsWith("resources:") -> resources
            dataType == "skills" -> skills
            dataType == "resources" -> resources
            else -> null
        }
    }
}
