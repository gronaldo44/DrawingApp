package com.example.drawingapp

import android.os.Bundle
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
 */
class DrawingScreenFragment : Fragment() {
    private lateinit var viewModel: DrawingViewModel

    /**
     * Initializes the view model and adds click listeners to the button
     * Sets up a drawing view that the user draws on
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDrawingScreenBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_drawing_screen, container, false
        )

        // Sets the view model and the lifecycle
        viewModel = ViewModelProvider(requireActivity())[DrawingViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Add the drawing screen/view the view model
        val drawingView = binding.drawingView
        drawingView.setViewModel(viewModel, viewLifecycleOwner)
        drawingView.setIsDrawing(true)



        // Implement logic for handling navbar interactions
        // Set the color button listener to show a color wheel and close when a color is pressed
        binding.btnColor.setOnClickListener{
            binding.colorPickerView.visibility = View.VISIBLE
            binding.drawingView.visibility = View.GONE
            binding.colorPickerView.addOnColorChangedListener { selectedColor ->
                viewModel.setBrushColor(selectedColor)
                binding.colorPickerView.visibility = View.GONE
                binding.drawingView.visibility = View.VISIBLE
            }

            // Removes other views
            binding.SizeLayout.visibility = View.GONE
            binding.ShapeLayout.visibility = View.GONE
        }

        // Set the size button listener to show a slider and have the user submit a size to close
        binding.btnSize.setOnClickListener{
            binding.SizeLayout.visibility = View.VISIBLE
            binding.seekButton.setOnClickListener{
                viewModel.setBrushSize(binding.seekBar.progress.toFloat())
                binding.SizeLayout.visibility = View.GONE
            }

            // Removes other views
            binding.drawingView.visibility = View.VISIBLE
            binding.colorPickerView.visibility = View.GONE
            binding.ShapeLayout.visibility = View.GONE
        }

        // Sets the shape button to display the four shapes that the user can pick from
        // When a shape is selected all of the button are removed
        binding.btnShapes.setOnClickListener {
            binding.ShapeLayout.visibility = View.VISIBLE
            binding.pathButton.setOnClickListener{ viewModel.selectShape(Brush.Shape.PATH)
                binding.ShapeLayout.visibility = View.GONE}
            binding.rectButton.setOnClickListener{ viewModel.selectShape(Brush.Shape.RECTANGLE)
                binding.ShapeLayout.visibility = View.GONE}
            binding.circleButton.setOnClickListener{ viewModel.selectShape(Brush.Shape.CIRCLE)
                binding.ShapeLayout.visibility = View.GONE}
            binding.triButton.setOnClickListener{ viewModel.selectShape(Brush.Shape.TRIANGLE)
                binding.ShapeLayout.visibility = View.GONE}

            binding.colorPickerView.visibility = View.GONE
            binding.SizeLayout.visibility = View.GONE
            binding.drawingView.visibility = View.VISIBLE
        }

        // Removes other views
        binding.btnSaveLoad.setOnClickListener {
            viewModel.showSaveLoadDialog()
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
     * Displays the save/load dialog.
     * NOTE: Does not need to be implemented for phase 1
     */
    private fun showSaveLoadDialog() {
        // Placeholder:
        Toast.makeText(requireContext(), "Save/Load Dialog", Toast.LENGTH_SHORT).show()
    }
}