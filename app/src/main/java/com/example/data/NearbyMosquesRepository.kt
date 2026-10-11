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
    val bearingDegrees: Float,
    val hasCustomName: Boolean = true
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
 * live GPS coordinates using OpenStreetMap Overpass API without requiring paid API keys.
 * Includes relation support, Indonesian name regex matching, and intelligent address heuristics.
 */
class NearbyMosquesRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val overpassEndpoints = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter",
        "https://maps.mail.ru/osm/tools/overpass/api/interpreter"
    )

    suspend fun fetchNearbyMosques(
        userLat: Double,
        userLon: Double,
        radiusMeters: Int = 6000
    ): Result<List<NearbyMosqueItem>> = withContext(Dispatchers.IO) {
        val query = """
            [out:json][timeout:15];
            (
              node["amenity"="place_of_worship"]["religion"="muslim"](around:$radiusMeters,$userLat,$userLon);
              way["amenity"="place_of_worship"]["religion"="muslim"](around:$radiusMeters,$userLat,$userLon);
              relation["amenity"="place_of_worship"]["religion"="muslim"](around:$radiusMeters,$userLat,$userLon);
              node["building"="mosque"](around:$radiusMeters,$userLat,$userLon);
              way["building"="mosque"](around:$radiusMeters,$userLat,$userLon);
              relation["building"="mosque"](around:$radiusMeters,$userLat,$userLon);
              node["name"~"Masjid|Musholla|Musala|Surau|Langgar", i](around:$radiusMeters,$userLat,$userLon);
              way["name"~"Masjid|Musholla|Musala|Surau|Langgar", i](around:$radiusMeters,$userLat,$userLon);
            );
            out center tags 60;
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
        val seenPositions = HashSet<String>()

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

            // Deduplicate identical places within ~40 meters
            val coordKey = "${(lat * 2500).toInt()}_${(lon * 2500).toInt()}"
            if (!seenPositions.add(coordKey)) continue

            val tags = el.optJSONObject("tags")
            val rawName = tags?.optString("name")?.trim().orEmpty().ifEmpty {
                tags?.optString("name:id")?.trim().orEmpty().ifEmpty {
                    tags?.optString("official_name")?.trim().orEmpty().ifEmpty {
                        tags?.optString("alt_name")?.trim().orEmpty().ifEmpty {
                            tags?.optString("short_name")?.trim().orEmpty().ifEmpty {
                                tags?.optString("loc_name")?.trim().orEmpty()
                            }
                        }
                    }
                }
            }

            val street = tags?.optString("addr:street")?.trim().orEmpty()
            val suburb = tags?.optString("addr:suburb")?.trim().orEmpty().ifEmpty {
                tags?.optString("addr:village")?.trim().orEmpty().ifEmpty {
                    tags?.optString("addr:hamlet")?.trim().orEmpty().ifEmpty {
                        tags?.optString("addr:city")?.trim().orEmpty()
                    }
                }
            }
            val operator = tags?.optString("operator")?.trim().orEmpty()

            val hasCustomName = rawName.isNotBlank()

            // Determine category
            val combinedText = "$rawName ${tags?.optString("building").orEmpty()} ${tags?.optString("description").orEmpty()}".lowercase()
            val category = if (combinedText.contains("mushola") ||
                combinedText.contains("musholla") ||
                combinedText.contains("musala") ||
                combinedText.contains("langgar") ||
                combinedText.contains("surau")
            ) {
                "Musholla"
            } else {
                "Masjid"
            }

            val displayName = if (hasCustomName) {
                rawName
            } else if (operator.isNotBlank()) {
                "$category $operator"
            } else if (street.isNotBlank()) {
                "$category Jl. $street"
            } else if (suburb.isNotBlank()) {
                "$category Wilayah $suburb"
            } else {
                "$category Lingkungan Sekitar"
            }

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
                    bearingDegrees = bearing,
                    hasCustomName = hasCustomName
                )
            )
        }

        // Prioritize named mosques first sorted by proximity, followed by unnamed/inferred ones
        return results
            .sortedWith(
                compareByDescending<NearbyMosqueItem> { it.hasCustomName }
                    .thenBy { it.distanceMeters }
            )
            .take(35)
    }
}
