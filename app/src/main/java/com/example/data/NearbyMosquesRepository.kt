package com.example.data

import com.example.domain.QiblaCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class NearbyMosqueItem(
    val id: String,
    val name: String,
    val addressOrArea: String,
    val categoryLabel: String, // "Masjid" or "Musholla"
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val bearingDegrees: Float
) {
    val formattedDistance: String
        get() = if (distanceMeters < 1000.0) {
            "${distanceMeters.roundToInt()} m"
        } else {
            String.format(Locale.US, "%.1f km", distanceMeters / 1000.0)
        }

    val cardinalDirection: String
        get() = QiblaCalculator.getIndonesianCardinalDirection(bearingDegrees)
}

/**
 * Fetches real-time nearby mosques and prayer rooms (Masjid & Musholla) around the user's
 * live GPS coordinates using the OpenStreetMap Overpass API (`amenity=place_of_worship` + `religion=muslim`).
 */
class NearbyMosquesRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val overpassEndpoints = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter"
    )

    suspend fun fetchNearbyMosques(
        userLat: Double,
        userLon: Double,
        radiusMeters: Int = 4500
    ): Result<List<NearbyMosqueItem>> = withContext(Dispatchers.IO) {
        val query = """
            [out:json][timeout:14];
            (
              node["amenity"="place_of_worship"]["religion"="muslim"](around:$radiusMeters,$userLat,$userLon);
              way["amenity"="place_of_worship"]["religion"="muslim"](around:$radiusMeters,$userLat,$userLon);
              node["building"="mosque"](around:$radiusMeters,$userLat,$userLon);
              way["building"="mosque"](around:$radiusMeters,$userLat,$userLon);
            );
            out center 35;
        """.trimIndent()

        var lastException: Exception? = null

        for (endpoint in overpassEndpoints) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = "$endpoint?data=$encodedQuery"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "KompasKiblatAndroidApp/1.0")
                    .get()
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("HTTP ${response.code}")
                    }
                    val bodyStr = response.body?.string().orEmpty()
                    val parsedList = parseOverpassJson(bodyStr, userLat, userLon)
                    return@withContext Result.success(parsedList)
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        Result.failure(lastException ?: Exception("Gagal memuat data masjid terdekat"))
    }

    private fun parseOverpassJson(
        jsonString: String,
        userLat: Double,
        userLon: Double
    ): List<NearbyMosqueItem> {
        val root = JSONObject(jsonString)
        val elements = root.optJSONArray("elements") ?: return emptyList()
        val results = mutableListOf<NearbyMosqueItem>()
        val seenNamesAndCoords = HashSet<String>()

        for (i in 0 until elements.length()) {
            val el = elements.optJSONObject(i) ?: continue
            val id = el.optLong("id", i.toLong()).toString()

            val lat = if (el.has("lat")) {
                el.optDouble("lat", Double.NaN)
            } else {
                el.optJSONObject("center")?.optDouble("lat", Double.NaN) ?: Double.NaN
            }

            val lon = if (el.has("lon")) {
                el.optDouble("lon", Double.NaN)
            } else {
                el.optJSONObject("center")?.optDouble("lon", Double.NaN) ?: Double.NaN
            }

            if (lat.isNaN() || lon.isNaN()) continue

            val tags = el.optJSONObject("tags")
            val rawName = tags?.optString("name")?.trim()
                ?: tags?.optString("name:id")?.trim()
                ?: tags?.optString("alt_name")?.trim()
                ?: ""

            val displayName = if (rawName.isNotEmpty()) {
                rawName
            } else {
                "Masjid / Musholla Setempat"
            }

            // Deduplicate identical places
            val dedupeKey = "${displayName.lowercase()}_${(lat * 1000).toInt()}_${(lon * 1000).toInt()}"
            if (!seenNamesAndCoords.add(dedupeKey)) continue

            val lowerName = displayName.lowercase()
            val category = if (lowerName.contains("mushola") ||
                lowerName.contains("musholla") ||
                lowerName.contains("musala") ||
                lowerName.contains("langgar") ||
                lowerName.contains("surau")
            ) {
                "Musholla"
            } else {
                "Masjid"
            }

            val street = tags?.optString("addr:street")?.trim().orEmpty()
            val suburb = tags?.optString("addr:suburb")?.trim()
                ?: tags?.optString("addr:village")?.trim()
                ?: tags?.optString("addr:city")?.trim()
                ?: ""
            val fullAddr = tags?.optString("addr:full")?.trim().orEmpty()

            val addressLabel = when {
                fullAddr.isNotEmpty() -> fullAddr
                street.isNotEmpty() && suburb.isNotEmpty() -> "$street, $suburb"
                street.isNotEmpty() -> street
                suburb.isNotEmpty() -> suburb
                else -> String.format(Locale.US, "Koordinat %.4f°, %.4f°", lat, lon)
            }

            val distMeters = QiblaCalculator.calculateDistanceBetweenPointsMeters(
                lat1 = userLat,
                lon1 = userLon,
                lat2 = lat,
                lon2 = lon
            )
            val bearing = QiblaCalculator.calculateBearingBetweenPoints(
                lat1 = userLat,
                lon1 = userLon,
                lat2 = lat,
                lon2 = lon
            )

            results.add(
                NearbyMosqueItem(
                    id = id,
                    name = displayName,
                    addressOrArea = addressLabel,
                    categoryLabel = category,
                    latitude = lat,
                    longitude = lon,
                    distanceMeters = distMeters,
                    bearingDegrees = bearing
                )
            )
        }

        // Prioritize named mosques and sort strictly by shortest distance from user's GPS
        return results
            .sortedWith(
                compareBy<NearbyMosqueItem> { it.distanceMeters }
                    .thenBy { it.name == "Masjid / Musholla Setempat" }
            )
            .take(25)
    }
}
