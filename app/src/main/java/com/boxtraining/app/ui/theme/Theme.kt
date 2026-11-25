package com.boxtraining.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de color Material 3 configurado en modo oscuro permanente
 * para garantizar legibilidad en entornos de entrenamiento.
 */
private val BoxingDarkColorScheme = darkColorScheme(
    primary = BoxingCrimson,
    secondary = BagActionGreen,
    tertiary = SpeedGold,
    background = GymDarkBackground,
    surface = GymCardSurface,
    surfaceVariant = GymCardElevated,
    onPrimary = TextPrimaryWhite,
    onSecondary = GymDarkBackground,
    onBackground = TextPrimaryWhite,
    onSurface = TextPrimaryWhite,
    onSurfaceVariant = TextSecondaryLight
)

/**
 * Tema raíz de Jetpack Compose para toda la aplicación BoxCoach.
 *
 * @param content Contenido Composable de la pantalla actual.
 */
@Composable
fun BoxTrainingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BoxingDarkColorScheme,
        content = content
    )
}
