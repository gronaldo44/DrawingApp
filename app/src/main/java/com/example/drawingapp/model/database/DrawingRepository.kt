package com.example.drawingapp.model.database

import android.content.Context
import android.util.Log
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Repository class responsible for handling data operations for drawings.
 * Provides methods to interact with the local database and perform drawing-related operations.
 *
 * @property scope CoroutineScope for launching coroutine tasks.
 * @property drawingDao DAO interface for interacting with the drawing database.
 */
class DrawingRepository(private val scope: CoroutineScope,
                        private val drawingDao: DrawingDao) {
    /**
     * Flow of all drawings from the database.
     */
    val allDbDrawings: Flow<List<DbDrawing>> = drawingDao.getAllDrawings()

    /**
     * Retrieves the last ID used in the database for drawings.
     *
     * @return The last ID used in the database.
     */
    suspend fun getLastId(): Long{
        var num: Long? = -1
        scope.launch {
            num = drawingDao.getLastId()
        }
        if (num == null){
            return 1
        } else {
            return num as Long
        }
    }

    /**
     * Retrieves the number of drawings in the database.
     *
     * @return The count of drawings in the database.
     */
    suspend fun getSize(): Int{
        return drawingDao.getDrawingCount()
    }

    /**
     * Inserts a new drawing entry into the database.
     *
     * @param fileDir The file directory associated with the drawing.
     */
    suspend fun insertDrawing(fileDir: String) {
        scope.launch {
            drawingDao.insertDrawing(DbDrawing(fileDir = fileDir))
        }
    }

    /**
     * Checks if a drawing with the specified ID exists in the database.
     *
     * @param id The ID of the drawing to check.
     * @return True if the drawing exists, false otherwise.
     */
    suspend fun isExists(id: Long): Boolean {
        return drawingDao.exists(id)
    }

    /**
     * Retrieves all drawings from the database and converts them to Drawing objects.
     *
     * @param context The application context for serialization.
     * @return List of converted Drawing objects.
     */
    suspend fun getAllConvertedDrawings(context: Context): ArrayList<Drawing> {
        val dbDrawings = allDbDrawings.first() // Or use .take(1).toList() for multiple emissions
        val convertedDrawings = ArrayList<Drawing>()
        for (dbDrawing in dbDrawings) {
            // Convert each DbDrawing to Drawing using DrawingSerializer
            val drawing = DrawingSerializer.toDrawing(dbDrawing, context)
            Log.d("Converted Drawing", drawing.id.toString())
            convertedDrawings.add(drawing)
        }
        return convertedDrawings
    }
}