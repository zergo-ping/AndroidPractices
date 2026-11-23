package com.example.androidpractise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EpisodeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val NAME = "android-practise.db"
    }
}
