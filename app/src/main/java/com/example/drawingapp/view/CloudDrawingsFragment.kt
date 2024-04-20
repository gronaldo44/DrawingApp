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
 * The Main Screen Fragment that shows the recycler view and allows to create a new drawing
 */
class CloudDrawingsFragment : Fragment() {
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
        Log.d("Creating View", "CloudDrawingFragment")
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        // Initialize your ViewModelFactory
        val activity = requireActivity() // Get the hosting activity
        val application = activity.application as DrawingApplication
        viewModelFactory = DrawingViewModelFactory(application.repo, application.authRepo)
        // Use the activity as the ViewModelStoreOwner to share ViewModel across fragments in the same activity
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
                                    viewLifecycleOwner = viewLifecycleOwner,
                                    drawingClicked = {
                                        findNavController().navigate(R.id.selectDrawing)
                                    }
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
                                    viewLifecycleOwner = viewLifecycleOwner,
                                    drawingClicked = {
                                        findNavController().navigate(R.id.selectDrawing)
                                    }
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

    private fun createDrawingsList(viewModel: DrawingViewModel, viewLifecycleOwner: LifecycleOwner,
                                   drawingClicked: () -> Unit){
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
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner,
                                    addDrawingClicked = {
                                        viewModel.resetModel()
                                        viewModel.isNewDrawing(true)
                                        findNavController().navigate(R.id.onDrawingClicked)},
                                    downloadClicked = {
                                        findNavController().navigate(R.id.cloudDrawingsFragment)
                                    }
                                )
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
                                    viewModel = viewModel,
                                    viewLifecycleOwner = viewLifecycleOwner,
                                    addDrawingClicked = {
                                        viewModel.resetModel()
                                        viewModel.isNewDrawing(true)
                                        findNavController().navigate(R.id.onDrawingClicked)},
                                    downloadClicked = {
                                        findNavController().navigate(R.id.cloudDrawingsFragment)
                                    }
                                )
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
