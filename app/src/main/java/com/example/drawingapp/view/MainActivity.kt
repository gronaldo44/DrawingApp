package com.example.drawingapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drawingapp.R

/**
 * Main activity for the drawing application.
 * Holds the container for every single one of the fragments.
 */
class MainActivity : AppCompatActivity() {
    /**
     * When the app is created set the content views to the main activity and run
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}