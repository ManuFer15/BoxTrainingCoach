package com.boxtraining.app.ui.components

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.PunchMove
import com.boxtraining.app.domain.model.VisualAnimationStyle
import com.boxtraining.app.ui.theme.GymCardElevated
import com.boxtraining.app.ui.theme.TextMutedSlate
import com.boxtraining.app.ui.theme.TextPrimaryWhite
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Visor del ejercicio actual:
 * - Si existe el archivo GIF generado con IA en `app/src/main/assets/<gifAssetFileName>`, lo reproduce en bucle infinito.
 * - Mientras no hayas metido el archivo GIF todavía, dibuja automáticamente una animación en bucle del ejercicio
 *   (saco moviéndose, comba girando, boxeador haciendo sentadillas/flexiones/abdominales y secuencia animada de puños).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseVisualStage(
    exercise: Exercise,
    secondaryExercise: Exercise?,
    accentColor: Color,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val hasAssetFile = remember(exercise.gifAssetFileName) {
        if (exercise.gifAssetFileName.isBlank()) {
            false
        } else {
            try {
                context.assets.open(exercise.gifAssetFileName).use { true }
            } catch (_: Exception) {
                false
            }
        }
    }

    val gifImageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GymCardElevated)
            .border(1.5.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera superior del visor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (hasAssetFile) "DEMO IA (GIF ACTIVO)" else "DEMO EN VIVO · ${exercise.gifAssetFileName.ifBlank { "LOOP" }}",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            exercise.repBadge?.let { badge ->
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badge,
                        color = TextPrimaryWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Área central de animación o GIF generado con IA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0E131C)),
            contentAlignment = Alignment.Center
        ) {
            if (hasAssetFile) {
                AsyncImage(
                    model = "file:///android_asset/${exercise.gifAssetFileName}",
                    imageLoader = gifImageLoader,
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ProceduralBoxingCanvas(
                    style = exercise.animationStyle,
                    accentColor = accentColor,
                    isPaused = isPaused,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Si el ejercicio tiene secuencia de puños (Técnica o 4 Puños + 4 Reps), mostramos los golpes iluminándose en orden
        if (exercise.punchSequence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedPunchSequenceRow(
                punches = exercise.punchSequence,
                accentColor = accentColor,
                isPaused = isPaused
            )
        }

        // Si es un round combinado (ej. 4 Puños + 4 Sentadillas), destacamos también el ejercicio secundario
        if (secondaryExercise != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = accentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "COMBINADO CON: ${secondaryExercise.name.uppercase()}",
                        color = TextPrimaryWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimatedPunchSequenceRow(
    punches: List<PunchMove>,
    accentColor: Color,
    isPaused: Boolean
) {
    val transition = rememberInfiniteTransition(label = "punch_seq")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = punches.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = punches.size * 650, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "punch_index"
    )

    val activeIndex = if (isPaused) 0 else progress.toInt().coerceIn(0, punches.lastIndex)

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        punches.forEachIndexed { index, punch ->
            val isHighlighted = index == activeIndex
            Surface(
                color = if (isHighlighted) accentColor else Color(0xFF141A26),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isHighlighted) Color.White else accentColor.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isHighlighted) Color.Black.copy(alpha = 0.35f) else accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = punch.numberCode,
                            color = TextPrimaryWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = punch.spanishName,
                        color = if (isHighlighted) Color.Black else TextPrimaryWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProceduralBoxingCanvas(
    style: VisualAnimationStyle,
    accentColor: Color,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "boxing_canvas")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    val t = if (isPaused) 0.35f else phase

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            when (style) {
                VisualAnimationStyle.HEAVY_BAG_PUNCHING,
                VisualAnimationStyle.BOXING_COMBO -> {
                    // Saco pesado oscilando con impactos de guantes
                    val swingX = cx + 38f + (t * 14f)
                    drawLine(
                        color = TextMutedSlate,
                        start = Offset(cx + 42f, 10f),
                        end = Offset(swingX, 34f),
                        strokeWidth = 5f
                    )
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.85f),
                        topLeft = Offset(swingX - 26f, 34f),
                        size = Size(52f, size.height - 48f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                    // Silueta del boxeador lanzando manos
                    val boxerX = cx - 55f
                    drawCircle(
                        color = Color.White,
                        radius = 16f,
                        center = Offset(boxerX, cy - 26f)
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(boxerX, cy - 8f),
                        end = Offset(boxerX, cy + 36f),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                    val punchReach = boxerX + 25f + (t * 58f)
                    drawLine(
                        color = Color.White,
                        start = Offset(boxerX, cy - 4f),
                        end = Offset(punchReach, cy - 6f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = accentColor,
                        radius = 12f,
                        center = Offset(punchReach, cy - 6f)
                    )
                }

                VisualAnimationStyle.JUMP_ROPE -> {
                    val jumpOffset = -t * 22f
                    val arcHeight = (t - 0.5f) * 110f
                    drawArc(
                        color = accentColor,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - 55f, cy - 45f + jumpOffset),
                        size = Size(110f, 90f + arcHeight),
                        style = Stroke(width = 5f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 15f,
                        center = Offset(cx, cy - 24f + jumpOffset)
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(cx, cy - 6f + jumpOffset),
                        end = Offset(cx, cy + 36f + jumpOffset),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }

                VisualAnimationStyle.SQUATS_LOWER_BODY,
                VisualAnimationStyle.LUNGES_LOWER_BODY -> {
                    val squatDrop = t * 28f
                    val headY = cy - 32f + squatDrop
                    val hipY = cy + 6f + squatDrop
                    val kneeX = cx + 18f + (t * 10f)
                    val kneeY = cy + 28f
                    drawCircle(color = Color.White, radius = 15f, center = Offset(cx, headY))
                    drawLine(
                        color = Color.White,
                        start = Offset(cx, headY + 16f),
                        end = Offset(cx - 6f, hipY),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accentColor,
                        start = Offset(cx - 6f, hipY),
                        end = Offset(kneeX, kneeY),
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = accentColor,
                        start = Offset(kneeX, kneeY),
                        end = Offset(cx + 6f, cy + 52f),
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                }

                VisualAnimationStyle.PUSHUPS_UPPER_BODY,
                VisualAnimationStyle.BURPEES_FULL_BODY,
                VisualAnimationStyle.ABS_CORE -> {
                    val bodyY = cy + 12f + (t * 18f)
                    drawCircle(
                        color = Color.White,
                        radius = 13f,
                        center = Offset(cx - 56f, bodyY - 8f)
                    )
                    drawLine(
                        color = accentColor,
                        start = Offset(cx - 42f, bodyY),
                        end = Offset(cx + 55f, cy + 34f),
                        strokeWidth = 11f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(cx - 34f, bodyY),
                        end = Offset(cx - 34f, cy + 40f),
                        strokeWidth = 7f,
                        cap = StrokeCap.Round
                    )
                }

                else -> {
                    val angle = t * 2f * PI.toFloat()
                    val offsetX = cos(angle) * 20f
                    val offsetY = sin(angle) * 8f
                    drawCircle(
                        color = accentColor.copy(alpha = 0.25f),
                        radius = 48f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 16f,
                        center = Offset(cx + offsetX, cy - 18f + offsetY)
                    )
                    drawCircle(
                        color = accentColor,
                        radius = 11f,
                        center = Offset(cx + offsetX + 28f, cy - 10f)
                    )
                    drawCircle(
                        color = accentColor,
                        radius = 11f,
                        center = Offset(cx + offsetX - 28f, cy - 10f)
                    )
                }
            }
        }
    }
}
