package com.example.drawingapp.model.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DrawingRepository(private val scope: CoroutineScope,
                        private val drawingDao: DrawingDao) {
    val allDbDrawings: Flow<List<DbDrawing>> = drawingDao.getAllDrawings()

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

    suspend fun getSize(): Int{
        return drawingDao.getDrawingCount()
    }

    suspend fun insertDrawing(fileDir: String) {
        scope.launch {
            drawingDao.insertDrawing(DbDrawing(fileDir = fileDir))
        }
    }

    suspend fun isExists(id: Long): Boolean {
        return drawingDao.exists(id)
    }

    suspend fun getAllConvertedDrawings(context: Context): ArrayList<Drawing> {
        val dbDrawings = allDbDrawings.first() // Or use .take(1).toList() for multiple emissions
        val convertedDrawings = ArrayList<Drawing>()
        for (dbDrawing in dbDrawings) {
            // Convert each DbDrawing to Drawing using DrawingSerializer
            val drawing = DrawingSerializer.toDrawing(dbDrawing, context)
            convertedDrawings.add(drawing)
        }
        return convertedDrawings
    }
}