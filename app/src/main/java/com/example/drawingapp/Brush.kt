package com.example.drawingapp

import android.graphics.Color
import android.graphics.Path

/**
 * Represents the properties of a brush used for drawing.
 * This class stores information such as color, size, and selected shape.
 * It also provides an enum for different shapes and a property to store custom shapes.
 *
 * TODO: implement and make using other shapes than PATH possible
 */
data class Brush(
    var color: Int = Color.BLACK, // Default color for the brush
    var size: Float = 5f, // Default size for the brush
    var selectedShape: Shape = Shape.PATH // Default selected shape
) {
    // Enum representing different shapes for drawing
    enum class Shape {
        PATH, // Free-form path
        STAR, // Star shape
        RECTANGLE, // Rectangle shape
        TRIANGLE // Triangle shape
    }

    // Path to store custom shapes
    var customPath: Path? = null
}