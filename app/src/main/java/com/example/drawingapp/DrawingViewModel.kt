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
    private var _brush = MutableLiveData<Brush>()
    val brush: LiveData<Brush>
        get() = _brush

    // LiveData for showing/hiding save/load dialog
    private val _showSaveLoadDialog = MutableLiveData<Boolean>()
    val showSaveLoadDialog: LiveData<Boolean>
        get() = _showSaveLoadDialog

    // LiveData for storing the drawing
    private var _drawing = Drawing()
    val drawing: Drawing
        get() = _drawing

    // LiveData for all stored drawings
    private val _drawingList = MutableLiveData(
        mutableListOf(_drawing)
    )

    val drawingList = _drawingList as LiveData<out List<Drawing>>

    var isFirstDrawing = true

    var isNewDrawing = true



    // Initialize default values
    init {
        _brush.value = Brush()
        // Initialize alert dialog screens
        _showSaveLoadDialog.value = false
        _drawing = Drawing()
    }

    /**
     * Adds a new path to the current drawing.
     * @param path The path to be added.
     * @param color The color of the path.
     * @param size The size of the path.
     */
    fun addPath(path: Path, color: Int, size: Float) {
        // Ensure the drawing data is not null
        val currentDrawing = _drawing ?: return

        // Add the new path to the drawing data
        val pathData = Drawing.PathData(path, color, size)
        currentDrawing.paths.add(pathData)

        // Notify observers about the updated drawing data
        _drawing = currentDrawing
    }

    /**
     * Clears the current drawing.
     */
    fun clearDrawing() {
        _drawing = Drawing()
    }

    /**
     *
     */
    fun saveCurrentDrawing() {
        if (isFirstDrawing) {
            isFirstDrawing = false
            _drawingList.value?.removeAt(0)
            _drawingList.value?.add(_drawing)
        }
        else if (isNewDrawing){
            _drawingList.value?.add(_drawing)
        }
        _drawingList.value = _drawingList.value
        clearDrawing()
    }

    /**
     * Sets the brush color.
     * @param color The new color value.
     */
    fun setBrushColor(color: Int) {
        val currentBrush = _brush
        currentBrush.value?.color = color
        _brush = currentBrush
    }

    /**
     * Sets the brush size.
     * @param size The new size value.
     */
    fun setBrushSize(size: Float) {
        val currentBrush = _brush
        currentBrush.value?.size = size
        _brush = currentBrush
    }

    /**
     * Sets the brush shape.
     * @param shape The new shape value.
     */
    fun selectShape(shape: Brush.Shape) {
        val currentBrush = _brush
        currentBrush.value?.selectedShape = shape
        _brush = currentBrush
    }

    /**
     *
     */
    fun setDrawing(drawing: Drawing) {
        _drawing = drawing
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

    fun isNewDrawing(isNew : Boolean) {
        isNewDrawing = isNew
    }
}