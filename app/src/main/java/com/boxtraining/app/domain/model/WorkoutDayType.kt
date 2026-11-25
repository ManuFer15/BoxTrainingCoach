package com.boxtraining.app.domain.model

/**
 * Tipos de días de entrenamiento en las clases de boxeo.
 */
enum class WorkoutDayType(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val description: String
) {
    TECHNIQUE(
        title = "Día de Técnica",
        subtitle = "Combinaciones de boxeo por rounds",
        badgeText = "TÉCNICA",
        description = "Calentamiento + Rounds de combinaciones técnicas en saco/sombra + Abdominales como descanso activo + Saco libre final."
    ),
    STRENGTH_BODY(
        title = "Fuerza y Cuerpo",
        subtitle = "Saco + Ejercicio al '¡YA!' (Tramos de 30s)",
        badgeText = "FUERZA (3:30 / ROUND)",
        description = "Rounds de 3:30 min sin parar: 30s Saco -> 30s Ejercicio -> 30s Saco -> 30s Ejercicio -> 30s Abdominales -> 30s Saco -> 30s Ejercicio."
    ),
    STRENGTH_SPEED(
        title = "Fuerza y Velocidad",
        subtitle = "4 Puños + 4 Repeticiones y Reacción",
        badgeText = "VELOCIDAD + 4x4",
        description = "2:30 min de '4 puños + 4 repeticiones de ejercicio' + últimos 30s de abdominales + Rounds de juego de tocar hombros y rodillas."
    ),
    CUSTOM(
        title = "Rutina Libre",
        subtitle = "Configura tus propios rounds y ejercicios",
        badgeText = "PERSONALIZADA",
        description = "Elige tú mismo los ejercicios, combinaciones y número de rounds para montar tu propio entrenamiento."
    )
}

/**
 * Enfoque muscular para el día de Fuerza y Cuerpo.
 */
enum class BodyFocus(
    val title: String,
    val shortLabel: String,
    val description: String
) {
    LOWER_BODY(
        title = "Tren Inferior (Pierna)",
        shortLabel = "Piernas",
        description = "Sentadillas, zancadas, saltos y potencia de piernas entre ráfagas de saco."
    ),
    UPPER_BODY(
        title = "Tren Superior (Torso y Brazos)",
        shortLabel = "Tren Superior",
        description = "Flexiones, fondos, escaladores y hombros entre ráfagas de saco."
    ),
    FULL_BODY(
        title = "Cuerpo Completo (Mixto)",
        shortLabel = "Cuerpo Completo",
        description = "Alterna ejercicios de tren inferior y tren superior en cada round."
    )
}

/**
 * Fase dentro de la estructura fija de la clase de boxeo.
 */
enum class WorkoutPhase(val displayName: String) {
    WARMUP("1. Calentamiento Común"),
    EQUIPMENT("2. Vendaje y Guantes"),
    BAG_ACTIVATION("3. Activación de Saco"),
    MAIN_BLOCK("4. Bloque Principal"),
    REACTION_GAME("5. Juego Hombros y Rodillas"),
    FINISHER("6. Round Final Saco Libre")
}

/**
 * Rol específico de cada tramo (segmento) dentro de un Round.
 * Define el color en pantalla, el comportamiento del audio y cómo se muestran las instrucciones.
 */
enum class SegmentRole(
    val label: String,
    val isActiveRecoveryAbs: Boolean = false,
    val requiresShoutCue: Boolean = false
) {
    WARMUP_ROPE("COMBA LIBRE"),
    WARMUP_SHADOW("SOMBRA LIBRE"),
    EQUIPMENT_PREP("VENDAS Y GUANTES"),
    WARMUP_BAG("SACO LIBRE"),
    TECHNIQUE_COMBO("COMBINACIÓN TÉCNICA"),
    BAG_CONTINUOUS("¡SACO SIN PARAR!", requiresShoutCue = true),
    STRENGTH_BURST("¡YA! EJERCICIO DE FUERZA", requiresShoutCue = true),
    FOUR_PUNCH_FOUR_REP("4 PUÑOS + 4 REPETICIONES"),
    REACTION_DRILL("HOMMBROS Y RODILLAS (4 REPS)"),
    ACTIVE_RECOVERY_ABS("ABDOMINALES / TOMAR AIRE", isActiveRecoveryAbs = true, requiresShoutCue = true),
    FINISHER_BAG("ÚLTIMO ROUND: SACO LIBRE A TOPE")
}
