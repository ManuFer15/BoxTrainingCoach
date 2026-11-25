package com.boxtraining.app.domain.model

/**
 * Categoría del ejercicio dentro del catálogo de la app.
 */
enum class ExerciseCategory(val displayTitle: String) {
    WARMUP("Calentamiento (Comba / Sombra / Saco)"),
    BOXING_COMBO("Combinación de Boxeo"),
    LOWER_BODY("Fuerza: Tren Inferior"),
    UPPER_BODY("Fuerza: Tren Superior"),
    FULL_BODY("Fuerza y Velocidad (Cuerpo Completo)"),
    ABS_RECOVERY("Abdominales (Descanso Activo)"),
    REACTION("Juego Hombros y Rodillas")
}

/**
 * Golpes individuales para mostrar visualmente paso a paso las combinaciones en pantalla.
 */
enum class PunchMove(
    val numberCode: String,
    val spanishName: String,
    val handSide: String
) {
    JAB_LEFT("1", "Directo Izquierda (Jab)", "IZQ"),
    CROSS_RIGHT("2", "Directo Derecha (Cross)", "DER"),
    HOOK_LEFT("3", "Croché Izquierda", "IZQ"),
    HOOK_RIGHT("4", "Croché Derecha", "DER"),
    UPPERCUT_LEFT("5", "Gancho (Uppercut) Izquierda", "IZQ"),
    UPPERCUT_RIGHT("6", "Gancho (Uppercut) Derecha", "DER"),
    BODY_HOOK_LEFT("3B", "Gancho al Hígado (Izq)", "IZQ"),
    BODY_HOOK_RIGHT("4B", "Gancho al Cuerpo (Der)", "DER"),
    SLIP_LEFT("ESQ-I", "Esquiva Izquierda", "DEF"),
    SLIP_RIGHT("ESQ-D", "Esquiva Derecha", "DEF"),
    ROLL_DUCK("ROLL", "Paso Atrás / Cintura", "DEF")
}

/**
 * Representa un ejercicio físico, una combinación de boxeo o una actividad de calentamiento.
 * Incluye textos claros para mostrar escritos en pantalla, locución para el entrenador de voz
 * y referencia al archivo GIF/Vídeo generado con IA.
 */
data class Exercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val shortScreenBanner: String,
    val writtenSteps: List<String>,
    val spokenCoachPrompt: String,
    val punchSequence: List<PunchMove> = emptyList(),
    val gifAssetFileName: String = "",
    val animationStyle: VisualAnimationStyle = VisualAnimationStyle.BOXING_COMBO,
    val repBadge: String? = null
)

/**
 * Estilo de demostración animada que se dibuja en el reproductor cuando aún no se ha cargado
 * un archivo GIF externo, o como apoyo visual junto al GIF.
 */
enum class VisualAnimationStyle {
    JUMP_ROPE,
    SHADOW_BOXING,
    HEAVY_BAG_PUNCHING,
    BOXING_COMBO,
    SQUATS_LOWER_BODY,
    LUNGES_LOWER_BODY,
    PUSHUPS_UPPER_BODY,
    BURPEES_FULL_BODY,
    ABS_CORE,
    SHOULDER_KNEE_REACTION,
    GLOVES_PREP
}
