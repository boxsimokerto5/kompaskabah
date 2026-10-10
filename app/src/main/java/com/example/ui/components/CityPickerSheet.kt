package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.CityPreset
import com.example.domain.PresetCitiesRepository
import com.example.domain.QiblaCalculator
import com.example.ui.theme.AlignedEmerald
import com.example.ui.theme.MeccaGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerSheet(
    currentLocationLabel: String,
    currentLatitude: Double,
    currentLongitude: Double,
    isLiveGpsActive: Boolean,
    onRequestLiveGps: () -> Unit,
    onSelectCity: (CityPreset) -> Unit,
    onSaveCustomCoordinates: (Double, Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    var customLatText by remember { mutableStateOf("%.4f".format(currentLatitude)) }
    var customLonText by remember { mutableStateOf("%.4f".format(currentLongitude)) }
    var customLabelText by remember { mutableStateOf("") }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            PresetCitiesRepository.allCities
        } else {
            val q = searchQuery.trim().lowercase()
            PresetCitiesRepository.allCities.filter {
                it.name.lowercase().contains(q) ||
                        it.provinceOrRegion.lowercase().contains(q) ||
                        it.country.lowercase().contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Pilih Lokasi & Koordinat Kiblat",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gunakan GPS real-time otomatis atau pilih kota di Indonesia & dunia untuk akurasi sudut Kiblat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row: Live GPS vs Custom Coordinates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onRequestLiveGps()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("use_live_gps_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLiveGpsActive) "Segarkan GPS" else "Gunakan GPS",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                OutlinedButton(
                    onClick = { showCustomInput = !showCustomInput },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_custom_coords_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditLocationAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Input Koordinat",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            if (showCustomInput) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Masukkan Koordinat GPS Manual",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = customLatText,
                                onValueChange = { customLatText = it },
                                label = { Text("Lintang (Lat)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_custom_latitude")
                            )
                            OutlinedTextField(
                                value = customLonText,
                                onValueChange = { customLonText = it },
                                label = { Text("Bujur (Lon)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_custom_longitude")
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customLabelText,
                            onValueChange = { customLabelText = it },
                            label = { Text("Nama Tempat / Masjid (Opsional)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_custom_label")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val lat = customLatText.replace(',', '.').toDoubleOrNull()
                                val lon = customLonText.replace(',', '.').toDoubleOrNull()
                                if (lat != null && lon != null) {
                                    onSaveCustomCoordinates(lat, lon, customLabelText.trim())
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("apply_custom_coordinates_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Terapkan Koordinat")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari kota (mis. Jakarta, Surabaya, Madinah…)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari kota"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("city_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCities, key = { it.id }) { city ->
                    val cityQibla = QiblaCalculator.calculateQiblaBearing(city.latitude, city.longitude)
                    val cityDist = QiblaCalculator.calculateDistanceToKaabaKm(city.latitude, city.longitude)
                    val isSelected = !isLiveGpsActive && currentLocationLabel.contains(city.name.substringBefore(" ("))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectCity(city) }
                            .testTag("city_item_${city.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null,
                                        tint = MeccaGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${city.provinceOrRegion} • ${"%,.0f".format(cityDist)} km",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "%.1f°".format(cityQibla),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Terpilih",
                                        tint = AlignedEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
