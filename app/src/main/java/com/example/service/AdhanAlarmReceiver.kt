package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MuezzinVoiceOption
import com.example.data.QiblaPreferencesRepository
import com.example.widget.QiblaPrayerWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receives exact prayer time alarms (`ACTION_TRIGGER_ADHAN_ALARM`) from `AlarmManager`,
 * dispatches the local push notification via [AdhanNotificationHelper], starts the foreground
 * Adhan audio player ([AdhanForegroundService]), and reschedules upcoming prayer alarms.
 */
class AdhanAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        if (action == ACTION_TRIGGER_ADHAN_ALARM || action == ACTION_PLAY_ADHAN_FROM_NOTIFICATION) {
            val prayerName = intent.getStringExtra(AdhanForegroundService.EXTRA_PRAYER_NAME) ?: "Sholat"
            val prayerTimeFormatted = intent.getStringExtra(AdhanForegroundService.EXTRA_PRAYER_TIME).orEmpty()
            val locationLabel = intent.getStringExtra(AdhanForegroundService.EXTRA_LOCATION_LABEL).orEmpty()
            val muezzinId = intent.getStringExtra(AdhanForegroundService.EXTRA_MUEZZIN_ID)
            val isPreview = intent.getBooleanExtra(AdhanForegroundService.EXTRA_IS_PREVIEW, false)
            val muezzin = MuezzinVoiceOption.fromId(muezzinId)

            if (action == ACTION_TRIGGER_ADHAN_ALARM) {
                // 1. Trigger the local push notification for the scheduled prayer time
                AdhanNotificationHelper.showPrayerTimePushNotification(
                    context = context,
                    prayerName = prayerName,
                    prayerTimeFormatted = prayerTimeFormatted,
                    locationLabel = locationLabel,
                    muezzin = muezzin,
                    isTestNotification = isPreview
                )
            }

            // 2. Start the Foreground Service to play the Adzan audio immediately
            AdhanForegroundService.startAdhan(
                context = context,
                prayerName = prayerName,
                muezzin = muezzin,
                isPreview = isPreview
            )
        }

        // Reschedule upcoming alarms (both after an alarm fires and on BOOT_COMPLETED / TIME_SET / TIMEZONE_CHANGED)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = QiblaPreferencesRepository(context)
                val prefs = repo.preferencesFlow.first()
                AdhanAlarmScheduler.scheduleDailyAdhanAlarms(
                    context = context,
                    latitude = prefs.latitude,
                    longitude = prefs.longitude,
                    timezoneOffsetHours = prefs.timezoneOffsetHours,
                    adhanAutoEnabled = prefs.adhanAutoEnabled,
                    enabledPrayers = prefs.enabledAdhanPrayers,
                    muezzin = prefs.selectedMuezzin,
                    locationLabel = prefs.locationLabel
                )
                QiblaPrayerWidgetProvider.updateAllWidgets(context)
            } catch (e: Exception) {
                // Ignore errors during background reschedule
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TRIGGER_ADHAN_ALARM = "com.example.ACTION_TRIGGER_ADHAN_ALARM"
        const val ACTION_PLAY_ADHAN_FROM_NOTIFICATION = "com.example.ACTION_PLAY_ADHAN_FROM_NOTIFICATION"
    }
}
