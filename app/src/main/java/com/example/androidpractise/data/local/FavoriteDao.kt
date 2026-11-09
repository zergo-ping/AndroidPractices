package com.example.androidpractise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT id FROM favorites")
    fun observeIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun observeIsFavorite(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Int): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Int)
}
