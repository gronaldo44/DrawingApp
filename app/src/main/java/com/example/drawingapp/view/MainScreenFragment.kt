package com.example.drawingapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentMainScreenBinding
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.R
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModelFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.model.Drawing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The Main Screen Fragment that shows the recycler view and allows to create a new drawing
 */
class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var viewModel: DrawingViewModel
    private lateinit var viewModelFactory: DrawingViewModelFactory

    /**
     * Displays the fragment, which includes a recycler view and a create new drawing button
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Creating View", "MainScreenFragment")

        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        // Initialize your ViewModelFactory
        val activity = requireActivity() // Get the hosting activity
        val application = activity.application as DrawingApplication
        viewModelFactory = DrawingViewModelFactory(application.repo)
        // Use the activity as the ViewModelStoreOwner to share ViewModel across fragments in the same activity
        viewModel = ViewModelProvider(activity, viewModelFactory)[DrawingViewModel::class.java]

        binding.addDrawingButton.setOnClickListener {// GOTO drawing screen
            viewModel.resetModel()
            viewModel.isNewDrawing(true)
            findNavController().navigate(R.id.AddDrawingClicked)
        }

        var drawingsList: ArrayList<Drawing> = ArrayList()
        // Call getAllDrawings using lifecycleScope to get the list of drawings
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Loading Drawings...", Toast.LENGTH_SHORT).show()
            }
            drawingsList = viewModel.getAllDrawings(requireContext())
            Log.d("Drawings", drawingsList.count().toString())

            binding.composeView!!.setContent {
                ScrollableDrawingColumn(
                    data = drawingsList, viewModel = viewModel, viewLifecycleOwner = viewLifecycleOwner) {
                    findNavController().navigate(R.id.selectDrawing)
                }
            }
        }

        return binding.root
    }
}

/**
 * A composable function to display a scrollable column of drawings.
 * Each drawing is represented as a ListItem, allowing interaction with the drawing view.
 *
 * @param data The list of drawings to display.
 * @param viewLifecycleOwner The lifecycle owner for observing LiveData in the ViewModel.
 * @param viewModel The ViewModel instance associated with the drawing screen.
 * @param navigation The callback function to navigate to another destination.
 */
@Composable
fun ScrollableDrawingColumn(data: ArrayList<Drawing>, viewLifecycleOwner: LifecycleOwner, viewModel: DrawingViewModel, navigation: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3D3D3))
    )
    {
        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(data) { item ->
                ListItem(viewModel = viewModel, viewLifecycleOwner = viewLifecycleOwner, drawing = item) {
                    viewModel.resetModel()
                    viewModel.setDrawing(item)
                    viewModel.isNewDrawing(false)
                    navigation()
                }
            }
        }
    }
}

/**
 * A composable function to display a single drawing item in the list.
 * Clicking on the item triggers the specified onClick callback.
 *
 * @param viewModel The ViewModel instance associated with the drawing screen.
 * @param viewLifecycleOwner The lifecycle owner for observing LiveData in the ViewModel.
 * @param drawing The Drawing object to display in the item.
 * @param onClick The callback function to execute when the item is clicked.
 */
@Composable
fun ListItem(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner, drawing: Drawing, onClick: () -> Unit) {
    val drawingVisible = remember { mutableStateOf(true) }
    if (drawingVisible.value) {
        Box(
            modifier = Modifier
                .size(330.dp)
                .background(Color.White)
                .clipToBounds()
                .clickable { onClick() }
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    DrawingView(context, null).apply {
                        setViewModel(viewModel, viewLifecycleOwner)
                        specifyDrawing(drawing)
                        setIsDrawing(false)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp).background(Color.Black))
    }
}