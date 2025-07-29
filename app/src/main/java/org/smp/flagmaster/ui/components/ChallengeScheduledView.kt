package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.smp.flagmaster.R

@Composable
fun ChallengeScheduledView(scheduledTime: String) {
    Text(
        text = stringResource(R.string.challenge_scheduled_at, scheduledTime),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}