package com.example.drawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.math.hypot

/**
 * Custom View for drawing functionality.
 *
 * This view allows users to draw on a canvas using touch gestures.
 * It supports setting brush color and size based on the provided ViewModel.
 */
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var drawPaint: Paint
    private lateinit var viewModel: DrawingViewModel
    private var startX = 0f
    private var startY = 0f
    var tempEndX: Float = 0f
    var tempEndY: Float = 0f
    var isDrawing: Boolean = false
    val previewPaint = Paint().apply {
        color = Color.LTGRAY // Example color for the preview
        style = Paint.Style.STROKE// Draw only the outline
        strokeWidth = 3f // Example stroke width
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
        val drawing = viewModel.drawing.value

        drawing?.paths?.forEach { pathData ->
            drawPaint.color = pathData.color
            drawPaint.strokeWidth = pathData.size
            canvas.drawPath(pathData.path, drawPaint)
        }

        if (((viewModel.brush.value?.selectedShape ?: Brush.Shape.PATH) == Brush.Shape.RECTANGLE) && isDrawing) {
            canvas.drawRect(startX, startY, tempEndX, tempEndY, previewPaint)
        }
        if (isDrawing && viewModel.brush.value?.selectedShape == Brush.Shape.TRIANGLE) {
            val topX = (startX + tempEndX) / 2
            val previewPath = Path().apply {
                moveTo(topX, startY)
                lineTo(startX, tempEndY)
                lineTo(tempEndX, tempEndY)
                close()
            }
            canvas.drawPath(previewPath, previewPaint)
        }
        if (isDrawing && viewModel.brush.value?.selectedShape == Brush.Shape.CIRCLE) {
            val radius = hypot((tempEndX - startX).toDouble(), (tempEndY - startY).toDouble()).toFloat()
            canvas.drawCircle(startX, startY, radius, previewPaint)
        }
    }


    /**
     * Handles touch events for drawing.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val curShape = viewModel.brush.value?.selectedShape ?: Brush.Shape.PATH
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                if (curShape == Brush.Shape.PATH){
                    val newPath = Path()
                    newPath.moveTo(event.x, event.y)
                    viewModel.addPath(newPath, viewModel.brush.value?.color ?: Color.BLACK, viewModel.brush.value?.size ?: 5f)
                }
                if (curShape != Brush.Shape.PATH) {
                    isDrawing = true
                    tempEndX = event.x
                    tempEndY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (curShape == Brush.Shape.PATH){
                    val drawing = viewModel.drawing.value
                    drawing?.paths?.lastOrNull()?.path?.lineTo(event.x, event.y)
                }
                if (curShape != Brush.Shape.PATH && isDrawing) {
                    tempEndX = event.x
                    tempEndY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                var newPath = Path()
                if (curShape == Brush.Shape.RECTANGLE && isDrawing) {
                    newPath = Path().apply {
                        moveTo(startX, startY)
                        lineTo(tempEndX, startY)
                        lineTo(tempEndX, tempEndY)
                        lineTo(startX, tempEndY)
                        close()
                    }
                    viewModel.addPath(newPath, viewModel.brush.value?.color ?: Color.BLACK, viewModel.brush.value?.size ?: 5f)
                    isDrawing = false
                    invalidate()
                }
                if (curShape == Brush.Shape.TRIANGLE && isDrawing) {
                    val topX = (startX + tempEndX) / 2
                    newPath = Path().apply {
                        moveTo(topX, startY)
                        lineTo(startX, tempEndY)
                        lineTo(tempEndX, tempEndY)
                        close()
                    }
                    viewModel.addPath(newPath, viewModel.brush.value?.color ?: Color.BLACK, viewModel.brush.value?.size ?: 5f)
                    isDrawing = false
                    invalidate() // Clear preview
                }
                if (curShape == Brush.Shape.CIRCLE && isDrawing) {
                    val radius = Math.hypot((tempEndX - startX).toDouble(), (tempEndY - startY).toDouble()).toFloat()
                    newPath = Path().apply {
                        addCircle(startX, startY, radius, Path.Direction.CW)
                    }

                }
                viewModel.addPath(newPath, viewModel.brush.value?.color ?: Color.BLACK, viewModel.brush.value?.size ?: 5f)
                isDrawing = false
            }
        }
        invalidate() // Clear preview
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
}