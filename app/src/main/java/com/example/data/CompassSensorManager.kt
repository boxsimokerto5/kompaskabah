package com.example.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.domain.QiblaCalculator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class SensorAccuracyLevel {
    HIGH,
    MEDIUM,
    LOW,
    UNRELIABLE
}

data class CompassSensorReading(
    val magneticAzimuthDeg: Float = 0f,
    val pitchDeg: Float = 0f,
    val rollDeg: Float = 0f,
    val magneticFieldStrengthUt: Float = 45f,
    val accuracy: SensorAccuracyLevel = SensorAccuracyLevel.HIGH,
    val sensorSourceName: String = "Fusi Sensor + Filter Low-Pass Adaptif",
    val isHardwareSensorAvailable: Boolean = true
) {
    val hasMagneticInterference: Boolean
        get() = isHardwareSensorAvailable && (magneticFieldStrengthUt < 18f || magneticFieldStrengthUt > 85f)

    val isDeviceTilted: Boolean
        get() = kotlin.math.abs(pitchDeg) > 32f || kotlin.math.abs(rollDeg) > 32f
}

/**
 * Manages real-time Android hardware sensors (`TYPE_ROTATION_VECTOR` or fallback
 * `TYPE_ACCELEROMETER` + `TYPE_MAGNETIC_FIELD`) with a two-stage Low-Pass Filter &
 * Linear Interpolation pipeline:
 * 1. Raw 3-axis vector Low-Pass Filter on Magnetometer (`Bx, By, Bz`) and Accelerometer (`Ax, Ay, Az`).
 * 2. Circular Unit-Vector EMA (`sin`/`cos` accumulator) + Adaptive Shortest-Arc Linear Interpolation
 *    with deadband tremor suppression so the Qibla needle glides without jitter or shake.
 */
class CompassSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
    private val accelerometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val hasHardwareCompass: Boolean =
        rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)

    fun observeCompassReadings(): Flow<CompassSensorReading> = callbackFlow {
        if (!hasHardwareCompass) {
            trySend(
                CompassSensorReading(
                    magneticAzimuthDeg = 0f,
                    pitchDeg = 0f,
                    rollDeg = 0f,
                    magneticFieldStrengthUt = 44.5f,
                    accuracy = SensorAccuracyLevel.HIGH,
                    sensorSourceName = "Mode Putar Manual / Simulasi",
                    isHardwareSensorAvailable = false
                )
            )
            awaitClose { }
            return@callbackFlow
        }

        // Stage 1: Raw 3-axis Low-Pass filtered sensor vectors
        var filteredGravity = FloatArray(3)
        var filteredGeomagnetic = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        var hasGravity = false
        var hasGeomagnetic = false
        var isFirstReading = true

        // Stage 2: Circular unit-vector EMA + adaptive shortest-arc linear interpolation state
        var smoothedSin = 0.0
        var smoothedCos = 1.0
        var stabilizedAzimuthDeg = 0f
        var smoothedPitchDeg = 0f
        var smoothedRollDeg = 0f
        var currentFieldUt = 45f
        var currentAccuracy = SensorAccuracyLevel.HIGH
        var lastTimestampNanos = 0L

        // Low-pass filter alpha for raw 3D magnetometer & accelerometer axes (strong noise rejection)
        val rawVectorAlpha = 0.08f
        // Tilt (pitch/roll) low-pass smoothing factor for spirit level bubble
        val tiltAlpha = 0.12f

        val sourceLabel = if (rotationVectorSensor != null) {
            "Rotation Vector + Filter Low-Pass Adaptif"
        } else {
            "Magnetometer + Filter Low-Pass Linear"
        }

        fun processAndEmitOrientation(
            rawAzimuthDeg: Float,
            rawPitchDeg: Float,
            rawRollDeg: Float,
            eventTimestampNanos: Long
        ) {
            val dtSeconds = if (lastTimestampNanos == 0L) {
                0.02f
            } else {
                ((eventTimestampNanos - lastTimestampNanos) * 1e-9f).coerceIn(0.004f, 0.25f)
            }
            lastTimestampNanos = eventTimestampNanos

            val rawRad = Math.toRadians(rawAzimuthDeg.toDouble())

            if (isFirstReading) {
                isFirstReading = false
                smoothedSin = sin(rawRad)
                smoothedCos = cos(rawRad)
                stabilizedAzimuthDeg = rawAzimuthDeg
                smoothedPitchDeg = rawPitchDeg
                smoothedRollDeg = rawRollDeg
            } else {
                // First pass: Circular trigonometric low-pass filter on unit circle (sin, cos)
                val vectorSmoothingAlpha = 0.18
                smoothedSin += vectorSmoothingAlpha * (sin(rawRad) - smoothedSin)
                smoothedCos += vectorSmoothingAlpha * (cos(rawRad) - smoothedCos)
                val vectorFilteredDeg =
                    ((Math.toDegrees(kotlin.math.atan2(smoothedSin, smoothedCos)).toFloat()) + 360f) % 360f

                // Second pass: Adaptive shortest-arc Linear Interpolation with micro-jitter deadband
                stabilizedAzimuthDeg = QiblaCalculator.adaptiveLowPassFilterAngle(
                    previousAngle = stabilizedAzimuthDeg,
                    rawTargetAngle = vectorFilteredDeg,
                    dtSeconds = dtSeconds
                )

                // Smooth pitch and roll with linear interpolation
                smoothedPitchDeg += tiltAlpha * (rawPitchDeg - smoothedPitchDeg)
                smoothedRollDeg += tiltAlpha * (rawRollDeg - smoothedRollDeg)
            }

            trySend(
                CompassSensorReading(
                    magneticAzimuthDeg = stabilizedAzimuthDeg,
                    pitchDeg = smoothedPitchDeg,
                    rollDeg = smoothedRollDeg,
                    magneticFieldStrengthUt = currentFieldUt,
                    accuracy = currentAccuracy,
                    sensorSourceName = sourceLabel,
                    isHardwareSensorAvailable = true
                )
            )
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        // Apply low-pass filter directly to raw 3-axis magnetometer readings (Bx, By, Bz)
                        if (!hasGeomagnetic) {
                            filteredGeomagnetic = event.values.copyOf()
                            hasGeomagnetic = true
                        } else {
                            filteredGeomagnetic = QiblaCalculator.applyVectorLowPassFilter(
                                input = event.values,
                                output = filteredGeomagnetic,
                                alpha = rawVectorAlpha
                            )
                        }

                        val bx = filteredGeomagnetic[0]
                        val by = filteredGeomagnetic[1]
                        val bz = filteredGeomagnetic[2]
                        val fieldMag = sqrt(bx * bx + by * by + bz * bz)
                        currentFieldUt += 0.10f * (fieldMag - currentFieldUt)

                        // Compute orientation from low-pass filtered Accel + Magnetometer when RotationVector is absent
                        if (rotationVectorSensor == null && hasGravity && hasGeomagnetic) {
                            val success = SensorManager.getRotationMatrix(
                                rotationMatrix,
                                null,
                                filteredGravity,
                                filteredGeomagnetic
                            )
                            if (success) {
                                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                                val rawAzimuth =
                                    ((Math.toDegrees(orientationAngles[0].toDouble()).toFloat()) + 360f) % 360f
                                val rawPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                                val rawRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                                currentAccuracy = mapAccuracy(event.accuracy)
                                processAndEmitOrientation(
                                    rawAzimuthDeg = rawAzimuth,
                                    rawPitchDeg = rawPitch,
                                    rawRollDeg = rawRoll,
                                    eventTimestampNanos = event.timestamp
                                )
                            }
                        }
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        // Apply low-pass filter directly to raw 3-axis accelerometer readings (Ax, Ay, Az)
                        if (!hasGravity) {
                            filteredGravity = event.values.copyOf()
                            hasGravity = true
                        } else {
                            filteredGravity = QiblaCalculator.applyVectorLowPassFilter(
                                input = event.values,
                                output = filteredGravity,
                                alpha = rawVectorAlpha
                            )
                        }
                    }

                    Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)

                        val rawAzimuth =
                            ((Math.toDegrees(orientationAngles[0].toDouble()).toFloat()) + 360f) % 360f
                        val rawPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                        val rawRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                        if (event.accuracy != SensorManager.SENSOR_STATUS_NO_CONTACT) {
                            currentAccuracy = mapAccuracy(event.accuracy)
                        }

                        processAndEmitOrientation(
                            rawAzimuthDeg = rawAzimuth,
                            rawPitchDeg = rawPitch,
                            rawRollDeg = rawRoll,
                            eventTimestampNanos = event.timestamp
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD ||
                    sensor?.type == Sensor.TYPE_ROTATION_VECTOR
                ) {
                    currentAccuracy = mapAccuracy(accuracy)
                }
            }
        }

        // Register listeners with SENSOR_DELAY_GAME (~20ms / 50Hz) for smooth 60fps-ready interpolation
        rotationVectorSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        accelerometerSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        magnetometerSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    private fun mapAccuracy(sensorStatus: Int): SensorAccuracyLevel {
        return when (sensorStatus) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracyLevel.HIGH
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracyLevel.MEDIUM
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracyLevel.LOW
            SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracyLevel.UNRELIABLE
            else -> SensorAccuracyLevel.HIGH
        }
    }
}
