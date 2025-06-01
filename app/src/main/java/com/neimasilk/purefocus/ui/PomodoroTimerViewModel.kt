package com.neimasilk.purefocus.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neimasilk.purefocus.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.neimasilk.purefocus.model.PomodoroState
import com.neimasilk.purefocus.model.SessionType
import com.neimasilk.purefocus.service.PomodoroService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel untuk mengelola timer Pomodoro dan state sesi fokus.
 * 
 * Kelas ini bertanggung jawab untuk:
 * - Mengelola state timer Pomodoro (work, short break, long break)
 * - Berkomunikasi dengan PomodoroService untuk operasi background
 * - Menyediakan events untuk notifikasi UI
 * - Mengelola siklus Pomodoro (4 sesi kerja + 1 istirahat panjang)
 * 
 * @param preferencesManager Manager untuk mengakses preferensi pengguna
 */
@HiltViewModel
class PomodoroTimerViewModel @Inject constructor(private val preferencesManager: PreferencesManager) : ViewModel() {

    /**
     * Mengambil durasi sesi kerja dari preferensi dalam milidetik.
     * @return Durasi sesi kerja dalam milidetik
     */
    private fun getWorkDurationMillis(): Long {
        return TimeUnit.MINUTES.toMillis(preferencesManager.focusDuration.value.toLong())
    }

    /** State UI Pomodoro yang dapat diobservasi oleh UI */
    private val _uiState = MutableStateFlow(PomodoroState(timeLeftInMillis = getWorkDurationMillis()))
    val uiState: StateFlow<PomodoroState> = _uiState.asStateFlow()

    /** Event untuk menampilkan notifikasi akhir sesi fokus */
    private val _showFocusEndNotificationEvent = MutableSharedFlow<Unit>()
    val showFocusEndNotificationEvent: SharedFlow<Unit> = _showFocusEndNotificationEvent.asSharedFlow()
    
    /** Event untuk menampilkan notifikasi akhir sesi istirahat */
    private val _showBreakEndNotificationEvent = MutableSharedFlow<Unit>()
    val showBreakEndNotificationEvent: SharedFlow<Unit> = _showBreakEndNotificationEvent.asSharedFlow()

    /** Event untuk mengelola foreground service (start/stop) */
    private val _serviceCommandEvent = MutableSharedFlow<ServiceAction>()
    val serviceCommandEvent: SharedFlow<ServiceAction> = _serviceCommandEvent.asSharedFlow()

    /** State untuk teks Focus Write yang dapat diobservasi oleh UI */
    private val _focusWriteText = MutableStateFlow("")
    val focusWriteText: StateFlow<String> = _focusWriteText.asStateFlow()

    init {
        // Observasi state dari PomodoroService
        viewModelScope.launch {
            PomodoroService.serviceState.collect { serviceState ->
                _uiState.update {
                    it.copy(
                        timeLeftInMillis = serviceState.timeLeftInMillis,
                        currentSessionType = serviceState.currentSessionType,
                        isTimerRunning = serviceState.isRunning,
                        pomodorosCompletedInCycle = serviceState.pomodorosCompletedInCycle
                    )
                }
            }
        }
    }

    fun startPauseTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        // Delegasikan ke service
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.START)
        }
    }

    fun pauseTimer() {
        // Delegasikan ke service
        viewModelScope.launch {
            _serviceCommandEvent.emit(ServiceAction.PAUSE)
        }
    }

    fun resetTimer() {
        // Delegasikan ke service
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
        
        // Delegasikan ke service
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
    
    /**
     * Pause timer saat aplikasi tidak di foreground dan simpan state
     */
    fun pauseTimerForAppPause() {
        val currentState = _uiState.value
        if (currentState.isTimerRunning) {
            // Simpan bahwa timer sedang berjalan sebelum di-pause
            preferencesManager.saveTimerRunningStateBeforePause(true)
            pauseTimer()
        } else {
            // Timer tidak sedang berjalan, simpan false
            preferencesManager.saveTimerRunningStateBeforePause(false)
        }
    }
    
    /**
     * Resume timer saat aplikasi kembali ke foreground jika sebelumnya sedang berjalan
     */
    fun resumeTimerForAppResume() {
        val wasRunningBeforePause = preferencesManager.getTimerRunningStateBeforePause()
        if (wasRunningBeforePause) {
            // Timer sedang berjalan sebelum aplikasi di-pause, resume timer
            startTimer()
        }
        // Hapus state yang tersimpan setelah digunakan
        preferencesManager.clearTimerRunningStateBeforePause()
    }
    
    enum class ServiceAction { START, PAUSE, RESET, SKIP }
}
