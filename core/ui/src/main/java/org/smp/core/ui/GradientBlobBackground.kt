package org.smp.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Soft, low-alpha blurred color blobs behind [content] — a colorful, playful backdrop
 * that still lets [colors] be theme-derived (e.g. MaterialTheme.colorScheme.primary),
 * so it adapts to light/dark instead of hardcoding a palette.
 */
@Composable
fun GradientBlobBackground(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(90.dp)
        ) {
            val spots = listOf(
                Offset(size.width * 0.12f, size.height * 0.08f) to size.minDimension * 0.85f,
                Offset(size.width * 0.9f, size.height * 0.22f) to size.minDimension * 0.65f,
                Offset(size.width * 0.25f, size.height * 0.95f) to size.minDimension * 0.8f,
            )
            spots.forEachIndexed { index, (center, radius) ->
                drawCircle(
                    color = colors[index % colors.size].copy(alpha = 0.22f),
                    radius = radius,
                    center = center,
                )
            }
        }
        content()
    }
}
