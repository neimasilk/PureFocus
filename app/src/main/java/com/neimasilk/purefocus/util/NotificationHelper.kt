package com.neimasilk.purefocus.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.neimasilk.purefocus.MainActivity
import com.neimasilk.purefocus.PureFocusApplication
import com.neimasilk.purefocus.R
import com.neimasilk.purefocus.model.SessionType

object NotificationHelper {
    // ID Notifikasi dipindahkan ke NotificationConstants
    
    /**
     * Menampilkan notifikasi ketika sesi fokus berakhir
     */
    fun showFocusSessionEndNotification(context: Context, enableSound: Boolean = true) {
        showSessionEndNotification(context, SessionType.WORK, enableSound)
    }
    
    /**
     * Menampilkan notifikasi ketika sesi istirahat berakhir
     */
    fun showBreakSessionEndNotification(context: Context, enableSound: Boolean = true) {
        showSessionEndNotification(context, SessionType.SHORT_BREAK, enableSound)
    }
    
    /**
     * Menampilkan notifikasi ketika sesi berakhir dengan suara yang sesuai
     */
    private fun showSessionEndNotification(context: Context, sessionType: SessionType, enableSound: Boolean) {
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
        
        // Gunakan suara notifikasi default sistem
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        // Tentukan konten notifikasi berdasarkan tipe sesi
        val (title, text, notificationId) = when (sessionType) {
            SessionType.WORK -> Triple(
                "Sesi Fokus Selesai!",
                "Waktunya istirahat sejenak. Sesi Istirahat Singkat akan dimulai.",
                NotificationConstants.NOTIFICATION_ID_FOCUS_END // Menggunakan konstanta dari NotificationConstants
            )
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> Triple(
                "Sesi Istirahat Selesai!",
                "Waktunya kembali fokus. Sesi Fokus baru akan dimulai.",
                NotificationConstants.NOTIFICATION_ID_BREAK_END // Menggunakan konstanta dari NotificationConstants
            )
        }
        
        // Buat notifikasi dengan atau tanpa suara berdasarkan preferensi
        val notificationBuilder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID_SESSION_END) // Menggunakan konstanta dari NotificationConstants
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Menggunakan ikon standar untuk sementara
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        // Hanya tambahkan suara jika enableSound adalah true
        if (enableSound) {
            notificationBuilder.setSound(soundUri)
        }
        
        val notification = notificationBuilder.build()
        
        // Tampilkan notifikasi hanya jika permission tersedia
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}