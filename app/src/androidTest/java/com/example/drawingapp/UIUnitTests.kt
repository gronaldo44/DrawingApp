package com.example.drawingapp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.drawingapp.view.*
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class UIUnitTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSplashScreenContent_Portrait() {
        // Launch the Composable
        composeTestRule.setContent {
            ComposableSplashPort {
                // Mock onClick callback
            }
        }

        // Verify the presence of specific texts and button
        composeTestRule
            .onNodeWithText("Welcome To Our Drawing App")
            .assertExists()
        composeTestRule
            .onNodeWithText("In this app you can create drawings through a paint type editor. You can add, edit, and remove such drawings. Please continue to the next screen in order to create a drawing!")
            .assertExists()
        composeTestRule
            .onNodeWithText("Continue To App")
            .assertExists()
    }

    @Test
    fun testSplashScreenContent_Landscape() {
        // Launch the Composable
        composeTestRule.setContent {
            ComposableSplashLand {
                // Mock onClick callback
            }
        }

        // Verify the presence of specific texts and button
        composeTestRule
            .onNodeWithText("Welcome To Our Drawing App")
            .assertExists()
        composeTestRule
            .onNodeWithText("In this app you can create drawings through a paint type editor. You can add, edit, and remove such drawings. Please continue to the next screen in order to create a drawing!")
            .assertExists()
        composeTestRule
            .onNodeWithText("Continue To App")
            .assertExists()
    }

    @Test
    fun testButtonOnSplashScreen() {
        var myCounter = 0;
        composeTestRule.setContent {
            ComposableSplashLand {
                myCounter += 1;
            }
        }

        composeTestRule
            .onNodeWithText("Continue To App")
            .assertExists()
            .performClick()

        assert(myCounter == 1)

    }

}