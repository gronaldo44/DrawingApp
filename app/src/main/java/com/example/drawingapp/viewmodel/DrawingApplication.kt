package com.example.drawingapp.viewmodel

import android.app.Application
import com.example.drawingapp.model.database.DrawingDatabase
import com.example.drawingapp.model.database.DrawingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())
    val db by lazy { DrawingDatabase.getDatabase(applicationContext) }
    val repo by lazy { DrawingRepository(scope, db.drawingDao()) }
}