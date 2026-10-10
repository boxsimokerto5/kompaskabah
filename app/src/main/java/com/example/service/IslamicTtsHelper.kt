package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class IslamicTtsHelper(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlayingFlow = MutableStateFlow(false)
    val isPlayingFlow: StateFlow<Boolean> = _isPlayingFlow.asStateFlow()

    private val _currentlyPlayingId = MutableStateFlow<String?>(null)
    val currentlyPlayingId: StateFlow<String?> = _currentlyPlayingId.asStateFlow()

    init {
        try {
            textToSpeech = TextToSpeech(appContext, this)
        } catch (_: Exception) {
            isInitialized = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupVoiceLocale()
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlayingFlow.value = true
                    _currentlyPlayingId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isPlayingFlow.value = false
                    if (_currentlyPlayingId.value == utteranceId) {
                        _currentlyPlayingId.value = null
                    }
                }

                override fun onError(utteranceId: String?) {
                    _isPlayingFlow.value = false
                    if (_currentlyPlayingId.value == utteranceId) {
                        _currentlyPlayingId.value = null
                    }
                }
            })
        } else {
            isInitialized = false
        }
    }

    private fun setupVoiceLocale() {
        val tts = textToSpeech ?: return
        val arabicLocale = Locale("ar")
        val result = tts.isLanguageAvailable(arabicLocale)
        if (result >= TextToSpeech.LANG_AVAILABLE) {
            tts.language = arabicLocale
            tts.setPitch(1.0f)
            tts.setSpeechRate(0.85f) // Clear, measured pace for Islamic recitations
        } else {
            val idLocale = Locale("id", "ID")
            if (tts.isLanguageAvailable(idLocale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.language = idLocale
            }
            tts.setPitch(1.0f)
            tts.setSpeechRate(0.9f)
        }
    }

    fun speakRecitation(id: String, arabicText: String, fallbackLatinText: String = "") {
        if (!isInitialized || textToSpeech == null) return

        // If already reciting this exact item, toggle off/stop
        if (_isPlayingFlow.value && _currentlyPlayingId.value == id) {
            stop()
            return
        }

        stop()

        val tts = textToSpeech ?: return
        val arabicLocale = Locale("ar")
        val canSpeakArabic = tts.isLanguageAvailable(arabicLocale) >= TextToSpeech.LANG_AVAILABLE

        val textToSpeak = if (canSpeakArabic) {
            arabicText
        } else {
            fallbackLatinText.ifBlank { arabicText }
        }

        if (canSpeakArabic) {
            tts.language = arabicLocale
        } else {
            tts.language = Locale("id", "ID")
        }

        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, id)
    }

    fun stop() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        _isPlayingFlow.value = false
        _currentlyPlayingId.value = null
    }

    fun shutdown() {
        try {
            stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {}
    }
}
