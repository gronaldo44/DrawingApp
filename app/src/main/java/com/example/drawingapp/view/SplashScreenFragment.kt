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
        binding.composeSplashScreen.setContent {
            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ComposableSplashLand{
                        findNavController().navigate(R.id.closedSplashScreen)
                    }
                }
                else -> {
                    ComposableSplashPort{
                        findNavController().navigate(R.id.closedSplashScreen)
                    }
                }
            }
        }
        return binding.root
    }
}

// Shared Composable Functions
/**
 * Displays the drawing logo on the screen
 */
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.paintlogo),
        contentDescription = "App Logo",
        modifier = modifier
    )
}

/**
 * Welcome message displayed on the splash screen
 */
@Composable
fun WelcomeMessage(textSize: Int, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome To Our Drawing App",
        modifier = modifier,
        fontSize = textSize.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Text(
        text = "In this app you can create drawings through a paint type editor. You can add, edit, and remove such drawings. Please continue to the next screen in order to create a drawing!",
        modifier = modifier,
        fontSize = (textSize.sp.value - 12).sp,
        textAlign = TextAlign.Center
    )
}

/**
 * Continue button used to navigate to the main screen
 */
@Composable
fun ContinueToAppButton(onClick: ()->Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = "Continue To App")
    }
}

// Portrait and Landscape Composable using shared elements
@Composable
fun ComposableSplashPort(onClick: ()->Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppLogo(Modifier.fillMaxWidth(0.4f))
        WelcomeMessage(28,
            Modifier
                .fillMaxWidth()
                .padding(10.dp))
        ContinueToAppButton(onClick, Modifier.fillMaxWidth(0.5f))
    }
}

@Composable
fun ComposableSplashLand(onClick: ()->Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = 16.dp),
        ) {
            AppLogo(Modifier.fillMaxWidth())
            ContinueToAppButton(onClick, Modifier.fillMaxWidth())
        }
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(end = 16.dp, top = 50.dp),
        ) {
            WelcomeMessage(36, Modifier.fillMaxWidth())
        }
    }
}