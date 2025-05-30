package com.neimasilk.purefocus.util

/**
 * Mock version of PerformanceMonitor for testing
 */
object PerformanceMonitor {
    private const val TAG = "PerformanceMonitor"
    private val timers = mutableMapOf<String, Long>()
    
    /**
     * Memulai timer untuk mengukur durasi operasi
     * @param tag Identifier unik untuk timer
     */
    fun startTimer(tag: String) {
        // Do nothing in tests
    }
    
    /**
     * Mengakhiri timer dan mencatat durasi operasi
     * @param tag Identifier yang sama dengan yang digunakan di startTimer
     */
    fun endTimer(tag: String) {
        // Do nothing in tests
    }
    
    /**
     * Mencatat penggunaan memori saat ini
     */
    fun logMemoryUsage() {
        // Do nothing in tests
    }
    
    /**
     * Mencatat waktu startup aplikasi
     * @param startTime Waktu mulai aplikasi (biasanya dari onCreate Application)
     */
    fun logStartupTime(startTime: Long) {
        // Do nothing in tests
    }
}