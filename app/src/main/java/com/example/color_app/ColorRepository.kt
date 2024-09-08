package com.example.color_app

import android.util.Log
import com.example.color_app.roomdb.ColorDao
import com.example.color_app.roomdb.ColorItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class ColorRepository(private val colorDao: ColorDao) {
    private val database: FirebaseDatabase = Firebase.database
    private val colorsRef = database.getReference("colors")

    fun getAllColors(): Flow<List<ColorItem>> = colorDao.getAllColors()
    suspend fun insertColor(color: ColorItem) {
        colorDao.insertColor(color)
    }
    fun getUnsyncedCount(): Flow<Int> = colorDao.getUnsyncedCount()

    suspend fun syncColors() {
        try {
            val unsyncedColors = colorDao.getUnsyncedColors()
            Log.d("ColorRepository", "Starting sync. Unsynced colors count: ${unsyncedColors.size}")

            unsyncedColors.forEach { color ->
                try {
                    Log.d("ColorRepository", "Syncing color: ${color.id} with value: ${color.color}")

                    // Issue: This operation is not waiting for completion
                    colorsRef.push().setValue(color)

                    Log.d("ColorRepository", "Successfully synced color: ${color.id}")
                    color.isSynced = true
                } catch (e: Exception) {
                    Log.e("ColorRepository", "Failed to sync color: ${color.id}", e)
                }
            }
            // Issue: This is called regardless of individual sync success
            colorDao.markAllAsSynced()
            colorDao.updateColors(unsyncedColors)
        } catch (e: Exception) {
            Log.e("ColorRepository", "Failed to sync colors", e)
            throw e
        }
    }
    // Add this method to test Firebase connection

}