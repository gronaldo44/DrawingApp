package com.example.drawingapp.viewmodel

import android.content.Context
import android.graphics.Path
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drawingapp.model.database.DrawingRepository
import com.example.drawingapp.model.Brush
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.DrawingSerializer
import com.example.drawingapp.model.PathData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ViewModel for managing drawing properties and dialogs in the Drawing Screen.
 * Responsible for managing brush color, brush size, and showing/hiding dialogs.
 */
class DrawingViewModel(private val repository: DrawingRepository) : ViewModel() {
    // LiveData for brush
    private var _brush = MutableLiveData<Brush>()
    val brush: LiveData<Brush>
        get() = _brush

    // LiveData for storing the drawing
    private var _drawing = MutableLiveData<Drawing>()
    val drawing: LiveData<Drawing>
        get() = _drawing

    // This is so that the drawing does not get added if the user is only editing a drawing.
    private var isNewDrawing: Boolean = false

    val saveCompletionChannel = Channel<Unit>()
    private var saveInProgress = false
    private val saveSemaphore = Semaphore(1)

    // ViewModel components for UI
    val drawingVisible: MutableState<Boolean> = mutableStateOf(true)
    val shapeLayoutVisible: MutableState<Boolean> = mutableStateOf(false)
    val modifierLayoutVisible: MutableState<Boolean> = mutableStateOf(false)
    val colorPickerVisible: MutableState<Boolean> = mutableStateOf(false)
    val sizeLayoutVisible: MutableState<Boolean> = mutableStateOf(false)
    val sliderPosition: MutableState<Float> = mutableFloatStateOf(0f)


    // initialize default values
    init {
        _brush.value = Brush()
        _drawing.value = Drawing(ArrayList())
        System.loadLibrary("drawingapp")
    }


    /**
     * Semaphore ensures that only one coroutine can proceed at a time.
     * If saveCurrentDrawing is in progress, getAllDrawings will wait.
     */
    suspend fun getAllDrawings(context: Context): ArrayList<Drawing> {
        // Acquire semaphore
        saveSemaphore.acquire()
        try {
            Log.d("Getting Drawings from", this.toString())
            if (saveInProgress) {
                Log.d("Save in progress", "Waiting")
                // If saveCurrentDrawing is in progress, wait for completion
                saveCompletionChannel.receive() // Wait for saveCurrentDrawing completion signal
            }
            return repository.getAllConvertedDrawings(context)
        } finally {
            // Release semaphore
            saveSemaphore.release()
        }
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
        val pathData = PathData(path, color, size)
        currentDrawing.paths.add(pathData)

        // Notify observers about the updated drawing data
        _drawing.value = currentDrawing
    }

    /**
     * Saves the current drawing
     * If the drawing is edited, it will be edited correctly without adding it to
     * the list. Only add to the list if the drawing didn't exist before.
     */
    suspend fun saveCurrentDrawing(context: Context) {
        val curr = _drawing.value!!
        var filename: String = "Drawing"

        // Perform file writing operation in IO dispatcher
        withContext(Dispatchers.IO) {
            saveInProgress = true // Set flag to indicate save in progress
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Saving Drawing", Toast.LENGTH_SHORT).show()
            }
            saveSemaphore.acquire() // Wait until previous getAllDrawings completes (if any)
            try {
                val isUpdate: Boolean = repository.isExists(curr.id)
                if (!isUpdate){
                    filename += repository.getSize()
                    repository.insertDrawing(filename)
                    Log.d("Inserting Drawing", filename)
                } else {
                    filename += (curr.id - 1)
                    Log.d("Updating Drawing", filename)
                }
                val serializedPathData: String = DrawingSerializer.fromPathDataList(curr.paths)
                val f = File(context.filesDir, filename)
                f.writeText(serializedPathData)
                Log.d("Wrote To:", filename)
            } catch (e: Exception){
                Log.e("Save Error", "Error saving drawing: ${e.message}")
            } finally {
                saveInProgress = false // Reset flag after save operation completes
                saveSemaphore.release() // Release semaphore to allow next getAllDrawings
            }
        }
    }

    /**
     * Sets the brush color.
     * @param color The new color value.
     */
    fun setBrushColor(color: Int) {
        val currentBrush = _brush.value?.copy(color = color)
        _brush.value = currentBrush!!
    }

    /**
     * Updates the brush
     * @param size The new size value.
     */
    fun updateBrush(size: Float? = null, color: Int? = null, shape: Brush.Shape? = null) {
        val currentBrush = _brush.value?.copy()
        if (size != null)
            currentBrush!!.size = size
        if (color != null)
            currentBrush!!.color = color
        if (shape != null)
            currentBrush!!.selectedShape = shape
        _brush.value = currentBrush!!
    }

    /**
     * This sets the drawing to the specified drawing.
     *  @param drawing The drawing that will be the focus of the DrawingView.
     */
    fun setDrawing(drawing: Drawing) {
        _drawing.value = drawing
    }

    /**
     * Sets the isNewDrawing variable.
     * This is so that the drawing does not get added if the user is only editing a drawing.
     * @param isNew True if the drawing is new, false otherwise.
     */
    fun isNewDrawing(isNew : Boolean) {
        isNewDrawing = isNew
    }

    /**
     * Sets color of all paths white
     */
    fun blankPaths(){
        _drawing.value?.let{ makePathsWhiteJIN(it) }
    }
    fun scalePaths(scalar: Float){
        _drawing.value?.let { multPathSizeJIN(it, scalar) }
    }
    fun invertColor(){
        _drawing.value?.let { invertPathColorsJIN(it) }
    }

    external fun makePathsWhiteJIN(drawing: Drawing)
    external fun multPathSizeJIN(drawing: Drawing, scaleFactor: Float)
    external fun invertPathColorsJIN(drawing: Drawing)

    /**
     * Resets the model
     */
    fun resetModel(){
        _brush.value = Brush()
        _drawing.value = Drawing(ArrayList())
    }
}

