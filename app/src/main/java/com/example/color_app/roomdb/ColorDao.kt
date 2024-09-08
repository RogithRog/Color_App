package com.example.color_app.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorDao {
    @Query("SELECT * FROM color_items ORDER BY created_at DESC")
    fun getAllColors(): Flow<List<ColorItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColor(color: ColorItem)

    @Query("SELECT COUNT(*) FROM color_items WHERE is_synced = 0")
    fun getUnsyncedCount(): Flow<Int>

    @Query("SELECT * FROM color_items WHERE is_synced = 0")
    suspend fun getUnsyncedColors(): List<ColorItem>

    @Update
    suspend fun updateColors(colors: List<ColorItem>)


    @Query("UPDATE color_items SET is_synced = 1 WHERE is_synced = 0")
    suspend fun markAllAsSynced()


}