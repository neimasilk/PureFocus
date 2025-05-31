package com.neimasilk.purefocus.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.neimasilk.purefocus.MainActivity
import com.neimasilk.purefocus.PureFocusApplication
import com.neimasilk.purefocus.R

object NotificationHelper {
    private const val FOCUS_END_NOTIFICATION_ID = 1
    
    /**
     * Menampilkan notifikasi ketika sesi fokus berakhir
     */
    fun showFocusSessionEndNotification(context: Context) {
        // Intent untuk membuka aplikasi ketika notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Buat notifikasi
        val notification = NotificationCompat.Builder(context, PureFocusApplication.POMODORO_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Menggunakan ikon standar untuk sementara
            .setContentTitle("Sesi Fokus Selesai!")
            .setContentText("Waktunya istirahat sejenak. Sesi Istirahat Singkat akan dimulai.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        // Tampilkan notifikasi hanya jika permission tersedia
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(FOCUS_END_NOTIFICATION_ID, notification)
        }
    }
}