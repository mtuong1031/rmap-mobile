package com.rmap.mobile.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rmap.mobile.features.bookmarks.data.local.BookmarkDao
import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity

@Database(
    entities = [
        RoadmapBookmarkEntity::class,
        SkillBookmarkEntity::class
    ],
    version = 2,
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
                ).addMigrations(MIGRATION_1_2).build().also { database ->
                    instance = database
                }
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN title TEXT")
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN categoryId TEXT")
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN categoryLabel TEXT")
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN nodesTotal INTEGER")
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN durationLabel TEXT")
                db.execSQL("ALTER TABLE roadmap_bookmarks ADD COLUMN iconKey TEXT")
            }
        }
    }
}
