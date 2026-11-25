package com.boxtraining.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.boxtraining.app.ui.screens.coach.CoachPlayerScreen
import com.boxtraining.app.ui.screens.home.HomeScreen
import com.boxtraining.app.ui.theme.BoxTrainingTheme
import com.boxtraining.app.ui.viewmodel.BoxingCoachViewModel

/**
 * Actividad principal y punto de entrada de la aplicación BoxCoach Entrenador.
 *
 * Responsabilidades principales:
 * 1. Mantener la pantalla del dispositivo activa (`FLAG_KEEP_SCREEN_ON`) para evitar que el
 *    móvil se bloquee en mitad de un round mientras el usuario lleva los guantes puestos.
 * 2. Observar el flujo reactivo (`StateFlow`) de [BoxingCoachViewModel] para alternar entre
 *    la pantalla de configuración ([HomeScreen]) y el reproductor en vivo ([CoachPlayerScreen]).
 * 3. Soportar rotación fluida vertical/horizontal sin perder el estado del cronómetro.
 */
class MainActivity : ComponentActivity() {

    /** Instancia del ViewModel principal vinculada al ciclo de vida de la Activity. */
    private val viewModel: BoxingCoachViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evita que la pantalla se apague automáticamente durante los rounds de entrenamiento
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            BoxTrainingTheme {
                val uiState by viewModel.uiState.collectAsState()
                val exercises by viewModel.exercisesCatalogFlow.collectAsState()
                val customSelectedIds by viewModel.customSelectedIdsFlow.collectAsState()

                if (uiState.isWorkoutActive) {
                    // Intercepta el botón 'Atrás' del sistema para salir de forma segura al menú
                    BackHandler {
                        viewModel.stopAndExitWorkout()
                    }
                    CoachPlayerScreen(
                        uiState = uiState,
                        onTogglePause = viewModel::togglePauseResume,
                        onPreviousSegment = viewModel::goToPreviousSegment,
                        onNextSegment = viewModel::skipToNextSegment,
                        onRepeatVoice = viewModel::repeatCurrentVoiceInstruction,
                        onToggleVoice = viewModel::toggleVoiceEnabled,
                        onToggleSound = viewModel::toggleSoundEnabled,
                        onExitWorkout = viewModel::stopAndExitWorkout
                    )
                } else {
                    HomeScreen(
                        uiState = uiState,
                        allExercises = exercises,
                        customSelectedIds = customSelectedIds,
                        onSelectDayType = viewModel::selectDayType,
                        onSelectBodyFocus = viewModel::selectBodyFocus,
                        onSelectDuration = viewModel::selectDurationMinutes,
                        onToggleReactionGame = viewModel::toggleIncludeReactionGame,
                        onToggleCustomExercise = viewModel::toggleCustomExerciseSelection,
                        onCreateCustomExercise = viewModel::createNewCustomExercise,
                        onStartWorkout = viewModel::startWorkoutSession,
                        onStartFromSpecificRound = { roundIndex ->
                            viewModel.startWorkoutSession()
                            viewModel.jumpToRound(roundIndex)
                        }
                    )
                }
            }
        }
    }
}
