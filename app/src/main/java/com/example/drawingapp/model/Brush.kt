package com.example.drawingapp.model

import android.graphics.Color

/**
 * Represents the properties of a brush used for drawing.
 */
data class Brush(
    var color: Int = Color.BLACK, // Default color for the brush
    var size: Float = 5f, // Default size for the brush
    var selectedShape: Shape = Shape.PATH // Default selected shape
) {
    // Enum representing different shapes for drawing
    enum class Shape {
        PATH,
        TRIANGLE,
        RECTANGLE,
        CIRCLE
    }

}