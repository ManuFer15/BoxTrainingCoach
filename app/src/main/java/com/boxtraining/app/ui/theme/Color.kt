package com.boxtraining.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores oficial de BoxCoach Entrenador.
 *
 * Diseñada específicamente con alto contraste sobre fondo oscuro (Dark Gym Mode)
 * para permitir la lectura rápida a 2-3 metros de distancia mientras se entrena
 * con guantes frente al saco.
 */

// --- Fondos y Superficies ---
/** Fondo principal oscuro profundo para reducir fatiga visual y ahorrar batería en pantallas OLED. */
val GymDarkBackground = Color(0xFF0B0E14)

/** Superficie base para tarjetas contenedoras y barras de navegación. */
val GymCardSurface = Color(0xFF151A24)

/** Superficie elevada para elementos seleccionados, chips activos y visores. */
val GymCardElevated = Color(0xFF1E2533)

/** Borde sutil para delimitar tarjetas no seleccionadas. */
val GymBorderSubtle = Color(0xFF2B3548)

// --- Colores Semánticos por Fase y Rol de Entrenamiento ---
/** Color corporativo principal y alerta de últimos segundos / Round Final. */
val BoxingCrimson = Color(0xFFFF2E4C)

/** Verde neón de alta visibilidad: asignado a trabajo de Saco y Combinaciones Técnicas. */
val BagActionGreen = Color(0xFF00E676)

/** Naranja intenso: asignado a las ráfagas de "¡YA! Ejercicio de Fuerza" de 30 segundos. */
val StrengthShoutOrange = Color(0xFFFF5722)

/** Dorado eléctrico: asignado a bloques de "Fuerza y Velocidad (4x4)" y Juegos de Reacción. */
val SpeedGold = Color(0xFFFFB300)

/** Azul cian: asignado a los tramos de 30s de Abdominales / Descanso Activo (tomar aire). */
val ActiveRecoveryCyan = Color(0xFF00B8D4)

/** Violeta deportivo: asignado a bloques de Calentamiento (Comba libre y Sombra libre). */
val WarmupPurple = Color(0xFF7C4DFF)

// --- Tipografía y Texto ---
/** Blanco principal de máximo contraste para cronómetros y títulos de ejercicios. */
val TextPrimaryWhite = Color(0xFFF8FAFC)

/** Gris claro para subtítulos e instrucciones paso a paso. */
val TextSecondaryLight = Color(0xFFCBD5E1)

/** Gris pizarra para etiquetas secundarias y metadatos de tiempo. */
val TextMutedSlate = Color(0xFF94A3B8)
