package com.example.drawingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentMainScreenBinding

class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private var clickCallback : () -> Unit = {}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainScreenBinding.inflate(layoutInflater, container, false)
        binding.addDrawingButton.setOnClickListener {
            findNavController().navigate(R.id.AddDrawingClicked)
        }
        return binding.root
    }

    public fun setListener(listener: () -> Unit){
        clickCallback = listener
    }
}