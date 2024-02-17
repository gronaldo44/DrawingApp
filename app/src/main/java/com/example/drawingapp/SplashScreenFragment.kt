package com.example.drawingapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentSplashScreenBinding

/**
 * Splash Screen Fragment class. Holds a splash screen with a simple button that moves to the
 * main screen fragment
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding

    /**
     * Displays the fragment and adds a listener to the button to continue to the home screen
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the appropriate layout based on screen orientation
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            inflater.inflate(R.layout.fragment_splash_screen_land, container, false)
        } else {
            inflater.inflate(R.layout.fragment_splash_screen, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views and set click listeners as necessary
        binding = FragmentSplashScreenBinding.bind(view)
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.closedSplashScreen)
        }
        return binding.root
    }
}