package com.example

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppLanguageManager
import com.example.data.MuezzinVoiceOption
import com.example.service.AdhanAlarmReceiver
import com.example.service.AdhanAlarmScheduler
import com.example.service.AdhanForegroundService
import com.example.service.AdhanNotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    @Config(qualifiers = "id")
    fun `read string from context in Indonesian`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kitab Hadits Pro", appName)
    }

    @Test
    @Config(qualifiers = "en")
    fun `read string from context in English`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Qibla Compass", appName)
    }

    @Test
    fun `AppLanguageManager returns supported languages and persists preference`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertTrue(AppLanguageManager.supportedLanguages.isNotEmpty())
        AppLanguageManager.setLanguage(context, AppLanguageManager.LANG_EN)
        assertEquals(AppLanguageManager.LANG_EN, AppLanguageManager.getCurrentLanguage(context))
        assertTrue(AppLanguageManager.isLanguageOnboardingCompleted(context))
    }

    @Test
    fun `scheduleDailyAdhanAlarms schedules exact alarms for all enabled prayers`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = shadowOf(alarmManager)

        val enabledPrayers = setOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        val scheduledList = AdhanAlarmScheduler.scheduleDailyAdhanAlarms(
            context = context,
            latitude = -6.1702,
            longitude = 106.8314,
            timezoneOffsetHours = 7.0,
            adhanAutoEnabled = true,
            enabledPrayers = enabledPrayers,
            muezzin = MuezzinVoiceOption.MAKKAH,
            locationLabel = "Jakarta"
        )

        assertEquals(5, scheduledList.size)
        assertTrue(shadowAlarmManager.scheduledAlarms.isNotEmpty())
    }

    @Test
    fun `AdhanAlarmReceiver posts local push notification when prayer alarm triggers`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(app).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)

        val notificationManager =
            app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowNotificationManager = shadowOf(notificationManager)

        val alarmIntent = Intent(app, AdhanAlarmReceiver::class.java).apply {
            action = AdhanAlarmReceiver.ACTION_TRIGGER_ADHAN_ALARM
            putExtra(AdhanForegroundService.EXTRA_PRAYER_NAME, "Maghrib")
            putExtra(AdhanForegroundService.EXTRA_PRAYER_TIME, "17:54")
            putExtra(AdhanForegroundService.EXTRA_LOCATION_LABEL, "Jakarta")
            putExtra(AdhanForegroundService.EXTRA_MUEZZIN_ID, MuezzinVoiceOption.MAKKAH.id)
        }

        val receiver = AdhanAlarmReceiver()
        receiver.onReceive(app, alarmIntent)

        val notifications = shadowNotificationManager.allNotifications
        assertTrue("Expected a local push notification to be posted", notifications.isNotEmpty())
        val channel = notificationManager.getNotificationChannel(AdhanNotificationHelper.PUSH_CHANNEL_ID)
        assertNotNull("Expected push notification channel to be created", channel)
    }
}
