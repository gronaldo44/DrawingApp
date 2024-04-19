package com.example.drawingapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.drawingapp.R
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.viewmodel.DrawingViewModelFactory

/**
 * Main activity for the drawing application.
 * Holds the container for every single one of the fragments.
 */
class MainActivity : AppCompatActivity() {
    companion object {
      init {
         System.loadLibrary("drawingapp")
      }
    }

    /**
     * When the app is created set the content views to the main activity and run
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: DrawingViewModel by viewModels{
            DrawingViewModelFactory((application as DrawingApplication).repo)}
        setContentView(R.layout.activity_main)
    }
}