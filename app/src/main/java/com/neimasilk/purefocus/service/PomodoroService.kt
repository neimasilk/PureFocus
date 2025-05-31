package com.neimasilk.purefocus.service

import android.app.NotificationChannel
import android.app.NotificationManager

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.app.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import com.neimasilk.purefocus.R
import com.neimasilk.purefocus.data.PreferencesManager
import com.neimasilk.purefocus.model.SessionType
import com.neimasilk.purefocus.util.DefaultSettings
import com.neimasilk.purefocus.util.NotificationConstants
import com.neimasilk.purefocus.util.PomodoroServiceActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PomodoroService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Internal state untuk service
    data class PomodoroServiceState(
        val timeLeftInMillis: Long = 0L,
        val currentSessionType: SessionType = SessionType.WORK,
        val isRunning: Boolean = false,
        val pomodorosCompletedInCycle: Int = 0
    )

    private val _serviceState = MutableStateFlow(PomodoroServiceState())
    val serviceState: StateFlow<PomodoroServiceState> = _serviceState.asStateFlow()

    private var timerJob: Job? = null
    private lateinit var preferencesManager: PreferencesManager

    companion object {
        val SHORT_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_SHORT_BREAK_DURATION_MINUTES)
        val LONG_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_LONG_BREAK_DURATION_MINUTES)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        preferencesManager = PreferencesManager(this)
        
        // Initialize dengan durasi work default
        val workDurationMillis = getWorkDurationMillis()
        _serviceState.value = PomodoroServiceState(
            timeLeftInMillis = workDurationMillis,
            currentSessionType = SessionType.WORK,
            isRunning = false,
            pomodorosCompletedInCycle = 0
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            PomodoroServiceActions.ACTION_START_RESUME -> {
                startForegroundService()
                startTimerInternal()
            }
            PomodoroServiceActions.ACTION_PAUSE -> {
                pauseTimerInternal()
                stopForegroundService()
            }
            PomodoroServiceActions.ACTION_RESET -> {
                resetTimerInternal()
                stopForegroundService()
            }
            PomodoroServiceActions.ACTION_SKIP -> {
                skipSessionInternal()
            }
        }
        return START_STICKY
    }

    private fun getWorkDurationMillis(): Long {
        return TimeUnit.MINUTES.toMillis(preferencesManager.focusDuration.value.toLong())
    }

    private fun startTimerInternal() {
        if (_serviceState.value.isRunning) {
            return
        }

        _serviceState.value = _serviceState.value.copy(isRunning = true)

        timerJob = serviceScope.launch {
            var currentTime = _serviceState.value.timeLeftInMillis
            while (currentTime > 0 && isActive) {
                delay(1000L)
                currentTime -= 1000L
                _serviceState.value = _serviceState.value.copy(
                    timeLeftInMillis = maxOf(0, currentTime)
                )
            }

            // Timer selesai
            if (isActive && currentTime <= 0) {
                handleSessionFinish()
            }
        }
    }

    private fun pauseTimerInternal() {
        timerJob?.cancel()
        _serviceState.value = _serviceState.value.copy(isRunning = false)
    }

    private fun resetTimerInternal() {
        pauseTimerInternal()
        _serviceState.value = _serviceState.value.copy(
            timeLeftInMillis = getWorkDurationMillis(),
            currentSessionType = SessionType.WORK,
            isRunning = false
        )
    }

    private fun skipSessionInternal() {
        pauseTimerInternal()
        handleSessionFinish()
    }

    private fun handleSessionFinish() {
        pauseTimerInternal()

        val currentState = _serviceState.value
        when (currentState.currentSessionType) {
            SessionType.WORK -> {
                Log.d("PomodoroService", "Sesi fokus selesai")
                
                val newPomodorosCompleted = currentState.pomodorosCompletedInCycle + 1
                _serviceState.value = currentState.copy(
                    currentSessionType = SessionType.SHORT_BREAK,
                    timeLeftInMillis = SHORT_BREAK_DURATION_MILLIS,
                    pomodorosCompletedInCycle = newPomodorosCompleted,
                    isRunning = false
                )
                // TODO: Add logic for LONG_BREAK after POMODOROS_PER_CYCLE in future baby-step
            }
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> {
                Log.d("PomodoroService", "Sesi istirahat selesai")
                
                _serviceState.value = currentState.copy(
                    currentSessionType = SessionType.WORK,
                    timeLeftInMillis = getWorkDurationMillis(),
                    isRunning = false
                )
            }
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NotificationConstants.CHANNEL_ID_POMODORO)
            .setContentTitle("PureFocus Timer Aktif")
            .setContentText("Sesi Pomodoro sedang berjalan...")
            .setSmallIcon(R.drawable.ic_notification_pomodoro)
            .setOngoing(true)
            .build()

        startForeground(NotificationConstants.NOTIFICATION_ID_POMODORO, notification)
        Log.d("PomodoroService", "Foreground service dimulai.")
    }

    private fun stopForegroundService() {
        Log.d("PomodoroService", "Foreground service dihentikan.")
        stopForeground(true) // Hapus notifikasi saat service berhenti
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_POMODORO, // Menggunakan konstanta dari NotificationConstants
                "PureFocus Timer Service",
                NotificationManager.IMPORTANCE_LOW // Atau DEFAULT, tapi LOW lebih baik untuk notif persisten
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.coroutineContext.cancel()
        Log.d("PomodoroService", "Service dihancurkan.")
    }
}