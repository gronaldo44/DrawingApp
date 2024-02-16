package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * Custom View for drawing functionality.
 *
 * This view allows users to draw on a canvas using touch gestures.
 * It supports setting brush color and size based on the provided ViewModel.
 */
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var drawPaint: Paint
    private lateinit var viewModel: DrawingViewModel

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
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawing = viewModel.drawing.value

        drawing?.paths?.forEach { pathData ->
            drawPaint.color = pathData.color
            drawPaint.strokeWidth = pathData.size
            canvas.drawPath(pathData.path, drawPaint)
        }
    }


    /**
     * Handles touch events for drawing.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {    // user touches the screen
                val newPath = Path()
                newPath.moveTo(event.x, event.y)
                viewModel.addPath(newPath, viewModel.brush.value?.color ?: Color.BLACK, viewModel.brush.value?.size ?: 5f)
            }
            MotionEvent.ACTION_MOVE -> {    // user moves their finger
                val drawing = viewModel.drawing.value
                drawing?.paths?.lastOrNull()?.path?.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {      // user lifts their finger
                // Handle ACTION_UP if needed
            }
        }
        invalidate()
        return true
    }

    /**
     * Draws a predefined shape on the canvas.
     *
     * TODO: implement shapes. The example below is a star
     */
    private fun drawShape(shape: Brush.Shape, path: Path, size: Float) {
        val color = viewModel.brush.value?.color ?: Color.BLACK // Default to black if color is not available
        val brushSize = viewModel.brush.value?.size ?: 5f // Default size if size is not available

        val newPath = Path(path) // Create a copy of the provided path
        val bounds = RectF()
        newPath.computeBounds(bounds, true)

        val cx = bounds.centerX()
        val cy = bounds.centerY()
        val radius = size / 2
        val angleStep = Math.toRadians(360.0 / 5)
        val outerRadius = size / 2
        val innerRadius = outerRadius / 2.5f

        val startPoint = PointF(
            cx + radius * Math.cos(-Math.PI / 2).toFloat(),
            cy + radius * Math.sin(-Math.PI / 2).toFloat()
        )
        newPath.moveTo(startPoint.x, startPoint.y)

        for (i in 1 until 5) {
            val angle = angleStep * i
            val outerPoint = PointF(
                cx + outerRadius * Math.cos(angle - Math.PI / 2).toFloat(),
                cy + outerRadius * Math.sin(angle - Math.PI / 2).toFloat()
            )
            newPath.lineTo(outerPoint.x, outerPoint.y)

            val innerPoint = PointF(
                cx + innerRadius * Math.cos(angle - Math.PI / 2 + angleStep / 2).toFloat(),
                cy + innerRadius * Math.sin(angle - Math.PI / 2 + angleStep / 2).toFloat()
            )
            newPath.lineTo(innerPoint.x, innerPoint.y)
        }

        newPath.close()
        viewModel.addPath(newPath, color, brushSize)
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
        })
    }
}