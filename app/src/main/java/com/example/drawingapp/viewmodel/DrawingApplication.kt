package com.example.drawingapp.viewmodel

import android.app.Application
import com.example.drawingapp.model.database.DrawingDatabase
import com.example.drawingapp.model.database.DrawingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Custom application class for managing global app state and resources.
 */
class DrawingApplication : Application() {

    // Coroutine scope for managing coroutines in the application
    private val scope = CoroutineScope(SupervisorJob())

    // Lazily initialize the database instance using DrawingDatabase singleton
    val db by lazy { DrawingDatabase.getDatabase(applicationContext) }

    // Lazily initialize the repository instance using DrawingRepository
    val repo by lazy { DrawingRepository(scope, db.drawingDao()) }

    // Lazily initialize the authRepository instance using AuthRepository
    val authRepo by lazy {AuthRepository()}

    /**
     * Called when the application is starting, before any other application objects have been created.
     * You can override this method to initialize application-wide resources.
     */
    override fun onCreate() {
        super.onCreate()
        // Perform any application initialization here
    }
}