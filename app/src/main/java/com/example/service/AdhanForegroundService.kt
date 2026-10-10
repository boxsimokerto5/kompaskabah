package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.MuezzinVoiceOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdhanPlaybackState(
    val isPlaying: Boolean = false,
    val prayerName: String = "",
    val muezzinTitle: String = "",
    val isPreview: Boolean = false
)

/**
 * Foreground MediaPlayback Service that plays melodious Adhan audio in the foreground
 * with a high-priority heads-up notification, Stop button, and WakeLock so Adhan sounds
 * clearly even if the phone screen is off or the user is in another app.
 */
class AdhanForegroundService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_ADHAN) {
            stopAdhanAndService()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "Waktu Sholat"
        val muezzinId = intent?.getStringExtra(EXTRA_MUEZZIN_ID) ?: MuezzinVoiceOption.MAKKAH.id
        val isPreview = intent?.getBooleanExtra(EXTRA_IS_PREVIEW, false) ?: false
        val muezzin = MuezzinVoiceOption.fromId(muezzinId)

        startForegroundWithNotification(prayerName, muezzin, isPreview)
        startAdhanAudioPlayback(prayerName, muezzin, isPreview)

        return START_NOT_STICKY
    }

    private fun startForegroundWithNotification(
        prayerName: String,
        muezzin: MuezzinVoiceOption,
        isPreview: Boolean
    ) {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            100,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AdhanForegroundService::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            101,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isPreview) {
            "Mengumandangkan Adzan ($prayerName)"
        } else {
            "Waktu Sholat $prayerName Telah Tiba"
        }

        val subtitle = "${muezzin.title} • ${muezzin.subtitle}"

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_mosque_notification)
            .setColor(0xFF0A4D3C.toInt())
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$subtitle\nHayya 'alash-shalaah • Mari menunaikan sholat tepat waktu."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openAppPendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Hentikan Adzan",
                stopPendingIntent
            )
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startAdhanAudioPlayback(
        prayerName: String,
        muezzin: MuezzinVoiceOption,
        isPreview: Boolean
    ) {
        try {
            releasePlayer()
            acquireWakeLock()

            val afd = resources.openRawResourceFd(muezzin.rawResId) ?: return
            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            player.setOnCompletionListener {
                stopAdhanAndService()
            }
            player.setOnErrorListener { _, _, _ ->
                stopAdhanAndService()
                true
            }

            player.prepare()
            player.start()
            mediaPlayer = player

            _playbackState.value = AdhanPlaybackState(
                isPlaying = true,
                prayerName = prayerName,
                muezzinTitle = muezzin.title,
                isPreview = isPreview
            )
        } catch (e: Exception) {
            stopAdhanAndService()
        }
    }

    private fun acquireWakeLock() {
        try {
            if (wakeLock?.isHeld == true) return
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "KompasKiblat::AdhanWakeLock"
            ).apply {
                acquire(4 * 60 * 1000L) // Max 4 minutes for full Adhan completion
            }
        } catch (e: Exception) {
            // Ignore wake lock errors
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        } finally {
            mediaPlayer = null
        }

        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            wakeLock = null
        }
    }

    private fun stopAdhanAndService() {
        releasePlayer()
        _playbackState.value = AdhanPlaybackState(isPlaying = false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        releasePlayer()
        _playbackState.value = AdhanPlaybackState(isPlaying = false)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Panggilan Adzan Waktu Sholat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi latar depan saat berkumandang suara Adzan waktu sholat"
                setSound(null, null) // Sound is handled directly by MediaPlayer in the Foreground Service
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "adhan_foreground_channel"
        private const val NOTIFICATION_ID = 7001

        const val ACTION_START_ADHAN = "com.example.ACTION_START_ADHAN"
        const val ACTION_STOP_ADHAN = "com.example.ACTION_STOP_ADHAN"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_LOCATION_LABEL = "extra_location_label"
        const val EXTRA_MUEZZIN_ID = "extra_muezzin_id"
        const val EXTRA_IS_PREVIEW = "extra_is_preview"

        private val _playbackState = MutableStateFlow(AdhanPlaybackState())
        val playbackState: StateFlow<AdhanPlaybackState> = _playbackState.asStateFlow()

        fun startAdhan(
            context: Context,
            prayerName: String,
            muezzin: MuezzinVoiceOption,
            isPreview: Boolean = false
        ) {
            val intent = Intent(context, AdhanForegroundService::class.java).apply {
                action = ACTION_START_ADHAN
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_MUEZZIN_ID, muezzin.id)
                putExtra(EXTRA_IS_PREVIEW, isPreview)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAdhan(context: Context) {
            val intent = Intent(context, AdhanForegroundService::class.java).apply {
                action = ACTION_STOP_ADHAN
            }
            context.startService(intent)
        }
    }
}
