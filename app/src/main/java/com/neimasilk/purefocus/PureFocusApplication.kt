package com.neimasilk.purefocus 
 
import android.app.Application 
import android.app.NotificationChannel 
import android.app.NotificationManager 
import android.os.Build 
import android.os.SystemClock 
import com.neimasilk.purefocus.util.PerformanceMonitor 
import com.neimasilk.purefocus.util.NotificationConstants 
import dagger.hilt.android.HiltAndroidApp
 
@HiltAndroidApp
class PureFocusApplication : Application() { 
    private val startupTime = SystemClock.elapsedRealtime() 
 
    override fun onCreate() { 
        super.onCreate() 
 
        // StrictMode temporarily disabled to fix crash 
        // if (BuildConfig.DEBUG) { 
        //     enableStrictMode() 
        // } 
 
        createNotificationChannel() 
        PerformanceMonitor.logStartupTime(startupTime) 
    } 
 
    private fun createNotificationChannel() { 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { 
            val sessionEndChannel = NotificationChannel( 
                NotificationConstants.CHANNEL_ID_SESSION_END, 
                "Notifikasi Akhir Sesi", 
                NotificationManager.IMPORTANCE_HIGH 
            ).apply { 
                description = "Notifikasi saat sesi fokus atau istirahat berakhir." 
            } 
 
            val pomodoroServiceChannel = NotificationChannel( 
                NotificationConstants.CHANNEL_ID_POMODORO, 
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
}
