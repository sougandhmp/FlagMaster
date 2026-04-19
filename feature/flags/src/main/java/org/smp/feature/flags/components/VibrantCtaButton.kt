package org.smp.feature.flags.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.core.ui.BlueVibrantTheme
import org.smp.core.ui.OrangeVibrantTheme
import org.smp.core.ui.VibrantThemeConfig

@Composable
fun VibrantCtaButton(
    text: String,
    config: VibrantThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    countdownFraction: Float = 1f,
    showCountdownLabel: Boolean = false,
) {
    val animatable = remember { Animatable(countdownFraction) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "PressScale"
    )

    LaunchedEffect(countdownFraction) {
        animatable.animateTo(
            targetValue = countdownFraction,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    val animatedFraction = animatable.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.horizontalGradient(config.buttonGradient))
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
    ) {
        // Draining overlay — starts full width, shrinks right-to-left as countdown ticks
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedFraction)
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.20f))
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            if (showCountdownLabel && countdownFraction > 0f) {
                Text(
                    text = "auto in ${(countdownFraction * 10).toInt()}s",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun VibrantCtaButtonFullPreview() {
    VibrantCtaButton(
        text = "Next Question",
        config = BlueVibrantTheme,
        onClick = {},
        countdownFraction = 1f,
        showCountdownLabel = true,
    )
}

@Preview
@Composable
fun VibrantCtaButtonMidPreview() {
    VibrantCtaButton(
        text = "Next Question",
        config = OrangeVibrantTheme,
        onClick = {},
        countdownFraction = 0.4f,
        showCountdownLabel = true,
    )
}
