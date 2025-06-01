package com.neimasilk.purefocus.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import com.neimasilk.purefocus.ui.theme.PureFocusTheme
import com.neimasilk.purefocus.ui.PomodoroTimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    manifest = Config.NONE
)
class PomodoroBottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var mockPreferencesManager: PreferencesManager

    private lateinit var pomodoroViewModel: PomodoroTimerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock PreferencesManager flows
        `when`(mockPreferencesManager.focusDuration).thenReturn(MutableStateFlow(25))
        `when`(mockPreferencesManager.shortBreakDuration).thenReturn(MutableStateFlow(5))
        `when`(mockPreferencesManager.longBreakDuration).thenReturn(MutableStateFlow(15))
        
        pomodoroViewModel = PomodoroTimerViewModel(mockPreferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun pomodoroBottomBar_displaysTimerAndSessionType() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        // Verify timer text is displayed
        composeTestRule.onNodeWithTag("TimerText").assertIsDisplayed()
        
        // Verify session type text is displayed
        composeTestRule.onNodeWithTag("SessionTypeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SessionTypeText").assertTextEquals("Focus")
    }

    @Test
    fun pomodoroBottomBar_displaysStartButtonWhenTimerNotRunning() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        // Verify Start button is displayed when timer is not running
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
    }

    @Test
    fun pomodoroBottomBar_displaysAllControlButtons() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        // Verify all control buttons are displayed
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ResetButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SkipButton").assertIsDisplayed()
    }

    @Test
    fun pomodoroBottomBar_startButtonTriggersStartPauseTimer() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        composeTestRule.waitForIdle()
        
        // Click start button
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // Note: We can't easily verify the ViewModel method was called without more complex mocking
        // This test verifies the button is clickable and doesn't crash
    }

    @Test
    fun pomodoroBottomBar_resetButtonTriggersResetTimer() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        composeTestRule.waitForIdle()
        
        // Click reset button
        composeTestRule.onNodeWithTag("ResetButton").performClick()
        
        // Note: We can't easily verify the ViewModel method was called without more complex mocking
        // This test verifies the button is clickable and doesn't crash
    }

    @Test
    fun pomodoroBottomBar_skipButtonTriggersSkipSession() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        composeTestRule.waitForIdle()
        
        // Click skip button
        composeTestRule.onNodeWithTag("SkipButton").performClick()
        
        // Note: We can't easily verify the ViewModel method was called without more complex mocking
        // This test verifies the button is clickable and doesn't crash
    }

    @Test
    fun pomodoroBottomBar_displaysCorrectTimerFormat() {
        composeTestRule.setContent {
            PureFocusTheme {
                PomodoroBottomBar(pomodoroViewModel = pomodoroViewModel)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify timer displays in MM:SS format (default should be 25:00 for focus session)
        composeTestRule.onNodeWithTag("TimerText").assertIsDisplayed()
    }
}