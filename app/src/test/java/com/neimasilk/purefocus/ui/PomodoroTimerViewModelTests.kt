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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class PomodoroTimerViewModelTests {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
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
    fun `initial state is correct`() = testScope.runTest {
        val initialState = viewModel.uiState.value
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, initialState.timeLeftInMillis)
        assertEquals(SessionType.WORK, initialState.currentSessionType)
        assertFalse(initialState.isTimerRunning)
        assertEquals(0, initialState.pomodorosCompletedInCycle)
    }

    @Test
    fun `startTimer updates state and time correctly`() = testScope.runTest {
        viewModel.uiState.test {
            assertFalse("Initial state should be not running", awaitItem().isTimerRunning)

            viewModel.startTimer()
            assertTrue("Timer should be running after start", awaitItem().isTimerRunning)

            // Advance time by 1 second
            testScope.testScheduler.advanceTimeBy(1000L)
            testScope.testScheduler.runCurrent() // Execute tasks scheduled for the current time
            val firstUpdate = awaitItem()
            assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS - 1000L, firstUpdate.timeLeftInMillis)

            // Advance time by another 1 second (total 2 seconds)
            testScope.testScheduler.advanceTimeBy(1000L)
            testScope.testScheduler.runCurrent()
            val secondUpdate = awaitItem()
            assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS - 2000L, secondUpdate.timeLeftInMillis)

            // Cancel turbine to avoid further emissions if any
            cancelAndConsumeRemainingEvents()
        }
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
        viewModel.startTimer()
        testScope.testScheduler.advanceTimeBy(3000L)
        testScope.testScheduler.runCurrent()
        viewModel.pauseTimer()
        val timeWhenPaused = viewModel.uiState.value.timeLeftInMillis
        assertFalse(viewModel.uiState.value.isTimerRunning)

        viewModel.startTimer() // Resume
        assertTrue(viewModel.uiState.value.isTimerRunning)
        assertEquals("Time should be same immediately after resume", timeWhenPaused, viewModel.uiState.value.timeLeftInMillis)

        testScope.testScheduler.advanceTimeBy(2000L)
        testScope.testScheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isTimerRunning)
        assertEquals(timeWhenPaused - 2000L, viewModel.uiState.value.timeLeftInMillis)
    }

    @Test
    fun `resetTimer resets to initial WORK state when running`() = testScope.runTest {
        viewModel.startTimer()
        testScope.testScheduler.advanceTimeBy(5000L)
        testScope.testScheduler.runCurrent()
        // Assuming pomodorosCompletedInCycle is 0 initially
        viewModel.resetTimer()

        val state = viewModel.uiState.value
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
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
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertFalse(state.isTimerRunning)
        assertEquals(0, state.pomodorosCompletedInCycle) // As per spec, resetTimer doesn't reset cycle count
    }

    @Test
    fun `work session finishes transitions to short break`() = testScope.runTest {
        viewModel.uiState.test {
            assertEquals(SessionType.WORK, awaitItem().currentSessionType)
            viewModel.startTimer()
            awaitItem() // isTimerRunning = true, WORK session

            // Advance time by full work duration plus a bit more to ensure timer finishes
            testScope.testScheduler.advanceTimeBy(PomodoroTimerViewModel.WORK_DURATION_MILLIS + 1000L)
            testScope.testScheduler.runCurrent()
            
            // Skip intermediate states and get to the final state after session transition
            var finalState = awaitItem()
            while (finalState.currentSessionType == SessionType.WORK || finalState.isTimerRunning) {
                finalState = awaitItem()
            }

            assertEquals(SessionType.SHORT_BREAK, finalState.currentSessionType)
            assertEquals(PomodoroTimerViewModel.SHORT_BREAK_DURATION_MILLIS, finalState.timeLeftInMillis)
            assertFalse(finalState.isTimerRunning)
            assertEquals(1, finalState.pomodorosCompletedInCycle)
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `skipSession from WORK transitions to SHORT_BREAK`() = testScope.runTest {
        assertEquals(0, viewModel.uiState.value.pomodorosCompletedInCycle)
        viewModel.skipSession()
        val state = viewModel.uiState.value
        assertEquals(SessionType.SHORT_BREAK, state.currentSessionType)
        assertEquals(PomodoroTimerViewModel.SHORT_BREAK_DURATION_MILLIS, state.timeLeftInMillis)
        assertFalse(state.isTimerRunning)
        assertEquals("Pomodoros should increment when skipping WORK", 1, state.pomodorosCompletedInCycle)
    }

    @Test
    fun `skipSession from SHORT_BREAK transitions to WORK`() = testScope.runTest {
        // Get to SHORT_BREAK first by skipping a WORK session
        viewModel.skipSession() // Now in SHORT_BREAK, pomodorosCompletedInCycle = 1
        assertEquals(SessionType.SHORT_BREAK, viewModel.uiState.value.currentSessionType)
        assertEquals(1, viewModel.uiState.value.pomodorosCompletedInCycle)

        viewModel.skipSession() // Skip SHORT_BREAK
        val state = viewModel.uiState.value
        assertEquals(SessionType.WORK, state.currentSessionType)
        assertEquals(PomodoroTimerViewModel.WORK_DURATION_MILLIS, state.timeLeftInMillis)
        assertFalse(state.isTimerRunning)
        assertEquals("Pomodoros should NOT increment when skipping SHORT_BREAK", 1, state.pomodorosCompletedInCycle)
    }
}
