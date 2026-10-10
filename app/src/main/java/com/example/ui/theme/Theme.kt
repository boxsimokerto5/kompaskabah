package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SanctuaryLightColorScheme = lightColorScheme(
    primary = DeepEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = MeccaGoldContainer,
    onPrimaryContainer = CharcoalText,
    secondary = DeepGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = AlabasterSoftGold,
    onSecondaryContainer = DeepGoldAccent,
    tertiary = AlignedEmerald,
    onTertiary = Color.White,
    background = AlabasterBg,
    onBackground = CharcoalText,
    surface = AlabasterSurface,
    onSurface = CharcoalText,
    surfaceVariant = AlabasterCard,
    onSurfaceVariant = SlateSageSecondary,
    outline = LightBorderGold,
    error = DangerCoral,
    onError = Color.White
)

private val SanctuaryDarkColorScheme = darkColorScheme(
    primary = MeccaGold,
    onPrimary = SanctuaryDeepBg,
    primaryContainer = Color(0xFF33290D),
    onPrimaryContainer = MeccaGoldBright,
    secondary = AlignedEmeraldGlow,
    onSecondary = SanctuaryDeepBg,
    secondaryContainer = Color(0xFF083A27),
    onSecondaryContainer = AlignedEmeraldGlow,
    tertiary = MeccaGoldBright,
    onTertiary = SanctuaryDeepBg,
    background = SanctuaryDeepBg,
    onBackground = IvoryTextPrimary,
    surface = SanctuaryEmeraldSurface,
    onSurface = IvoryTextPrimary,
    surfaceVariant = SanctuaryCardSurface,
    onSurfaceVariant = SageTextSecondary,
    outline = Color(0xFF2A4D40),
    error = DangerCoral,
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to Bright Alabaster & Pearl Theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SanctuaryDarkColorScheme else SanctuaryLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
