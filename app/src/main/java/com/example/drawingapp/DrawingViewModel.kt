package com.example.drawingapp

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ViewModel for managing drawing properties and dialogs in the Drawing Screen.
 * Responsible for managing brush color, brush size, and showing/hiding dialogs.
 *
 * TODO: Create the model. Maybe make a pen model &/or a canvas model that this updates.
 */
class DrawingViewModel : ViewModel() {
    // LiveData for brush color
    private val _brushColor = MutableLiveData<Int>()
    val brushColor: LiveData<Int>
        get() = _brushColor

    // LiveData for brush size
    private val _brushSize = MutableLiveData<Float>()
    val brushSize: LiveData<Float>
        get() = _brushSize

    // LiveData for showing/hiding shapes dialog
    private val _showShapesDialog = MutableLiveData<Boolean>()
    val showShapesDialog: LiveData<Boolean>
        get() = _showShapesDialog

    // LiveData for showing/hiding save/load dialog
    private val _showSaveLoadDialog = MutableLiveData<Boolean>()
    val showSaveLoadDialog: LiveData<Boolean>
        get() = _showSaveLoadDialog

    // Initialize default values
    init {
        // Initialize default values
        _brushColor.value = Color.BLACK
        _brushSize.value = 5f
        _showShapesDialog.value = false
        _showSaveLoadDialog.value = false
    }

    /**
     * Sets the brush color.
     * @param color The new color value.
     */
    fun setBrushColor(color: Int) {
        _brushColor.value = color
    }

    /**
     * Sets the brush size.
     * @param size The new size value.
     */
    fun setBrushSize(size: Float) {
        _brushSize.value = size
    }

    /**
     * Shows the shapes dialog.
     */
    fun showShapesDialog() {
        _showShapesDialog.value = true
    }

    /**
     * Marks the shapes dialog as shown.
     */
    fun shapesDialogShown() {
        _showShapesDialog.value = false
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