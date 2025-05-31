package com.neimasilk.purefocus.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.neimasilk.purefocus.R

class PomodoroService : Service() {

    companion object {
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        private const val NOTIFICATION_ID = 2 // ID berbeda dari notifikasi sesi akhir
        private const val NOTIFICATION_CHANNEL_ID = "purefocus_foreground_service_channel"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Tidak menggunakan binding untuk saat ini
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                startForegroundService()
            }
            ACTION_STOP_SERVICE -> {
                stopForegroundService()
            }
        }
        return START_STICKY // Atau START_NOT_STICKY, tergantung kebutuhan
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("PureFocus Timer Aktif")
            .setContentText("Sesi Pomodoro sedang berjalan...")
            .setSmallIcon(R.drawable.ic_notification_pomodoro) // Gunakan ikon yang sama atau yang lain
            .setOngoing(true) // Penting untuk notifikasi foreground service
            .build()

        startForeground(NOTIFICATION_ID, notification)
        // TODO: Di baby step selanjutnya, pindahkan logika timer ke sini
        Log.d("PomodoroService", "Foreground service dimulai.")
    }

    private fun stopForegroundService() {
        Log.d("PomodoroService", "Foreground service dihentikan.")
        stopForeground(STOP_FOREGROUND_REMOVE) // Hapus notifikasi saat service berhenti
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "PureFocus Timer Service",
                NotificationManager.IMPORTANCE_LOW // Atau DEFAULT, tapi LOW lebih baik untuk notif persisten
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PomodoroService", "Service dihancurkan.")
    }
}