package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.billing.BillingManager
import com.example.data.AppLanguageManager

@Composable
fun SettingsInfoScreen(
    onBack: () -> Unit,
    currentLanguageCode: String = AppLanguageManager.getCurrentLanguage(LocalContext.current),
    onLanguageChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var isPrivacyExpanded by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableIntStateOf(5) }
    var hasRated by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }

    if (showSubscriptionDialog) {
        MonthlySubscriptionDialog(
            onDismiss = { showSubscriptionDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLanguageCode,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { newLang ->
                showLanguageDialog = false
                onLanguageChange(newLang)
                val langObj = AppLanguageManager.getLanguageByCode(newLang)
                Toast.makeText(
                    context,
                    context.getString(R.string.language_toast_applied, langObj.nativeName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    val handleShareApp: () -> Unit = {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Aplikasi Kompas Kiblat & Jadwal Sholat")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Assalamu'alaikum! Yuk gunakan aplikasi Kompas Kiblat & Jadwal Sholat. Arah Ka'bah presisi tinggi, waktu sholat akurat, amalan harian lengkap, adzan otomatis, dan widget layar utama megah. Unduh di: https://play.google.com/store/apps/details?id=${context.packageName}"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Aplikasi Kompas Kiblat"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuka menu berbagi", Toast.LENGTH_SHORT).show()
        }
    }

    val handleRateApp: (Int) -> Unit = { stars ->
        hasRated = true
        Toast.makeText(context, "Terima kasih atas rating $stars bintang Anda!", Toast.LENGTH_SHORT).show()
        try {
            val marketUri = Uri.parse("market://details?id=${context.packageName}")
            val playIntent = Intent(Intent.ACTION_VIEW, marketUri)
            context.startActivity(playIntent)
        } catch (_: Exception) {
            try {
                val webUri = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            } catch (_: Exception) {
                // Ignore if in test sandbox
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("settings_info_screen")
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_settings_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = "Pengaturan & Info",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Kompas Kiblat & Jadwal Sholat",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. App Header Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFD4AF37).copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1B382B), Color(0xFF0F261D), Color(0xFF0A1C15))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.4.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_app_icon_kaaba_compass_1791647410532),
                                    contentDescription = "Logo Kompas Ka'bah",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Kompas Kiblat",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Versi 1.0.0 • Akurasi Tinggi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFFDF73),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Arah Ka'bah, Waktu Sholat & Amalan Islami",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // 1.2. Langganan Bebas Iklan Bulanan (Monthly Ad-Free Subscription)
            item {
                val isSubscribed by BillingManager.isSubscribed.collectAsState()
                val formattedPrice by BillingManager.formattedPrice.collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_ad_free_subscription"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSubscribed) Color(0xFF163E2D).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isSubscribed) Color(0xFF2E7D32) else Color(0xFFD4AF37)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.linearGradient(
                                            if (isSubscribed) listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                                            else listOf(Color(0xFFFFDF73), Color(0xFFD4AF37))
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSubscribed) Icons.Default.CheckCircle else Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isSubscribed) Color.White else Color(0xFF3E2723),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Bebas Iklan Bulanan",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSubscribed) Color(0xFF1B5E20) else Color(0xFFD4AF37).copy(alpha = 0.2f),
                                        border = BorderStroke(0.8.dp, if (isSubscribed) Color(0xFF2E7D32) else Color(0xFFD4AF37))
                                    ) {
                                        Text(
                                            text = if (isSubscribed) "AKTIF" else "VIP",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSubscribed) Color.White else Color(0xFF8B6508)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isSubscribed)
                                        "Semua iklan dinonaktifkan (Google Play)"
                                    else
                                        "Mulai $formattedPrice",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSubscribed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSubscribed)
                                "Alhamdulillah! Ibadah Anda bebas dari seluruh iklan interstitial (jeda 15 klik), video berhadiah peta masjid, dan banner bawah."
                            else
                                "Nikmati ibadah sholat dan dzikir tanpa gangguan iklan sama sekali. Langganan bulanan resmi melalui Google Play Store, bisa dibatalkan kapan saja.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showSubscriptionDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_open_subscription_sheet"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubscribed) Color(0xFF1B5E20) else Color(0xFF0F3826)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = if (isSubscribed) Icons.Default.Settings else Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFDF73),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSubscribed) "Kelola Langganan Google Play" else "Berlangganan Bebas Iklan",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 1.5. Bahasa Aplikasi (App Language)
            item {
                val currentLang = AppLanguageManager.getLanguageByCode(currentLanguageCode)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_language_settings"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.2.dp, Color(0xFFD4AF37).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF1B5E20).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = Color(0xFF1B5E20),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(id = R.string.language_setting_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(id = R.string.language_setting_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Current Language row with change action
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { showLanguageDialog = true }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = currentLang.flag, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentLang.nativeName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentLang.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            }
                            Button(
                                onClick = { showLanguageDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_open_language_dialog")
                            ) {
                                Text(
                                    text = stringResource(id = R.string.language_dialog_title),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 2. Beri Rating (Rate the App)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_rate_app"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFFFDF73).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Beri Rating Aplikasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Dukungan bintang 5 Anda sangat berarti bagi pengembangan aplikasi ini",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Star selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = {
                                        selectedRating = i
                                        handleRateApp(i)
                                    },
                                    modifier = Modifier.testTag("rate_star_$i")
                                ) {
                                    Icon(
                                        imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Bintang $i",
                                        tint = if (i <= selectedRating) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { handleRateApp(selectedRating) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_submit_rating"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B5E20)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFDF73), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasRated) "Rating Terkirim ($selectedRating Bintang)" else "Beri Rating di Play Store",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 3. Bagikan Aplikasi (Share the App)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_share_app"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF2196F3).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bagikan Aplikasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ajak sanak saudara dan teman menemukan arah Kiblat serta jadwal sholat",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = handleShareApp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_share_app"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bagikan ke WhatsApp / Media Sosial",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 4. Tentang Aplikasi (About the App)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_about_app"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tentang Aplikasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Informasi fitur, algoritma, dan pengembang",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aplikasi Kompas Kiblat dirancang untuk memberikan akurasi penentuan arah Ka'bah (Masjidil Haram, Makkah: 21.4225° LU, 39.8262° BT) dengan fusi sensor rotasi 3D real-time, koreksi True North deklinasi magnetik bumi, serta jadwal sholat otomatis berdasarkan rumus astronomi hisab akurat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Highlights
                        AboutFeatureBullet("Akurasi Geodesik", "Perhitungan rumus Great Circle (Vincenty/Haversine) menuju Ka'bah.")
                        AboutFeatureBullet("Jadwal Sholat & Adzan", "Jadwal 5 waktu otomatis dengan alarm notifikasi kumandang adzan.")
                        AboutFeatureBullet("Amalan Islami & Audio", "99 Asmaul Husna, wudhu, sholat, Yasin, tahlil, doa, dan sholawat.")
                        AboutFeatureBullet("Widget Layar Utama", "Widget megah panorama masjid dan langit biru di home screen Android.")
                    }
                }
            }

            // 5. Kebijakan Privasi (Privacy Policy)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_privacy_policy"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPrivacyExpanded = !isPrivacyExpanded }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF9C27B0).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFF7B1FA2),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Kebijakan Privasi (Privacy Policy)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isPrivacyExpanded) "Ketuk untuk menutup" else "Ketuk untuk membaca rincian kebijakan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { isPrivacyExpanded = !isPrivacyExpanded }) {
                                Icon(
                                    imageVector = if (isPrivacyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isPrivacyExpanded) "Tutup" else "Buka",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isPrivacyExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                PrivacySectionItem(
                                    title = "1. Penggunaan Izin Lokasi (GPS)",
                                    detail = "Aplikasi meminta izin lokasi semata-mata untuk menghitung sudut azimuth arah Kiblat secara geodesik dan menghitung jadwal sholat berdasarkan koordinat garis lintang dan bujur Anda. Data lokasi diproses secara lokal di perangkat Anda dan TIDAK PERNAH dikirim, disimpan, atau dibagikan ke server pihak ketiga manapun."
                                )

                                PrivacySectionItem(
                                    title = "2. Sensor Magnetometer & Giroskop",
                                    detail = "Data kompas dan akselerometer dibaca secara real-time pada memori sementara perangkat untuk memutar jarum kompas. Tidak ada data sensor yang direkam atau disimpan secara permanen."
                                )

                                PrivacySectionItem(
                                    title = "3. Izin Notifikasi & Audio",
                                    detail = "Izin notifikasi dan layanan audio digunakan semata-mata untuk menyuarakan adzan saat masuknya waktu sholat sesuai preferensi yang Anda aktifkan."
                                )

                                PrivacySectionItem(
                                    title = "4. Data Pribadi & Tanpa Pelacakan",
                                    detail = "Aplikasi ini tidak memungut biaya tersembunyi, tidak memerlukan registrasi akun, tidak melacak aktivitas pribadi, dan tidak memuat pelacak pihak ketiga yang merugikan privasi Anda."
                                )

                                PrivacySectionItem(
                                    title = "5. Kontak & Pertanyaan",
                                    detail = "Jika Anda memiliki pertanyaan mengenai kebijakan privasi atau aplikasi, silakan hubungi tim pengembang melalui email atau formulir feedback resmi."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutFeatureBullet(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "•", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun PrivacySectionItem(title: String, detail: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp,
            fontSize = 11.5.sp
        )
    }
}
