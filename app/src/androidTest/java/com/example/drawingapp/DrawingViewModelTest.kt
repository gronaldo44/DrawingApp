package com.example.drawingapp

import android.content.Context
import android.graphics.Color
import android.graphics.Path
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapp.model.Brush
import com.example.drawingapp.model.DbDrawing
import com.example.drawingapp.model.database.DrawingDao
import com.example.drawingapp.model.database.DrawingDatabase
import com.example.drawingapp.model.database.DrawingRepository
import com.example.drawingapp.viewmodel.DrawingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the DrawingViewModel class.
 */
@RunWith(AndroidJUnit4::class)
class DrawingViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestCoroutineScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val drawingRepository = DrawingRepository(testScope, dao)

    private lateinit var db: DrawingDatabase
    private lateinit var dao: DrawingDao

    // This rule is used to make LiveData work synchronously on the main thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java).build()
        dao = db.drawingDao()
    }

    @After
    fun closeDB(){
        db.close()
    }

    @Test
    fun testStartEmpty() = runBlocking{
        val allDrawings = dao.getAllDrawings()
        assertEquals(allDrawings.count(), 0)
    }

    @Test
    fun testDBAdd() = runBlocking{
        val testData = DbDrawing("Example.txt")
        dao.insertDrawing(testData)

        val allDrawings = dao.getAllDrawings()
        assertEquals(allDrawings.count(), 1)
        assertEquals(allDrawings.first().first(), testData)
    }

    @Test
    fun testIDExists() = runBlocking{
        val testData = DbDrawing("Drawing0")
        dao.insertDrawing(testData)

        var exists = dao.exists(0)
        assertEquals(exists, true)

        exists = dao.exists(1)
        assertEquals(exists, false)
    }

    @Test
    fun testGetLastId() = runBlocking{
        var lastID = dao.getLastId()
        assertEquals(lastID, null)

        val testData = DbDrawing("Drawing0")
        dao.insertDrawing(testData)

        lastID = dao.getLastId()
        assertEquals(lastID, 0)
    }

    @Test
    fun testDrawingCount() = runBlocking{
        var count = dao.getDrawingCount()
        assertEquals(count, 0)

        val testData = DbDrawing("Drawing0")
        dao.insertDrawing(testData)

        count = dao.getDrawingCount()
        assertEquals(count, 1)
    }


    @Test
    fun testSetBrushColor() {
            val viewModel = DrawingViewModel(drawingRepository)
            val color = Color.RED

            viewModel.setBrushColor(color)
            val brush = viewModel.brush.value

            assertEquals(color, brush?.color)
    }

    @Test
    fun testSetBrushSize() {
        val viewModel = DrawingViewModel(drawingRepository)
        val size = 10f

        viewModel.setBrushSize(size)
        val brush = viewModel.brush.value

        assertEquals(size, brush?.size)
    }

    @Test
    fun testSelectShape() {
        val viewModel = DrawingViewModel(drawingRepository)
        val shape = Brush.Shape.CIRCLE

        viewModel.selectShape(shape)
        val brush = viewModel.brush.value

        assertEquals(shape, brush?.selectedShape)
    }

    @Test
    fun testAddPath() {
        val viewModel = DrawingViewModel(drawingRepository)
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
        val viewModel = DrawingViewModel(drawingRepository)
        val path = Path()

        viewModel.addPath(path, Color.RED, 5f)
        viewModel.clearDrawing()
        val drawing = viewModel.drawing

        assertEquals(0,  drawing.value?.paths?.size)
    }
}