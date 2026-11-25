package com.boxtraining.app.core.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Motor de audio profesional para el Entrenador de Boxeo:
 * - Locución hablada en español (TextToSpeech) para cantar los ejercicios, combinaciones y el "¡YA!".
 * - Sonidos de campana de boxeo y pitidos de cambio de tramo de 30s (ToneGenerator).
 * - Vibración háptica en los cambios de tramo.
 */
class CoachAudioManager(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    var isVoiceEnabled: Boolean = true
    var isSoundEffectsEnabled: Boolean = true

    private val toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 95)
    } catch (_: Exception) {
        null
    }

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val spanish = Locale("es", "ES")
                val result = tts?.setLanguage(spanish)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(1.06f)
                tts?.setPitch(0.95f)
            }
        }
    }

    /**
     * Anuncia por voz la instrucción del entrenador y hace sonar la señal acústica correspondiente.
     */
    fun announceSegmentStart(
        spokenText: String,
        isRoundStart: Boolean,
        isShoutBurst: Boolean
    ) {
        if (isSoundEffectsEnabled) {
            when {
                isRoundStart -> playBoxingBell()
                isShoutBurst -> playSharpWhistle()
                else -> playTransitionBeep()
            }
        }
        vibrateChange()

        if (isVoiceEnabled && isTtsReady && spokenText.isNotBlank()) {
            tts?.speak(
                spokenText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "coach_segment_${System.currentTimeMillis()}"
            )
        }
    }

    /**
     * Aviso sonoro de últimos segundos o cuenta atrás 3-2-1.
     */
    fun playCountdownTick(secondsLeft: Int) {
        if (!isSoundEffectsEnabled) return
        try {
            when (secondsLeft) {
                10 -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                3, 2, 1 -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            }
        } catch (_: Exception) {
        }
    }

    fun speakCustomPrompt(text: String) {
        if (isVoiceEnabled && isTtsReady && text.isNotBlank()) {
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "coach_custom_${System.currentTimeMillis()}"
            )
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    private fun playBoxingBell() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 420)
        } catch (_: Exception) {
        }
    }

    private fun playSharpWhistle() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
        } catch (_: Exception) {
        }
    }

    private fun playTransitionBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 240)
        } catch (_: Exception) {
        }
    }

    private fun vibrateChange() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            vibrator?.vibrate(VibrationEffect.createOneShot(220, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        toneGenerator?.release()
    }
}
