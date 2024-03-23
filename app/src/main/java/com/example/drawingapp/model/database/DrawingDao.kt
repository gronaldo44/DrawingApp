package com.example.drawingapp.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drawingapp.model.DbDrawing
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Insert
    suspend fun insertDrawing(drawing: DbDrawing)

    @Query("SELECT * FROM Drawing")
    fun getAllDrawings(): Flow<List<DbDrawing>>

    @Query("SELECT COUNT(*) FROM Drawing")
    suspend fun getDrawingCount(): Int
}