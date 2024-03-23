package com.example.drawingapp.model.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingRepository(private val scope: CoroutineScope,
                        private val drawingDao: DrawingDao) {
    val allDbDrawings: Flow<List<DbDrawing>> = drawingDao.getAllDrawings()

    suspend fun insertDrawing(drawing: Drawing) {
        val dbDrawing = DrawingConverter.fromDrawing(drawing)
        scope.launch {
            drawingDao.insertDrawing(dbDrawing)
        }
    }

    suspend fun isDatabaseEmpty(): Boolean {
        return withContext(Dispatchers.IO) {
            val count = drawingDao.getDrawingCount()
            count == 0
        }
    }

    fun getAllConvertedDrawings(): MutableList<Drawing> {
        val convertedDrawings =  mutableListOf(Drawing(ArrayList()))

        scope.launch {
            allDbDrawings.collect { dbDrawings ->
                val drawings = dbDrawings.map { dbDrawing ->
                    DrawingConverter.toDrawing(dbDrawing)
                }
                convertedDrawings.addAll(drawings)
            }
        }

        return convertedDrawings
    }

}