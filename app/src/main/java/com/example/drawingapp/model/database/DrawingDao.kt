package com.example.drawingapp.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drawingapp.model.DbDrawing
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for handling operations related to drawings in the database.
 * This interface provides methods for inserting, querying, and checking existence of drawings.
 */
@Dao
interface DrawingDao {
    /**
     * Inserts a new drawing into the database.
     * @param drawing The drawing to be inserted.
     */
    @Insert
    fun insertDrawing(drawing: DbDrawing)

    /**
     * Retrieves all drawings from the database as a Flow of lists.
     * @return A Flow emitting lists of DbDrawing objects.
     */
    @Query("SELECT * FROM Drawing")
    fun getAllDrawings(): Flow<List<DbDrawing>>

    /**
     * Gets the total count of drawings in the database.
     * @return The count of drawings.
     */
    @Query("SELECT COUNT(*) FROM Drawing")
    suspend fun getDrawingCount(): Int

    /**
     * Checks if a drawing with the specified ID exists in the database.
     * @param id The ID of the drawing to check.
     * @return True if the drawing exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM Drawing WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    /**
     * Retrieves the last inserted drawing's ID from the database.
     * @return The ID of the last inserted drawing, or null if no drawing exists.
     */
    @Query("SELECT id FROM Drawing ORDER BY id DESC LIMIT 1")
    suspend fun getLastId(): Long?
}