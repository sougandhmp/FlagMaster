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

@Composable
fun CircularLoading(timeInSeconds: Int, onComplete: () -> Unit = {}) {
    var secondsLeft by remember { mutableIntStateOf(timeInSeconds) }

    LaunchedEffect(Unit) {
        repeat(10) {
            delay(1000)
            secondsLeft--
        }
        onComplete()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { 1f - secondsLeft / 10f },
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