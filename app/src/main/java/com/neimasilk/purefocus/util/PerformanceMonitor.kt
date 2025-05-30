package com.neimasilk.purefocus.util

import android.os.SystemClock
import android.util.Log

/**
 * Utilitas untuk memantau performa aplikasi.
 * Menyediakan fungsi untuk mengukur waktu eksekusi dan penggunaan memori.
 */
object PerformanceMonitor {
    private const val TAG = "PerformanceMonitor"
    private val timers = mutableMapOf<String, Long>()
    
    /**
     * Memulai timer untuk mengukur durasi operasi
     * @param tag Identifier unik untuk timer
     */
    fun startTimer(tag: String) {
        timers[tag] = SystemClock.elapsedRealtime()
    }
    
    /**
     * Mengakhiri timer dan mencatat durasi operasi
     * @param tag Identifier yang sama dengan yang digunakan di startTimer
     */
    fun endTimer(tag: String) {
        val startTime = timers[tag] ?: return
        val duration = SystemClock.elapsedRealtime() - startTime
        Log.d(TAG, "$tag: $duration ms")
        timers.remove(tag)
    }
    
    /**
     * Mencatat penggunaan memori saat ini
     */
    fun logMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        Log.d(TAG, "Memory usage: $usedMemory MB / $totalMemory MB")
    }
    
    /**
     * Mencatat waktu startup aplikasi
     * @param startTime Waktu mulai aplikasi (biasanya dari onCreate Application)
     */
    fun logStartupTime(startTime: Long) {
        val duration = SystemClock.elapsedRealtime() - startTime
        Log.d(TAG, "App startup time: $duration ms")
    }
}