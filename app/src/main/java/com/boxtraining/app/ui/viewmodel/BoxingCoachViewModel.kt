package com.boxtraining.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.boxtraining.app.core.audio.CoachAudioManager
import com.boxtraining.app.data.repository.InMemoryWorkoutRepository
import com.boxtraining.app.domain.model.BodyFocus
import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import com.boxtraining.app.domain.model.RoundSegment
import com.boxtraining.app.domain.model.WorkoutDayType
import com.boxtraining.app.domain.model.WorkoutRound
import com.boxtraining.app.domain.model.WorkoutSession
import com.boxtraining.app.domain.repository.WorkoutRepository
import com.boxtraining.app.domain.usecase.GenerateWorkoutUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Estado inmutable de la interfaz de usuario (UI State) dentro del patrón MVVM.
 *
 * Centraliza tanto la configuración previa de la clase (tipo de día, enfoque muscular y duración)
 * como el estado en tiempo real del reproductor del entrenador (round actual, tramo actual,
 * segundos restantes, estado de pausa y configuración de voz/sonido).
 */
data class CoachUiState(
    val selectedDayType: WorkoutDayType = WorkoutDayType.STRENGTH_BODY,
    val selectedBodyFocus: BodyFocus = BodyFocus.LOWER_BODY,
    val selectedDurationMinutes: Int = 60,
    val includeReactionGame: Boolean = true,
    val generatedSession: WorkoutSession? = null,
    val isWorkoutActive: Boolean = false,
    val isWorkoutCompleted: Boolean = false,
    val isPaused: Boolean = false,
    val currentRoundIndex: Int = 0,
    val currentSegmentIndex: Int = 0,
    val remainingSecondsInSegment: Int = 0,
    val elapsedSessionSeconds: Int = 0,
    val isVoiceEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true
) {
    /** Devuelve el objeto [WorkoutRound] que se está ejecutando actualmente. */
    val currentRound: WorkoutRound?
        get() = generatedSession?.rounds?.getOrNull(currentRoundIndex)

    /** Devuelve el tramo o segmento temporal ([RoundSegment]) activo dentro del round actual. */
    val currentSegment: RoundSegment?
        get() = currentRound?.segments?.getOrNull(currentSegmentIndex)

    /**
     * Calcula cuál será el próximo tramo que sonará al terminar la cuenta atrás actual,
     * ya sea dentro del mismo round o el primer tramo del siguiente round.
     */
    val nextUpcomingSegment: RoundSegment?
        get() {
            val round = currentRound ?: return null
            if (currentSegmentIndex + 1 < round.segments.size) {
                return round.segments[currentSegmentIndex + 1]
            }
            val nextRound = generatedSession?.rounds?.getOrNull(currentRoundIndex + 1)
            return nextRound?.segments?.firstOrNull()
        }

    /** Calcula el tiempo total restante para concluir el round completo actual. */
    val remainingSecondsInCurrentRound: Int
        get() {
            val round = currentRound ?: return 0
            val futureSegmentsSeconds = round.segments
                .drop(currentSegmentIndex + 1)
                .sumOf { it.durationSeconds }
            return remainingSecondsInSegment + futureSegmentsSeconds
        }
}

/**
 * ViewModel principal que orquesta la lógica de presentación de BoxCoach Entrenador.
 *
 * Conecta:
 * - [WorkoutRepository]: Catálogo de ejercicios, combinaciones y rutinas personalizadas.
 * - [GenerateWorkoutUseCase]: Algoritmo generador de sesiones de boxeo según tiempo y día.
 * - [CoachAudioManager]: Motor de síntesis de voz (TextToSpeech), campana y vibración.
 */
class BoxingCoachViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository = InMemoryWorkoutRepository()
    private val generateWorkoutUseCase = GenerateWorkoutUseCase(repository)
    private val audioManager = CoachAudioManager(application)

    val exercisesCatalogFlow: StateFlow<List<Exercise>> = repository.exercisesFlow
    val customSelectedIdsFlow: StateFlow<List<String>> = repository.customSelectedExerciseIdsFlow

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        regenerateSessionPreview()
    }

    fun selectDayType(dayType: WorkoutDayType) {
        _uiState.update { it.copy(selectedDayType = dayType) }
        regenerateSessionPreview()
    }

    fun selectBodyFocus(focus: BodyFocus) {
        _uiState.update { it.copy(selectedBodyFocus = focus) }
        regenerateSessionPreview()
    }

    fun selectDurationMinutes(minutes: Int) {
        _uiState.update { it.copy(selectedDurationMinutes = minutes) }
        regenerateSessionPreview()
    }

    fun toggleIncludeReactionGame(include: Boolean) {
        _uiState.update { it.copy(includeReactionGame = include) }
        regenerateSessionPreview()
    }

    fun regenerateSessionPreview() {
        val state = _uiState.value
        val session = generateWorkoutUseCase.execute(
            dayType = state.selectedDayType,
            targetMinutes = state.selectedDurationMinutes,
            bodyFocus = state.selectedBodyFocus,
            includeReactionGame = state.includeReactionGame,
            customExerciseIds = customSelectedIdsFlow.value
        )
        _uiState.update { it.copy(generatedSession = session) }
    }

    /**
     * Inicia el entrenamiento en modo Entrenador desde el Round 1.
     */
    fun startWorkoutSession() {
        val session = _uiState.value.generatedSession ?: return
        val firstRound = session.rounds.firstOrNull() ?: return
        val firstSegment = firstRound.segments.firstOrNull() ?: return

        _uiState.update {
            it.copy(
                isWorkoutActive = true,
                isWorkoutCompleted = false,
                isPaused = false,
                currentRoundIndex = 0,
                currentSegmentIndex = 0,
                remainingSecondsInSegment = firstSegment.durationSeconds,
                elapsedSessionSeconds = 0
            )
        }

        announceCurrentSegment(isRoundStart = true)
        startTimerLoop()
    }

    /**
     * Pausa o reanuda el entrenamiento en cualquier momento.
     */
    fun togglePauseResume() {
        val newPaused = !_uiState.value.isPaused
        _uiState.update { it.copy(isPaused = newPaused) }
        if (newPaused) {
            audioManager.speakCustomPrompt("Entrenamiento en pausa.")
        } else {
            val seg = _uiState.value.currentSegment
            audioManager.speakCustomPrompt("Reanudamos. ${seg?.titleOnScreen ?: ""}")
        }
    }

    /**
     * Repite en voz alta la instrucción del tramo actual.
     */
    fun repeatCurrentVoiceInstruction() {
        val seg = _uiState.value.currentSegment ?: return
        audioManager.speakCustomPrompt(seg.spokenAnnouncement)
    }

    /**
     * Salta inmediatamente al siguiente tramo de 30s o al siguiente round.
     */
    fun skipToNextSegment() {
        advanceToNextSegmentOrRound()
    }

    /**
     * Vuelve al tramo anterior o reinicia el actual.
     */
    fun goToPreviousSegment() {
        val state = _uiState.value
        val session = state.generatedSession ?: return

        if (state.currentSegmentIndex > 0) {
            val newSegIdx = state.currentSegmentIndex - 1
            val seg = session.rounds[state.currentRoundIndex].segments[newSegIdx]
            _uiState.update {
                it.copy(
                    currentSegmentIndex = newSegIdx,
                    remainingSecondsInSegment = seg.durationSeconds
                )
            }
            announceCurrentSegment(isRoundStart = newSegIdx == 0)
        } else if (state.currentRoundIndex > 0) {
            val prevRoundIdx = state.currentRoundIndex - 1
            val prevRound = session.rounds[prevRoundIdx]
            val firstSeg = prevRound.segments.first()
            _uiState.update {
                it.copy(
                    currentRoundIndex = prevRoundIdx,
                    currentSegmentIndex = 0,
                    remainingSecondsInSegment = firstSeg.durationSeconds
                )
            }
            announceCurrentSegment(isRoundStart = true)
        } else {
            val firstSeg = session.rounds.first().segments.first()
            _uiState.update {
                it.copy(remainingSecondsInSegment = firstSeg.durationSeconds)
            }
            announceCurrentSegment(isRoundStart = true)
        }
    }

    /**
     * Salta directamente a un round específico de la sesión.
     */
    fun jumpToRound(roundIndex: Int) {
        val session = _uiState.value.generatedSession ?: return
        val targetRound = session.rounds.getOrNull(roundIndex) ?: return
        val firstSeg = targetRound.segments.firstOrNull() ?: return

        _uiState.update {
            it.copy(
                currentRoundIndex = roundIndex,
                currentSegmentIndex = 0,
                remainingSecondsInSegment = firstSeg.durationSeconds
            )
        }
        announceCurrentSegment(isRoundStart = true)
    }

    fun stopAndExitWorkout() {
        timerJob?.cancel()
        audioManager.stopSpeaking()
        _uiState.update {
            it.copy(
                isWorkoutActive = false,
                isWorkoutCompleted = false,
                isPaused = false
            )
        }
    }

    fun toggleVoiceEnabled() {
        val updated = !_uiState.value.isVoiceEnabled
        audioManager.isVoiceEnabled = updated
        if (!updated) audioManager.stopSpeaking()
        _uiState.update { it.copy(isVoiceEnabled = updated) }
    }

    fun toggleSoundEnabled() {
        val updated = !_uiState.value.isSoundEnabled
        audioManager.isSoundEffectsEnabled = updated
        _uiState.update { it.copy(isSoundEnabled = updated) }
    }

    fun toggleCustomExerciseSelection(exerciseId: String) {
        repository.toggleExerciseInCustomRoutine(exerciseId)
        regenerateSessionPreview()
    }

    fun createNewCustomExercise(
        name: String,
        category: ExerciseCategory,
        shortBanner: String,
        writtenTip: String
    ) {
        if (name.isBlank()) return
        val newEx = Exercise(
            id = "custom_${UUID.randomUUID()}",
            name = name.trim(),
            category = category,
            shortScreenBanner = shortBanner.ifBlank { name.trim() },
            writtenSteps = listOf(
                writtenTip.ifBlank { "Mantén buena técnica y guardia alta durante todo el round." }
            ),
            spokenCoachPrompt = "$name. ${shortBanner.ifBlank { "" }}",
            repBadge = "PERSONALIZADO"
        )
        repository.addCustomExercise(newEx)
        repository.toggleExerciseInCustomRoutine(newEx.id)
        regenerateSessionPreview()
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _uiState.value
                if (!state.isWorkoutActive || state.isPaused || state.isWorkoutCompleted) {
                    continue
                }

                if (state.remainingSecondsInSegment > 1) {
                    val nextRemaining = state.remainingSecondsInSegment - 1
                    _uiState.update {
                        it.copy(
                            remainingSecondsInSegment = nextRemaining,
                            elapsedSessionSeconds = it.elapsedSessionSeconds + 1
                        )
                    }
                    audioManager.playCountdownTick(nextRemaining)
                } else {
                    advanceToNextSegmentOrRound()
                }
            }
        }
    }

    private fun advanceToNextSegmentOrRound() {
        val state = _uiState.value
        val session = state.generatedSession ?: return
        val currentRound = session.rounds.getOrNull(state.currentRoundIndex) ?: return

        if (state.currentSegmentIndex + 1 < currentRound.segments.size) {
            val nextSegIndex = state.currentSegmentIndex + 1
            val nextSeg = currentRound.segments[nextSegIndex]
            _uiState.update {
                it.copy(
                    currentSegmentIndex = nextSegIndex,
                    remainingSecondsInSegment = nextSeg.durationSeconds
                )
            }
            announceCurrentSegment(isRoundStart = false)
        } else if (state.currentRoundIndex + 1 < session.rounds.size) {
            val nextRoundIndex = state.currentRoundIndex + 1
            val nextRound = session.rounds[nextRoundIndex]
            val firstSeg = nextRound.segments.first()
            _uiState.update {
                it.copy(
                    currentRoundIndex = nextRoundIndex,
                    currentSegmentIndex = 0,
                    remainingSecondsInSegment = firstSeg.durationSeconds
                )
            }
            announceCurrentSegment(isRoundStart = true)
        } else {
            // Fin del entrenamiento completo
            timerJob?.cancel()
            audioManager.speakCustomPrompt("¡Entrenamiento terminado! Gran trabajo campeón.")
            _uiState.update {
                it.copy(
                    isWorkoutCompleted = true,
                    remainingSecondsInSegment = 0
                )
            }
        }
    }

    private fun announceCurrentSegment(isRoundStart: Boolean) {
        val seg = _uiState.value.currentSegment ?: return
        audioManager.announceSegmentStart(
            spokenText = seg.spokenAnnouncement,
            isRoundStart = isRoundStart,
            isShoutBurst = seg.role.requiresShoutCue
        )
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioManager.release()
    }
}
