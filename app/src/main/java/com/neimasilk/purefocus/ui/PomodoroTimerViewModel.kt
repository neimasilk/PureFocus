package com.neimasilk.purefocus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PomodoroTimerViewModel : ViewModel() {

    companion object {
        const val WORK_DURATION_MINUTES = 25L
        const val SHORT_BREAK_DURATION_MINUTES = 5L
        const val LONG_BREAK_DURATION_MINUTES = 15L // For future use

        // Using TimeUnit for clarity and correctness
        val WORK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(WORK_DURATION_MINUTES)
        val SHORT_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(SHORT_BREAK_DURATION_MINUTES)
        val LONG_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(LONG_BREAK_DURATION_MINUTES)

        // const val POMODOROS_PER_CYCLE = 4 // For future use
    }

    private val _uiState = MutableStateFlow(PomodoroState(timeLeftInMillis = WORK_DURATION_MILLIS))
    val uiState: StateFlow<PomodoroState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer() {
        if (_uiState.value.isTimerRunning) {
            return
        }

        _uiState.update { it.copy(isTimerRunning = true) }

        timerJob = viewModelScope.launch {
            var currentTime = _uiState.value.timeLeftInMillis
            while (currentTime > 0 && isActive) {
                delay(1000L)
                currentTime -= 1000L
                // Ensure timeLeftInMillis doesn't go negative if delay caused slight overrun
                _uiState.update { it.copy(timeLeftInMillis = maxOf(0, currentTime)) }
            }

            // After loop finishes (time is up or job cancelled)
            if (isActive && currentTime <= 0) { // Ensure it was time up, not cancellation
                handleSessionFinish()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    private fun handleSessionFinish() {
        pauseTimer() // Stop any existing timer job and set isTimerRunning to false

        val currentState = _uiState.value
        when (currentState.currentSessionType) {
            SessionType.WORK -> {
                val newPomodorosCompleted = currentState.pomodorosCompletedInCycle + 1
                _uiState.update {
                    it.copy(
                        currentSessionType = SessionType.SHORT_BREAK,
                        timeLeftInMillis = SHORT_BREAK_DURATION_MILLIS,
                        pomodorosCompletedInCycle = newPomodorosCompleted,
                        isTimerRunning = false // Ensure timer is paused after transition
                    )
                }
                // TODO: Add logic for LONG_BREAK after POMODOROS_PER_CYCLE in a future baby-step
            }
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> { // Consolidate break handling
                _uiState.update {
                    it.copy(
                        currentSessionType = SessionType.WORK,
                        timeLeftInMillis = WORK_DURATION_MILLIS,
                        isTimerRunning = false // Ensure timer is paused after transition
                    )
                }
            }
        }
    }

    fun resetTimer() {
        pauseTimer()
        // Resets current session to a full WORK session, keeps completed cycle count.
        _uiState.update {
            it.copy(
                timeLeftInMillis = WORK_DURATION_MILLIS,
                currentSessionType = SessionType.WORK,
                isTimerRunning = false
                // pomodorosCompletedInCycle = it.pomodorosCompletedInCycle // Stays the same as per spec
            )
        }
    }

    fun skipSession() {
        // pauseTimer() is called by handleSessionFinish, so not strictly needed here
        // but explicitly calling it ensures isTimerRunning is false before handleSessionFinish logic
        pauseTimer() 
        handleSessionFinish() // This will transition to the next session and set its duration
    }
}
