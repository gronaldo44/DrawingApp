package com.example.drawingapp

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ViewModel for managing drawing properties and dialogs in the Drawing Screen.
 * Responsible for managing brush color, brush size, and showing/hiding dialogs.
 */
class DrawingViewModel : ViewModel() {
    // LiveData for brush
    private val _brush = MutableLiveData<Brush>()
    val brush: LiveData<Brush>
        get() = _brush

    // LiveData to handle color picker dialog visibility
    private val _showColorPickerDialog = MutableLiveData<Boolean>()
    val showColorPickerDialog: LiveData<Boolean>
        get() = _showColorPickerDialog

    // LiveData for showing/hiding shapes dialog
    private val _showShapesDialog = MutableLiveData<Boolean>()
    val showShapesDialog: LiveData<Boolean>
        get() = _showShapesDialog

    // LiveData for showing/hiding save/load dialog
    private val _showSaveLoadDialog = MutableLiveData<Boolean>()
    val showSaveLoadDialog: LiveData<Boolean>
        get() = _showSaveLoadDialog

    // LiveData for storing the drawing
    private val _drawing = MutableLiveData<Drawing>()
    val drawing: LiveData<Drawing>
        get() = _drawing

    // Initialize default values
    init {
        // Initialize default brush
        _brush.value = Brush()
        // Initialize alert dialog screens
        _showShapesDialog.value = false
        _showSaveLoadDialog.value = false
        // Initialize the drawing data
        _drawing.value = Drawing()
    }

    /**
     * Adds a new path to the current drawing.
     * @param path The path to be added.
     * @param color The color of the path.
     * @param size The size of the path.
     */
    fun addPath(path: Path, color: Int, size: Float) {
        // Ensure the drawing data is not null
        val currentDrawing = _drawing.value ?: return

        // Add the new path to the drawing data
        val pathData = Drawing.PathData(path, color, size)
        currentDrawing.paths.add(pathData)

        // Notify observers about the updated drawing data
        _drawing.value = currentDrawing
    }

    /**
     * Clears the current drawing.
     */
    fun clearDrawing() {
        _drawing.value = Drawing()
    }

    /**
     * Sets the brush color.
     * @param color The new color value.
     */
    fun setBrushColor(color: Int) {
        val currentBrush = _brush.value ?: Brush()
        currentBrush.color = color
        _brush.value = currentBrush
    }

    /**
     * Sets the brush size.
     * @param size The new size value.
     */
    fun setBrushSize(size: Float) {
        val currentBrush = _brush.value ?: Brush()
        currentBrush.size = size
        _brush.value = currentBrush
    }

    /**
     * Sets the brush shape.
     * @param shape The new shape value.
     */
    fun selectShape(shape: Brush.Shape) {
        val currentBrush = _brush.value ?: Brush()
        currentBrush.selectedShape = shape
        _brush.value = currentBrush
    }

    /**
     * Shows the save/load dialog.
     */
    fun showSaveLoadDialog() {
        _showSaveLoadDialog.value = true
    }

    /**
     * Marks the save/load dialog as shown.
     */
    fun saveLoadDialogShown() {
        _showSaveLoadDialog.value = false
    }
}