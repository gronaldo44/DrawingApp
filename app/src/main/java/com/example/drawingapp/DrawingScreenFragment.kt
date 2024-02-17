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
        binding.btnColor.setOnClickListener{
            binding.colorPickerView.visibility = View.VISIBLE
            binding.colorPickerView.addOnColorChangedListener { selectedColor ->
                viewModel.setBrushColor(selectedColor)
                binding.colorPickerView.visibility = View.GONE
            }
        }

        binding.btnSize.setOnClickListener{
            binding.SizeLayout.visibility = View.VISIBLE
            binding.seekButton.setOnClickListener{
                viewModel.setBrushSize(binding.seekBar.progress.toFloat())
                binding.SizeLayout.visibility = View.GONE
            }
        }

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
        }

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