package com.rmap.mobile.features.roadmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rmap.mobile.features.roadmap.data.local.entity.SkillEntity
import com.rmap.mobile.features.roadmap.data.local.entity.SkillResourceEntity

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills WHERE id = :id")
    suspend fun getSkill(id: String): SkillEntity?

    @Query("SELECT * FROM skill_resources WHERE skillId = :skillId ORDER BY title ASC")
    suspend fun getResources(skillId: String): List<SkillResourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSkill(skill: SkillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<SkillResourceEntity>)

    @Query("DELETE FROM skill_resources WHERE skillId = :skillId")
    suspend fun deleteResources(skillId: String)

    @Transaction
    suspend fun replaceSkillWithResources(
        skill: SkillEntity,
        resources: List<SkillResourceEntity>
    ) {
        upsertSkill(skill)
        deleteResources(skill.id)
        insertResources(resources)
    }
}
