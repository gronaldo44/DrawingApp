package com.example.drawingapp.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.databinding.FragmentSplashScreenBinding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.drawingapp.R

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
    ): View {
        Log.d("Creating View", "SplashScreenFragment")

        binding = FragmentSplashScreenBinding.inflate(layoutInflater,  container, false)
        binding.composeSplashScreen?.setContent {
            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ComposableSplashLand(Modifier.padding(16.dp)){
                        findNavController().navigate(R.id.closedSplashScreen)
                    }
                }
                else -> {
                    ComposableSplashPort(Modifier.padding(16.dp)){
                        findNavController().navigate(R.id.closedSplashScreen)
                    }
                }
            }
        }
        return binding.root
    }
}

/**
 * Composable function for displaying the Splash screen layout in portrait orientation.
 *
 * @param modifier Modifier for the layout
 * @param onClick Callback function to handle button click event
 */
@Composable
fun ComposableSplashPort(modifier: Modifier = Modifier,
                 onClick: ()->Unit){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.paintlogo),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth(0.4f)
        )

        Text(
            text = "Welcome To Our Drawing App",
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "In this app you can create drawings through a paint type editor. You can add, edit, and remove such drawings. Please continue to the next screen in order to create a drawing!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "Continue To App")
        }
    }
}

/**
 * Composable function for displaying the Splash screen layout in landscape orientation.
 *
 * @param modifier Modifier for the layout
 * @param onClick Callback function to handle button click event
 */
@Composable
fun ComposableSplashLand(modifier: Modifier = Modifier,
                     onClick: ()->Unit){
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.paintlogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Continue To App")
            }
        }

        // Container for welcome message and content message (2/3 of width)
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Welcome To Our Drawing App",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 50.dp),
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "In this app you can create drawings through a paint type editor. You can add, edit, and remove such drawings. Please continue to the next screen in order to create a drawing!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                fontSize = 26.sp
            )
        }
    }
}