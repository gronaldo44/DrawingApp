package com.example.drawingapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawingapp.databinding.FragmentMainScreenBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.R
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModelFactory
import kotlinx.coroutines.launch

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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)

        // Initialize your ViewModelFactory
        val application = requireActivity().application as DrawingApplication
        viewModelFactory = DrawingViewModelFactory(application.repo)
        // Initialize your ViewModel
        viewModel = ViewModelProvider(this, viewModelFactory)[DrawingViewModel::class.java]


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

        // Observe the drawingList Flow and collect data
        viewModel.drawingList.observe(this@MainScreenFragment.viewLifecycleOwner) {
            (binding.drawingRecyclerView.adapter as DrawingAdapter).updateList(it)
        }


        return binding.root
    }
}