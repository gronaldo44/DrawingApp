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

    @Query("SELECT EXISTS (SELECT 1 FROM Drawing WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    @Query("SELECT id FROM Drawing ORDER BY id DESC LIMIT 1")
    suspend fun getLastId(): Long?
}