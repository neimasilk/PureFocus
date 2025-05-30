package com.neimasilk.purefocus

import android.app.Application
import android.os.StrictMode
import android.os.SystemClock
import com.neimasilk.purefocus.BuildConfig
import com.neimasilk.purefocus.util.PerformanceMonitor
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class untuk PureFocus.
 * Menangani inisialisasi aplikasi dan konfigurasi StrictMode.
 * Menggunakan Hilt untuk dependency injection.
 */
@HiltAndroidApp
class PureFocusApplication : Application() {
    private val startupTime = SystemClock.elapsedRealtime()
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        // Log startup time
        PerformanceMonitor.logStartupTime(startupTime)
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
}