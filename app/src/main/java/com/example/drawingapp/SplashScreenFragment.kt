package com.example.drawingapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentSplashScreenBinding

/**
 * Splash Screen Fragment class. Holds a splash screen with a simple button that moves to the
 * main screen fragment
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding
    private var clickCallback : () -> Unit = {}

    /**
     * Displays the fragment and adds a listener to the button to continue to the home screen
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater,  container, false)
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.closedSplashScreen)
        }
        return binding.root
    }
}