package com.example.drawingapp

import android.graphics.Color
import android.graphics.Path
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the DrawingViewModel class.
 */
@RunWith(AndroidJUnit4::class)
class DrawingViewModelTest {

    // This rule is used to make LiveData work synchronously on the main thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testSetBrushColor() {
        val viewModel = DrawingViewModel()
        val color = Color.RED

        viewModel.setBrushColor(color)
        val brush = viewModel.brush.value

        assertEquals(color, brush?.color)
    }

    @Test
    fun testSetBrushSize() {
        val viewModel = DrawingViewModel()
        val size = 10f

        viewModel.setBrushSize(size)
        val brush = viewModel.brush.value

        assertEquals(size, brush?.size)
    }

    @Test
    fun testSelectShape() {
        val viewModel = DrawingViewModel()
        val shape = Brush.Shape.CIRCLE

        viewModel.selectShape(shape)
        val brush = viewModel.brush.value

        assertEquals(shape, brush?.selectedShape)
    }

    @Test
    fun testAddPath() {
        val viewModel = DrawingViewModel()
        val path = Path()
        val color = Color.BLACK
        val size = 5f

        viewModel.addPath(path, color, size)
        val drawing = viewModel.drawing

        assertEquals(1, drawing.value?.paths?.size)
        assertEquals(path, drawing.value?.paths?.get(0)?.path)
        assertEquals(color, drawing.value?.paths?.get(0)?.color)
        assertEquals(size, drawing.value?.paths?.get(0)?.size)
    }

    @Test
    fun testClearDrawing() {
        val viewModel = DrawingViewModel()
        val path = Path()

        viewModel.addPath(path, Color.RED, 5f)
        viewModel.clearDrawing()
        val drawing = viewModel.drawing

        assertEquals(0,  drawing.value?.paths?.size)
    }
}