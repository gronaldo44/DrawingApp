package com.example.drawingapp

import android.graphics.Path

/**
 * Represents a drawing composed of paths with associated color and size information.
 * This class holds the paths drawn by the user.
 */
class Drawing {
    /**
     * Data class representing a single path with associated color and size.
     * @property path The path drawn by the user.
     * @property color The color of the path.
     * @property size The size of the path.
     */
    data class PathData(val path: Path, val color: Int, val size: Float)

    // List of path data representing the drawing
    val paths = mutableListOf<PathData>()
}