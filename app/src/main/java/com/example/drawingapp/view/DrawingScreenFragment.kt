package com.example.drawingapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
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
    private lateinit var binding: FragmentDrawingScreenBinding

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

        binding.composeDrawingScreen?.setContent {
            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ComposableDrawingLand(Modifier.padding(16.dp), viewModel, viewLifecycleOwner) {
                        viewModel.viewModelScope.launch {
                            viewModel.saveCurrentDrawing(requireContext())
                            findNavController().navigate(R.id.onSaved)
                        }
                    }
                }
                else -> {
                    ComposableDrawingPort(Modifier.padding(16.dp), viewModel, viewLifecycleOwner) {
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

@Composable
fun ComposableDrawingPort(modifier: Modifier = Modifier, viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner,
                          onClick: ()->Unit){
    // Assuming viewModel has properties to control visibility and other interactions
    val drawingVisible = remember { mutableStateOf(true) }
    val colorPickerVisible = remember { mutableStateOf(false) }
    val shapeLayoutVisible = remember { mutableStateOf(false) }
    val sizeLayoutVisible = remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3D3D3))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.1f))
            if(drawingVisible.value) {
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

            if (colorPickerVisible.value) {
                AndroidView(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(320.dp)
                        .wrapContentSize(),
                    factory = { context ->
                        ColorPickerView(context, null).apply {
                            addOnColorChangedListener { selectedColor ->
                                viewModel.setBrushColor(selectedColor)
                                colorPickerVisible.value = !colorPickerVisible.value
                                drawingVisible.value = true
                            }
                        }
                    }
                )
            }

            if (shapeLayoutVisible.value) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { viewModel.selectShape(Brush.Shape.PATH)
                        shapeLayoutVisible.value = !shapeLayoutVisible.value}) {
                        Text("Path")
                    }
                    Button(onClick = { viewModel.selectShape(Brush.Shape.RECTANGLE)
                        shapeLayoutVisible.value = !shapeLayoutVisible.value}) {
                        Text("Rect")
                    }
                    Button(onClick = { viewModel.selectShape(Brush.Shape.CIRCLE)
                        shapeLayoutVisible.value = !shapeLayoutVisible.value}) {
                        Text("Circle")
                    }
                    Button(onClick = { viewModel.selectShape(Brush.Shape.TRIANGLE)
                        shapeLayoutVisible.value = !shapeLayoutVisible.value}) {
                        Text("Triangle")
                    }
                }
            }

            if (sizeLayoutVisible.value) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Button(
                    onClick = {
                        viewModel.setBrushSize(sliderPosition)
                        sizeLayoutVisible.value = !sizeLayoutVisible.value
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Pick Size")
                }
            }

            Spacer(modifier = Modifier.weight(1f))


        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = { colorPickerVisible.value = !colorPickerVisible.value
            sizeLayoutVisible.value = false
            shapeLayoutVisible.value = false
            drawingVisible.value = !drawingVisible.value}) {
            Text("Color")
        }
        Button(onClick = { sizeLayoutVisible.value  = !sizeLayoutVisible.value
            colorPickerVisible.value = false
            shapeLayoutVisible.value = false
            drawingVisible.value = true}) {
            Text("Size")
        }
        Button(onClick = { shapeLayoutVisible.value  = !shapeLayoutVisible.value
            colorPickerVisible.value = false
            sizeLayoutVisible.value = false
            drawingVisible.value = true}) {
            Text("Shapes")
        }
        Button(onClick = onClick) {
            Text("Save")
        }
    }
}

@Composable
fun ComposableDrawingLand(modifier: Modifier = Modifier, viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner,
                          onClick: ()->Unit){
    // States for visibility toggles
    val drawingVisible = remember { mutableStateOf(true) }
    val colorPickerVisible = remember { mutableStateOf(false) }
    val shapeLayoutVisible = remember { mutableStateOf(false) }
    val sizeLayoutVisible = remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

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
            Button(
                onClick = { colorPickerVisible.value = true
                    sizeLayoutVisible.value = false
                    shapeLayoutVisible.value = false
                    drawingVisible.value = false},
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .height(60.dp)
            ) {
                Text("Color")
            }
            Button(
                onClick = { sizeLayoutVisible.value  = true
                    colorPickerVisible.value = false
                    shapeLayoutVisible.value = false
                    drawingVisible.value = false },
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .height(60.dp)
            ) {
                Text("Size")
            }
            Button(
                onClick = { shapeLayoutVisible.value  = true
                    colorPickerVisible.value = false
                    sizeLayoutVisible.value = false
                    drawingVisible.value = false },
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .height(60.dp)
            ) {
                Text("Shapes")
            }
            Button(
                onClick = onClick,
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .height(60.dp)
            ) {
                Text("Save")
            }
        }

        if(drawingVisible.value) {
            Spacer(modifier = Modifier.weight(1f))
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
            Spacer(modifier = Modifier.weight(1f))
        }

        // ColorPickerView - Hidden by default, visibility toggled by a button
        if (colorPickerVisible.value) {
            Spacer(modifier = Modifier.weight(1f))
            AndroidView(
                modifier = Modifier
                    .padding(16.dp)
                    .size(320.dp)
                    .wrapContentSize(),
                factory = { context ->
                    ColorPickerView(context, null).apply {
                        addOnColorChangedListener { selectedColor ->
                            viewModel.setBrushColor(selectedColor)
                            colorPickerVisible.value = !colorPickerVisible.value
                            drawingVisible.value = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // Shape Layout - Hidden by default, visibility toggled by a button
        if (shapeLayoutVisible.value) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(onClick = { viewModel.selectShape(Brush.Shape.PATH)
                    shapeLayoutVisible.value = !shapeLayoutVisible.value
                    drawingVisible.value = true}) { Text("Path") }
                Button(onClick = { viewModel.selectShape(Brush.Shape.RECTANGLE)
                    shapeLayoutVisible.value = !shapeLayoutVisible.value
                    drawingVisible.value = true}) { Text("Rect") }
                Button(onClick = { viewModel.selectShape(Brush.Shape.CIRCLE)
                    shapeLayoutVisible.value = !shapeLayoutVisible.value
                    drawingVisible.value = true}) { Text("Circle") }
                Button(onClick = { viewModel.selectShape(Brush.Shape.TRIANGLE)
                    shapeLayoutVisible.value = !shapeLayoutVisible.value
                    drawingVisible.value = true}) { Text("Triangle") }
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        // Size Layout - Hidden by default, visibility toggled by a button
        if (sizeLayoutVisible.value) {
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Button(
                    onClick = {
                        viewModel.setBrushSize(sliderPosition)
                        sizeLayoutVisible.value = !sizeLayoutVisible.value
                        drawingVisible.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Pick Size")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}