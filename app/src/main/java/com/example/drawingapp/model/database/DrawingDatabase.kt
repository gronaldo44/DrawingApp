package com.example.drawingapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drawingapp.model.DbDrawing

/**
 * Room Database class for managing drawings data.
 */
@Database(entities = [DbDrawing::class], version = 1)
abstract class DrawingDatabase : RoomDatabase() {

    /**
     * Abstract method to retrieve the DAO (Data Access Object) for drawings.
     */
    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        /**
         * Get a singleton instance of the DrawingDatabase.
         *
         * @param context Application context.
         * @return Singleton instance of DrawingDatabase.
         */
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