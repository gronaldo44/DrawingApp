package com.example.drawingapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.databinding.FragmentDrawingScreenBinding
import androidx.databinding.DataBindingUtil


/**
 * A Fragment responsible for displaying the drawing screen with a navbar.
 * The navbar allows users to interact with the drawing view and perform actions such as
 * changing brush color and size, selecting shapes, and saving/loading drawings.
 *
 * TODO: MainScreenFragment.kt needs to have functionality to reach here
 */
class DrawingScreenFragment : Fragment() {
    private lateinit var viewModel: DrawingViewModel

    /**
     * Inflates the layout for the fragment and sets up data binding.
     * Initializes ViewModel and binds it to the layout.
     * Sets up click listeners for navbar buttons.
     * Observes changes in LiveData to show dialogs when needed.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDrawingScreenBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drawing_screen, container, false
        )

        viewModel = ViewModelProvider(requireActivity()).get(DrawingViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val drawingView = binding.drawingView
        drawingView.setViewModel(viewModel, viewLifecycleOwner)

        // Implement logic for handling navbar interactions
        binding.colorButton.setOnClickListener{
            binding.colorPickerView.visibility = View.VISIBLE
            binding.colorPickerView.addOnColorChangedListener { selectedColor ->
                viewModel.setBrushColor(selectedColor)
                binding.colorPickerView.visibility = View.GONE
            }
        }

        binding.sizeButton.setOnClickListener{
            binding.seekBar.visibility = View.VISIBLE
            binding.seekButton.visibility = View.VISIBLE
            binding.seekButton.setOnClickListener{
                viewModel.setBrushSize(binding.seekBar.progress.toFloat())
                binding.seekBar.visibility = View.GONE
                binding.seekButton.visibility = View.GONE
            }
        }

        binding.btnShapes.setOnClickListener {
            viewModel.showShapesDialog()
        }

        binding.btnSaveLoad.setOnClickListener {
            viewModel.showSaveLoadDialog()
        }

        viewModel.showShapesDialog.observe(viewLifecycleOwner) { showShapesDialog ->
            if (showShapesDialog) {
                viewModel.setShape(true)
                showShapesDialog()
                viewModel.shapesDialogShown()
            }
        }

        viewModel.showSaveLoadDialog.observe(viewLifecycleOwner) { showSaveLoadDialog ->
            if (showSaveLoadDialog) {
                showSaveLoadDialog()
                viewModel.saveLoadDialogShown()
            }
        }

        return binding.root
    }

    /**
     * Displays the shapes dialog where users can choose a shape to enter into their drawing.
     * TODO: Implement logic to show the shapes dialog.
     */
    private fun showShapesDialog() {
        // Placeholder:
        Toast.makeText(requireContext(), "Shapes Dialog", Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays the save/load dialog.
     * NOTE: Does not need to be implemented for phase 1
     */
    private fun showSaveLoadDialog() {
        // Placeholder:
        Toast.makeText(requireContext(), "Save/Load Dialog", Toast.LENGTH_SHORT).show()
    }
}