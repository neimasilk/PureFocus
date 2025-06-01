package com.neimasilk.purefocus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import android.os.SystemClock
import com.neimasilk.purefocus.BuildConfig
import com.neimasilk.purefocus.util.PerformanceMonitor
import com.neimasilk.purefocus.util.NotificationConstants

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
            // Channel untuk Notifikasi Sesi Akhir (Fokus/Istirahat)
            val sessionEndChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_SESSION_END, // Menggunakan konstanta dari NotificationConstants
                "Notifikasi Akhir Sesi",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi saat sesi fokus atau istirahat berakhir."
            }

            // Channel untuk Foreground Service Timer Pomodoro
            val pomodoroServiceChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID_POMODORO, // Menggunakan konstanta dari NotificationConstants
                "PureFocus Timer Service",
                NotificationManager.IMPORTANCE_LOW 
            ).apply {
                description = "Notifikasi persisten untuk timer Pomodoro yang sedang berjalan."
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(sessionEndChannel)
            notificationManager?.createNotificationChannel(pomodoroServiceChannel)
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
    
    // Companion object tidak lagi diperlukan karena konstanta dipindah ke Constants.kt
}