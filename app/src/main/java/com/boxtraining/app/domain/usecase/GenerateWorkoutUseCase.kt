package com.boxtraining.app.domain.usecase

import com.boxtraining.app.domain.model.BodyFocus
import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import com.boxtraining.app.domain.model.RoundSegment
import com.boxtraining.app.domain.model.SegmentRole
import com.boxtraining.app.domain.model.WorkoutDayType
import com.boxtraining.app.domain.model.WorkoutPhase
import com.boxtraining.app.domain.model.WorkoutRound
import com.boxtraining.app.domain.model.WorkoutSession
import com.boxtraining.app.domain.repository.WorkoutRepository
import java.util.UUID

/**
 * Caso de uso principal que genera una clase completa de boxeo adaptada al tiempo elegido
 * por el usuario (ej. 30, 45, 60, 75 o 90 minutos) respetando la estructura real del gimnasio:
 *
 * 1. Calentamiento común:
 *    - Comba libre (3-4 rounds en clase completa, escalado según tiempo)
 *    - Sombra libre (2-3 rounds)
 *    - Ponerse vendas y guantes
 *    - Saco libre de activación (1-2 rounds)
 * 2. Bloque Principal según el tipo de día:
 *    - Técnica: Combinaciones por round + 30s abdominales (descanso activo)
 *    - Fuerza y Cuerpo: Rounds de 3:30 (30s saco / 30s ejercicio / 30s saco / 30s ejercicio / 30s abs / 30s saco / 30s ejercicio)
 *    - Fuerza y Velocidad: Rounds de 3:00 (2:30 de 4 puños + 4 reps + 0:30 abs) + 1-3 rounds de Hombros y Rodillas
 *    - Rutina Libre: Basada en la selección del usuario
 * 3. Cierre fijo:
 *    - 1 Round final de 3 minutos de Saco Libre.
 */
class GenerateWorkoutUseCase(
    private val repository: WorkoutRepository
) {

    data class AllocationPlan(
        val ropeRounds: Int,
        val shadowRounds: Int,
        val bagWarmupRounds: Int,
        val mainBlockRounds: Int,
        val reactionRounds: Int,
        val finisherBagRounds: Int = 1
    )

    fun execute(
        dayType: WorkoutDayType,
        targetMinutes: Int,
        bodyFocus: BodyFocus = BodyFocus.LOWER_BODY,
        includeReactionGame: Boolean = true,
        customExerciseIds: List<String> = emptyList()
    ): WorkoutSession {
        val allExercises = repository.getAllExercises()

        val ropeExercise = allExercises.first { it.id == "warmup_rope" }
        val shadowExercise = allExercises.first { it.id == "warmup_shadow" }
        val glovesExercise = allExercises.first { it.id == "warmup_gloves" }
        val freeBagExercise = allExercises.first { it.id == "warmup_free_bag" }
        val finisherBagExercise = allExercises.first { it.id == "finisher_free_bag" }
        val absList = allExercises.filter { it.category == ExerciseCategory.ABS_RECOVERY }

        val plan = calculateAllocationPlan(
            dayType = dayType,
            targetMinutes = targetMinutes,
            includeReactionGame = includeReactionGame,
            customCount = customExerciseIds.size
        )

        val preliminaryRounds = mutableListOf<WorkoutRound>()

        // 1. COMBA LIBRE
        for (i in 1..plan.ropeRounds) {
            val absEx = absList[(i - 1) % absList.size]
            preliminaryRounds.add(
                buildWarmupRound(
                    phase = WorkoutPhase.WARMUP,
                    roundInPhase = i,
                    totalInPhase = plan.ropeRounds,
                    roundTitle = "Comba Libre ($i/${plan.ropeRounds})",
                    mainExercise = ropeExercise,
                    absExercise = absEx,
                    role = SegmentRole.WARMUP_ROPE
                )
            )
        }

        // 2. SOMBRA LIBRE
        for (i in 1..plan.shadowRounds) {
            val absEx = absList[(i) % absList.size]
            preliminaryRounds.add(
                buildWarmupRound(
                    phase = WorkoutPhase.WARMUP,
                    roundInPhase = i,
                    totalInPhase = plan.shadowRounds,
                    roundTitle = "Sombra Libre ($i/${plan.shadowRounds})",
                    mainExercise = shadowExercise,
                    absExercise = absEx,
                    role = SegmentRole.WARMUP_SHADOW
                )
            )
        }

        // 3. TRANSICIÓN: PONERSE VENDAS Y GUANTES
        preliminaryRounds.add(
            WorkoutRound(
                roundNumber = 0,
                totalRoundsInSession = 0,
                roundNumberInPhase = 1,
                totalRoundsInPhase = 1,
                phase = WorkoutPhase.EQUIPMENT,
                roundTitle = "Vendas y Guantes",
                roundSummarySubtitle = "1 min para equiparte (puedes pausar o pasar al siguiente tramo)",
                segments = listOf(
                    RoundSegment(
                        id = UUID.randomUUID().toString(),
                        segmentIndexInRound = 1,
                        totalSegmentsInRound = 1,
                        titleOnScreen = "PONTE LAS VENDAS Y LOS GUANTES",
                        subtitleOnScreen = "Prepárate frente al saco (usa PAUSA si necesitas más tiempo)",
                        writtenInstructions = glovesExercise.writtenSteps,
                        spokenAnnouncement = glovesExercise.spokenCoachPrompt,
                        durationSeconds = 60,
                        role = SegmentRole.EQUIPMENT_PREP,
                        primaryExercise = glovesExercise
                    )
                )
            )
        )

        // 4. SACO LIBRE DE ACTIVACIÓN
        for (i in 1..plan.bagWarmupRounds) {
            val absEx = absList[(i + 1) % absList.size]
            preliminaryRounds.add(
                buildWarmupRound(
                    phase = WorkoutPhase.BAG_ACTIVATION,
                    roundInPhase = i,
                    totalInPhase = plan.bagWarmupRounds,
                    roundTitle = "Saco Libre Activación ($i/${plan.bagWarmupRounds})",
                    mainExercise = freeBagExercise,
                    absExercise = absEx,
                    role = SegmentRole.WARMUP_BAG
                )
            )
        }

        // 5. BLOQUE PRINCIPAL SEGÚN EL DÍA ELEGIDO
        when (dayType) {
            WorkoutDayType.TECHNIQUE -> {
                val combos = allExercises.filter { it.category == ExerciseCategory.BOXING_COMBO }
                for (i in 1..plan.mainBlockRounds) {
                    val combo = combos[(i - 1) % combos.size]
                    val absEx = absList[(i - 1) % absList.size]
                    preliminaryRounds.add(
                        buildTechniqueRound(
                            roundInPhase = i,
                            totalInPhase = plan.mainBlockRounds,
                            combo = combo,
                            absExercise = absEx
                        )
                    )
                }
            }

            WorkoutDayType.STRENGTH_BODY -> {
                val strengthPool = when (bodyFocus) {
                    BodyFocus.LOWER_BODY -> allExercises.filter { it.category == ExerciseCategory.LOWER_BODY }
                    BodyFocus.UPPER_BODY -> allExercises.filter { it.category == ExerciseCategory.UPPER_BODY }
                    BodyFocus.FULL_BODY -> allExercises.filter {
                        it.category == ExerciseCategory.LOWER_BODY || it.category == ExerciseCategory.UPPER_BODY
                    }
                }
                for (i in 1..plan.mainBlockRounds) {
                    val strengthEx = strengthPool[(i - 1) % strengthPool.size]
                    val absEx = absList[(i - 1) % absList.size]
                    preliminaryRounds.add(
                        buildStrengthBodyRound(
                            roundInPhase = i,
                            totalInPhase = plan.mainBlockRounds,
                            bagExercise = freeBagExercise,
                            strengthExercise = strengthEx,
                            absExercise = absEx
                        )
                    )
                }
            }

            WorkoutDayType.STRENGTH_SPEED -> {
                val fourPunchCombos = allExercises.filter {
                    it.category == ExerciseCategory.BOXING_COMBO && it.punchSequence.size == 4
                }.ifEmpty {
                    allExercises.filter { it.category == ExerciseCategory.BOXING_COMBO }
                }
                val fullBodyExercises = allExercises.filter {
                    it.category == ExerciseCategory.FULL_BODY ||
                        it.category == ExerciseCategory.LOWER_BODY ||
                        it.category == ExerciseCategory.UPPER_BODY
                }

                for (i in 1..plan.mainBlockRounds) {
                    val combo = fourPunchCombos[(i - 1) % fourPunchCombos.size]
                    val exercise = fullBodyExercises[(i - 1) % fullBodyExercises.size]
                    val absEx = absList[(i - 1) % absList.size]
                    preliminaryRounds.add(
                        buildStrengthSpeed4x4Round(
                            roundInPhase = i,
                            totalInPhase = plan.mainBlockRounds,
                            combo4Punches = combo,
                            repExercise = exercise,
                            absExercise = absEx
                        )
                    )
                }

                if (plan.reactionRounds > 0) {
                    val reactionDrills = allExercises.filter { it.category == ExerciseCategory.REACTION }
                    for (i in 1..plan.reactionRounds) {
                        val drill = reactionDrills[(i - 1) % reactionDrills.size]
                        val absEx = absList[(i) % absList.size]
                        preliminaryRounds.add(
                            buildReactionRound(
                                roundInPhase = i,
                                totalInPhase = plan.reactionRounds,
                                reactionExercise = drill,
                                absExercise = absEx
                            )
                        )
                    }
                }
            }

            WorkoutDayType.CUSTOM -> {
                val selectedExercises = customExerciseIds.mapNotNull { id ->
                    allExercises.find { it.id == id }
                }.ifEmpty {
                    allExercises.filter { it.category == ExerciseCategory.BOXING_COMBO }.take(4)
                }

                for (i in 1..plan.mainBlockRounds) {
                    val chosen = selectedExercises[(i - 1) % selectedExercises.size]
                    val absEx = absList[(i - 1) % absList.size]
                    if (chosen.category == ExerciseCategory.LOWER_BODY || chosen.category == ExerciseCategory.UPPER_BODY) {
                        preliminaryRounds.add(
                            buildStrengthBodyRound(
                                roundInPhase = i,
                                totalInPhase = plan.mainBlockRounds,
                                bagExercise = freeBagExercise,
                                strengthExercise = chosen,
                                absExercise = absEx
                            )
                        )
                    } else {
                        preliminaryRounds.add(
                            buildTechniqueRound(
                                roundInPhase = i,
                                totalInPhase = plan.mainBlockRounds,
                                combo = chosen,
                                absExercise = absEx
                            )
                        )
                    }
                }
            }
        }

        // 6. ROUND FINAL: SACO LIBRE
        preliminaryRounds.add(
            WorkoutRound(
                roundNumber = 0,
                totalRoundsInSession = 0,
                roundNumberInPhase = 1,
                totalRoundsInPhase = 1,
                phase = WorkoutPhase.FINISHER,
                roundTitle = "Round Final: Saco Libre",
                roundSummarySubtitle = "3:00 min finales de saco libre para vaciarte por completo",
                segments = listOf(
                    RoundSegment(
                        id = UUID.randomUUID().toString(),
                        segmentIndexInRound = 1,
                        totalSegmentsInRound = 1,
                        titleOnScreen = "ÚLTIMO ROUND: SACO LIBRE A TOPE",
                        subtitleOnScreen = "Combina todo lo trabajado hoy sin parar hasta la campana final",
                        writtenInstructions = finisherBagExercise.writtenSteps,
                        spokenAnnouncement = "Último round de la clase. Tres minutos de saco libre. ¡Vacíate hasta la campana!",
                        durationSeconds = 180,
                        role = SegmentRole.FINISHER_BAG,
                        primaryExercise = finisherBagExercise
                    )
                )
            )
        )

        // Numeración global de los rounds
        val totalRounds = preliminaryRounds.size
        val numberedRounds = preliminaryRounds.mapIndexed { index, round ->
            round.copy(
                roundNumber = index + 1,
                totalRoundsInSession = totalRounds
            )
        }

        return WorkoutSession(
            id = UUID.randomUUID().toString(),
            title = when (dayType) {
                WorkoutDayType.TECHNIQUE -> "Clase de Técnica ($targetMinutes min)"
                WorkoutDayType.STRENGTH_BODY -> "Fuerza y Cuerpo · ${bodyFocus.shortLabel} ($targetMinutes min)"
                WorkoutDayType.STRENGTH_SPEED -> "Fuerza y Velocidad 4x4 ($targetMinutes min)"
                WorkoutDayType.CUSTOM -> "Mi Rutina Personalizada ($targetMinutes min)"
            },
            dayType = dayType,
            bodyFocus = bodyFocus,
            targetMinutes = targetMinutes,
            includeReactionGame = includeReactionGame,
            rounds = numberedRounds
        )
    }

    /**
     * Calcula cuántos rounds asignar a cada bloque según el tiempo total elegido.
     * En una clase completa de 90 min:
     * - 4 rounds comba + 3 rounds sombra + 1 equip + 2 rounds saco libre + ~15-17 rounds bloque principal + 1 round saco final.
     */
    private fun calculateAllocationPlan(
        dayType: WorkoutDayType,
        targetMinutes: Int,
        includeReactionGame: Boolean,
        customCount: Int
    ): AllocationPlan {
        // Cada round dura 3 min (180s), o 3.5 min (210s) en el bloque principal de Fuerza y Cuerpo
        val totalAvailableRounds = (targetMinutes / 3).coerceAtLeast(6)

        val ropeRounds: Int
        val shadowRounds: Int
        val bagWarmupRounds: Int
        val reactionRounds: Int

        when {
            targetMinutes >= 85 -> {
                ropeRounds = 4
                shadowRounds = 3
                bagWarmupRounds = 2
                reactionRounds = if (dayType == WorkoutDayType.STRENGTH_SPEED && includeReactionGame) 3 else 0
            }
            targetMinutes >= 60 -> {
                ropeRounds = 3
                shadowRounds = 2
                bagWarmupRounds = 2
                reactionRounds = if (dayType == WorkoutDayType.STRENGTH_SPEED && includeReactionGame) 2 else 0
            }
            targetMinutes >= 45 -> {
                ropeRounds = 2
                shadowRounds = 2
                bagWarmupRounds = 1
                reactionRounds = if (dayType == WorkoutDayType.STRENGTH_SPEED && includeReactionGame) 2 else 0
            }
            else -> {
                ropeRounds = 1
                shadowRounds = 1
                bagWarmupRounds = 1
                reactionRounds = if (dayType == WorkoutDayType.STRENGTH_SPEED && includeReactionGame) 1 else 0
            }
        }

        val fixedRounds = ropeRounds + shadowRounds + bagWarmupRounds + reactionRounds + 1 // +1 finisher
        val remainingMinutes = (targetMinutes - (fixedRounds * 3) - 1).coerceAtLeast(9)

        val mainBlockRounds = when (dayType) {
            WorkoutDayType.STRENGTH_BODY -> (remainingMinutes * 60 / 210).coerceAtLeast(4)
            WorkoutDayType.CUSTOM -> if (customCount > 0) customCount.coerceAtLeast(3) else (remainingMinutes / 3).coerceAtLeast(3)
            else -> (remainingMinutes / 3).coerceAtLeast(3)
        }

        return AllocationPlan(
            ropeRounds = ropeRounds,
            shadowRounds = shadowRounds,
            bagWarmupRounds = bagWarmupRounds,
            mainBlockRounds = mainBlockRounds,
            reactionRounds = reactionRounds,
            finisherBagRounds = 1
        )
    }

    /**
     * Construye un round de calentamiento (2:30 de trabajo + 0:30 de abdominales/recuperación activa).
     */
    private fun buildWarmupRound(
        phase: WorkoutPhase,
        roundInPhase: Int,
        totalInPhase: Int,
        roundTitle: String,
        mainExercise: Exercise,
        absExercise: Exercise,
        role: SegmentRole
    ): WorkoutRound {
        return WorkoutRound(
            roundNumber = 0,
            totalRoundsInSession = 0,
            roundNumberInPhase = roundInPhase,
            totalRoundsInPhase = totalInPhase,
            phase = phase,
            roundTitle = roundTitle,
            roundSummarySubtitle = "2:30 ${mainExercise.name} + 0:30 Abdominales (o tomar aire)",
            segments = listOf(
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 1,
                    totalSegmentsInRound = 2,
                    titleOnScreen = mainExercise.name.uppercase(),
                    subtitleOnScreen = mainExercise.shortScreenBanner,
                    writtenInstructions = mainExercise.writtenSteps,
                    spokenAnnouncement = "Round $roundInPhase de ${mainExercise.name}. ${mainExercise.spokenCoachPrompt}",
                    durationSeconds = 150,
                    role = role,
                    primaryExercise = mainExercise
                ),
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 2,
                    totalSegmentsInRound = 2,
                    titleOnScreen = "ABDOMINALES / TOMAR AIRE (${absExercise.name.uppercase()})",
                    subtitleOnScreen = "Últimos 30s: Haz abdominales o úsalos para recuperar el aliento",
                    writtenInstructions = absExercise.writtenSteps,
                    spokenAnnouncement = "¡Cambio! Últimos 30 segundos de abdominales, o recupera aire si lo necesitas.",
                    durationSeconds = 30,
                    role = SegmentRole.ACTIVE_RECOVERY_ABS,
                    primaryExercise = absExercise
                )
            )
        )
    }

    /**
     * Construye un round de Técnica:
     * 2:30 practicando la combinación asignada al round + 0:30 de abdominales/descanso activo.
     */
    private fun buildTechniqueRound(
        roundInPhase: Int,
        totalInPhase: Int,
        combo: Exercise,
        absExercise: Exercise
    ): WorkoutRound {
        return WorkoutRound(
            roundNumber = 0,
            totalRoundsInSession = 0,
            roundNumberInPhase = roundInPhase,
            totalRoundsInPhase = totalInPhase,
            phase = WorkoutPhase.MAIN_BLOCK,
            roundTitle = "Técnica Round $roundInPhase/$totalInPhase: ${combo.name}",
            roundSummarySubtitle = "${combo.shortScreenBanner} (2:30 Combo + 0:30 Abs)",
            segments = listOf(
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 1,
                    totalSegmentsInRound = 2,
                    titleOnScreen = combo.name.uppercase(),
                    subtitleOnScreen = combo.shortScreenBanner,
                    writtenInstructions = combo.writtenSteps,
                    spokenAnnouncement = "Round $roundInPhase de técnica. ${combo.spokenCoachPrompt}",
                    durationSeconds = 150,
                    role = SegmentRole.TECHNIQUE_COMBO,
                    primaryExercise = combo
                ),
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 2,
                    totalSegmentsInRound = 2,
                    titleOnScreen = "ABDOMINALES / DESCANSO ACTIVO (${absExercise.name.uppercase()})",
                    subtitleOnScreen = "30s de abdominales (o recupera aire para la siguiente combinación)",
                    writtenInstructions = absExercise.writtenSteps,
                    spokenAnnouncement = "¡Tiempo! 30 segundos de abdominales o recupera aire.",
                    durationSeconds = 30,
                    role = SegmentRole.ACTIVE_RECOVERY_ABS,
                    primaryExercise = absExercise
                )
            )
        )
    }

    /**
     * Construye el Round exacto de Fuerza y Cuerpo de 3 minutos y 30 segundos (7 bloques de 30s):
     * 1. 30s Saco sin parar
     * 2. 30s "¡YA!" Ejercicio
     * 3. 30s Saco sin parar
     * 4. 30s "¡YA!" Ejercicio
     * 5. 30s Abdominales (o descanso activo)
     * 6. 30s Saco sin parar
     * 7. 30s "¡YA!" Ejercicio
     */
    private fun buildStrengthBodyRound(
        roundInPhase: Int,
        totalInPhase: Int,
        bagExercise: Exercise,
        strengthExercise: Exercise,
        absExercise: Exercise
    ): WorkoutRound {
        val segments = listOf(
            // 1. 30s Saco
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 1,
                totalSegmentsInRound = 7,
                titleOnScreen = "1/7 · ¡PEGA AL SACO SIN PARAR!",
                subtitleOnScreen = "30 seg de ritmo continuo en el saco (Preparado para: ${strengthExercise.name})",
                writtenInstructions = listOf(
                    "Golpea el saco de forma continua sin bajar las manos.",
                    "Al oír el '¡YA!' pasarás a 30 seg de: ${strengthExercise.name}."
                ),
                spokenAnnouncement = "Round $roundInPhase. ¡30 segundos pegando al saco sin parar! El ejercicio de este round es ${strengthExercise.name}.",
                durationSeconds = 30,
                role = SegmentRole.BAG_CONTINUOUS,
                primaryExercise = bagExercise,
                secondaryExercise = strengthExercise
            ),
            // 2. 30s Ejercicio
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 2,
                totalSegmentsInRound = 7,
                titleOnScreen = "2/7 · ¡YA! ${strengthExercise.name.uppercase()}",
                subtitleOnScreen = "30 seg de ${strengthExercise.name} sin parar",
                writtenInstructions = strengthExercise.writtenSteps,
                spokenAnnouncement = "¡YA! 30 segundos de ${strengthExercise.name}.",
                durationSeconds = 30,
                role = SegmentRole.STRENGTH_BURST,
                primaryExercise = strengthExercise
            ),
            // 3. 30s Saco
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 3,
                totalSegmentsInRound = 7,
                titleOnScreen = "3/7 · ¡VUELVE AL SACO SIN PARAR!",
                subtitleOnScreen = "30 seg soltando manos rápido en el saco",
                writtenInstructions = listOf(
                    "Vuelve directo al saco y mantén volumen constante de golpes.",
                    "Respira con cada golpe, enseguida vuelve el '¡YA!'."
                ),
                spokenAnnouncement = "¡YA! ¡Vuelve al saco, 30 segundos sin parar!",
                durationSeconds = 30,
                role = SegmentRole.BAG_CONTINUOUS,
                primaryExercise = bagExercise,
                secondaryExercise = strengthExercise
            ),
            // 4. 30s Ejercicio
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 4,
                totalSegmentsInRound = 7,
                titleOnScreen = "4/7 · ¡YA! ${strengthExercise.name.uppercase()}",
                subtitleOnScreen = "Segunda serie de 30 seg · Después tocan Abdominales",
                writtenInstructions = strengthExercise.writtenSteps,
                spokenAnnouncement = "¡YA! ${strengthExercise.name}, 30 segundos.",
                durationSeconds = 30,
                role = SegmentRole.STRENGTH_BURST,
                primaryExercise = strengthExercise
            ),
            // 5. 30s Abdominales (Descanso Activo)
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 5,
                totalSegmentsInRound = 7,
                titleOnScreen = "5/7 · ABDOMINALES / TOMAR AIRE (${absExercise.name.uppercase()})",
                subtitleOnScreen = "30 seg de abdominales (o recupera el aliento si vas al límite)",
                writtenInstructions = absExercise.writtenSteps,
                spokenAnnouncement = "¡YA! 30 segundos de abdominales o recupera aire.",
                durationSeconds = 30,
                role = SegmentRole.ACTIVE_RECOVERY_ABS,
                primaryExercise = absExercise
            ),
            // 6. 30s Saco
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 6,
                totalSegmentsInRound = 7,
                titleOnScreen = "6/7 · ¡SACO SIN PARAR (ÚLTIMA DE SACO)!",
                subtitleOnScreen = "30 seg de saco antes del último bloque de ejercicio",
                writtenInstructions = listOf(
                    "Arriba y directo al saco durante 30 segundos.",
                    "Queda solo esta ráfaga de saco y el último '¡YA!' del round."
                ),
                spokenAnnouncement = "¡Arriba! ¡30 segundos de saco sin parar!",
                durationSeconds = 30,
                role = SegmentRole.BAG_CONTINUOUS,
                primaryExercise = bagExercise,
                secondaryExercise = strengthExercise
            ),
            // 7. 30s Ejercicio (cierra el round de 3:30)
            RoundSegment(
                id = UUID.randomUUID().toString(),
                segmentIndexInRound = 7,
                totalSegmentsInRound = 7,
                titleOnScreen = "7/7 · ¡YA! ÚLTIMOS 30s DE ${strengthExercise.name.uppercase()}",
                subtitleOnScreen = "Termina el round de 3:30 a tope con ${strengthExercise.name}",
                writtenInstructions = strengthExercise.writtenSteps,
                spokenAnnouncement = "¡YA! Últimos 30 segundos del round: ${strengthExercise.name}.",
                durationSeconds = 30,
                role = SegmentRole.STRENGTH_BURST,
                primaryExercise = strengthExercise
            )
        )

        return WorkoutRound(
            roundNumber = 0,
            totalRoundsInSession = 0,
            roundNumberInPhase = roundInPhase,
            totalRoundsInPhase = totalInPhase,
            phase = WorkoutPhase.MAIN_BLOCK,
            roundTitle = "Fuerza Round $roundInPhase/$totalInPhase: Saco + ${strengthExercise.name}",
            roundSummarySubtitle = "3:30 min · 7 tramos de 30s (Saco / ${strengthExercise.name} / Abs)",
            segments = segments
        )
    }

    /**
     * Construye el Round de Fuerza y Velocidad (3:00 min):
     * - 2:30 min de "4 Puños (combinación) + 4 Repeticiones de ejercicio" sin parar.
     * - Últimos 0:30 min de Abdominales / descanso activo.
     */
    private fun buildStrengthSpeed4x4Round(
        roundInPhase: Int,
        totalInPhase: Int,
        combo4Punches: Exercise,
        repExercise: Exercise,
        absExercise: Exercise
    ): WorkoutRound {
        val combinedSteps = buildList {
            add("1º LANZA 4 PUÑOS EN EL SACO: ${combo4Punches.shortScreenBanner}")
            add("2º HAZ INMEDIATAMENTE: 4 repeticiones de ${repExercise.name}")
            add("3º Vuelve al saco y repite el ciclo 4 puños + 4 repeticiones sin parar durante 2:30 min.")
            addAll(repExercise.writtenSteps.take(2))
        }

        return WorkoutRound(
            roundNumber = 0,
            totalRoundsInSession = 0,
            roundNumberInPhase = roundInPhase,
            totalRoundsInPhase = totalInPhase,
            phase = WorkoutPhase.MAIN_BLOCK,
            roundTitle = "Velocidad 4x4 ($roundInPhase/$totalInPhase): 4 Puños + 4 ${repExercise.name}",
            roundSummarySubtitle = "2:30 de [${combo4Punches.name} + 4 ${repExercise.name}] + 0:30 Abdominales",
            segments = listOf(
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 1,
                    totalSegmentsInRound = 2,
                    titleOnScreen = "4 PUÑOS (${combo4Punches.name.uppercase()}) + 4 ${repExercise.name.uppercase()}",
                    subtitleOnScreen = "Puños: ${combo4Punches.shortScreenBanner}  ➔  4 reps de ${repExercise.name}",
                    writtenInstructions = combinedSteps,
                    spokenAnnouncement = "Round $roundInPhase de Fuerza y Velocidad. Lanza 4 puños: ${combo4Punches.shortScreenBanner}, y haz 4 repeticiones de ${repExercise.name}. Repite sin parar.",
                    durationSeconds = 150,
                    role = SegmentRole.FOUR_PUNCH_FOUR_REP,
                    primaryExercise = combo4Punches,
                    secondaryExercise = repExercise
                ),
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 2,
                    totalSegmentsInRound = 2,
                    titleOnScreen = "ÚLTIMOS 30s: ABDOMINALES (${absExercise.name.uppercase()})",
                    subtitleOnScreen = "30s finales del round: Abdominales o recuperar aire",
                    writtenInstructions = absExercise.writtenSteps,
                    spokenAnnouncement = "¡Cambio! Últimos 30 segundos del round: abdominales o tomar aire.",
                    durationSeconds = 30,
                    role = SegmentRole.ACTIVE_RECOVERY_ABS,
                    primaryExercise = absExercise
                )
            )
        )
    }

    /**
     * Construye el Round del juego de tocar hombros y rodillas + 4 repeticiones de castigo.
     */
    private fun buildReactionRound(
        roundInPhase: Int,
        totalInPhase: Int,
        reactionExercise: Exercise,
        absExercise: Exercise
    ): WorkoutRound {
        return WorkoutRound(
            roundNumber = 0,
            totalRoundsInSession = 0,
            roundNumberInPhase = roundInPhase,
            totalRoundsInPhase = totalInPhase,
            phase = WorkoutPhase.REACTION_GAME,
            roundTitle = "Reacción ($roundInPhase/$totalInPhase): ${reactionExercise.name}",
            roundSummarySubtitle = "2:30 Juego Hombros y Rodillas (4 reps por toque) + 0:30 Abdominales",
            segments = listOf(
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 1,
                    totalSegmentsInRound = 2,
                    titleOnScreen = reactionExercise.name.uppercase(),
                    subtitleOnScreen = reactionExercise.shortScreenBanner,
                    writtenInstructions = reactionExercise.writtenSteps,
                    spokenAnnouncement = "Round de reacción. ${reactionExercise.spokenCoachPrompt}",
                    durationSeconds = 150,
                    role = SegmentRole.REACTION_DRILL,
                    primaryExercise = reactionExercise
                ),
                RoundSegment(
                    id = UUID.randomUUID().toString(),
                    segmentIndexInRound = 2,
                    totalSegmentsInRound = 2,
                    titleOnScreen = "ABDOMINALES / TOMAR AIRE (${absExercise.name.uppercase()})",
                    subtitleOnScreen = "Últimos 30s del round de reacción",
                    writtenInstructions = absExercise.writtenSteps,
                    spokenAnnouncement = "¡Últimos 30 segundos! Abdominales o recupera el aliento.",
                    durationSeconds = 30,
                    role = SegmentRole.ACTIVE_RECOVERY_ABS,
                    primaryExercise = absExercise
                )
            )
        )
    }
}
