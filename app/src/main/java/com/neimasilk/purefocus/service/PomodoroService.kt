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
import com.neimasilk.purefocus.util.NotificationConstants
import com.neimasilk.purefocus.util.PomodoroServiceActions

class PomodoroService : Service() {

    // Tidak ada companion object lagi, konstanta dipindah ke Constants.kt

    override fun onBind(intent: Intent?): IBinder? {
        return null // Tidak menggunakan binding untuk saat ini
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            PomodoroServiceActions.ACTION_START_RESUME -> { // Menggunakan konstanta dari PomodoroServiceActions
                startForegroundService()
            }
            PomodoroServiceActions.ACTION_PAUSE -> { // Menggunakan konstanta dari PomodoroServiceActions (asumsi pause juga menghentikan foreground)
                stopForegroundService()
            }
            // Tambahkan case untuk ACTION_RESET dan ACTION_SKIP jika diperlukan
        }
        return START_STICKY // Atau START_NOT_STICKY, tergantung kebutuhan
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NotificationConstants.CHANNEL_ID_POMODORO) // Menggunakan konstanta dari NotificationConstants
            .setContentTitle("PureFocus Timer Aktif")
            .setContentText("Sesi Pomodoro sedang berjalan...")
            .setSmallIcon(R.drawable.ic_notification_pomodoro) // Gunakan ikon yang sama atau yang lain
            .setOngoing(true) // Penting untuk notifikasi foreground service
            .build()

        startForeground(NotificationConstants.NOTIFICATION_ID_POMODORO, notification) // Menggunakan konstanta dari NotificationConstants
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
                NotificationConstants.CHANNEL_ID_POMODORO, // Menggunakan konstanta dari NotificationConstants
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