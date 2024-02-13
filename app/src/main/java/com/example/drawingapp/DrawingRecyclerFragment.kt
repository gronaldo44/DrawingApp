package com.example.drawingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawingapp.databinding.FragmentDrawingRecyclerBinding

class DrawingRecyclerFragment : Fragment() {
    private lateinit var binding: FragmentDrawingRecyclerBinding
    private var clickCallback : () -> Unit = {}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawing_recycler, container, false)
    }

    public fun setListener(listener: () -> Unit){
        clickCallback = listener
    }
}