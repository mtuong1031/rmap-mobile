package com.rmap.mobile.features.roadmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateCategoryEntity

@Dao
interface TemplateCategoryDao {
    @Query("SELECT * FROM template_categories ORDER BY name ASC")
    suspend fun getAll(): List<TemplateCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<TemplateCategoryEntity>)

    @Query("DELETE FROM template_categories")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(categories: List<TemplateCategoryEntity>) {
        deleteAll()
        insertAll(categories)
    }
}
