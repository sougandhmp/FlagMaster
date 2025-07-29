package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.smp.flagmaster.R

@Composable
fun CountDownView(remainingTime: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.will_start_in), style = MaterialTheme.typography.headlineLarge)
        Text(remainingTime, style = MaterialTheme.typography.displayLarge)
    }
}