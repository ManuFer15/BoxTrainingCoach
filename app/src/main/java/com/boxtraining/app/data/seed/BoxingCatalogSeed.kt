package com.boxtraining.app.data.seed

import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import com.boxtraining.app.domain.model.PunchMove
import com.boxtraining.app.domain.model.VisualAnimationStyle

/**
 * Catálogo ampliado de ejercicios, combinaciones técnicas de boxeo y bloques de clase.
 */
object BoxingCatalogSeed {

    fun defaultExercises(): List<Exercise> = listOf(
        // =========================================================================
        // 1. CALENTAMIENTO Y CIERRE COMÚN
        // =========================================================================
        Exercise(
            id = "warmup_rope",
            name = "Saltar a la Comba Libre",
            category = ExerciseCategory.WARMUP,
            shortScreenBanner = "Ritmo libre sobre las puntas de los pies · Suelta hombros y muñecas",
            writtenSteps = listOf(
                "Salta sobre las puntas de los pies sin apoyar los talones.",
                "Codos pegados al costado; el giro de la cuerda sale solo de las muñecas.",
                "Ritmo libre: alterna pies, paso de boxeador o saltos dobles suaves."
            ),
            spokenCoachPrompt = "Empezamos a saltar a la comba libre. Suelta hombros y mantén ritmo constante.",
            gifAssetFileName = "gif_comba_libre.gif",
            animationStyle = VisualAnimationStyle.JUMP_ROPE,
            repBadge = "COMBA"
        ),
        Exercise(
            id = "warmup_shadow",
            name = "Sombra Libre de Boxeo",
            category = ExerciseCategory.WARMUP,
            shortScreenBanner = "Desplazamientos, fintas, esquivas y golpes sueltos al aire",
            writtenSteps = listOf(
                "Mantén la guardia alta protegiendo el mentón en todo momento.",
                "Muévete en todas las direcciones: pasos cortos sin cruzar los pies.",
                "Lanza combinaciones fluidas imaginando al rival y mete esquivas tras golpear."
            ),
            spokenCoachPrompt = "Sombra libre. Muévete ligero de piernas, manos arriba y trabaja defensas.",
            gifAssetFileName = "gif_sombra_libre.gif",
            animationStyle = VisualAnimationStyle.SHADOW_BOXING,
            repBadge = "SOMBRA"
        ),
        Exercise(
            id = "warmup_gloves",
            name = "Ponerse Vendas y Guantes",
            category = ExerciseCategory.WARMUP,
            shortScreenBanner = "Ajusta bien muñecas y nudillos · Pulsa PAUSA si necesitas más tiempo",
            writtenSteps = listOf(
                "Asegura las vendas sujetando bien el pulgar y la muñeca.",
                "Ponte los guantes y ajusta el velcro firme.",
                "Si necesitas más tiempo para vendarte, toca el botón PAUSA en pantalla."
            ),
            spokenCoachPrompt = "Toca ponerse las vendas y los guantes. Si necesitas más tiempo, dale al botón de pausa.",
            gifAssetFileName = "gif_vendas_guantes.gif",
            animationStyle = VisualAnimationStyle.GLOVES_PREP,
            repBadge = "EQUIPO"
        ),
        Exercise(
            id = "warmup_free_bag",
            name = "Saco Libre",
            category = ExerciseCategory.WARMUP,
            shortScreenBanner = "Pega al saco libremente midiendo la distancia y girando cadera",
            writtenSteps = listOf(
                "Trabaja a tu distancia larga con directos y entra en media distancia con crochés.",
                "Cada mano que sale vuelve directo a cubrir tu barbilla.",
                "No empujes el saco: golpea seco y recoge el brazo rápido."
            ),
            spokenCoachPrompt = "Saco libre. Trabaja la distancia, suelta combinaciones y mantén la guardia cerrada.",
            gifAssetFileName = "gif_saco_libre.gif",
            animationStyle = VisualAnimationStyle.HEAVY_BAG_PUNCHING,
            repBadge = "SACO LIBRE"
        ),
        Exercise(
            id = "finisher_free_bag",
            name = "Round Final: Saco Libre a Tope",
            category = ExerciseCategory.WARMUP,
            shortScreenBanner = "Último round de la clase · Vacía toda la energía en el saco",
            writtenSteps = listOf(
                "Últimos 3 minutos del entrenamiento: mantén volumen alto de golpes.",
                "Aplica las combinaciones trabajadas hoy con velocidad y potencia.",
                "En el último minuto no bajes el ritmo hasta que suene la campana final."
            ),
            spokenCoachPrompt = "Último round de saco libre. Da todo lo que te queda hasta la campana.",
            gifAssetFileName = "gif_saco_final.gif",
            animationStyle = VisualAnimationStyle.HEAVY_BAG_PUNCHING,
            repBadge = "FINISHER"
        ),

        // =========================================================================
        // 2. COMBINACIONES TÉCNICAS DE BOXEO (14 COMBINACIONES VARIADAS)
        // =========================================================================
        Exercise(
            id = "combo_1",
            name = "2 Directos + Croché Izq + Gancho Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Directo Izq (1) ➔ Directo Der (2) ➔ Croché Izq (3) ➔ Gancho Der (6)",
            writtenSteps = listOf(
                "1º Lanza dos directos largos a la cara: Izquierda (1) y Derecha (2).",
                "2º Gira el pie adelantado y conecta Croché de izquierda (3) a la mandíbula.",
                "3º Baja ligeramente las piernas y remata con Gancho (Uppercut) de derecha (6).",
                "4º Recoge las manos a la guardia, da un paso fuera de línea y repite."
            ),
            spokenCoachPrompt = "Combinación del round: Dos directos, croché de izquierda y gancho de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.HOOK_LEFT,
                PunchMove.UPPERCUT_RIGHT
            ),
            gifAssetFileName = "gif_combo_1_2_3_6.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_2",
            name = "2 Directos Izq + 1 Der + Croché Izq + Croché Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Doble Jab (1-1) ➔ Directo Der (2) ➔ Croché Izq (3) ➔ Croché Der (4)",
            writtenSteps = listOf(
                "1º Entra caminando con dos directos rápidos de izquierda (Doble Jab: 1 - 1).",
                "2º Clava el directo potente de derecha (Cross: 2) girando el talón trasero.",
                "3º Encadena Croché de izquierda (3) y cierra con Croché de derecha (4).",
                "4º Sal desplazándote o mete esquiva al terminar la serie."
            ),
            spokenCoachPrompt = "Combinación: Dos directos de izquierda, uno de derecha, croché de izquierda y croché de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.HOOK_LEFT,
                PunchMove.HOOK_RIGHT
            ),
            gifAssetFileName = "gif_combo_1_1_2_3_4.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "5 GOLPES"
        ),
        Exercise(
            id = "combo_3",
            name = "4 Directos Continuos en Velocidad",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Directo Izq (1) ➔ Der (2) ➔ Izq (1) ➔ Der (2)",
            writtenSteps = listOf(
                "1º Lanza 4 directos rápidos y largos (1 - 2 - 1 - 2) a la altura de los ojos.",
                "2º Rota bien los hombros en cada golpe para cubrirte el mentón.",
                "3º En el cuarto directo (derecha) mete toda la cadera para clavar el golpe."
            ),
            spokenCoachPrompt = "Cuatro directos seguidos: izquierda, derecha, izquierda, derecha con velocidad máxima.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT
            ),
            gifAssetFileName = "gif_combo_4_directos.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_4",
            name = "Directo Izq + Directo Der + Gancho Izq + Directo Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab Izq (1) ➔ Cross Der (2) ➔ Gancho Izq (5) ➔ Cross Der (2)",
            writtenSteps = listOf(
                "1º Abre la guardia con Directo de izquierda (1) y Directo de derecha (2).",
                "2º Sube el Gancho (Uppercut) de izquierda (5) por el centro.",
                "3º Remata con un Directo de derecha (2) potente antes de salir."
            ),
            spokenCoachPrompt = "Combinación: Uno, dos, gancho de izquierda y directo de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.UPPERCUT_LEFT,
                PunchMove.CROSS_RIGHT
            ),
            gifAssetFileName = "gif_combo_1_2_5_2.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_5",
            name = "Directo Izq + Croché Der + Gancho Hígado + Croché Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab (1) ➔ Croché Der (4) ➔ Gancho Hígado Izq (3B) ➔ Croché Der (4)",
            writtenSteps = listOf(
                "1º Finta arriba con Directo de izquierda (1) y Croché de derecha (4) a la cabeza.",
                "2º Flexiona rodillas y clava el Gancho de izquierda abajo al hígado (3B).",
                "3º Sube de nuevo a la cabeza con Croché de derecha (4)."
            ),
            spokenCoachPrompt = "Combinación arriba y abajo: Directo izquierda, croché derecha, gancho al hígado y croché de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.HOOK_RIGHT,
                PunchMove.BODY_HOOK_LEFT,
                PunchMove.HOOK_RIGHT
            ),
            gifAssetFileName = "gif_combo_higado.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_6",
            name = "2 Ganchos Abajo + 2 Crochés Arriba",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Gancho Izq (5) ➔ Gancho Der (6) ➔ Croché Izq (3) ➔ Croché Der (4)",
            writtenSteps = listOf(
                "1º En corta distancia, lanza Gancho de izquierda (5) y Gancho de derecha (6).",
                "2º Sin pausa, sube el nivel con Croché de izquierda (3) y Croché de derecha (4).",
                "3º Empuja desde las piernas girando talones en cada golpe."
            ),
            spokenCoachPrompt = "Corta distancia: Gancho izquierda, gancho derecha, croché izquierda y croché derecha.",
            punchSequence = listOf(
                PunchMove.UPPERCUT_LEFT,
                PunchMove.UPPERCUT_RIGHT,
                PunchMove.HOOK_LEFT,
                PunchMove.HOOK_RIGHT
            ),
            gifAssetFileName = "gif_combo_5_6_3_4.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_7",
            name = "1-2 + Esquiva Derecha + Directo Der + Croché Izq",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab (1) ➔ Cross (2) ➔ Esquiva Der ➔ Cross (2) ➔ Croché Izq (3)",
            writtenSteps = listOf(
                "1º Lanza el 1-2 clásico (Directo izquierda + Directo derecha).",
                "2º Haz una esquiva hacia tu derecha cargando el peso en la pierna atrasada.",
                "3º Contraataca desde ahí con Directo de derecha (2) y Croché de izquierda (3)."
            ),
            spokenCoachPrompt = "Técnica con defensa: Uno, dos, esquiva a la derecha, directo de derecha y croché de izquierda.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.SLIP_RIGHT,
                PunchMove.CROSS_RIGHT,
                PunchMove.HOOK_LEFT
            ),
            gifAssetFileName = "gif_combo_contraataque.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "COMBO + DEF"
        ),
        Exercise(
            id = "combo_8",
            name = "Directo Izq + Gancho Der + Croché Izq + Directo Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab Izq (1) ➔ Gancho Der (6) ➔ Croché Izq (3) ➔ Directo Der (2)",
            writtenSteps = listOf(
                "1º Mide con el Directo de izquierda (1) y mete Gancho de derecha (6) por dentro.",
                "2º Gira cadera con Croché de izquierda (3) lateral.",
                "3º Remata con Directo de derecha (2) recto y largo."
            ),
            spokenCoachPrompt = "Combinación cruzada: Directo izquierda, gancho derecha, croché izquierda y directo derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.UPPERCUT_RIGHT,
                PunchMove.HOOK_LEFT,
                PunchMove.CROSS_RIGHT
            ),
            gifAssetFileName = "gif_combo_1_6_3_2.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_9",
            name = "Doble Gancho al Hígado + Croché Arriba + Directo Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab (1) ➔ Gancho Hígado (3B) ➔ Croché Izq Arriba (3) ➔ Directo Der (2)",
            writtenSteps = listOf(
                "1º Tapa la visión del rival con un Directo de izquierda (1).",
                "2º Baja el peso y clava Gancho de izquierda al hígado (3B).",
                "3º Con la misma mano izquierda sube un Croché a la mandíbula (3) y cierra con Derecha (2)."
            ),
            spokenCoachPrompt = "Trabajo de mano izquierda: Directo izquierda, gancho al hígado, croché arriba con la misma mano y directo de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.BODY_HOOK_LEFT,
                PunchMove.HOOK_LEFT,
                PunchMove.CROSS_RIGHT
            ),
            gifAssetFileName = "gif_combo_doble_izq.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_10",
            name = "Triple Directo (1-2-1) + Paso Atrás + Directo Der + Croché Izq",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "1 ➔ 2 ➔ 1 ➔ Paso Atrás ➔ Directo Der (2) ➔ Croché Izq (3)",
            writtenSteps = listOf(
                "1º Entra lanzando tres directos rápidos: Izquierda, Derecha, Izquierda (1-2-1).",
                "2º Da medio paso atrás con el pie trasero para dejar fallar al rival.",
                "3º Vuelve a entrar explosivo con Directo de derecha (2) y Croché de izquierda (3)."
            ),
            spokenCoachPrompt = "Uno, dos, uno, medio paso atrás y contraataca con directo de derecha y croché de izquierda.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.JAB_LEFT,
                PunchMove.ROLL_DUCK,
                PunchMove.CROSS_RIGHT,
                PunchMove.HOOK_LEFT
            ),
            gifAssetFileName = "gif_combo_paso_atras.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "DISTANCIA"
        ),
        Exercise(
            id = "combo_11",
            name = "Gancho Izq + Directo Der + Croché Izq + Directo Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Gancho Izq (5) ➔ Directo Der (2) ➔ Croché Izq (3) ➔ Directo Der (2)",
            writtenSteps = listOf(
                "1º Sorprende empezando con Gancho de izquierda (5) en media distancia.",
                "2º Encadena Directo de derecha (2) y Croché de izquierda (3).",
                "3º Termina clavando un segundo Directo de derecha (2)."
            ),
            spokenCoachPrompt = "Cuatro golpes de potencia: Gancho izquierda, directo derecha, croché izquierda y directo derecha.",
            punchSequence = listOf(
                PunchMove.UPPERCUT_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.HOOK_LEFT,
                PunchMove.CROSS_RIGHT
            ),
            gifAssetFileName = "gif_combo_5_2_3_2.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "4 PUÑOS"
        ),
        Exercise(
            id = "combo_12",
            name = "1-2 + Cintura (Roll) + Croché Izq + Croché Der",
            category = ExerciseCategory.BOXING_COMBO,
            shortScreenBanner = "Jab (1) ➔ Cross (2) ➔ Cintura Bajo Croché ➔ Croché Izq (3) ➔ Croché Der (4)",
            writtenSteps = listOf(
                "1º Lanza Directo de izquierda (1) y Directo de derecha (2).",
                "2º Dibuja una 'U' con la cabeza pasando por debajo del croché rival.",
                "3º Al subir conecta Croché de izquierda (3) y Croché de derecha (4)."
            ),
            spokenCoachPrompt = "Uno, dos, movimiento de cintura por debajo y sal con croché de izquierda y croché de derecha.",
            punchSequence = listOf(
                PunchMove.JAB_LEFT,
                PunchMove.CROSS_RIGHT,
                PunchMove.ROLL_DUCK,
                PunchMove.HOOK_LEFT,
                PunchMove.HOOK_RIGHT
            ),
            gifAssetFileName = "gif_combo_cintura.gif",
            animationStyle = VisualAnimationStyle.BOXING_COMBO,
            repBadge = "CINTURA"
        ),

        // =========================================================================
        // 3. FUERZA Y CUERPO: TREN INFERIOR (8 EJERCICIOS DE PIERNA)
        // =========================================================================
        Exercise(
            id = "lower_squats",
            name = "Sentadillas Explosivas",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Baja profundo con espalda recta y sube explosivo en guardia",
            writtenSteps = listOf(
                "Pies a la anchura de los hombros y manos arriba en guardia de boxeo.",
                "Baja la cadera hasta romper los 90 grados en las rodillas.",
                "Sube rápido empujando el suelo con los talones."
            ),
            spokenCoachPrompt = "Sentadillas explosivas con las manos en guardia.",
            gifAssetFileName = "gif_sentadillas.gif",
            animationStyle = VisualAnimationStyle.SQUATS_LOWER_BODY,
            repBadge = "PIERNA"
        ),
        Exercise(
            id = "lower_lunges",
            name = "Zancadas Alternas (Lunges)",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Da un paso largo al frente rozando el suelo con la rodilla trasera",
            writtenSteps = listOf(
                "Da una zancada amplia hacia delante manteniendo el torso erguido.",
                "Ambas rodillas forman un ángulo de 90 grados al bajar.",
                "Impúlsate para volver y cambia de pierna en cada repetición."
            ),
            spokenCoachPrompt = "Zancadas alternas de pierna.",
            gifAssetFileName = "gif_zancadas.gif",
            animationStyle = VisualAnimationStyle.LUNGES_LOWER_BODY,
            repBadge = "PIERNA"
        ),
        Exercise(
            id = "lower_jump_squats",
            name = "Sentadillas con Salto",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Sentadilla profunda + salto vertical amortiguando con las puntas",
            writtenSteps = listOf(
                "Realiza una sentadilla completa y despega del suelo con potencia.",
                "Aterriza suave sobre las puntas amortiguando directamente en la siguiente sentadilla.",
                "Mantén el abdomen fuerte para no inclinar la espalda."
            ),
            spokenCoachPrompt = "Sentadillas con salto explosivo.",
            gifAssetFileName = "gif_sentadilla_salto.gif",
            animationStyle = VisualAnimationStyle.SQUATS_LOWER_BODY,
            repBadge = "POTENCIA"
        ),
        Exercise(
            id = "lower_jump_lunges",
            name = "Tijeras / Zancadas con Salto",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Cambia de pierna en el aire desde posición de zancada",
            writtenSteps = listOf(
                "Baja en posición de zancada y salta cambiando el pie adelantado en el aire.",
                "Si te fatigas mucho, hazlas como zancadas rápidas sin salto sin parar.",
                "Mantén la mirada al frente y las manos cerca del mentón."
            ),
            spokenCoachPrompt = "Zancadas cambiando de pierna.",
            gifAssetFileName = "gif_zancada_salto.gif",
            animationStyle = VisualAnimationStyle.LUNGES_LOWER_BODY,
            repBadge = "PIERNA"
        ),
        Exercise(
            id = "lower_squat_pulses",
            name = "Sentadilla Isométrica con Rebotes",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Aguanta abajo a 90° haciendo cortos rebotes sin llegar a estirar piernas",
            writtenSteps = listOf(
                "Baja a posición de sentadilla media y quédate en la zona de máxima tensión.",
                "Haz pequeños rebotes de 10 centímetros arriba y abajo sin bloquear rodillas.",
                "Mantén los guantes pegados a los pómulos."
            ),
            spokenCoachPrompt = "Sentadilla con rebotes cortos abajo sin estirar las piernas.",
            gifAssetFileName = "gif_sentadilla_rebotes.gif",
            animationStyle = VisualAnimationStyle.SQUATS_LOWER_BODY,
            repBadge = "QUEMAZÓN"
        ),
        Exercise(
            id = "lower_skater_jumps",
            name = "Saltos Laterales de Patinador",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Salta de lado a lado apoyando una pierna para ganar desplazamiento lateral",
            writtenSteps = listOf(
                "Impúlsate lateralmente de un pie al otro amortiguando con la rodilla flexionada.",
                "Pasa la pierna libre por detrás sin apoyar del todo para mantener el equilibrio.",
                "Potencia el empuje lateral que usas al salir por los ángulos del ring."
            ),
            spokenCoachPrompt = "Saltos laterales de patinador de lado a lado.",
            gifAssetFileName = "gif_patinador.gif",
            animationStyle = VisualAnimationStyle.LUNGES_LOWER_BODY,
            repBadge = "PIERNA LAT"
        ),
        Exercise(
            id = "lower_duck_walks",
            name = "Sentadilla Sumo + Esquiva en Círculo",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Pies abiertos, baja profundo y dibuja una U con el tronco al subir",
            writtenSteps = listOf(
                "Abre los pies más que la anchura de tus hombros con las puntas hacia fuera.",
                "Baja en sentadilla profunda dibujando a la vez una esquiva circular de cintura.",
                "Cambia el sentido del giro en cada repetición."
            ),
            spokenCoachPrompt = "Sentadilla abierta pasando cintura al bajar y subir.",
            gifAssetFileName = "gif_sentadilla_esquiva.gif",
            animationStyle = VisualAnimationStyle.SQUATS_LOWER_BODY,
            repBadge = "PIERNA+CINTURA"
        ),
        Exercise(
            id = "lower_high_knees",
            name = "Skipping Alto Explosivo en Guardia",
            category = ExerciseCategory.LOWER_BODY,
            shortScreenBanner = "Sube las rodillas al pecho a máxima velocidad sobre las puntas",
            writtenSteps = listOf(
                "Corre en el sitio subiendo las rodillas por encima de la cintura.",
                "Mantén las manos arriba en guardia cerrada para no bajar los brazos.",
                "Ritmo de sprint durante los 30 segundos."
            ),
            spokenCoachPrompt = "Skipping alto subiendo rodillas al pecho a máxima velocidad.",
            gifAssetFileName = "gif_skipping.gif",
            animationStyle = VisualAnimationStyle.LUNGES_LOWER_BODY,
            repBadge = "EXPLOSIVO"
        ),

        // =========================================================================
        // 4. FUERZA Y CUERPO: TREN SUPERIOR (8 EJERCICIOS DE TORSO Y BRAZOS)
        // =========================================================================
        Exercise(
            id = "upper_pushups",
            name = "Flexiones de Brazos (Push-Ups)",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Pecho al suelo con el cuerpo recto en bloque y subida rápida",
            writtenSteps = listOf(
                "Manos ligeramente más abiertas que los hombros y abdomen bloqueado.",
                "Baja hasta casi tocar el suelo con el pecho con los codos a 45 grados.",
                "Empuja con velocidad extendiendo los brazos por completo."
            ),
            spokenCoachPrompt = "Flexiones de pecho rápidas.",
            gifAssetFileName = "gif_flexiones.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "TREN SUP"
        ),
        Exercise(
            id = "upper_climbers",
            name = "Escaladores (Mountain Climbers)",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "En posición de plancha lleva las rodillas al pecho a máxima velocidad",
            writtenSteps = listOf(
                "Apoya las manos justo debajo de los hombros con los brazos firmes.",
                "Corre en el sitio llevando las rodillas hacia el pecho sin levantar la cadera.",
                "Mantén un ritmo alto y constante durante todo el tramo."
            ),
            spokenCoachPrompt = "Escaladores rápidos en el suelo.",
            gifAssetFileName = "gif_escaladores.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "HOMBRO/CORE"
        ),
        Exercise(
            id = "upper_shoulder_taps",
            name = "Plancha + Toques de Hombro + Flexión",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Haz 1 flexión y arriba toca hombro contrario con cada mano",
            writtenSteps = listOf(
                "Baja en una flexión completa y al subir toca tu hombro derecho con la mano izquierda.",
                "Toca el hombro izquierdo con la mano derecha sin balancear la cadera.",
                "Repite el ciclo manteniendo los hombros activos."
            ),
            spokenCoachPrompt = "Flexión más toque de hombros en plancha.",
            gifAssetFileName = "gif_plancha_hombros.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "TREN SUP"
        ),
        Exercise(
            id = "upper_burpees",
            name = "Burpees de Boxeo (Sprawl + Guardia)",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Manos al suelo, pies atrás, levántate rápido en guardia",
            writtenSteps = listOf(
                "Apoya las manos en el suelo y lanza los dos pies atrás haciendo una flexión.",
                "Recoge los pies de un salto y ponte de pie directo en posición de guardia.",
                "Suelta un 1-2 rápido al aire cada vez que te levantes."
            ),
            spokenCoachPrompt = "Burpees de boxeo subiendo en guardia.",
            gifAssetFileName = "gif_burpees.gif",
            animationStyle = VisualAnimationStyle.BURPEES_FULL_BODY,
            repBadge = "EXPLOSIVO"
        ),
        Exercise(
            id = "upper_diamond_pushups",
            name = "Flexiones Cerradas de Tríceps",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Manos juntas bajo el pecho con los codos pegados a las costillas",
            writtenSteps = listOf(
                "Coloca las manos debajo del esternón y pega los codos al cuerpo al bajar.",
                "Empuja explosivo enfocando el trabajo en los tríceps (clave para la velocidad del directo).",
                "Si cuesta mucho, apoya las rodillas pero no pares el ritmo."
            ),
            spokenCoachPrompt = "Flexiones cerradas de tríceps con codos pegados.",
            gifAssetFileName = "gif_flexiones_triceps.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "TRÍCEPS"
        ),
        Exercise(
            id = "upper_pike_pushups",
            name = "Flexiones de Hombros (Pike Push-Ups)",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Cadera arriba en V invertida y baja la cabeza entre las manos",
            writtenSteps = listOf(
                "Desde posición de plancha, da un paso adelante con los pies subiendo la cadera en forma de V.",
                "Flexiona los codos llevando la parte alta de la frente hacia el suelo.",
                "Empuja fuerte con los hombros hasta estirar los brazos."
            ),
            spokenCoachPrompt = "Flexiones de hombro con la cadera arriba.",
            gifAssetFileName = "gif_flexiones_hombro.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "HOMBROS"
        ),
        Exercise(
            id = "upper_plank_up_down",
            name = "Plancha Dinámica (Codos a Manos)",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Pasa de plancha sobre antebrazos a plancha sobre manos alternando brazo",
            writtenSteps = listOf(
                "Empieza en plancha apoyando los antebrazos.",
                "Sube a posición de flexión apoyando una mano y luego la otra.",
                "Vuelve a bajar a los codos cambiando el brazo con el que empujas."
            ),
            spokenCoachPrompt = "Plancha dinámica subiendo y bajando de codos a manos.",
            gifAssetFileName = "gif_plancha_dinamica.gif",
            animationStyle = VisualAnimationStyle.PUSHUPS_UPPER_BODY,
            repBadge = "HOMBROS/CORE"
        ),
        Exercise(
            id = "upper_speed_punches",
            name = "Metralleta de Directos 1-2 al Aire",
            category = ExerciseCategory.UPPER_BODY,
            shortScreenBanner = "Semiflexiona piernas y lanza directos 1-2 continuos a máxima velocidad",
            writtenSteps = listOf(
                "Clava los pies en el suelo con las rodillas ligeramente flexionadas.",
                "Lanza directos de izquierda y derecha sin parar estirando los brazos al 100%.",
                "Resistencia pura de hombros para no bajar la guardia en los últimos rounds."
            ),
            spokenCoachPrompt = "Metralleta de directos al aire a máxima velocidad sin bajar los brazos.",
            gifAssetFileName = "gif_metralleta_punios.gif",
            animationStyle = VisualAnimationStyle.SHADOW_BOXING,
            repBadge = "RESISTENCIA"
        ),

        // =========================================================================
        // 5. ABDOMINALES / DESCANSO ACTIVO (6 VARIANTES)
        // =========================================================================
        Exercise(
            id = "abs_crunch",
            name = "Abdominales Crunch (o Recuperar Aire)",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "30s de abdominales cortos · Si vas al límite, úsalos para respirar profundo",
            writtenSteps = listOf(
                "Túmbate boca arriba con rodillas flexionadas y sube los hombros apretando el abdomen.",
                "Suelta el aire al subir sin tirar del cuello con las manos.",
                "NOTA: Como en clase, si estás muy fatigado usa estos 30s para recuperar aire."
            ),
            spokenCoachPrompt = "Treinta segundos de abdominales o recupera el aire.",
            gifAssetFileName = "gif_abs_crunch.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),
        Exercise(
            id = "abs_bicycle",
            name = "Abdominales Bicicleta (Oblicuos)",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "Lleva codo a rodilla contraria alternando piernas (o toma aire)",
            writtenSteps = listOf(
                "Gira el torso llevando el codo derecho hacia la rodilla izquierda y viceversa.",
                "Fortalece los oblicuos para ganar pegada en los crochés y ganchos.",
                "Si necesitas descansar para el siguiente round, respira profundo por la nariz."
            ),
            spokenCoachPrompt = "Abdominales bicicleta para oblicuos o recupera aire.",
            gifAssetFileName = "gif_abs_bicicleta.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),
        Exercise(
            id = "abs_leg_raises",
            name = "Tijeras / Elevación de Piernas",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "Manos bajo el glúteo y bate las piernas rectas sin tocar el suelo",
            writtenSteps = listOf(
                "Coloca las manos debajo de la zona lumbar para proteger la espalda.",
                "Sube y baja las piernas alternas a pocos centímetros del suelo.",
                "Recuerda: este tramo de 30s es tu descanso activo entre asaltos."
            ),
            spokenCoachPrompt = "Abdominales de tijeras de piernas o recupera el aliento.",
            gifAssetFileName = "gif_abs_tijeras.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),
        Exercise(
            id = "abs_boxer_situp",
            name = "Sit-Ups de Boxeador (+ 2 Directos Arriba)",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "Sube el tronco completo y lanza 1-2 al llegar arriba (o toma aire)",
            writtenSteps = listOf(
                "Sube desde el suelo hasta sentarte con las manos en guardia.",
                "Lanza dos directos cruzados (1-2) arriba antes de volver a bajar controlado.",
                "Si estás muy cansado del round anterior, aprovecha para recuperar pulsaciones."
            ),
            spokenCoachPrompt = "Abdominales completos lanzando dos directos arriba, o recupera aire.",
            gifAssetFileName = "gif_abs_boxeador.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),
        Exercise(
            id = "abs_plank_hold",
            name = "Plancha Abdominal Isométrica",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "Apoya antebrazos y aguanta el abdomen duro como una roca 30s",
            writtenSteps = listOf(
                "Apoya los antebrazos en el suelo con el cuerpo totalmente alineado.",
                "Aprieta fuerte el abdomen como si fueras a encajar un gancho al cuerpo.",
                "Respira de forma controlada sin dejar caer la cadera."
            ),
            spokenCoachPrompt = "Treinta segundos de plancha isométrica o recupera el aliento.",
            gifAssetFileName = "gif_abs_plancha.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),
        Exercise(
            id = "abs_russian_twists",
            name = "Giros Rusos de Cintura",
            category = ExerciseCategory.ABS_RECOVERY,
            shortScreenBanner = "Sentado en V gira los hombros tocando el suelo a cada lado",
            writtenSteps = listOf(
                "Siéntate inclinando ligeramente la espalda hacia atrás con el abdomen activo.",
                "Gira el torso de lado a lado tocando el suelo junto a tu cadera con ambas manos.",
                "Excelente para la rotación de cintura en los golpes curvos."
            ),
            spokenCoachPrompt = "Giros rusos de cintura o recupera aire.",
            gifAssetFileName = "gif_abs_giros.gif",
            animationStyle = VisualAnimationStyle.ABS_CORE,
            repBadge = "30s ABS / AIRE"
        ),

        // =========================================================================
        // 6. JUEGO DE REACCIÓN: TOCAR HOMBROS Y RODILLAS (5 VARIANTES)
        // =========================================================================
        Exercise(
            id = "reaction_squats",
            name = "Hombros y Rodillas ➔ Castigo: 4 Sentadillas",
            category = ExerciseCategory.REACTION,
            shortScreenBanner = "Esquiva y toca hombros/rodillas · Por cada toque: 4 Sentadillas",
            writtenSteps = listOf(
                "EN PAREJA: Intentad tocar los hombros o rodillas del compañero. Al que toquen hace 4 Sentadillas rápidas.",
                "SOLO EN CASA: Haz sombra rápida defendiendo y tocando el saco arriba/abajo; cada 15s haz una esquiva + 4 Sentadillas explosivas.",
                "Mantén las rodillas semiflexionadas y entra y sal de distancia."
            ),
            spokenCoachPrompt = "Juego de tocar hombros y rodillas. Cada toque son cuatro sentadillas rápidas.",
            gifAssetFileName = "gif_reaccion_sentadillas.gif",
            animationStyle = VisualAnimationStyle.SHOULDER_KNEE_REACTION,
            repBadge = "4 SENTADILLAS"
        ),
        Exercise(
            id = "reaction_pushups",
            name = "Hombros y Rodillas ➔ Castigo: 4 Flexiones",
            category = ExerciseCategory.REACTION,
            shortScreenBanner = "Juego de reflejos y distancia · Cada toque: 4 Flexiones rápidas",
            writtenSteps = listOf(
                "EN PAREJA: Desplázate y finta para tocar hombros o rodillas. Quien reciba un toque baja y hace 4 Flexiones.",
                "SOLO EN CASA: Muévete frente al saco amagando tocar arriba y abajo; cada 15s baja a hacer 4 Flexiones y levántate al instante.",
                "No te quedes quieto en el mismo sitio."
            ),
            spokenCoachPrompt = "Juego de tocar hombros y rodillas. En este round el castigo son cuatro flexiones.",
            gifAssetFileName = "gif_reaccion_flexiones.gif",
            animationStyle = VisualAnimationStyle.SHOULDER_KNEE_REACTION,
            repBadge = "4 FLEXIONES"
        ),
        Exercise(
            id = "reaction_lunges",
            name = "Hombros y Rodillas ➔ Castigo: 4 Zancadas",
            category = ExerciseCategory.REACTION,
            shortScreenBanner = "Fintas y reflejos rápidos · Cada toque: 4 Zancadas de pierna",
            writtenSteps = listOf(
                "EN PAREJA: Juego de tocar hombros y rodillas; al que toquen realiza 4 Zancadas alternas.",
                "SOLO EN CASA: Trabaja entradas y salidas rápidas tocando el saco arriba y abajo y encadena 4 Zancadas cada vez.",
                "Usa la cintura y los pasos laterales para esquivar."
            ),
            spokenCoachPrompt = "Variante de hombros y rodillas: cada toque son cuatro zancadas.",
            gifAssetFileName = "gif_reaccion_zancadas.gif",
            animationStyle = VisualAnimationStyle.SHOULDER_KNEE_REACTION,
            repBadge = "4 ZANCADAS"
        ),
        Exercise(
            id = "reaction_burpees",
            name = "Hombros y Rodillas ➔ Castigo: 4 Burpees / Sprawls",
            category = ExerciseCategory.REACTION,
            shortScreenBanner = "Reflejos a máxima intensidad · Cada toque: 4 Burpees rápidos",
            writtenSteps = listOf(
                "EN PAREJA: Si te tocan hombro o rodilla, haces 4 Burpees subiendo directo con las manos en guardia.",
                "SOLO EN CASA: Finta frente al saco y cada 20 segundos encadena 4 Burpees rápidos.",
                "Mantén la vista siempre arriba al levantarte."
            ),
            spokenCoachPrompt = "Hombros y rodillas de alta intensidad: cada toque son cuatro burpees.",
            gifAssetFileName = "gif_reaccion_burpees.gif",
            animationStyle = VisualAnimationStyle.SHOULDER_KNEE_REACTION,
            repBadge = "4 BURPEES"
        )
    )
}
