package com.boxtraining.app.domain.model

/**
 * Un tramo temporal dentro de un Round.
 * Por ejemplo:
 * - En "Fuerza y Cuerpo", un Round tiene 7 tramos de 30 segundos (Saco, Ejercicio, Saco, Ejercicio, Abdominales, Saco, Ejercicio).
 * - En "Fuerza y Velocidad", un Round tiene 2 tramos (2:30 de 4 puños + 4 reps, y 0:30 finales de Abdominales).
 * - En "Técnica", un Round tiene 2 tramos (2:30 de la combinación técnica + 0:30 de Abdominales/recuperación activa).
 */
data class RoundSegment(
    val id: String,
    val segmentIndexInRound: Int,
    val totalSegmentsInRound: Int,
    val titleOnScreen: String,
    val subtitleOnScreen: String,
    val writtenInstructions: List<String>,
    val spokenAnnouncement: String,
    val durationSeconds: Int,
    val role: SegmentRole,
    val primaryExercise: Exercise,
    val secondaryExercise: Exercise? = null
)

/**
 * Representa un Round completo dentro del entrenamiento.
 */
data class WorkoutRound(
    val roundNumber: Int,
    val totalRoundsInSession: Int,
    val roundNumberInPhase: Int,
    val totalRoundsInPhase: Int,
    val phase: WorkoutPhase,
    val roundTitle: String,
    val roundSummarySubtitle: String,
    val segments: List<RoundSegment>
) {
    val totalDurationSeconds: Int
        get() = segments.sumOf { it.durationSeconds }
}

/**
 * Sesión completa generada dinámicamente según el tiempo disponible y el tipo de día elegido.
 */
data class WorkoutSession(
    val id: String,
    val title: String,
    val dayType: WorkoutDayType,
    val bodyFocus: BodyFocus,
    val targetMinutes: Int,
    val includeReactionGame: Boolean,
    val rounds: List<WorkoutRound>
) {
    val totalDurationSeconds: Int
        get() = rounds.sumOf { it.totalDurationSeconds }

    val totalDurationFormatted: String
        get() {
            val totalMinutes = totalDurationSeconds / 60
            val remSeconds = totalDurationSeconds % 60
            return if (remSeconds == 0) "$totalMinutes min" else "${totalMinutes}m ${remSeconds}s"
        }

    val totalSegmentsCount: Int
        get() = rounds.sumOf { it.segments.size }
}
