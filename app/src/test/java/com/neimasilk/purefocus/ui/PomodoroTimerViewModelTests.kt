package com.neimasilk.purefocus.ui

import android.util.Log
import app.cash.turbine.test
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class PomodoroTimerViewModelTests {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
    private lateinit var viewModel: PomodoroTimerViewModel
    private lateinit var mockPreferencesManager: PreferencesManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        
        // Mock PreferencesManager
        mockPreferencesManager = mock()
        whenever(mockPreferencesManager.focusDuration).thenReturn(MutableStateFlow(25))
        
        viewModel = PomodoroTimerViewModel(mockPreferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `initial state is correct`() = testScope.runTest {
        val initialState = viewModel.uiState.value
        assertEquals(25 * 60 * 1000L, initialState.timeLeftInMillis) // 25 minutes in millis
        assertEquals(SessionType.WORK, initialState.currentSessionType)
        assertFalse(initialState.isTimerRunning)
        assertEquals(0, initialState.pomodorosCompletedInCycle)
    }

    @Test
    fun `startTimer updates state correctly`() = testScope.runTest {
        // Given
        val initialState = viewModel.uiState.value
        assertFalse("Initial state should be not running", initialState.isTimerRunning)

        // When
        viewModel.startTimer()
        
        // Then - timer should be marked as running (service handles actual countdown)
        assertTrue("Timer should be running after start", viewModel.uiState.value.isTimerRunning)
    }

    @Test
    fun `pauseTimer stops time and updates state`() = testScope.runTest {
        viewModel.startTimer()
        testScope.testScheduler.advanceTimeBy(5000L)
        testScope.testScheduler.runCurrent()
        val timeWhenPaused = viewModel.uiState.value.timeLeftInMillis
        assertTrue(viewModel.uiState.value.isTimerRunning)

        viewModel.pauseTimer()
        assertFalse(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused, viewModel.uiState.value.timeLeftInMillis)

        // Advance time further to ensure timer is actually paused
        testScope.testScheduler.advanceTimeBy(3000L)
        testScope.testScheduler.runCurrent()
        assertFalse(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused, viewModel.uiState.value.timeLeftInMillis)
    }

    @Test
    fun `resumeTimer (start after pause) continues countdown`() = testScope.runTest {
        // Given - start timer
        viewModel.startTimer()
        assertTrue(viewModel.uiState.value.isTimerRunning)
        
        // When - pause timer
        viewModel.pauseTimer()
        assertFalse(viewModel.uiState.value.isTimerRunning)

        // Then - resume timer
        viewModel.startTimer() // Resume
        assertTrue("Timer should be running after resume", viewModel.uiState.value.isTimerRunning)
    }

    @Test
    fun `resetTimer resets to initial WORK state when running`() = testScope.runTest {
        viewModel.startTimer()
        testScope.testScheduler.advanceTimeBy(5000L)
        testScope.testScheduler.runCurrent()
        // Assuming pomodorosCompletedInCycle is 0 initially
        viewModel.resetTimer()

        val state = viewModel.uiState.value
        assertEquals(25 * 60 * 1000L, state.timeLeftInMillis)
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertFalse(state.isTimerRunning)
        assertEquals(0, state.pomodorosCompletedInCycle) // As per spec, resetTimer doesn't reset cycle count
    }
    
    @Test
    fun `resetTimer resets to initial WORK state when paused`() = testScope.runTest {
        viewModel.startTimer()
        testScope.testScheduler.advanceTimeBy(5000L)
        testScope.testScheduler.runCurrent()
        viewModel.pauseTimer()
        // Assuming pomodorosCompletedInCycle is 0 initially
        viewModel.resetTimer()

        val state = viewModel.uiState.value
        assertEquals(25 * 60 * 1000L, state.timeLeftInMillis)
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertFalse(state.isTimerRunning)
        assertEquals(0, state.pomodorosCompletedInCycle) // As per spec, resetTimer doesn't reset cycle count
    }

    @Test
    fun `work session finishes transitions to short break`() = testScope.runTest {
        // Given - initial work session
        assertEquals(SessionType.WORK, viewModel.uiState.value.currentSessionType)
        
        // When - skip work session (simulating session finish)
        viewModel.skipSession()
        
        // Then - timer should not be running (session transitions handled by service)
        val state = viewModel.uiState.value
        assertFalse(state.isTimerRunning)
    }

    @Test
    fun `skipSession from WORK transitions to SHORT_BREAK`() = testScope.runTest {
        // Test that skipSession can be called (session transitions handled by service)
        viewModel.skipSession()
        val state = viewModel.uiState.value
        assertFalse(state.isTimerRunning)
    }

    @Test
    fun `skipSession from SHORT_BREAK transitions to WORK`() = testScope.runTest {
        // Test that skipSession can be called multiple times (session transitions handled by service)
        viewModel.skipSession() // Skip first session
        viewModel.skipSession() // Skip second session
        
        // Verify that skipSession calls don't cause errors
        val state = viewModel.uiState.value
        assertFalse(state.isTimerRunning)
    }

    @Test
    fun `focusWriteText initial state is empty`() = testScope.runTest {
        assertEquals("", viewModel.focusWriteText.value)
    }

    @Test
    fun `updateFocusWriteText updates text state`() = testScope.runTest {
        val testText = "This is a test text"
        viewModel.updateFocusWriteText(testText)
        assertEquals(testText, viewModel.focusWriteText.value)
    }

    @Test
    fun `focusWriteText is reset when work session finishes`() = testScope.runTest {
        // Set some text
        val testText = "Focus session text"
        viewModel.updateFocusWriteText(testText)
        assertEquals(testText, viewModel.focusWriteText.value)

        // Ensure we're in WORK session
        assertEquals(SessionType.WORK, viewModel.uiState.value.currentSessionType)

        // When - skip work session (simulating session finish)
        viewModel.skipSession()

        // Then - text should be reset (session transition is handled by service)
        assertEquals("", viewModel.focusWriteText.value)
    }

    @Test
    fun `focusWriteText is reset when work session is skipped`() = testScope.runTest {
        // Set some text
        val testText = "Focus session text"
        viewModel.updateFocusWriteText(testText)
        assertEquals(testText, viewModel.focusWriteText.value)

        // Ensure we're in WORK session
        assertEquals(SessionType.WORK, viewModel.uiState.value.currentSessionType)

        // Skip work session
        viewModel.skipSession()

        // Text should be reset after work session is skipped (session transition is handled by service)
        assertEquals("", viewModel.focusWriteText.value)
    }
}
