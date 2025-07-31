package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * A composable that displays a circular countdown timer with animated progress.
 *
 * Shows:
 * - A circular progress indicator that fills over time.
 * - A countdown text (in seconds) displayed in the center.
 * - Automatically counts down from [timeInSeconds] to 0.
 *
 * @param timeInSeconds The total time in seconds for the countdown.
 * @param modifier Optional [Modifier] for layout customization.
 * @param onComplete A callback function triggered when the countdown finishes.
 */
@Composable
fun CircularLoading(
    timeInSeconds: Int,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    var secondsLeft by remember { mutableIntStateOf(timeInSeconds) }

    LaunchedEffect(timeInSeconds) {
        repeat(timeInSeconds) {
            delay(1000)
            secondsLeft--
        }
        onComplete()
    }

    val progress = 1f - (secondsLeft.toFloat() / timeInSeconds.coerceAtLeast(1))

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { progress },
            color = ProgressIndicatorDefaults.circularColor,
            strokeWidth = 2.dp,
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )
        Text("$secondsLeft", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
@Preview
fun CircularLoadingPreview(){
    CircularLoading(20)
}