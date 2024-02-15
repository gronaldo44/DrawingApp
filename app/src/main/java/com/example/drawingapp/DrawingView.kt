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
 *
 * TODO: Should we tie this to a canvas model rather than drawing here?
 */
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var drawPath: Path
    private lateinit var drawPaint: Paint
    private lateinit var canvasPaint: Paint
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawCanvas: Canvas
    private var viewModel: DrawingViewModel? = null

    init {
        setupDrawing()
    }

    /**
     * Setup the drawing components.
     */
    private fun setupDrawing() {
        drawPath = Path()
        drawPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    /**
     * Called when the size of the view changes.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    /**
     * Draws the canvas and paths on the view.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    /**
     * Handles touch events for drawing.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    /**
     * Sets the ViewModel for updating brush color and size.
     */
    fun setViewModel(viewModel: DrawingViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        this.viewModel?.brushColor?.observe(lifecycleOwner, Observer { color ->
            drawPaint.color = color
        })
        this.viewModel?.brushSize?.observe(lifecycleOwner, Observer { size ->
            drawPaint.strokeWidth = size
        })
    }

    /**
     * Clears the drawing canvas.
     */
    fun clearDrawing() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}