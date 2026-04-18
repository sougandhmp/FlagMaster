package org.smp.flagmaster.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.smp.flagmaster.ui.theme.VibrantThemeConfig
import kotlin.random.Random

@Composable
fun VibrantBackground(
    config: VibrantThemeConfig,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(config.mainGradient))
    ) {
        ChallengeStarryBackground()
        NebulaSpots(config.accentColor)
        content()
    }
}

@Composable
fun NebulaSpots(accentColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize().blur(60.dp)) {
        drawCircle(
            color = accentColor.copy(alpha = 0.2f),
            radius = size.minDimension / 1.5f,
            center = Offset(size.width * 0.2f, size.height * 0.3f)
        )
        drawCircle(
            color = accentColor.copy(alpha = 0.15f),
            radius = size.minDimension / 2f,
            center = Offset(size.width * 0.8f, size.height * 0.7f)
        )
    }
}

@Composable
fun ChallengeStarryBackground() {
    val stars = remember {
        List(60) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 1.5f + 0.5f)
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (x, y, radius) ->
            drawCircle(
                color = Color.White.copy(alpha = Random.nextFloat() * 0.4f + 0.3f),
                radius = radius.dp.toPx(),
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}
