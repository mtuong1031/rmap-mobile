package com.rmap.mobile.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rmap.mobile.features.bookmarks.data.local.BookmarkDao
import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity

@Database(
    entities = [
        RoadmapBookmarkEntity::class,
        SkillBookmarkEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RMapDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private const val DATABASE_NAME = "rmap.db"

        @Volatile
        private var instance: RMapDatabase? = null

        fun getInstance(context: Context): RMapDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RMapDatabase::class.java,
                    DATABASE_NAME
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}
