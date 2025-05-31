package com.neimasilk.purefocus.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

import com.neimasilk.purefocus.util.DefaultSettings // Import tambahan

class PomodoroTimerViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    companion object {
        // SHORT_BREAK_DURATION_MINUTES dipindahkan ke Constants.kt
        // LONG_BREAK_DURATION_MINUTES dipindahkan ke Constants.kt

        // Using TimeUnit for clarity and correctness
        val SHORT_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_SHORT_BREAK_DURATION_MINUTES)
        val LONG_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_LONG_BREAK_DURATION_MINUTES)

        // const val POMODOROS_PER_CYCLE = 4 // For future use
    }

    // Get work duration from preferences
    private fun getWorkDurationMillis(): Long {
        return TimeUnit.MINUTES.toMillis(preferencesManager.focusDuration.value.toLong())
    }

    private val _uiState = MutableStateFlow(PomodoroState(timeLeftInMillis = getWorkDurationMillis()))
    val uiState: StateFlow<PomodoroState> = _uiState.asStateFlow()

    // Event untuk menampilkan notifikasi akhir sesi fokus
    private val _showFocusEndNotificationEvent = MutableSharedFlow<Unit>()
    val showFocusEndNotificationEvent: SharedFlow<Unit> = _showFocusEndNotificationEvent.asSharedFlow()
    
    // Event untuk menampilkan notifikasi akhir sesi istirahat
    private val _showBreakEndNotificationEvent = MutableSharedFlow<Unit>()
    val showBreakEndNotificationEvent: SharedFlow<Unit> = _showBreakEndNotificationEvent.asSharedFlow()

    // Event untuk mengelola foreground service
    private val _serviceCommandEvent = MutableSharedFlow<ServiceAction>()
    val serviceCommandEvent: SharedFlow<ServiceAction> = _serviceCommandEvent.asSharedFlow()

    // State untuk teks Focus Write
    private val _focusWriteText = MutableStateFlow("")
    val focusWriteText: StateFlow<String> = _focusWriteText.asStateFlow()

    fun startPauseTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        if (_uiState.value.isTimerRunning) {
            return
        }

        _uiState.update { it.copy(isTimerRunning = true) }
        
        // Emit event untuk memulai foreground service
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.START)
        }
    }

    fun pauseTimer() {
        _uiState.update { it.copy(isTimerRunning = false) }
        
        // Emit event untuk pause service
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.PAUSE)
        }
    }

    // handleSessionFinish dipindahkan ke PomodoroService

    fun resetTimer() {
        _uiState.update {
            it.copy(
                timeLeftInMillis = getWorkDurationMillis(),
                currentSessionType = SessionType.WORK,
                isTimerRunning = false
            )
        }
        
        // Emit event untuk reset service
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.RESET)
        }
    }

    fun skipSession() {
        // Jika sedang dalam sesi WORK, reset teks dan log sebelum transisi
        val currentState = _uiState.value
        if (currentState.currentSessionType == SessionType.WORK) {
            val currentText = _focusWriteText.value
            if (currentText.isNotEmpty()) {
                Log.d("FocusWrite", "Sesi Fokus Dilewati. Teks: $currentText")
            }
            _focusWriteText.value = ""
        }
        
        // Emit event untuk skip session
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.SKIP)
        }
    }

    /**
     * Update teks Focus Write
     */
    fun updateFocusWriteText(newText: String) {
        _focusWriteText.value = newText
    }
    
    enum class ServiceAction { START, PAUSE, RESET, SKIP }
}
