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
    val sensorSourceName: String = "Rotation Vector + Magnetometer",
    val isHardwareSensorAvailable: Boolean = true
) {
    /**
     * Checks whether the magnetic field strength is abnormal (interference from metal/magnets).
     * Earth's natural magnetic field ranges roughly from 22 µT to 68 µT.
     */
    val hasMagneticInterference: Boolean
        get() = isHardwareSensorAvailable && (magneticFieldStrengthUt < 18f || magneticFieldStrengthUt > 85f)

    /**
     * Checks whether the phone is tilted excessively (> 30 degrees from horizontal).
     */
    val isDeviceTilted: Boolean
        get() = kotlin.math.abs(pitchDeg) > 32f || kotlin.math.abs(rollDeg) > 32f
}

/**
 * Manages real-time Android hardware sensors (`TYPE_ROTATION_VECTOR` or fallback
 * `TYPE_ACCELEROMETER` + `TYPE_MAGNETIC_FIELD`) and emits smoothed compass headings,
 * 3D tilt (pitch/roll), and geomagnetic field strength (µT).
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
            // Emit initial reading indicating hardware sensor is unavailable so interactive dial can be used
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

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        var hasGravity = false
        var hasGeomagnetic = false
        var currentAzimuth = 0f
        var isFirstReading = true
        var currentFieldUt = 45f
        var currentAccuracy = SensorAccuracyLevel.HIGH

        val sourceLabel = if (rotationVectorSensor != null) {
            "Fusi Sensor 3D (Rotation Vector + Magnetometer)"
        } else {
            "Akselerometer + Magnetometer"
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        val alpha = 0.15f
                        if (!hasGeomagnetic) {
                            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                            hasGeomagnetic = true
                        } else {
                            geomagnetic[0] = geomagnetic[0] + alpha * (event.values[0] - geomagnetic[0])
                            geomagnetic[1] = geomagnetic[1] + alpha * (event.values[1] - geomagnetic[1])
                            geomagnetic[2] = geomagnetic[2] + alpha * (event.values[2] - geomagnetic[2])
                        }
                        val bx = event.values[0]
                        val by = event.values[1]
                        val bz = event.values[2]
                        val fieldMag = sqrt(bx * bx + by * by + bz * bz)
                        currentFieldUt = currentFieldUt * 0.85f + fieldMag * 0.15f

                        // If we don't have a rotation vector sensor, compute orientation from Accel + Mag
                        if (rotationVectorSensor == null && hasGravity && hasGeomagnetic) {
                            val success = SensorManager.getRotationMatrix(
                                rotationMatrix,
                                null,
                                gravity,
                                geomagnetic
                            )
                            if (success) {
                                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                                val rawAzimuth = ((Math.toDegrees(orientationAngles[0].toDouble()).toFloat()) + 360f) % 360f
                                val rawPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                                val rawRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                                currentAzimuth = if (isFirstReading) {
                                    isFirstReading = false
                                    rawAzimuth
                                } else {
                                    QiblaCalculator.smoothAngle(currentAzimuth, rawAzimuth, 0.18f)
                                }

                                trySend(
                                    CompassSensorReading(
                                        magneticAzimuthDeg = currentAzimuth,
                                        pitchDeg = rawPitch,
                                        rollDeg = rawRoll,
                                        magneticFieldStrengthUt = currentFieldUt,
                                        accuracy = mapAccuracy(event.accuracy),
                                        sensorSourceName = sourceLabel,
                                        isHardwareSensorAvailable = true
                                    )
                                )
                            }
                        }
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        val alpha = 0.15f
                        if (!hasGravity) {
                            System.arraycopy(event.values, 0, gravity, 0, 3)
                            hasGravity = true
                        } else {
                            gravity[0] = gravity[0] + alpha * (event.values[0] - gravity[0])
                            gravity[1] = gravity[1] + alpha * (event.values[1] - gravity[1])
                            gravity[2] = gravity[2] + alpha * (event.values[2] - gravity[2])
                        }
                    }

                    Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)

                        val rawAzimuth = ((Math.toDegrees(orientationAngles[0].toDouble()).toFloat()) + 360f) % 360f
                        val rawPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                        val rawRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                        currentAzimuth = if (isFirstReading) {
                            isFirstReading = false
                            rawAzimuth
                        } else {
                            QiblaCalculator.smoothAngle(currentAzimuth, rawAzimuth, 0.22f)
                        }

                        if (event.accuracy != SensorManager.SENSOR_STATUS_NO_CONTACT) {
                            currentAccuracy = mapAccuracy(event.accuracy)
                        }

                        trySend(
                            CompassSensorReading(
                                magneticAzimuthDeg = currentAzimuth,
                                pitchDeg = rawPitch,
                                rollDeg = rawRoll,
                                magneticFieldStrengthUt = currentFieldUt,
                                accuracy = currentAccuracy,
                                sensorSourceName = sourceLabel,
                                isHardwareSensorAvailable = true
                            )
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

        // Register listeners with SENSOR_DELAY_GAME (~20ms / 50Hz) for ultra-fluid compass needle response
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
