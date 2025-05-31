package com.neimasilk.purefocus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import android.os.SystemClock
import com.neimasilk.purefocus.BuildConfig
import com.neimasilk.purefocus.util.PerformanceMonitor

/**
 * Application class untuk PureFocus.
 * Menangani inisialisasi aplikasi dan konfigurasi StrictMode.
 */
class PureFocusApplication : Application() {
    private val startupTime = SystemClock.elapsedRealtime()
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        // Create notification channel for Pomodoro notifications
        createNotificationChannel()
        
        // Log startup time
        PerformanceMonitor.logStartupTime(startupTime)
    }
    
    /**
     * Membuat notification channel untuk notifikasi Pomodoro
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                POMODORO_CHANNEL_ID,
                "Pomodoro Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Pomodoro session updates"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    /**
     * Mengaktifkan StrictMode untuk mendeteksi masalah performa dan memory leak
     * hanya pada build debug
     */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }
    
    companion object {
        const val POMODORO_CHANNEL_ID = "purefocus_pomodoro_channel"
    }
}