package com.example.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.MuezzinVoiceOption
import com.example.widget.QiblaPrayerWidgetProvider

/**
 * Manages the Local Push Notification system for Prayer Times & Adzan Playback.
 * Posts rich, heads-up local notifications when a scheduled `AlarmManager` prayer alarm fires
 * or when the user tests the notification from the Prayer Times screen, complete with:
 * - "Putar Adzan" / "Hentikan Adzan" interactive action buttons
 * - "Buka Kompas Kiblat" & "Jadwal Sholat" deep-link actions
 * - Full-screen / lock-screen wake visibility
 */
object AdhanNotificationHelper {

    const val PUSH_CHANNEL_ID = "adhan_prayer_push_alert_channel"
    const val PUSH_CHANNEL_NAME = "Notifikasi Jadwal Sholat & Adzan"
    private const val BASE_PUSH_NOTIFICATION_ID = 8200

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                PUSH_CHANNEL_ID,
                PUSH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat waktu sholat dan pemutaran suara Adzan otomatis"
                setSound(null, null) // Audio playback is handled by AdhanForegroundService MediaPlayer
                enableVibration(true)
                vibrationPattern = longArrayOf(0L, 250L, 150L, 250L)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Dispatches a high-priority local push notification for the specified prayer time
     * with direct controls for Adzan audio playback.
     */
    fun showPrayerTimePushNotification(
        context: Context,
        prayerName: String,
        prayerTimeFormatted: String,
        locationLabel: String,
        muezzin: MuezzinVoiceOption,
        isTestNotification: Boolean = false
    ) {
        ensureNotificationChannel(context)
        if (!hasNotificationPermission(context)) return

        // Content tap: Open MainActivity directly on the Prayer Times tab
        val openPrayerTabIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(QiblaPrayerWidgetProvider.EXTRA_OPEN_TAB, QiblaPrayerWidgetProvider.TAB_PRAYER_TIMES)
        }
        val openPrayerPendingIntent = PendingIntent.getActivity(
            context,
            8001 + prayerName.hashCode(),
            openPrayerTabIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Stop Adzan Playback
        val stopAdhanIntent = Intent(context, AdhanForegroundService::class.java).apply {
            action = AdhanForegroundService.ACTION_STOP_ADHAN
        }
        val stopAdhanPendingIntent = PendingIntent.getService(
            context,
            8101,
            stopAdhanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Play / Replay Adzan Playback via BroadcastReceiver so it works reliably from background
        val playAdhanIntent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            action = AdhanAlarmReceiver.ACTION_PLAY_ADHAN_FROM_NOTIFICATION
            putExtra(AdhanForegroundService.EXTRA_PRAYER_NAME, prayerName)
            putExtra(AdhanForegroundService.EXTRA_MUEZZIN_ID, muezzin.id)
        }
        val playAdhanPendingIntent = PendingIntent.getBroadcast(
            context,
            8102 + prayerName.hashCode(),
            playAdhanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: Open Qibla Compass directly
        val openCompassIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(QiblaPrayerWidgetProvider.EXTRA_OPEN_TAB, QiblaPrayerWidgetProvider.TAB_COMPASS)
        }
        val openCompassPendingIntent = PendingIntent.getActivity(
            context,
            8103,
            openCompassIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeSuffix = if (prayerTimeFormatted.isNotBlank()) " ($prayerTimeFormatted)" else ""
        val title = if (isTestNotification) {
            "Notifikasi Adzan Sholat $prayerName$timeSuffix"
        } else {
            "Waktunya Sholat $prayerName$timeSuffix"
        }

        val cleanLocation = locationLabel.ifBlank { "Lokasi GPS Anda" }
        val shortBody = "Kumandang ${muezzin.title} • $cleanLocation"
        val expandedBody = buildString {
            append("Hayya 'alash-shalaah • Waktu Sholat $prayerName$timeSuffix telah tiba untuk wilayah $cleanLocation.\n")
            append("Muadzin: ${muezzin.title} (${muezzin.subtitle}).")
        }

        val notification = NotificationCompat.Builder(context, PUSH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_mosque_notification)
            .setColor(0xFF0A4D3C.toInt())
            .setContentTitle(title)
            .setContentText(shortBody)
            .setSubText(cleanLocation)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedBody))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openPrayerPendingIntent)
            .setFullScreenIntent(openPrayerPendingIntent, true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Hentikan Adzan",
                stopAdhanPendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_play,
                "Putar Ulang",
                playAdhanPendingIntent
            )
            .addAction(
                R.drawable.ic_stat_mosque_notification,
                "Arah Kiblat",
                openCompassPendingIntent
            )
            .build()

        val notificationId = BASE_PUSH_NOTIFICATION_ID + (prayerName.hashCode() and 0x7F)
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
        }
    }

    fun cancelAllPrayerPushNotifications(context: Context) {
        try {
            val manager = NotificationManagerCompat.from(context)
            listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya", "Uji Notifikasi").forEach { name ->
                val id = BASE_PUSH_NOTIFICATION_ID + (name.hashCode() and 0x7F)
                manager.cancel(id)
            }
        } catch (_: Exception) {
        }
    }
}
