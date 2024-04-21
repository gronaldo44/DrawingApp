package com.example.drawingapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentMainScreenBinding
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.R
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModelFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.model.Drawing
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment for displaying cloud drawings and creating new drawings.
 * This fragment includes a recycler view and a button for creating new drawings.
 */
class CloudDrawingsFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var viewModel: DrawingViewModel
    private lateinit var viewModelFactory: DrawingViewModelFactory

    /**
     * Inflates the layout for this fragment and initializes necessary components.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Creating View", "CloudDrawingFragment")
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        // Initialize ViewModel and ViewModelFactory
        val activity = requireActivity() // Get the hosting activity
        val application = activity.application as DrawingApplication
        viewModelFactory = DrawingViewModelFactory(application.repo, application.authRepo)
        viewModel = ViewModelProvider(activity, viewModelFactory)[DrawingViewModel::class.java]

        // Call getAllDrawings using lifecycleScope to get the list of drawings
        viewLifecycleOwner.lifecycleScope.launch {
            binding.composeView.setContent {
                val configuration = LocalConfiguration.current
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        DownloadNavbar (
                            downloadClicked = {
                                createDrawingsList(
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner
                                )
                            },
                            viewModel = viewModel
                        )
                    }
                    else -> {
                        DownloadNavbar (
                            downloadClicked = {
                                createDrawingsList(
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner
                                )
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        return binding.root
    }

    /**
     * Creates a list of drawings.
     * @param viewModel ViewModel instance for managing drawing data
     * @param viewLifecycleOwner LifecycleOwner instance for observing LiveData
     */
    private fun createDrawingsList(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner){
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Loading Drawings...", Toast.LENGTH_SHORT).show()
            }
            if (viewModel.drawingLibrary.value != null && viewModel.drawingLibrary.value != "") {
                val downloadDrawings: Task<ArrayList<Drawing>> = viewModel.loadFromFirebase(
                    viewModel.drawingLibrary.value!!
                )
                downloadDrawings.addOnSuccessListener {drawings ->
                    Log.d("Drawings", drawings.count().toString())

                    binding.composeView.setContent {
                        val configuration = LocalConfiguration.current
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                ScrollableDrawingColumn(
                                    data = drawings,
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner
                                ) {
                                    findNavController().navigate(R.id.onDrawingClicked)
                                }
                                Navbar(
                                    addDrawingClicked = {
                                        viewModel.resetModel()
                                        viewModel.isNewDrawing(true)
                                        findNavController().navigate(R.id.onDrawingClicked)}
                                ) {
                                    findNavController().navigate(R.id.cloudDrawingsFragment)
                                }
                            }
                            else -> {
                                ScrollableDrawingColumn(
                                    data = drawings,
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner
                                ) {
                                    findNavController().navigate(R.id.onDrawingClicked)
                                }
                                Navbar(
                                    addDrawingClicked = {
                                        viewModel.resetModel()
                                        viewModel.isNewDrawing(true)
                                        findNavController().navigate(R.id.onDrawingClicked)}
                                ) {
                                    findNavController().navigate(R.id.cloudDrawingsFragment)
                                }
                            }
                        }
                    }
                }.addOnFailureListener{e ->
                    Log.e("Downloading Drawings", "Failed to load downloaded drawings. ${e.stackTraceToString()}")
                }
            }
        }
    }
}

/**
 * Composable function for displaying the download navbar.
 * @param downloadClicked Callback for handling download button clicks
 * @param viewModel ViewModel instance for managing drawing data
 */
@Composable
fun DownloadNavbar(downloadClicked: () -> Unit, viewModel: DrawingViewModel){
    var email by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Text entry
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.drawingLibrary.value = email
            },
            label = { Text("Email") }
        )
        // Download Drawings Button
        Button(
            onClick = { downloadClicked() },
            modifier = Modifier.testTag("Download Library")){
            Text("Download Library")
        }
    }
}
