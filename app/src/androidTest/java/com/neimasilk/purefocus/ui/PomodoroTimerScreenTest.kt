package com.neimasilk.purefocus.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neimasilk.purefocus.MainActivity
import com.neimasilk.purefocus.util.DefaultSettings
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PomodoroTimerScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        // The FocusWriteScreen (which includes the Pomodoro Timer) is shown by default.
        // No navigation click is needed here.
        
        // Wait for the timer screen to load (already loaded by default)
        composeTestRule.waitForIdle()
        
        // Reset timer to ensure consistent test state
        composeTestRule.onNodeWithTag("ResetButton").performClick()
        composeTestRule.waitForIdle()
    }

    private fun formatTime(minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d", minutes, seconds)
    }

    @Test
    fun timer_displaysDefaultFocusTime_onInitialScreenLoad() {
        val defaultFocusTime = formatTime(DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES, 0)
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals(defaultFocusTime)
    }

    @Test
    fun clickingStartButton_startsTimer_andButtonChangesToPause() {
        val initialFocusTime = formatTime(DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES, 0)
        
        // Verify initial state
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals(initialFocusTime)
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        
        // Start the timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // Verify button changes to Pause
        composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
        
        // Wait for timer to run for a bit and verify time changes
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                val currentText = composeTestRule.onNodeWithTag("TimerText")
                    .fetchSemanticsNode()
                    .config[androidx.compose.ui.semantics.SemanticsProperties.Text][0].text
                currentText != initialFocusTime
            } catch (e: Exception) {
                false
            }
        }
        
        // Verify timer is counting down
        composeTestRule.onNodeWithTag("TimerText")
            .assert(hasText(initialFocusTime).not())
    }

    @Test
    fun clickingPauseButton_pausesTimer_andButtonChangesToStart() {
        // 1. Start timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        composeTestRule.onNodeWithTag("PauseButton").assertIsDisplayed()
        
        // 2. Wait for timer to run
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                val currentText = composeTestRule.onNodeWithTag("TimerText")
                    .fetchSemanticsNode()
                    .config[androidx.compose.ui.semantics.SemanticsProperties.Text][0].text
                currentText != formatTime(DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES, 0)
            } catch (e: Exception) {
                false
            }
        }
        
        // 3. Pause the timer
        composeTestRule.onNodeWithTag("PauseButton").performClick()
        
        // 4. Verify button changes to Start
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        
        // 5. Get the paused time
        val pausedTime = composeTestRule.onNodeWithTag("TimerText")
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.Text][0].text
        
        // 6. Wait a bit and verify time doesn't change (timer is paused)
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals(pausedTime)
    }

    @Test
    fun clickingResetButton_resetsTimerToDefaultFocusTime_andShowsStartButton() {
        // 1. Start timer
        composeTestRule.onNodeWithTag("StartButton").performClick()
        
        // 2. Wait for timer to run
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                val currentText = composeTestRule.onNodeWithTag("TimerText")
                    .fetchSemanticsNode()
                    .config[androidx.compose.ui.semantics.SemanticsProperties.Text][0].text
                currentText != formatTime(DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES, 0)
            } catch (e: Exception) {
                false
            }
        }
        
        // 3. Reset the timer
        composeTestRule.onNodeWithTag("ResetButton").performClick()
        
        // 4. Verify timer returns to default focus time
        val defaultFocusTime = formatTime(DefaultSettings.DEFAULT_FOCUS_DURATION_MINUTES, 0)
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals(defaultFocusTime)
        
        // 5. Verify Start button is displayed and timer is not running
        composeTestRule.onNodeWithTag("StartButton").assertIsDisplayed()
        
        // 6. Wait a bit and verify time doesn't change (timer is stopped)
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals(defaultFocusTime)
    }
}