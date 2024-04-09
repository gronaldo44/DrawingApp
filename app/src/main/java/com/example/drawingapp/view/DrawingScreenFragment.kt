package com.example.drawingapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.databinding.FragmentDrawingScreenBinding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.R
import com.example.drawingapp.model.Brush
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModelFactory
import com.flask.colorpicker.ColorPickerView
import kotlinx.coroutines.launch


/**
 * A Fragment responsible for displaying the drawing screen with a navbar.
 * The navbar allows users to interact with the drawing view and perform actions such as
 * changing brush color and size, selecting shapes, and saving/loading drawings.
 *
 */
class DrawingScreenFragment : Fragment() {
    private lateinit var viewModel: DrawingViewModel
    private lateinit var viewModelFactory: DrawingViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDrawingScreenBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drawing_screen, container, false
        )
        Log.d("Creating View", "DrawingScreenFragment")

        // Initialize your ViewModelFactory
        val activity = requireActivity() // Get the hosting activity
        val application = activity.application as DrawingApplication
        viewModelFactory = DrawingViewModelFactory(application.repo)
        // Use the activity as the ViewModelStoreOwner to share ViewModel across fragments in the same activity
        viewModel = ViewModelProvider(activity, viewModelFactory)[DrawingViewModel::class.java]
        // Sets the lifecycle
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.composeDrawingScreen.setContent {
            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ComposableDrawingLand(viewModel, viewLifecycleOwner) {
                        viewModel.viewModelScope.launch {
                            viewModel.saveCurrentDrawing(requireContext())
                            findNavController().navigate(R.id.onSaved)
                        }
                    }
                }

                else -> {
                    ComposableDrawingPort(viewModel, viewLifecycleOwner) {
                        viewModel.viewModelScope.launch {
                            viewModel.saveCurrentDrawing(requireContext())
                            findNavController().navigate(R.id.onSaved)
                        }
                    }
                }
            }
        }

        return binding.root
    }
}

/**
 * Composable Drawing View object. Shows the drawing at the correct time. Uses
 * box to attain clicking and bound drawing.
 * @param viewModel The ViewModel to communicate with
 * @param viewLifecycleOwner Lifecycle in order to access drawing
 */
@Composable
fun ComposableDrawing(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner){
    if(viewModel.drawingVisible.value) {
        Box(
            modifier = Modifier
                .size(330.dp)
                .background(Color.White)
                .clipToBounds()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    DrawingView(context, null).apply {
                        setViewModel(viewModel, viewLifecycleOwner)
                        setIsDrawing(true)
                    }
                }
            )
        }
    }
}

/**
 * Composable Color Selector object. Shows Color Selector in which the user can select a color
 * to draw with
 * @param viewModel The ViewModel to communicate with
 */
@Composable
fun ComposableColorSelector(viewModel: DrawingViewModel){
    if (viewModel.colorPickerVisible.value) {
        AndroidView(
            modifier = Modifier
                .padding(16.dp)
                .size(320.dp)
                .wrapContentSize()
                .testTag("colorLayoutShowing"),
            factory = { context ->
                ColorPickerView(context, null).apply {
                    addOnColorChangedListener { selectedColor ->
                        viewModel.setBrushColor(selectedColor)
                        viewModel.colorPickerVisible.value = !viewModel.colorPickerVisible.value
                        viewModel.drawingVisible.value = true
                    }
                }
            }
        )
    }
}

/**
 * Composable Shape Selector object. Shows the four selectable shapes.
 * Path, Circle, Rectangle, Triangle. Offers button for each to select
 * @param viewModel The ViewModel to communicate with
 */
@Composable
fun ComposableShapeSelector(viewModel: DrawingViewModel){
    if (viewModel.shapeLayoutVisible.value) {
        Row(
            modifier = Modifier.padding(8.dp)
                .testTag("shapesLayoutShowing"),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.updateBrush(shape=Brush.Shape.PATH)
                viewModel.shapeLayoutVisible.value = !viewModel.shapeLayoutVisible.value
                viewModel.drawingVisible.value = true}) {
                Text("Path")
            }
            Button(onClick = { viewModel.updateBrush(shape=Brush.Shape.RECTANGLE)
                viewModel.shapeLayoutVisible.value = !viewModel.shapeLayoutVisible.value
                viewModel.drawingVisible.value = true}) {
                Text("Rect")
            }
            Button(onClick = { viewModel.updateBrush(shape=Brush.Shape.CIRCLE)
                viewModel.shapeLayoutVisible.value = !viewModel.shapeLayoutVisible.value
                viewModel.drawingVisible.value = true}) {
                Text("Circle")
            }
            Button(onClick = { viewModel.updateBrush(shape=Brush.Shape.TRIANGLE)
                viewModel.shapeLayoutVisible.value = !viewModel.shapeLayoutVisible.value
                viewModel.drawingVisible.value = true}) {
                Text("Triangle")
            }
        }
    }
}

/**
 * Composable Size Selector object.Shows slider and button to select size
 * Sends to view model
 * @param viewModel The ViewModel to communicate with
 */
@Composable
fun ComposableSizeSelector(viewModel: DrawingViewModel){
    if (viewModel.sizeLayoutVisible.value) {
        Column {
            Slider(
                value = viewModel.sliderPosition.value,
                onValueChange = { viewModel.sliderPosition.value = it },
                valueRange = 0f..100f,
                modifier = Modifier.padding(horizontal = 32.dp)
                                    .testTag("sizeLayoutShowing")
            )
            Button(
                onClick = {
                    viewModel.updateBrush(size=viewModel.sliderPosition.value)
                    viewModel.sizeLayoutVisible.value = !viewModel.sizeLayoutVisible.value
                    viewModel.drawingVisible.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Pick Size")
            }
        }
    }
}

/**
 * Composable Setting object. Shows the four setting buttons in the UI.
 * Size, Color, Shapes, and Save. Responsible for showing correct views at correct time
 * @param modifier The Modifier for UI customization
 * @param viewModel The ViewModel to communicate with
 * @param onClick Click listener for the save button
 */
@Composable
fun ComposableSetting(modifier: Modifier, viewModel: DrawingViewModel, onClick: () -> Unit){
    Button(onClick = { viewModel.colorPickerVisible.value = !viewModel.colorPickerVisible.value
        viewModel.sizeLayoutVisible.value = false
        viewModel.shapeLayoutVisible.value = false
        viewModel.drawingVisible.value = !viewModel.drawingVisible.value},
        modifier = modifier.testTag("Color")) {
        Text("Color")
    }
    Button(onClick = { viewModel.sizeLayoutVisible.value  = !viewModel.sizeLayoutVisible.value
        viewModel.colorPickerVisible.value = false
        viewModel.shapeLayoutVisible.value = false
        viewModel.drawingVisible.value = true},
        modifier = modifier.testTag("Size")) {
        Text("Size")
    }
    Button(onClick = { viewModel.shapeLayoutVisible.value  = !viewModel.shapeLayoutVisible.value
        viewModel.colorPickerVisible.value = false
        viewModel.sizeLayoutVisible.value = false
        viewModel.drawingVisible.value = true},
        modifier = modifier.testTag("Shapes")) {
        Text("Shapes")
    }
    Button(onClick = onClick, modifier = modifier.testTag("Save")) {
        Text("Save")
    }
}

/**
 * Composable function responsible for rendering the drawing screen in portrait mode.
 * It provides UI elements for interacting with the drawing, such as color picker, shape selection,
 * brush size picker, and save button.
 */
@Composable
fun ComposableDrawingPort(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner,
                          onClick: ()->Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3D3D3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ComposableDrawing(viewModel, viewLifecycleOwner)
            ComposableColorSelector(viewModel)
            ComposableShapeSelector(viewModel)
            ComposableSizeSelector(viewModel)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ComposableSetting(modifier = Modifier.testTag("settingButton"), viewModel, onClick)
    }
}

/**
 * Composable function responsible for rendering the drawing screen in landscape mode.
 * It provides UI elements for interacting with the drawing, such as color picker, shape selection,
 * brush size picker, and save button.
 */
@Composable
fun ComposableDrawingLand(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner,
                          onClick: ()->Unit){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3D3D3)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Sidebar
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComposableSetting(
                modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .height(60.dp), viewModel, onClick)
        }

        Spacer(modifier = Modifier.weight(1f))
        ComposableDrawing(viewModel, viewLifecycleOwner)
        ComposableColorSelector(viewModel)
        ComposableShapeSelector(viewModel)
        ComposableSizeSelector(viewModel)
        Spacer(modifier = Modifier.weight(1f))
    }
}

