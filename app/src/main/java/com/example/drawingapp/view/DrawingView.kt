package com.example.drawingapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.model.Brush
import com.example.drawingapp.model.Drawing
import kotlin.math.hypot

/**
 * Custom View for drawing functionality.
 *
 * This view allows users to draw on a canvas using touch gestures.
 * It supports setting brush color and size based on the provided ViewModel.
 */
class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private lateinit var drawPaint: Paint
    private var isDrawingShapes = false
    private var viewModel: DrawingViewModel? = null    //tmp value
    private var isDrawing = false
    private var specifiedDrawing: Drawing? = null   // tmp value
    private var useSpecifiedDrawing = false

    // Variables used to draw the preview
    private var startX = 0f
    private var startY = 0f
    private var tempEndX: Float = 0f
    private var tempEndY: Float = 0f
    private val previewPaint = Paint().apply {// Preview paint brush
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    init {
        setupDrawing()
    }

    /**
     * Setup the drawing components.
     */
    private fun setupDrawing() {
        drawPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    /**
     * Draws the canvas and paths on the view.
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var drawing = viewModel?.drawing?.value
        if (useSpecifiedDrawing) {
            drawing = specifiedDrawing
        }

        // Draw each path in the list
        drawing?.paths?.forEach { pathData ->
            drawPaint.color = pathData.color
            drawPaint.strokeWidth = pathData.size
            canvas.drawPath(pathData.path, drawPaint)
        }

        // Handles the grey preview of each shape
        if (((viewModel?.brush?.value?.selectedShape ?: Brush.Shape.PATH) == Brush.Shape.RECTANGLE) && isDrawingShapes) { // Draws a rectangle preview
            canvas.drawRect(startX, startY, tempEndX, tempEndY, previewPaint)
        }
        if (isDrawingShapes && viewModel?.brush?.value?.selectedShape == Brush.Shape.TRIANGLE) { // Draws a rectangle preview
            val topX = (startX + tempEndX) / 2
            val previewPath = Path().apply {
                moveTo(topX, startY)
                lineTo(startX, tempEndY)
                lineTo(tempEndX, tempEndY)
                close()
            }
            canvas.drawPath(previewPath, previewPaint)
        }
        if (isDrawingShapes && viewModel?.brush?.value?.selectedShape == Brush.Shape.CIRCLE) { // Draws a circle preview
            val radius = hypot((tempEndX - startX).toDouble(), (tempEndY - startY).toDouble()).toFloat()
            canvas.drawCircle(startX, startY, radius, previewPaint)
        }
    }


    /**
     * Handles touch events for drawing. Handles all of the shapes and path drawing
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isDrawing)
            return false
        super.onTouchEvent(event)

        val curShape = viewModel?.brush?.value?.selectedShape ?: Brush.Shape.PATH
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> { // When mouse is pressed down
                startX = event.x
                startY = event.y
                if (curShape == Brush.Shape.PATH){ // If the shape is a path make a new path at x and y
                    val newPath = Path()
                    newPath.moveTo(event.x, event.y)
                    viewModel?.addPath(newPath, viewModel?.brush?.value?.color ?: Color.BLACK,
                        viewModel?.brush?.value?.size ?: 5f)
                }
                if (curShape != Brush.Shape.PATH) { // If the shape is anything other than a path set preview variables
                    isDrawingShapes = true
                    tempEndX = event.x
                    tempEndY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> { // When the mouse is moved when clicked
                if (curShape == Brush.Shape.PATH){ // When the shape is a path, add a line from the previous location to the current
                    val drawing = viewModel?.drawing?.value
                    drawing?.paths?.lastOrNull()?.path?.lineTo(event.x, event.y)
                }
                if (curShape != Brush.Shape.PATH && isDrawingShapes) { // When the shape is not a path update the temp variables and redraw to activate the preview
                    tempEndX = event.x
                    tempEndY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> { // When the mouse is released
                var newPath = Path()
                if (curShape == Brush.Shape.RECTANGLE && isDrawingShapes) { // When the shape is a rectangle draw four lines
                    newPath = Path().apply {
                        moveTo(startX, startY)
                        lineTo(tempEndX, startY)
                        lineTo(tempEndX, tempEndY)
                        lineTo(startX, tempEndY)
                        close()
                    }
                }
                if (curShape == Brush.Shape.TRIANGLE && isDrawingShapes) { // When the shape is a Triable draw the three lines starting at the middle top
                    val topX = (startX + tempEndX) / 2
                    newPath = Path().apply {
                        moveTo(topX, startY)
                        lineTo(startX, tempEndY)
                        lineTo(tempEndX, tempEndY)
                        close()
                    }
                }
                if (curShape == Brush.Shape.CIRCLE && isDrawingShapes) { // When the shape is a circle draw a circle path with the add circle method
                    val radius = Math.hypot((tempEndX - startX).toDouble(), (tempEndY - startY).toDouble()).toFloat()
                    newPath = Path().apply {
                        addCircle(startX, startY, radius, Path.Direction.CW)
                    }
                }
                viewModel?.addPath(newPath, viewModel!!.brush.value?.color ?: Color.BLACK,
                    viewModel!!.brush.value?.size ?: 5f)
                isDrawingShapes = false
            }
        }
        invalidate() // Refresh screen
        return true
    }

    /**
     * Sets the ViewModel associated with this DrawingView.
     */
    fun setViewModel(viewModel: DrawingViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        viewModel.brush.observe(lifecycleOwner, Observer { brush ->
            // Update paint properties when brush changes
            drawPaint.color = brush.color
            drawPaint.strokeWidth = brush.size
            previewPaint.strokeWidth = brush.size
        })
    }

    /**
     * This method takes in a drawing that will be displayed by the view.
     * This is used by the recycler view to show old drawings, rather than the current
     * drawing stored in the viewModel.
     *
     * @param drawing The drawing to be displayed in the recycler view.
     */
    fun specifyDrawing(drawing: Drawing) {
        specifiedDrawing = drawing
        useSpecifiedDrawing = true
    }


    /**
     * This method sets the variable isDrawing to true or false.
     * It's used by the recyclerview so that users can't draw on the display views.
     * @param isDrawing True if the view can be drawn on, false otherwise.
     */
    fun setIsDrawing(isDrawing: Boolean) {
        this.isDrawing = isDrawing
    }
}