package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.theme.BlueVibrantTheme
import org.smp.flagmaster.ui.theme.FlagMasterTheme
import org.smp.flagmaster.ui.theme.VibrantThemeConfig

/**
 * Displays an animated ring countdown shown 20 seconds before quiz start.
 *
 * @param remainingTime The time remaining in "MM:SS" format.
 */
@Composable
fun CountDownView(
    remainingTime: String,
    modifier: Modifier = Modifier,
    config: VibrantThemeConfig = BlueVibrantTheme
) {
    val progressFraction = remember(remainingTime) {
        val totalSecs = remainingTime.toIntOrNull() ?: 0
        (totalSecs / 20f).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800),
        label = "countdownProgress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glassmorphic Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(config.cardBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                    .padding(vertical = 48.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.will_start_in).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    )

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                        // Background track
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(200.dp),
                            strokeWidth = 12.dp,
                            color = config.progressTrack,
                            strokeCap = StrokeCap.Round
                        )
                        // Animated progress
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(200.dp),
                            strokeWidth = 12.dp,
                            color = config.progressIndicator,
                            strokeCap = StrokeCap.Round
                        )

                        Text(
                            text = remainingTime,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = stringResource(R.string.get_ready),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CountDownViewPreview() {
    FlagMasterTheme {
        CountDownView(remainingTime = "18")
    }
}
