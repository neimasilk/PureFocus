package com.neimasilk.purefocus.service

import android.util.Log
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PomodoroServiceTests {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `initial service state is correct`() = testScope.runTest {
        val initialState = PomodoroService.serviceState.value
        assertEquals(0L, initialState.timeLeftInMillis) // Default state before service initialization
        assertEquals(SessionType.WORK, initialState.currentSessionType)
        assertFalse(initialState.isRunning)
        assertEquals(0, initialState.pomodorosCompletedInCycle)
    }

    @Test
    fun `service state can be observed`() = testScope.runTest {
        // Test that the service state is accessible and observable
        val state = PomodoroService.serviceState.value
        assertTrue("Service state should be accessible", state != null)
        assertEquals(SessionType.WORK, state.currentSessionType)
    }

    @Test
    fun `long break cycle constant is correct`() {
        assertEquals(4, PomodoroService.POMODOROS_PER_CYCLE)
    }

    @Test
    fun `break duration constants are correct`() {
        assertEquals(5 * 60 * 1000L, PomodoroService.SHORT_BREAK_DURATION_MILLIS)
        assertEquals(15 * 60 * 1000L, PomodoroService.LONG_BREAK_DURATION_MILLIS)
    }
}