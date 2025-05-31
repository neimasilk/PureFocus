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

    // Companion object untuk expose state ke ViewModel
    companion object {
        private val _serviceState = MutableStateFlow(PomodoroServiceState())
        val serviceState: StateFlow<PomodoroServiceState> = _serviceState.asStateFlow()
        
        val SHORT_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_SHORT_BREAK_DURATION_MINUTES)
        val LONG_BREAK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(DefaultSettings.VM_LONG_BREAK_DURATION_MINUTES)
        const val POMODOROS_PER_CYCLE = 4
    }

    // Internal state reference untuk kemudahan akses
    private val internalState get() = _serviceState

    private var timerJob: Job? = null
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        preferencesManager = PreferencesManager(this)
        
        // Coba pulihkan state yang tersimpan
        val savedState = preferencesManager.getSavedServiceState()
        if (savedState != null) {
            // Pulihkan state dari preferences
            internalState.value = PomodoroServiceState(
                timeLeftInMillis = savedState.timeLeftInMillis,
                currentSessionType = try {
                    SessionType.valueOf(savedState.currentSessionType)
                } catch (e: IllegalArgumentException) {
                    SessionType.WORK
                },
                isRunning = false, // Selalu mulai dalam keadaan pause setelah restart
                pomodorosCompletedInCycle = savedState.pomodorosCompletedInCycle
            )
        } else {
            // Initialize dengan durasi work default
            val workDurationMillis = getWorkDurationMillis()
            internalState.value = PomodoroServiceState(
                timeLeftInMillis = workDurationMillis,
                currentSessionType = SessionType.WORK,
                isRunning = false,
                pomodorosCompletedInCycle = 0
            )
        }
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
        if (internalState.value.isRunning) {
            return
        }

        internalState.value = internalState.value.copy(isRunning = true)

        timerJob = serviceScope.launch {
            var currentTime = internalState.value.timeLeftInMillis
            var saveCounter = 0
            while (currentTime > 0 && isActive) {
                delay(1000L)
                currentTime -= 1000L
                internalState.value = internalState.value.copy(
                    timeLeftInMillis = maxOf(0, currentTime)
                )
                
                // Simpan state setiap 10 detik untuk persistence
                saveCounter++
                if (saveCounter >= 10) {
                    saveCurrentState()
                    saveCounter = 0
                }
            }

            // Timer selesai
            if (isActive && currentTime <= 0) {
                handleSessionFinish()
            }
        }
    }

    private fun pauseTimerInternal() {
        timerJob?.cancel()
        internalState.value = internalState.value.copy(isRunning = false)
        saveCurrentState() // Simpan state saat pause
    }

    private fun resetTimerInternal() {
        pauseTimerInternal()
        internalState.value = internalState.value.copy(
            timeLeftInMillis = getWorkDurationMillis(),
            currentSessionType = SessionType.WORK,
            isRunning = false
        )
        preferencesManager.clearSavedServiceState() // Hapus state tersimpan saat reset
    }

    private fun skipSessionInternal() {
        pauseTimerInternal()
        handleSessionFinish()
    }

    private fun handleSessionFinish() {
        pauseTimerInternal()

        val currentState = internalState.value
        when (currentState.currentSessionType) {
            SessionType.WORK -> {
                Log.d("PomodoroService", "Sesi fokus selesai")
                
                val newPomodorosCompleted = currentState.pomodorosCompletedInCycle + 1
                
                // Cek apakah sudah waktunya untuk long break
                if (newPomodorosCompleted >= POMODOROS_PER_CYCLE) {
                    internalState.value = currentState.copy(
                        currentSessionType = SessionType.LONG_BREAK,
                        timeLeftInMillis = LONG_BREAK_DURATION_MILLIS,
                        pomodorosCompletedInCycle = newPomodorosCompleted,
                        isRunning = false
                    )
                } else {
                    internalState.value = currentState.copy(
                        currentSessionType = SessionType.SHORT_BREAK,
                        timeLeftInMillis = SHORT_BREAK_DURATION_MILLIS,
                        pomodorosCompletedInCycle = newPomodorosCompleted,
                        isRunning = false
                    )
                }
            }
            SessionType.SHORT_BREAK -> {
                Log.d("PomodoroService", "Sesi istirahat pendek selesai")
                
                internalState.value = currentState.copy(
                    currentSessionType = SessionType.WORK,
                    timeLeftInMillis = getWorkDurationMillis(),
                    isRunning = false
                )
            }
            SessionType.LONG_BREAK -> {
                Log.d("PomodoroService", "Sesi istirahat panjang selesai")
                
                // Reset cycle setelah long break
                internalState.value = currentState.copy(
                    currentSessionType = SessionType.WORK,
                    timeLeftInMillis = getWorkDurationMillis(),
                    pomodorosCompletedInCycle = 0,
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
        serviceScope.cancel()
        
        // Simpan state terakhir sebelum service dihancurkan
        if (internalState.value.isRunning || internalState.value.timeLeftInMillis > 0) {
            saveCurrentState()
        }
    }
    
    /**
     * Menyimpan state saat ini ke preferences untuk persistence
     */
    private fun saveCurrentState() {
        val currentState = internalState.value
        preferencesManager.saveServiceState(
            timeLeftInMillis = currentState.timeLeftInMillis,
            currentSessionType = currentState.currentSessionType.name,
            isRunning = currentState.isRunning,
            pomodorosCompletedInCycle = currentState.pomodorosCompletedInCycle
        )
    }
}