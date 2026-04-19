package org.smp.feature.flags.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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

    val progress = secondsLeft.toFloat() / timeInSeconds.coerceAtLeast(1)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(40.dp)
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
fun CircularLoadingPreview() {
    CircularLoading(20)
}
