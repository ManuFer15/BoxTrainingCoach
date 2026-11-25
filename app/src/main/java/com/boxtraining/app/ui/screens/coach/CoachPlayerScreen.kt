package com.boxtraining.app.ui.screens.coach

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VoiceOverOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boxtraining.app.domain.model.RoundSegment
import com.boxtraining.app.domain.model.SegmentRole
import com.boxtraining.app.domain.model.WorkoutRound
import com.boxtraining.app.ui.components.ExerciseVisualStage
import com.boxtraining.app.ui.theme.ActiveRecoveryCyan
import com.boxtraining.app.ui.theme.BagActionGreen
import com.boxtraining.app.ui.theme.BoxingCrimson
import com.boxtraining.app.ui.theme.GymBorderSubtle
import com.boxtraining.app.ui.theme.GymCardElevated
import com.boxtraining.app.ui.theme.GymCardSurface
import com.boxtraining.app.ui.theme.GymDarkBackground
import com.boxtraining.app.ui.theme.SpeedGold
import com.boxtraining.app.ui.theme.StrengthShoutOrange
import com.boxtraining.app.ui.theme.TextMutedSlate
import com.boxtraining.app.ui.theme.TextPrimaryWhite
import com.boxtraining.app.ui.theme.TextSecondaryLight
import com.boxtraining.app.ui.theme.WarmupPurple
import com.boxtraining.app.ui.viewmodel.CoachUiState

/**
 * Pantalla "Modo Entrenador" limpia y totalmente responsiva (Vertical y Horizontal):
 * - Enfoque visual directo en: Cronómetro Gigante + Nombre del Ejercicio + Demostración Visual.
 * - Las "Pautas del Entrenador" están en un desplegable limpio para que solo las abras si quieres consultarlas.
 * - Al girar el móvil en horizontal, divide la pantalla en dos paneles (Izquierda: Animación/GIF y Puños; Derecha: Reloj gigante y Controles).
 */
@Composable
fun CoachPlayerScreen(
    uiState: CoachUiState,
    onTogglePause: () -> Unit,
    onPreviousSegment: () -> Unit,
    onNextSegment: () -> Unit,
    onRepeatVoice: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleSound: () -> Unit,
    onExitWorkout: () -> Unit
) {
    val round = uiState.currentRound
    val segment = uiState.currentSegment
    val nextSegment = uiState.nextUpcomingSegment

    if (uiState.isWorkoutCompleted || round == null || segment == null) {
        WorkoutCompletedScreen(
            sessionTitle = uiState.generatedSession?.title ?: "Entrenamiento de Boxeo",
            totalRounds = uiState.generatedSession?.rounds?.size ?: 0,
            onFinish = onExitWorkout
        )
        return
    }

    var instructionsExpanded by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val roleColor = when (segment.role) {
        SegmentRole.WARMUP_ROPE,
        SegmentRole.WARMUP_SHADOW -> WarmupPurple
        SegmentRole.EQUIPMENT_PREP -> ActiveRecoveryCyan
        SegmentRole.WARMUP_BAG,
        SegmentRole.BAG_CONTINUOUS,
        SegmentRole.TECHNIQUE_COMBO -> BagActionGreen
        SegmentRole.STRENGTH_BURST -> StrengthShoutOrange
        SegmentRole.FOUR_PUNCH_FOUR_REP,
        SegmentRole.REACTION_DRILL -> SpeedGold
        SegmentRole.ACTIVE_RECOVERY_ABS -> ActiveRecoveryCyan
        SegmentRole.FINISHER_BAG -> BoxingCrimson
    }

    Scaffold(
        containerColor = GymDarkBackground,
        bottomBar = {
            if (!isLandscape) {
                PlayerBottomControlsBar(
                    isPaused = uiState.isPaused,
                    onTogglePause = onTogglePause,
                    onPreviousSegment = onPreviousSegment,
                    onNextSegment = onNextSegment
                )
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            // =========================================================================
            // VISTA HORIZONTAL (LANDSCAPE) — IDEAL PARA APOYAR EL MÓVIL FRENTE AL SACO
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TopHeaderBar(
                    round = round,
                    segment = segment,
                    currentSegmentIndex = uiState.currentSegmentIndex,
                    remainingRoundSeconds = uiState.remainingSecondsInCurrentRound,
                    roleColor = roleColor,
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    isSoundEnabled = uiState.isSoundEnabled,
                    onToggleVoice = onToggleVoice,
                    onToggleSound = onToggleSound,
                    onExitWorkout = onExitWorkout
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Panel Izquierdo: Reloj Gigante + Nombre del Ejercicio + Controles de Pausa
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MainTimerAndExerciseCard(
                            segment = segment,
                            remainingSeconds = uiState.remainingSecondsInSegment,
                            isPaused = uiState.isPaused,
                            roleColor = roleColor,
                            isCompactLandscape = true
                        )

                        PlayerBottomControlsBar(
                            isPaused = uiState.isPaused,
                            onTogglePause = onTogglePause,
                            onPreviousSegment = onPreviousSegment,
                            onNextSegment = onNextSegment,
                            isInlineLandscape = true
                        )

                        if (nextSegment != null) {
                            NextSegmentCompactBar(nextSegment = nextSegment)
                        }
                    }

                    // Panel Derecho: Demostración Visual (GIF / Animación) + Desplegable de Pautas
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ExerciseVisualStage(
                            exercise = segment.primaryExercise,
                            secondaryExercise = segment.secondaryExercise,
                            accentColor = roleColor,
                            isPaused = uiState.isPaused
                        )

                        CollapsibleCoachInstructionsCard(
                            segment = segment,
                            roleColor = roleColor,
                            expanded = instructionsExpanded,
                            onToggleExpand = { instructionsExpanded = !instructionsExpanded },
                            onRepeatVoice = onRepeatVoice
                        )
                    }
                }
            }
        } else {
            // =========================================================================
            // VISTA VERTICAL (PORTRAIT) — LIMPIA Y SIN SOBRECARGA VISUAL
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopHeaderBar(
                    round = round,
                    segment = segment,
                    currentSegmentIndex = uiState.currentSegmentIndex,
                    remainingRoundSeconds = uiState.remainingSecondsInCurrentRound,
                    roleColor = roleColor,
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    isSoundEnabled = uiState.isSoundEnabled,
                    onToggleVoice = onToggleVoice,
                    onToggleSound = onToggleSound,
                    onExitWorkout = onExitWorkout
                )

                MainTimerAndExerciseCard(
                    segment = segment,
                    remainingSeconds = uiState.remainingSecondsInSegment,
                    isPaused = uiState.isPaused,
                    roleColor = roleColor,
                    isCompactLandscape = false
                )

                ExerciseVisualStage(
                    exercise = segment.primaryExercise,
                    secondaryExercise = segment.secondaryExercise,
                    accentColor = roleColor,
                    isPaused = uiState.isPaused
                )

                // DESPLEGABLE DE PAUTAS DEL ENTRENADOR (Cerrado por defecto para vista limpia)
                CollapsibleCoachInstructionsCard(
                    segment = segment,
                    roleColor = roleColor,
                    expanded = instructionsExpanded,
                    onToggleExpand = { instructionsExpanded = !instructionsExpanded },
                    onRepeatVoice = onRepeatVoice
                )

                if (nextSegment != null) {
                    NextSegmentCompactBar(nextSegment = nextSegment)
                }
            }
        }
    }
}

@Composable
private fun TopHeaderBar(
    round: WorkoutRound,
    segment: RoundSegment,
    currentSegmentIndex: Int,
    remainingRoundSeconds: Int,
    roleColor: Color,
    isVoiceEnabled: Boolean,
    isSoundEnabled: Boolean,
    onToggleVoice: () -> Unit,
    onToggleSound: () -> Unit,
    onExitWorkout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onExitWorkout,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(GymCardSurface)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Salir", tint = TextPrimaryWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND ${round.roundNumber} / ${round.totalRoundsInSession} · ${round.phase.displayName.uppercase()}",
                    color = TextPrimaryWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                if (round.segments.size > 1) {
                    Text(
                        text = "Tramo ${segment.segmentIndexInRound}/${segment.totalSegmentsInRound} · Restante Round: ${formatSeconds(remainingRoundSeconds)}",
                        color = TextMutedSlate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GymCardSurface)
                ) {
                    Icon(
                        imageVector = if (isVoiceEnabled) Icons.Default.RecordVoiceOver else Icons.Default.VoiceOverOff,
                        contentDescription = "Voz",
                        tint = if (isVoiceEnabled) BagActionGreen else TextMutedSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GymCardSurface)
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Campana",
                        tint = if (isSoundEnabled) SpeedGold else TextMutedSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Barra limpia de progreso por tramos (ej. los 7 bloques de 30s)
        if (round.segments.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                round.segments.forEachIndexed { idx, _ ->
                    val isCurrent = idx == currentSegmentIndex
                    val isDone = idx < currentSegmentIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    isCurrent -> roleColor
                                    isDone -> roleColor.copy(alpha = 0.4f)
                                    else -> GymCardElevated
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun MainTimerAndExerciseCard(
    segment: RoundSegment,
    remainingSeconds: Int,
    isPaused: Boolean,
    roleColor: Color,
    isCompactLandscape: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(roleColor.copy(alpha = 0.12f))
            .border(2.dp, roleColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = if (isCompactLandscape) 10.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = if (isPaused) SpeedGold else roleColor,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isPaused) "⏸ EN PAUSA" else segment.role.label,
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // NOMBRE DEL EJERCICIO EN PANTALLA
        Text(
            text = segment.titleOnScreen,
            color = TextPrimaryWhite,
            fontSize = if (isCompactLandscape) 19.sp else 21.sp,
            lineHeight = if (isCompactLandscape) 23.sp else 25.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // SUBTÍTULO / INSTRUCCIÓN CORTA DIRECTA
        Text(
            text = segment.subtitleOnScreen,
            color = TextSecondaryLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // RELOJ GIGANTE
        Text(
            text = formatSeconds(remainingSeconds),
            color = if (remainingSeconds <= 5) BoxingCrimson else TextPrimaryWhite,
            fontSize = if (isCompactLandscape) 56.sp else 66.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun CollapsibleCoachInstructionsCard(
    segment: RoundSegment,
    roleColor: Color,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onRepeatVoice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GymCardSurface)
            .border(1.dp, GymBorderSubtle, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = roleColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (expanded) "Ocultar Pautas del Entrenador" else "Ver Pautas del Entrenador (${segment.writtenInstructions.size} pasos)",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (!expanded) {
                        Text(
                            text = "Pincha aquí si quieres leer cómo hacer el ejercicio paso a paso",
                            color = TextMutedSlate,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onRepeatVoice,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(GymCardElevated)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Repetir por voz",
                    tint = roleColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                segment.writtenInstructions.forEachIndexed { idx, line ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(GymCardElevated)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(roleColor.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                color = roleColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = line,
                            color = TextPrimaryWhite,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NextSegmentCompactBar(nextSegment: RoundSegment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GymCardSurface)
            .border(1.dp, GymBorderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "SIGUIENTE:",
            color = TextMutedSlate,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${nextSegment.titleOnScreen} (${formatSeconds(nextSegment.durationSeconds)})",
            color = TextPrimaryWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PlayerBottomControlsBar(
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    onPreviousSegment: () -> Unit,
    onNextSegment: () -> Unit,
    isInlineLandscape: Boolean = false
) {
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPreviousSegment,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 14.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior")
            }

            Button(
                onClick = onTogglePause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaused) BagActionGreen else BoxingCrimson
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = if (isPaused) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPaused) "REANUDAR" else "PAUSAR",
                    color = if (isPaused) Color.Black else Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            OutlinedButton(
                onClick = onNextSegment,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 14.dp)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
            }
        }
    }

    if (isInlineLandscape) {
        content()
    } else {
        Surface(
            color = GymCardSurface,
            tonalElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun WorkoutCompletedScreen(
    sessionTitle: String,
    totalRounds: Int,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🥊 ¡CLASE COMPLETADA!",
            color = BagActionGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = sessionTitle,
            color = TextPrimaryWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Has completado los $totalRounds rounds de la sesión.",
            color = TextSecondaryLight,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = BoxingCrimson),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = "VOLVER AL INICIO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

private fun formatSeconds(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
