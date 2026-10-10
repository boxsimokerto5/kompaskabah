package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class GpsCoordinates(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val addressLabel: String? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

/**
 * Provides real-time GPS coordinates via Google Play Services FusedLocationProviderClient
 * and Android LocationManager fallback, plus reverse geocoding for human-readable city names.
 */
class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocationOnce(): GpsCoordinates? {
        if (!hasLocationPermission()) return null

        // First try FusedLocationProviderClient getCurrentLocation
        val fusedLocation: Location? = try {
            suspendCancellableCoroutine { cont ->
                val tokenSource = CancellationTokenSource()
                cont.invokeOnCancellation { tokenSource.cancel() }
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    tokenSource.token
                ).addOnSuccessListener { loc ->
                    if (cont.isActive) cont.resume(loc)
                }.addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
            }
        } catch (e: Exception) {
            null
        }

        val resolvedLocation = fusedLocation ?: getLastKnownFallbackLocation() ?: return null
        val addressName = resolveCityName(resolvedLocation.latitude, resolvedLocation.longitude)

        return GpsCoordinates(
            latitude = resolvedLocation.latitude,
            longitude = resolvedLocation.longitude,
            altitudeMeters = resolvedLocation.altitude,
            accuracyMeters = resolvedLocation.accuracy,
            addressLabel = addressName
        )
    }

    @SuppressLint("MissingPermission")
    fun observeLocationUpdates(): Flow<GpsCoordinates> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 8_000L)
            .setMinUpdateIntervalMillis(4_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                trySend(
                    GpsCoordinates(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        altitudeMeters = loc.altitude,
                        accuracyMeters = loc.accuracy,
                        addressLabel = null
                    )
                )
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            // Fallback to LocationManager if Play Services is unavailable
        }

        // Also attach a lightweight LocationManager listener as a backup
        val managerListener = LocationListener { loc ->
            trySend(
                GpsCoordinates(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    altitudeMeters = loc.altitude,
                    accuracyMeters = loc.accuracy,
                    addressLabel = null
                )
            )
        }

        try {
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                locationManager.requestLocationUpdates(
                    provider,
                    10_000L,
                    10f,
                    managerListener,
                    Looper.getMainLooper()
                )
            }
        } catch (e: Exception) {
            // Ignore if provider not supported
        }

        awaitClose {
            try {
                fusedLocationClient.removeLocationUpdates(callback)
                locationManager.removeUpdates(managerListener)
            } catch (e: Exception) {
                // Ignore cleanup exceptions
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownFallbackLocation(): Location? {
        return try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            bestLocation
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resolveCityName(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext null
            val geocoder = Geocoder(context, Locale("id", "ID"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val first = addresses?.firstOrNull() ?: return@withContext null
            val city = first.subAdminArea ?: first.locality ?: first.adminArea
            val country = first.countryName
            when {
                !city.isNullOrBlank() && !country.isNullOrBlank() -> "$city, $country"
                !city.isNullOrBlank() -> city
                !country.isNullOrBlank() -> country
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
