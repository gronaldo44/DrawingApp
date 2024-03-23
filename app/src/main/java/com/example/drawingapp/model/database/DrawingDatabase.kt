package com.example.drawingapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
<<<<<<< HEAD
import com.example.drawingapp.model.DbDrawing
=======
import androidx.room.TypeConverters
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.DrawingConverter
import com.example.drawingapp.model.PathDataConverter
>>>>>>> e89efcc9f00e17cdba7d3d25474c6d899f788787

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