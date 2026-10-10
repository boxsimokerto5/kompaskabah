package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.LocationTracker
import com.example.data.QiblaPreferencesRepository
import com.example.domain.QiblaCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Home Screen AppWidgetProvider that displays:
 * 1. Next obligatory prayer name, time, and remaining countdown badge.
 * 2. Real-time Qibla True North bearing degree, cardinal direction code, and distance to Kaaba (km).
 * 3. Compact 5-prayer daily schedule strip (Subuh, Dzuhur, Ashar, Maghrib, Isya) with the upcoming prayer highlighted.
 * 4. One-tap refresh button (updates GPS if permitted and recalculates prayer countdown) and deep-links to Compass or Prayer tabs.
 */
class QiblaPrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateWidgetsInternal(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetIds = appWidgetIds,
                    attemptGpsRefresh = false
                )
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action ?: return

        if (action == ACTION_REFRESH_WIDGET ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_DATE_CHANGED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val manager = AppWidgetManager.getInstance(context)
                    val ids = manager.getAppWidgetIds(
                        ComponentName(context, QiblaPrayerWidgetProvider::class.java)
                    )
                    if (ids.isNotEmpty()) {
                        updateWidgetsInternal(
                            context = context,
                            appWidgetManager = manager,
                            appWidgetIds = ids,
                            attemptGpsRefresh = (action == ACTION_REFRESH_WIDGET)
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_QIBLA_WIDGET"
        const val EXTRA_OPEN_TAB = "extra_open_tab"
        const val TAB_COMPASS = "COMPASS"
        const val TAB_PRAYER_TIMES = "PRAYER_TIMES"

        /**
         * Triggers an immediate update of all placed Home Screen widgets.
         */
        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val manager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, QiblaPrayerWidgetProvider::class.java)
                    val ids = manager.getAppWidgetIds(componentName)
                    if (ids.isNotEmpty()) {
                        updateWidgetsInternal(
                            context = context,
                            appWidgetManager = manager,
                            appWidgetIds = ids,
                            attemptGpsRefresh = false
                        )
                    }
                } catch (_: Exception) {
                }
            }
        }

        /**
         * Returns true if the device Launcher supports pinning this App Widget directly from the app.
         */
        fun isPinWidgetSupported(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val appWidgetManager = AppWidgetManager.getInstance(context)
            return appWidgetManager.isRequestPinAppWidgetSupported
        }

        /**
         * Requests the Android Launcher to pin the Qibla & Prayer Schedule widget to the Home Screen.
         */
        fun requestPinWidgetToHomeScreen(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            return try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                if (!appWidgetManager.isRequestPinAppWidgetSupported) return false
                val providerComponent = ComponentName(context, QiblaPrayerWidgetProvider::class.java)
                appWidgetManager.requestPinAppWidget(providerComponent, null, null)
            } catch (_: Exception) {
                false
            }
        }

        private suspend fun updateWidgetsInternal(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
            attemptGpsRefresh: Boolean
        ) {
            val prefsRepo = QiblaPreferencesRepository(context)
            var prefs = prefsRepo.preferencesFlow.first()

            if (attemptGpsRefresh) {
                val locationTracker = LocationTracker(context)
                if (locationTracker.hasLocationPermission()) {
                    val liveCoords = locationTracker.fetchCurrentLocationOnce()
                    if (liveCoords != null) {
                        val tzHours = TimeZone.getDefault().rawOffset / 3600000.0
                        val fallbackLabel = String.format(
                            Locale.US,
                            "GPS (%.4f°, %.4f°)",
                            liveCoords.latitude,
                            liveCoords.longitude
                        )
                        val resolvedLabel = liveCoords.addressLabel ?: fallbackLabel
                        prefsRepo.saveLocation(
                            latitude = liveCoords.latitude,
                            longitude = liveCoords.longitude,
                            label = resolvedLabel,
                            timezoneOffsetHours = tzHours,
                            isLiveGps = true
                        )
                        prefs = prefsRepo.preferencesFlow.first()
                    }
                }
            }

            val lat = prefs.latitude
            val lon = prefs.longitude
            val tzOffset = prefs.timezoneOffsetHours
            val locationLabel = prefs.locationLabel.ifBlank { "Lokasi GPS Otomatis" }

            val qiblaBearing = QiblaCalculator.calculateQiblaBearing(lat, lon).toFloat()
            val cardinalShort = QiblaCalculator.getShortCardinalCode(qiblaBearing)
            val distanceKm = QiblaCalculator.calculateDistanceToKaabaKm(lat, lon)

            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val prayers = QiblaCalculator.calculateDailyPrayerTimes(
                latitude = lat,
                longitude = lon,
                timezoneOffsetHours = tzOffset,
                year = year,
                month = month,
                day = day
            )

            val currentDecimalHour = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0
            val obligatoryPrayers = prayers.filter { it.isObligatory }
            val nextPrayer = obligatoryPrayers.firstOrNull { it.decimalHours > currentDecimalHour }
                ?: obligatoryPrayers.firstOrNull()

            val countdownText = if (nextPrayer != null) {
                var diffHours = nextPrayer.decimalHours - currentDecimalHour
                if (diffHours < 0) diffHours += 24.0
                val h = diffHours.toInt()
                val m = ((diffHours - h) * 60).toInt().coerceIn(0, 59)
                if (h > 0) "${h}j ${m}m lagi" else "${m}m lagi"
            } else {
                "Jadwal Aktif"
            }

            val subuhTime = prayers.firstOrNull { it.nameId == "Subuh" }?.formattedTime ?: "04:35"
            val dzuhurTime = prayers.firstOrNull { it.nameId == "Dzuhur" }?.formattedTime ?: "11:52"
            val asharTime = prayers.firstOrNull { it.nameId == "Ashar" }?.formattedTime ?: "15:11"
            val maghribTime = prayers.firstOrNull { it.nameId == "Maghrib" }?.formattedTime ?: "17:54"
            val isyaTime = prayers.firstOrNull { it.nameId == "Isya" }?.formattedTime ?: "19:04"

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_qibla_prayer)

                views.setTextViewText(R.id.widget_location_text, locationLabel)
                views.setTextViewText(R.id.widget_countdown_badge, countdownText)
                views.setTextViewText(R.id.widget_next_prayer_name, nextPrayer?.nameId ?: "Subuh")
                views.setTextViewText(R.id.widget_next_prayer_time, nextPrayer?.formattedTime ?: subuhTime)

                views.setTextViewText(
                    R.id.widget_qibla_degree,
                    String.format(Locale.US, "%.1f° %s", qiblaBearing, cardinalShort)
                )
                views.setTextViewText(
                    R.id.widget_qibla_distance,
                    String.format(Locale.US, "%,.0f km ke Ka'bah", distanceKm)
                )

                views.setTextViewText(R.id.widget_time_subuh, subuhTime)
                views.setTextViewText(R.id.widget_time_dzuhur, dzuhurTime)
                views.setTextViewText(R.id.widget_time_ashar, asharTime)
                views.setTextViewText(R.id.widget_time_maghrib, maghribTime)
                views.setTextViewText(R.id.widget_time_isya, isyaTime)

                // Highlight the upcoming prayer time in radiant gold in the bottom 5-prayer strip
                val activeGold = 0xFFFFDF73.toInt()
                val normalWhite = 0xFFFFFFFF.toInt()
                val nextName = nextPrayer?.nameId ?: ""

                views.setTextColor(R.id.widget_time_subuh, if (nextName == "Subuh") activeGold else normalWhite)
                views.setTextColor(R.id.widget_time_dzuhur, if (nextName == "Dzuhur") activeGold else normalWhite)
                views.setTextColor(R.id.widget_time_ashar, if (nextName == "Ashar") activeGold else normalWhite)
                views.setTextColor(R.id.widget_time_maghrib, if (nextName == "Maghrib") activeGold else normalWhite)
                views.setTextColor(R.id.widget_time_isya, if (nextName == "Isya") activeGold else normalWhite)

                // PendingIntent 1: Tap Prayer Section or Daily Strip -> Open App on Prayer Times tab
                val openPrayerIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(EXTRA_OPEN_TAB, TAB_PRAYER_TIMES)
                }
                val openPrayerPendingIntent = PendingIntent.getActivity(
                    context,
                    7001,
                    openPrayerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_prayer_section, openPrayerPendingIntent)
                views.setOnClickPendingIntent(R.id.widget_daily_strip, openPrayerPendingIntent)

                // PendingIntent 2: Tap Qibla Summary Box or Root -> Open App on Qibla Compass tab
                val openCompassIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(EXTRA_OPEN_TAB, TAB_COMPASS)
                }
                val openCompassPendingIntent = PendingIntent.getActivity(
                    context,
                    7002,
                    openCompassIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_qibla_section, openCompassPendingIntent)
                views.setOnClickPendingIntent(R.id.widget_root_container, openCompassPendingIntent)

                // PendingIntent 3: Tap Refresh Button -> Refresh GPS & recalculate widget immediately
                val refreshIntent = Intent(context, QiblaPrayerWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                }
                val refreshPendingIntent = PendingIntent.getBroadcast(
                    context,
                    7003,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
