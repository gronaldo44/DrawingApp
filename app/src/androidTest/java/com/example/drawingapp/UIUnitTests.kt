package com.example.drawingapp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.launchFragmentInContainer
import com.example.drawingapp.view.*
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.base.CharMatcher.`is`
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

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

    @Test
    fun mainScreen_DisplayedInUi() {
        launchFragmentInContainer<MainScreenFragment>()
        onView(withId(R.id.mainScreenFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToDrawingScreen_OnButtonPress() {
        launchFragmentInContainer<MainScreenFragment>()
        onView(withText("Add Drawing")).perform(click())
        onView(withId(R.id.drawingScreenFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingButtons() {
        launchFragmentInContainer<DrawingScreenFragment>()
        onView(withText("Shapes")).perform(click())
        onView(withText("shapesLayoutShowing")).check(matches(isDisplayed()))

        onView(withText("Color")).perform(click())
        onView(withText("colorLayoutShowing")).check(matches(isDisplayed()))

        onView(withText("Size")).perform(click())
        onView(withText("sizeLayoutShowing")).check(matches(isDisplayed()))

        onView(withText("Save")).perform(click())
        onView(withId(R.id.mainScreenFragment)).check(matches(isDisplayed()))
    }

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        // Set up necessary mock behavior for the fragment
    }

    @Test
    fun signInButton_click_signsInUser() {
        val scenario = launchFragmentInContainer<FirebaseSignInFragment>()
        scenario.onFragment { fragment ->
            onView(withText("Log In")).perform(click())
            verify(mockAuth).signInWithEmailAndPassword(anyString(), anyString())
        }
    }

    @Test
    fun signInButton_click_createInUser() {
        val scenario = launchFragmentInContainer<FirebaseSignInFragment>()
        scenario.onFragment { fragment ->
            onView(withText("Sign Up")).perform(click())
            verify(mockAuth).createUserWithEmailAndPassword(anyString(), anyString())
        }
    }

    @Test
    fun signInButton_click_local() {
        val scenario = launchFragmentInContainer<FirebaseSignInFragment>()
        scenario.onFragment { fragment ->
            onView(withText("Continue With Local")).perform(click())
            onView(withId(R.id.mainScreenFragment)).check(matches(isDisplayed()))
        }
    }
}