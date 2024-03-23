package com.example.drawingapp.model.database

import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    fun getAllConvertedDrawings(context: Context): LiveData<List<Drawing>> {
        val convertedDrawings = MediatorLiveData<List<Drawing>>()

        scope.launch {
            allDbDrawings.collect { dbDrawings ->
                val drawings = dbDrawings.map { dbDrawing ->
                    DrawingSerializer.toDrawing(dbDrawing, context)
                }
                convertedDrawings.postValue(drawings)
            }
        }

        return convertedDrawings
    }

}