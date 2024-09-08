package com.example.color_app.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ColorItem::class], version = 3)  // Updated version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "color_database"
                )
                    .fallbackToDestructiveMigration()  // Add migration strategy
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}