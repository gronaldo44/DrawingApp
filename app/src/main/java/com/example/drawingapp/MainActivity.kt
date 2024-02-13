package com.example.drawingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sets up and adds the splash screen to the fragment
        val splashScreenFragment = SplashScreenFragment()
        splashScreenFragment.setListener(){ // When the splash is exited add recycler view to main activity container
            val recFragView = MainScreenFragment()
            recFragView.setListener(){ // TODO
                Log.e("Clicked", "Clicked")
            }

            // Add recycler screen to container
            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.replace(R.id.mainActivityFragmentContainer, recFragView)
            fTrans.commit()
        }

        // Add splash screen to container
        val fTrans = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.mainActivityFragmentContainer, splashScreenFragment)
        fTrans.commit()
    }
}