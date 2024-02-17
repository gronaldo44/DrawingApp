package com.example.drawingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawingapp.databinding.FragmentMainScreenBinding
import androidx.fragment.app.activityViewModels

/**
 * The Main Screen Fragment that shows the recycler view and allows to create a new drawing
 */
class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding

    /**
     * Displays the fragment, which includes a recycler view and a create new drawing button
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        val viewModel : DrawingViewModel by activityViewModels<DrawingViewModel>()

        binding.addDrawingButton.setOnClickListener {// GOTO drawing screen
            viewModel.resetModel()
            viewModel.isNewDrawing(true)
            findNavController().navigate(R.id.AddDrawingClicked)
        }

        with(binding.drawingRecyclerView) {
            setItemViewCacheSize(0)
            layoutManager = LinearLayoutManager(this@MainScreenFragment.context)
            adapter = DrawingAdapter(listOf(), viewModel, viewLifecycleOwner) {
                viewModel.resetModel()
                viewModel.setDrawing(it)
                viewModel.isNewDrawing(false)
                findNavController().navigate(R.id.selectDrawing)

            }
        }

        viewModel.drawingList.observe(this@MainScreenFragment.viewLifecycleOwner) {
            (binding.drawingRecyclerView.adapter as DrawingAdapter).updateList(it)
        }


        return binding.root
    }
}