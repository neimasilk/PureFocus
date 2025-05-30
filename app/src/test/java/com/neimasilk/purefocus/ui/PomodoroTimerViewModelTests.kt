package com.neimasilk.purefocus.ui

import app.cash.turbine.test
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class PomodoroTimerViewModelTests {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PomodoroTimerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PomodoroTimerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val initialState = viewModel.uiState.value
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, initialState.timeLeftInMillis)
        assertEquals(SessionType.WORK, initialState.currentSessionType)
        assertFalse(initialState.isTimerRunning)
        assertEquals(0, initialState.pomodorosCompletedInCycle)
    }

    @Test
    fun `startTimer updates state and time correctly`() = runTest {
        viewModel.uiState.test {
            assertFalse(awaitItem().isTimerRunning, "Initial state should be not running")

            viewModel.startTimer()
            assertTrue(awaitItem().isTimerRunning, "Timer should be running after start")

            // Advance time by 1 second
            testDispatcher.scheduler.advanceTimeBy(1000L)
            testDispatcher.scheduler.runCurrent() // Execute tasks scheduled for the current time
            assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS - 1000L, awaitItem().timeLeftInMillis)

            // Advance time by another 2 seconds
            testDispatcher.scheduler.advanceTimeBy(2000L)
            testDispatcher.scheduler.runCurrent()
            assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS - 3000L, awaitItem().timeLeftInMillis)

            // Cancel turbine to avoid further emissions if any
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `pauseTimer stops time and updates state`() = runTest {
        viewModel.startTimer()
        testDispatcher.scheduler.advanceTimeBy(5000L); testDispatcher.scheduler.runCurrent()
        val timeWhenPaused = viewModel.uiState.value.timeLeftInMillis
        assertTrue(viewModel.uiState.value.isTimerRunning)

        viewModel.pauseTimer()
        assertFalse(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused, viewModel.uiState.value.timeLeftInMillis)

        // Advance time further to ensure timer is actually paused
        testDispatcher.scheduler.advanceTimeBy(3000L); testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused, viewModel.uiState.value.timeLeftInMillis)
    }

    @Test
    fun `resumeTimer (start after pause) continues countdown`() = runTest {
        viewModel.startTimer()
        testDispatcher.scheduler.advanceTimeBy(3000L); testDispatcher.scheduler.runCurrent()
        viewModel.pauseTimer()
        val timeWhenPaused = viewModel.uiState.value.timeLeftInMillis
        assertFalse(viewModel.uiState.value.isTimerRunning)

        viewModel.startTimer() // Resume
        assertTrue(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused, viewModel.uiState.value.timeLeftInMillis, "Time should be same immediately after resume")

        testDispatcher.scheduler.advanceTimeBy(2000L); testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused - 2000L, viewModel.uiState.value.timeLeftInMillis)
    }

    @Test
    fun `resetTimer resets to initial WORK state when running`() = runTest {
        viewModel.startTimer()
        testDispatcher.scheduler.advanceTimeBy(5000L); testDispatcher.scheduler.runCurrent()
        // Assuming pomodorosCompletedInCycle is 0 initially
        viewModel.resetTimer()

        val state = viewModel.uiState.value
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertFalse(state.isTimerRunning)
        assertEquals(0, state.pomodorosCompletedInCycle) // As per spec, resetTimer doesn't reset cycle count
    }
    
    @Test
    fun `resetTimer resets to initial WORK state when paused`() = runTest {
        viewModel.startTimer()
        testDispatcher.scheduler.advanceTimeBy(5000L); testDispatcher.scheduler.runCurrent()
        viewModel.pauseTimer()
        // Assuming pomodorosCompletedInCycle is 0 initially
        viewModel.resetTimer()

        val state = viewModel.uiState.value
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertFalse(state.isTimerRunning)
        assertEquals(0, state.pomodorosCompletedInCycle) // As per spec, resetTimer doesn't reset cycle count
    }

    @Test
    fun `work session finishes transitions to short break`() = runTest {
        viewModel.uiState.test {
            assertEquals(SessionType.WORK, awaitItem().currentSessionType)
            viewModel.startTimer()
            awaitItem() // isTimerRunning = true, WORK session

            testDispatcher.scheduler.advanceTimeBy(PomodoroTimerViewModel.WORK_DURATION_MILLIS)
            testDispatcher.scheduler.runCurrent()
            
            val finishedState = awaitItem() // This should be the state after time is up but before handleSessionFinish fully updates
                                      // It might be timeLeftInMillis = 0, isTimerRunning = true
            // Depending on exact emission order, we might get an intermediate state or the final one.
            // Let's check the state after handleSessionFinish must have run.
            // handleSessionFinish calls pauseTimer, which sets isTimerRunning=false and emits.
            // Then it updates session type, time, etc. and emits again.

            var stateAfterFinish = finishedState
            if (stateAfterFinish.isTimerRunning || stateAfterFinish.timeLeftInMillis > 0) {
                 stateAfterFinish = awaitItem() // capture isTimerRunning = false if that's a separate emission
            }
             if (stateAfterFinish.isTimerRunning || stateAfterFinish.currentSessionType == SessionType.WORK) {
                 stateAfterFinish = awaitItem() // capture the final state
            }


            assertEquals(SessionType.SHORT_BREAK, stateAfterFinish.currentSessionType)
            assertEquals(PomodoroTimerViewModel.SHORT_BREAK_DURATION_MILLIS, stateAfterFinish.timeLeftInMillis)
            assertFalse(stateAfterFinish.isTimerRunning)
            assertEquals(1, stateAfterFinish.pomodorosCompletedInCycle)
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `skipSession from WORK transitions to SHORT_BREAK`() = runTest {
        assertEquals(0, viewModel.uiState.value.pomodorosCompletedInCycle)
        viewModel.skipSession()
        val state = viewModel.uiState.value
        assertEquals(SessionType.SHORT_BREAK, state.currentSessionType)
        assertEquals(PomodoroTimerViewModel.SHORT_BREAK_DURATION_MILLIS, state.timeLeftInMillis)
        assertFalse(state.isTimerRunning)
        assertEquals(1, state.pomodorosCompletedInCycle, "Pomodoros should increment when skipping WORK")
    }

    @Test
    fun `skipSession from SHORT_BREAK transitions to WORK`() = runTest {
        // Get to SHORT_BREAK first by skipping a WORK session
        viewModel.skipSession() // Now in SHORT_BREAK, pomodorosCompletedInCycle = 1
        assertEquals(SessionType.SHORT_BREAK, viewModel.uiState.value.currentSessionType)
        assertEquals(1, viewModel.uiState.value.pomodorosCompletedInCycle)

        viewModel.skipSession() // Skip SHORT_BREAK
        val state = viewModel.uiState.value
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
        assertFalse(state.isTimerRunning)
        assertEquals(1, state.pomodorosCompletedInCycle, "Pomodoros should NOT increment when skipping SHORT_BREAK")
    }
}
