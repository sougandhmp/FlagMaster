package org.smp.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiPiece(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val speed: Float,
    val angle: Float
)

@Composable
fun ConfettiShower(
    modifier: Modifier = Modifier,
    pieceCount: Int = 50,
    color: Color = Color(0xFF4CAF50) // Default Green
) {
    val pieces = remember {
        List(pieceCount) {
            ConfettiPiece(
                x = Random.nextFloat(),
                y = -0.1f - Random.nextFloat() * 0.5f,
                size = 5f + Random.nextFloat() * 10f,
                color = color.copy(alpha = 0.6f + Random.nextFloat() * 0.4f),
                speed = 0.005f + Random.nextFloat() * 0.01f,
                angle = Random.nextFloat() * 360f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val p = progress.value

        pieces.forEach { piece ->
            val currentY = (piece.y + p * piece.speed * 100) * height
            if (currentY < height) {
                drawCircle(
                    color = piece.color,
                    radius = piece.size,
                    center = Offset(piece.x * width, currentY)
                )
            }
        }
    }
}
