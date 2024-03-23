package com.example.drawingapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.DrawingConverter
import com.example.drawingapp.model.PathDataConverter

@Database(entities = [DbDrawing::class], version = 1)
abstract class DrawingDatabase : RoomDatabase() {
    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        fun getDatabase(context: Context): DrawingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "Drawings_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}