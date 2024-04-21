package com.example.drawingapp.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.R
import com.example.drawingapp.databinding.FragmentSplashScreenBinding
import com.example.drawingapp.viewmodel.DrawingApplication
import com.example.drawingapp.viewmodel.DrawingViewModel
import com.example.drawingapp.viewmodel.DrawingViewModelFactory

/**
 * Fragment responsible for displaying the Firebase sign-in screen.
 * Users are presented with options to sign in using various authentication methods.
 * Upon successful sign-in, users are redirected to the home screen.
 */
class FirebaseSignInFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding

    /**
     * Displays the fragment and adds a listener to the button to continue to the home screen
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Creating View", "SplashScreenFragment")

        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(layoutInflater,  container, false)

        // Initialize your ViewModelFactory
        val activity = requireActivity() // Get the hosting activity
        val application = activity.application as DrawingApplication
        val viewModelFactory = DrawingViewModelFactory(application.repo, application.authRepo)
        // Use the activity as the ViewModelStoreOwner to share ViewModel across fragments in the same activity
        val viewModel = ViewModelProvider(activity, viewModelFactory)[DrawingViewModel::class.java]

        // Set up the composables based on the device's orientation
        binding.composeSplashScreen.setContent {
            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    ComposableFirebasePort(viewModel){
                        findNavController().navigate(R.id.toMainScreen)
                    }
                }
                else -> {
                    ComposableFirebasePort(viewModel){
                        findNavController().navigate(R.id.toMainScreen)
                    }
                }
            }
        }
        return binding.root
    }
}

// Portrait and Landscape Composable using shared elements

/**
 * Composable function for Firebase sign-in/create view.
 *
 * @param viewModel [DrawingViewModel] ViewModel for Firebase authentication.
 * @param onClick Callback function invoked upon successful login or sign-up.
 */
@Composable
fun ComposableFirebasePort(viewModel: DrawingViewModel, onClick: ()->Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.observeAsState()

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Firebase Sign-In/Create",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Row {
            Button(onClick = {
                viewModel.login(email, password)
            }) {
                Text("Log In")
            }

            Button(onClick = {
                viewModel.createUser(email, password)
            }) {
                Text("Sign Up")
            }

            LaunchedEffect(loginState) {
                if (loginState == true) {
                    viewModel.username.value = email
                    onClick()
                }
            }
        }

        Button(onClick = {
            onClick()
        }) {
            Text(text = "Continue With Local")
        }
    }
}
