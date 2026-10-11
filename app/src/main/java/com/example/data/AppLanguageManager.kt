package com.example.data

import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import java.util.Locale

data class AppLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val subtitle: String
)

object AppLanguageManager {

    const val LANG_ID = "id"
    const val LANG_EN = "en"
    const val LANG_AR = "ar"

    private const val PREFS_NAME = "qibla_language_prefs"
    private const val KEY_SELECTED_LANGUAGE = "selected_language_code"
    private const val KEY_ONBOARDING_DONE = "language_onboarding_completed"

    val supportedLanguages = listOf(
        AppLanguage(
            code = LANG_ID,
            name = "Bahasa Indonesia",
            nativeName = "Bahasa Indonesia",
            flag = "🇮🇩",
            subtitle = "Bahasa Indonesia • Panduan & Teks Lengkap"
        ),
        AppLanguage(
            code = LANG_EN,
            name = "English",
            nativeName = "English",
            flag = "🇬🇧",
            subtitle = "English • High Precision & International"
        ),
        AppLanguage(
            code = LANG_AR,
            name = "Bahasa Arab",
            nativeName = "العربية",
            flag = "🇸🇦",
            subtitle = "العربية • اتجاه القبلة ومواقيت الصلاة"
        )
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_SELECTED_LANGUAGE, LANG_ID) ?: LANG_ID
    }

    fun isLanguageOnboardingCompleted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_DONE, false)
    }

    fun getLanguageByCode(code: String): AppLanguage {
        return supportedLanguages.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: supportedLanguages.first()
    }

    /**
     * Updates language preference and applies locale to system and app resources.
     */
    fun setLanguage(context: Context, langCode: String, markOnboardingCompleted: Boolean = true) {
        val validCode = if (supportedLanguages.any { it.code == langCode }) langCode else LANG_ID

        getPrefs(context).edit {
            putString(KEY_SELECTED_LANGUAGE, validCode)
            if (markOnboardingCompleted) {
                putBoolean(KEY_ONBOARDING_DONE, true)
            }
        }

        // Apply locale system-wide
        val locale = getLocaleForCode(validCode)
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                localeManager?.applicationLocales = LocaleList.forLanguageTags(validCode)
            } catch (_: Exception) {
                // Fallback to configuration update
            }
        }

        // Update application resources configuration
        try {
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    fun getLocaleForCode(langCode: String): Locale {
        return when (langCode.lowercase()) {
            LANG_EN -> Locale.ENGLISH
            LANG_AR -> Locale("ar")
            else -> Locale("in", "ID")
        }
    }

    /**
     * Creates a Configuration-wrapped Context for Compose LocalContext / LocalConfiguration.
     * Wraps baseContext in a ContextWrapper so getBaseContext() unwraps to the host Activity,
     * maintaining compatibility with ActivityResultRegistryOwner, Activity casting, and view trees.
     */
    fun createLocalizedContext(baseContext: Context, langCode: String): Context {
        val locale = getLocaleForCode(langCode)
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        val configContext = baseContext.createConfigurationContext(config)
        return object : android.content.ContextWrapper(baseContext) {
            override fun getResources(): android.content.res.Resources = configContext.resources
            override fun getAssets(): android.content.res.AssetManager = configContext.assets
        }
    }
}
