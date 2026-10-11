package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.AppLanguage
import com.example.data.AppLanguageManager

@Composable
fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    isOnboarding: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedCode by remember(currentLanguageCode) { mutableStateOf(currentLanguageCode) }

    Dialog(
        onDismissRequest = {
            if (!isOnboarding) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isOnboarding,
            dismissOnClickOutside = !isOnboarding,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .testTag(if (isOnboarding) "dialog_language_onboarding" else "dialog_language_selection"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Color(0xFFD4AF37).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1B382B), Color(0xFF0F261D), Color(0xFF1B382B))
                            )
                        )
                        .padding(vertical = 16.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFD4AF37).copy(alpha = 0.2f), CircleShape)
                                .border(1.2.dp, Color(0xFFD4AF37), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = Color(0xFFFFDF73),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isOnboarding) {
                            Text(
                                text = "Assalamu'alaikum",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFDF73)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(id = R.string.language_onboarding_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.language_onboarding_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.language_dialog_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(id = R.string.language_dialog_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Option Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguageManager.supportedLanguages.forEach { lang ->
                        val isSelected = selectedCode.equals(lang.code, ignoreCase = true)

                        LanguageOptionItem(
                            language = lang,
                            isSelected = isSelected,
                            onClick = { selectedCode = lang.code }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isOnboarding) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_cancel_language"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = stringResource(id = R.string.language_btn_cancel))
                        }
                    }

                    Button(
                        onClick = {
                            onLanguageSelected(selectedCode)
                        },
                        modifier = Modifier
                            .weight(if (isOnboarding) 1f else 1.3f)
                            .height(46.dp)
                            .testTag(if (isOnboarding) "btn_continue_language" else "btn_apply_language"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFFFFDF73),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnboarding) {
                                stringResource(id = R.string.language_btn_continue)
                            } else {
                                stringResource(id = R.string.language_btn_save)
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionItem(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF1B5E20) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val bgColor = if (isSelected) Color(0xFF1B5E20).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("lang_option_${language.code}"),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        color = bgColor,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) Color(0xFFD4AF37).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = language.flag,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = language.nativeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (language.nativeName != language.name) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${language.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = language.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Checkbox indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) Color(0xFF1B5E20) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .background(if (isSelected) Color(0xFF1B5E20) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Terpilih",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
