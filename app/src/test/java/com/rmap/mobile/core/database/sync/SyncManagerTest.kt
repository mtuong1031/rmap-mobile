package com.rmap.mobile.core.database.sync

import com.rmap.mobile.core.database.dao.SyncMetadataDao
import com.rmap.mobile.core.database.entity.SyncDataType
import com.rmap.mobile.core.database.entity.SyncMetadataEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SyncManagerTest {
    @Test
    fun `isStale returns true when local timestamp is missing`() = runTest {
        val manager = SyncManager(
            syncApi = FakeSyncApi(),
            syncMetadataDao = FakeSyncMetadataDao()
        )

        val stale = manager.isStale(
            dataType = SyncDataType.TEMPLATE_ROADMAPS,
            serverVersions = SyncVersionDto(
                roadmaps = "2026-06-11T10:00:00.000Z",
                skills = null,
                resources = null
            )
        )

        assertTrue(stale)
    }

    @Test
    fun `isStale returns false when local timestamp matches server timestamp`() = runTest {
        val dao = FakeSyncMetadataDao().apply {
            upsert(
                SyncMetadataEntity(
                    dataType = SyncDataType.TEMPLATE_CATEGORIES,
                    serverTimestamp = "2026-06-11T10:00:00.000Z"
                )
            )
        }
        val manager = SyncManager(
            syncApi = FakeSyncApi(),
            syncMetadataDao = dao
        )

        val stale = manager.isStale(
            dataType = SyncDataType.TEMPLATE_CATEGORIES,
            serverVersions = SyncVersionDto(
                roadmaps = "2026-06-11T10:00:00.000Z",
                skills = null,
                resources = null
            )
        )

        assertFalse(stale)
    }

    @Test
    fun `per-skill cache keys compare against skills and resources timestamps`() = runTest {
        val dao = FakeSyncMetadataDao().apply {
            upsert(
                SyncMetadataEntity(
                    dataType = SyncDataType.skill("skill-1"),
                    serverTimestamp = "2026-06-01T08:00:00.000Z"
                )
            )
            upsert(
                SyncMetadataEntity(
                    dataType = SyncDataType.resources("skill-1"),
                    serverTimestamp = "2026-05-20T12:00:00.000Z"
                )
            )
        }
        val manager = SyncManager(
            syncApi = FakeSyncApi(),
            syncMetadataDao = dao
        )
        val serverVersions = SyncVersionDto(
            roadmaps = null,
            skills = "2026-06-01T08:00:00.000Z",
            resources = "2026-06-01T12:00:00.000Z"
        )

        assertFalse(manager.isStale(SyncDataType.skill("skill-1"), serverVersions))
        assertTrue(manager.isStale(SyncDataType.resources("skill-1"), serverVersions))
    }
}

private class FakeSyncApi : SyncApi {
    override suspend fun getVersions(): Response<SyncVersionDto> {
        return Response.success(
            SyncVersionDto(
                roadmaps = "2026-06-11T10:00:00.000Z",
                skills = "2026-06-01T08:00:00.000Z",
                resources = "2026-05-20T12:00:00.000Z"
            )
        )
    }
}

private class FakeSyncMetadataDao : SyncMetadataDao {
    private val metadata = mutableMapOf<String, SyncMetadataEntity>()

    override suspend fun getByType(type: String): SyncMetadataEntity? = metadata[type]

    override suspend fun upsert(metadata: SyncMetadataEntity) {
        this.metadata[metadata.dataType] = metadata
    }

    override suspend fun clearAll() {
        metadata.clear()
    }
}
