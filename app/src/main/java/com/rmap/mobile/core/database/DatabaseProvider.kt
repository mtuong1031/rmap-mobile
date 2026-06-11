package com.rmap.mobile.core.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var instance: RMapDatabase? = null

    fun getDatabase(context: Context): RMapDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                RMapDatabase::class.java,
                "rmap_database"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { database -> instance = database }
        }
    }
}
