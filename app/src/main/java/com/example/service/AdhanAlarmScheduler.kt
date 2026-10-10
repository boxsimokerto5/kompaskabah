package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.MainActivity
import com.example.data.MuezzinVoiceOption
import com.example.domain.PrayerTimeItem
import com.example.domain.QiblaCalculator
import com.example.widget.QiblaPrayerWidgetProvider
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

data class ScheduledPrayerAlarmSummary(
    val prayerName: String,
    val formattedTime: String,
    val triggerAtMillis: Long,
    val isToday: Boolean
)

/**
 * Schedules exact system alarms (`setAlarmClock` / `setExactAndAllowWhileIdle`) for each enabled
 * obligatory prayer time (Subuh, Dzuhur, Ashar, Maghrib, Isya) so a local push notification is
 * triggered and Adhan playback starts at the exact minute of each scheduled prayer.
 */
object AdhanAlarmScheduler {

    val OBLIGATORY_PRAYER_NAMES = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

    fun scheduleDailyAdhanAlarms(
        context: Context,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        adhanAutoEnabled: Boolean,
        enabledPrayers: Set<String>,
        muezzin: MuezzinVoiceOption,
        locationLabel: String = ""
    ): List<ScheduledPrayerAlarmSummary> {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel any previously scheduled alarms first
        for ((index, prayerName) in OBLIGATORY_PRAYER_NAMES.withIndex()) {
            val cancelIntent = createAlarmPendingIntent(
                context = context,
                requestCode = 5000 + index,
                prayerName = prayerName,
                prayerTimeFormatted = "",
                locationLabel = locationLabel,
                muezzinId = muezzin.id
            )
            alarmManager.cancel(cancelIntent)
        }

        if (!adhanAutoEnabled || enabledPrayers.isEmpty()) return emptyList()

        val nowCal = Calendar.getInstance()
        val nowMillis = nowCal.timeInMillis

        // Compute today's and tomorrow's prayer times so that any prayer already passed today is scheduled for tomorrow
        val todayPrayers = QiblaCalculator.calculateDailyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = timezoneOffsetHours,
            year = nowCal.get(Calendar.YEAR),
            month = nowCal.get(Calendar.MONTH) + 1,
            day = nowCal.get(Calendar.DAY_OF_MONTH)
        )

        val tomorrowCal = (nowCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowPrayers = QiblaCalculator.calculateDailyPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = timezoneOffsetHours,
            year = tomorrowCal.get(Calendar.YEAR),
            month = tomorrowCal.get(Calendar.MONTH) + 1,
            day = tomorrowCal.get(Calendar.DAY_OF_MONTH)
        )

        val scheduledList = mutableListOf<ScheduledPrayerAlarmSummary>()

        for ((index, prayerName) in OBLIGATORY_PRAYER_NAMES.withIndex()) {
            if (!enabledPrayers.contains(prayerName)) continue

            val todayItem = todayPrayers.firstOrNull { it.nameId == prayerName } ?: continue
            val tomorrowItem = tomorrowPrayers.firstOrNull { it.nameId == prayerName } ?: continue

            val todayTargetMillis = buildCalendarForPrayer(nowCal, todayItem).timeInMillis
            val isTodayAlarm = todayTargetMillis > nowMillis + 15_000L
            val chosenItem = if (isTodayAlarm) todayItem else tomorrowItem
            val targetMillis = if (isTodayAlarm) {
                todayTargetMillis
            } else {
                buildCalendarForPrayer(tomorrowCal, tomorrowItem).timeInMillis
            }

            val pendingIntent = createAlarmPendingIntent(
                context = context,
                requestCode = 5000 + index,
                prayerName = prayerName,
                prayerTimeFormatted = chosenItem.formattedTime,
                locationLabel = locationLabel,
                muezzinId = muezzin.id
            )

            scheduleExactAlarmSafely(
                context = context,
                alarmManager = alarmManager,
                triggerAtMillis = targetMillis,
                operation = pendingIntent
            )

            scheduledList.add(
                ScheduledPrayerAlarmSummary(
                    prayerName = prayerName,
                    formattedTime = chosenItem.formattedTime,
                    triggerAtMillis = targetMillis,
                    isToday = isTodayAlarm
                )
            )
        }

        return scheduledList.sortedBy { it.triggerAtMillis }
    }

    /**
     * Schedules an immediate or short-delay exact alarm via `AlarmManager` to verify and demonstrate
     * the push notification + Adzan playback pipeline.
     */
    fun triggerTestAlarmNotificationNow(
        context: Context,
        prayerName: String,
        prayerTimeFormatted: String,
        locationLabel: String,
        muezzin: MuezzinVoiceOption
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerIntent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            action = AdhanAlarmReceiver.ACTION_TRIGGER_ADHAN_ALARM
            putExtra(AdhanForegroundService.EXTRA_PRAYER_NAME, prayerName)
            putExtra(AdhanForegroundService.EXTRA_PRAYER_TIME, prayerTimeFormatted)
            putExtra(AdhanForegroundService.EXTRA_LOCATION_LABEL, locationLabel)
            putExtra(AdhanForegroundService.EXTRA_MUEZZIN_ID, muezzin.id)
            putExtra(AdhanForegroundService.EXTRA_IS_PREVIEW, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            5999,
            triggerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + 400L
        scheduleExactAlarmSafely(
            context = context,
            alarmManager = alarmManager,
            triggerAtMillis = triggerAt,
            operation = pendingIntent
        )
    }

    private fun buildCalendarForPrayer(dayBase: Calendar, item: PrayerTimeItem): Calendar {
        val hNorm = ((item.decimalHours + 0.5 / 60.0) % 24.0 + 24.0) % 24.0
        val hour = floor(hNorm).toInt()
        val minute = floor((hNorm - hour) * 60.0).toInt().coerceIn(0, 59)

        return (dayBase.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun createAlarmPendingIntent(
        context: Context,
        requestCode: Int,
        prayerName: String,
        prayerTimeFormatted: String,
        locationLabel: String,
        muezzinId: String
    ): PendingIntent {
        val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            action = AdhanAlarmReceiver.ACTION_TRIGGER_ADHAN_ALARM
            putExtra(AdhanForegroundService.EXTRA_PRAYER_NAME, prayerName)
            putExtra(AdhanForegroundService.EXTRA_PRAYER_TIME, prayerTimeFormatted)
            putExtra(AdhanForegroundService.EXTRA_LOCATION_LABEL, locationLabel)
            putExtra(AdhanForegroundService.EXTRA_MUEZZIN_ID, muezzinId)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleExactAlarmSafely(
        context: Context,
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        operation: PendingIntent
    ) {
        try {
            val showIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(QiblaPrayerWidgetProvider.EXTRA_OPEN_TAB, QiblaPrayerWidgetProvider.TAB_PRAYER_TIMES)
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                5800,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    val clockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, operation)
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        operation
                    )
                }
            } else {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
                alarmManager.setAlarmClock(clockInfo, operation)
            }
        } catch (e: SecurityException) {
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    operation
                )
            } catch (_: Exception) {
            }
        } catch (e: Exception) {
            // Ignore unexpected scheduling errors on test environments
        }
    }
}
