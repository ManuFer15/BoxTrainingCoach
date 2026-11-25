package com.boxtraining.app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boxtraining.app.domain.model.BodyFocus
import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import com.boxtraining.app.domain.model.WorkoutDayType
import com.boxtraining.app.domain.model.WorkoutPhase
import com.boxtraining.app.domain.model.WorkoutRound
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

@Composable
fun HomeScreen(
    uiState: CoachUiState,
    allExercises: List<Exercise>,
    customSelectedIds: List<String>,
    onSelectDayType: (WorkoutDayType) -> Unit,
    onSelectBodyFocus: (BodyFocus) -> Unit,
    onSelectDuration: (Int) -> Unit,
    onToggleReactionGame: (Boolean) -> Unit,
    onToggleCustomExercise: (String) -> Unit,
    onCreateCustomExercise: (String, ExerciseCategory, String, String) -> Unit,
    onStartWorkout: () -> Unit,
    onStartFromSpecificRound: (Int) -> Unit
) {
    var showNewExerciseDialog by rememberSaveable { mutableStateOf(false) }
    var showRoundsBreakdown by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val session = uiState.generatedSession

    Scaffold(
        containerColor = GymDarkBackground,
        bottomBar = {
            if (!isLandscape) {
                StartWorkoutBottomBar(
                    roundsCount = session?.rounds?.size ?: 0,
                    durationFormatted = session?.totalDurationFormatted ?: "",
                    onStartWorkout = onStartWorkout
                )
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            // DISEÑO RESPONSIVO EN HORIZONTAL (2 COLUMNAS LIMPIAS)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Columna Izquierda: Configuración limpia
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HeaderBanner()
                    DurationSelectorCard(
                        selectedDuration = uiState.selectedDurationMinutes,
                        onSelectDuration = onSelectDuration
                    )
                    DayTypeSelectorCard(
                        selectedDayType = uiState.selectedDayType,
                        onSelectDayType = onSelectDayType
                    )
                }

                // Columna Derecha: Sub-opciones + Botón Empezar + Desplegable de Rounds
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DaySpecificOptionsSection(
                        uiState = uiState,
                        allExercises = allExercises,
                        customSelectedIds = customSelectedIds,
                        onSelectBodyFocus = onSelectBodyFocus,
                        onToggleReactionGame = onToggleReactionGame,
                        onToggleCustomExercise = onToggleCustomExercise,
                        onOpenNewExerciseDialog = { showNewExerciseDialog = true }
                    )

                    Button(
                        onClick = onStartWorkout,
                        colors = ButtonDefaults.buttonColors(containerColor = BoxingCrimson),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡EMPEZAR ENTRENO! (${session?.rounds?.size ?: 0} ROUNDS)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (session != null) {
                        CollapsibleRoundsSection(
                            rounds = session.rounds,
                            totalDurationFormatted = session.totalDurationFormatted,
                            expanded = showRoundsBreakdown,
                            onToggleExpand = { showRoundsBreakdown = !showRoundsBreakdown },
                            onStartFromSpecificRound = onStartFromSpecificRound
                        )
                    }
                }
            }
        } else {
            // DISEÑO VERTICAL LIMPIO Y DESPEJADO
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
            ) {
                item { HeaderBanner() }

                item {
                    DurationSelectorCard(
                        selectedDuration = uiState.selectedDurationMinutes,
                        onSelectDuration = onSelectDuration
                    )
                }

                item {
                    DayTypeSelectorCard(
                        selectedDayType = uiState.selectedDayType,
                        onSelectDayType = onSelectDayType
                    )
                }

                item {
                    DaySpecificOptionsSection(
                        uiState = uiState,
                        allExercises = allExercises,
                        customSelectedIds = customSelectedIds,
                        onSelectBodyFocus = onSelectBodyFocus,
                        onToggleReactionGame = onToggleReactionGame,
                        onToggleCustomExercise = onToggleCustomExercise,
                        onOpenNewExerciseDialog = { showNewExerciseDialog = true }
                    )
                }

                if (session != null) {
                    item {
                        CollapsibleRoundsSection(
                            rounds = session.rounds,
                            totalDurationFormatted = session.totalDurationFormatted,
                            expanded = showRoundsBreakdown,
                            onToggleExpand = { showRoundsBreakdown = !showRoundsBreakdown },
                            onStartFromSpecificRound = onStartFromSpecificRound
                        )
                    }
                }
            }
        }
    }

    if (showNewExerciseDialog) {
        CreateCustomExerciseDialog(
            onDismiss = { showNewExerciseDialog = false },
            onConfirm = { name, cat, banner, tip ->
                onCreateCustomExercise(name, cat, banner, tip)
                showNewExerciseDialog = false
            }
        )
    }
}

@Composable
private fun HeaderBanner() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "BOXCOACH · ENTRENADOR PERSONAL",
                color = BoxingCrimson,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Entrenamiento de Hoy",
                color = TextPrimaryWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
        Surface(
            color = GymCardElevated,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.SportsMma,
                contentDescription = null,
                tint = BoxingCrimson,
                modifier = Modifier
                    .padding(10.dp)
                    .size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DurationSelectorCard(
    selectedDuration: Int,
    onSelectDuration: (Int) -> Unit
) {
    SectionCard(title = "1. Duración de la Clase") {
        val durations = listOf(30, 45, 60, 75, 90)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            durations.forEach { mins ->
                val isSelected = selectedDuration == mins
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectDuration(mins) },
                    label = {
                        Text(
                            text = if (mins == 90) "90m (1,5h)" else "${mins}m",
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BoxingCrimson,
                        selectedLabelColor = TextPrimaryWhite,
                        selectedLeadingIconColor = TextPrimaryWhite
                    )
                )
            }
        }
    }
}

@Composable
private fun DayTypeSelectorCard(
    selectedDayType: WorkoutDayType,
    onSelectDayType: (WorkoutDayType) -> Unit
) {
    SectionCard(title = "2. Tipo de Rutina") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WorkoutDayType.entries.forEach { type ->
                val isSelected = selectedDayType == type
                val typeAccent = when (type) {
                    WorkoutDayType.TECHNIQUE -> BagActionGreen
                    WorkoutDayType.STRENGTH_BODY -> StrengthShoutOrange
                    WorkoutDayType.STRENGTH_SPEED -> SpeedGold
                    WorkoutDayType.CUSTOM -> WarmupPurple
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GymCardElevated else Color(0xFF10151F))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) typeAccent else GymBorderSubtle,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectDayType(type) }
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = type.title,
                                color = TextPrimaryWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = type.subtitle,
                                color = if (isSelected) typeAccent else TextMutedSlate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            color = typeAccent.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = type.badgeText,
                                color = typeAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Mostramos la explicación detallada solo en la tarjeta seleccionada para no sobrecargar la vista
                    AnimatedVisibility(visible = isSelected) {
                        Text(
                            text = type.description,
                            color = TextSecondaryLight,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySpecificOptionsSection(
    uiState: CoachUiState,
    allExercises: List<Exercise>,
    customSelectedIds: List<String>,
    onSelectBodyFocus: (BodyFocus) -> Unit,
    onToggleReactionGame: (Boolean) -> Unit,
    onToggleCustomExercise: (String) -> Unit,
    onOpenNewExerciseDialog: () -> Unit
) {
    when (uiState.selectedDayType) {
        WorkoutDayType.STRENGTH_BODY -> {
            SectionCard(title = "3. Enfoque Muscular de Hoy") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BodyFocus.entries.forEach { focus ->
                        val selected = uiState.selectedBodyFocus == focus
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) StrengthShoutOrange.copy(alpha = 0.22f) else Color(0xFF10151F))
                                .border(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) StrengthShoutOrange else GymBorderSubtle,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectBodyFocus(focus) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = focus.shortLabel,
                                color = if (selected) StrengthShoutOrange else TextPrimaryWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        WorkoutDayType.STRENGTH_SPEED -> {
            SectionCard(title = "3. Bloque Extra de Reflejos") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Incluir Juego Hombros y Rodillas",
                            color = TextPrimaryWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "1-3 rounds con castigo de 4 repeticiones.",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = uiState.includeReactionGame,
                        onCheckedChange = onToggleReactionGame
                    )
                }
            }
        }

        WorkoutDayType.CUSTOM -> {
            var showExercisePicker by rememberSaveable { mutableStateOf(false) }
            SectionCard(title = "3. Ejercicios de tu Rutina Libre (${customSelectedIds.size} elegidos)") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showExercisePicker = !showExercisePicker },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (showExercisePicker) "Ocultar Lista" else "Elegir Ejercicios (${allExercises.size})")
                        }
                        OutlinedButton(onClick = onOpenNewExerciseDialog) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Crear")
                        }
                    }

                    AnimatedVisibility(visible = showExercisePicker) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            val selectablePool = allExercises.filter {
                                it.category != ExerciseCategory.WARMUP && it.category != ExerciseCategory.ABS_RECOVERY
                            }
                            selectablePool.forEach { ex ->
                                val isChecked = customSelectedIds.contains(ex.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isChecked) WarmupPurple.copy(alpha = 0.2f) else Color(0xFF10151F))
                                        .border(
                                            1.dp,
                                            if (isChecked) WarmupPurple else GymBorderSubtle,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onToggleCustomExercise(ex.id) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ex.name,
                                            color = TextPrimaryWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = ex.category.displayTitle,
                                            color = TextMutedSlate,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = WarmupPurple
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        WorkoutDayType.TECHNIQUE -> {
            // En Día de Técnica no hace falta sobrecargar con opciones extra
        }
    }
}

@Composable
private fun CollapsibleRoundsSection(
    rounds: List<WorkoutRound>,
    totalDurationFormatted: String,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onStartFromSpecificRound: (Int) -> Unit
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "VER DESGLOSE DE LA CLASE (${rounds.size} ROUNDS · $totalDurationFormatted)",
                    color = TextPrimaryWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (expanded) "Toca un round para empezar directamente en él" else "Pincha para desplegar todos los rounds generados",
                    color = TextMutedSlate,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextPrimaryWhite
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rounds.forEachIndexed { index, round ->
                    RoundPreviewItemCard(
                        round = round,
                        onClickStartHere = { onStartFromSpecificRound(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StartWorkoutBottomBar(
    roundsCount: Int,
    durationFormatted: String,
    onStartWorkout: () -> Unit
) {
    Surface(
        color = GymCardSurface,
        tonalElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Button(
                onClick = onStartWorkout,
                colors = ButtonDefaults.buttonColors(containerColor = BoxingCrimson),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 15.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¡EMPEZAR ENTRENAMIENTO! ($roundsCount ROUNDS · $durationFormatted)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GymCardSurface)
            .border(1.dp, GymBorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title,
            color = TextPrimaryWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun RoundPreviewItemCard(
    round: WorkoutRound,
    onClickStartHere: () -> Unit
) {
    val phaseColor = when (round.phase) {
        WorkoutPhase.WARMUP -> WarmupPurple
        WorkoutPhase.EQUIPMENT -> ActiveRecoveryCyan
        WorkoutPhase.BAG_ACTIVATION -> BagActionGreen
        WorkoutPhase.MAIN_BLOCK -> BoxingCrimson
        WorkoutPhase.REACTION_GAME -> SpeedGold
        WorkoutPhase.FINISHER -> StrengthShoutOrange
    }

    val mins = round.totalDurationSeconds / 60
    val secs = round.totalDurationSeconds % 60
    val timeLabel = if (secs == 0) "${mins}:00" else "${mins}:${secs.toString().padStart(2, '0')}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF10151F))
            .border(1.dp, phaseColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClickStartHere() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(phaseColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R${round.roundNumber}",
                color = phaseColor,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = round.roundTitle,
                color = TextPrimaryWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = round.roundSummarySubtitle,
                color = TextMutedSlate,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = timeLabel,
            color = phaseColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun CreateCustomExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, ExerciseCategory, String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var shortBanner by rememberSaveable { mutableStateOf("") }
    var writtenTip by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(ExerciseCategory.BOXING_COMBO) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Ejercicio o Combinación", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej. 1-2 + Esquiva + Gancho)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shortBanner,
                    onValueChange = { shortBanner = it },
                    label = { Text("Resumen corto en pantalla") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = writtenTip,
                    onValueChange = { writtenTip = it },
                    label = { Text("Instrucción detallada paso a paso") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedCategory == ExerciseCategory.BOXING_COMBO,
                        onClick = { selectedCategory = ExerciseCategory.BOXING_COMBO },
                        label = { Text("Combo Boxeo") }
                    )
                    FilterChip(
                        selected = selectedCategory == ExerciseCategory.LOWER_BODY,
                        onClick = { selectedCategory = ExerciseCategory.LOWER_BODY },
                        label = { Text("Fuerza") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedCategory, shortBanner, writtenTip) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar y Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
