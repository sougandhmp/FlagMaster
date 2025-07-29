package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.ScheduleScreenAction
import org.smp.flagmaster.ui.ScheduleTimeUiState

@Composable
fun TimerScheduleView(
    uiState: ScheduleTimeUiState,
    onAction: (ScheduleScreenAction) -> Unit = {},
) {
    val focusRequesters = List(6) { remember { FocusRequester() } }
    val digits = uiState.digits
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
    Text(
        text = stringResource(R.string.schedule),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Hours Pair ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.hour), fontSize = 16.sp, color = Color.Gray)
            Row {
                SingleDigitBoxWithFocus(
                    value = digits[0],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        onAction(ScheduleScreenAction.OnDigitChange(0, filtered))
                        if (filtered.isNotEmpty()) focusRequesters[1].requestFocus()
                    },
                    focusRequester = focusRequesters[0],
                    onBackspace = { }
                )
                Spacer(modifier = Modifier.width(4.dp))
                SingleDigitBoxWithFocus(
                    value = digits[1],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        if (filtered.isNotEmpty() && digits[0].isEmpty()) {
                            onAction(ScheduleScreenAction.OnDigitChange(0, "0"))
                        }
                        onAction(ScheduleScreenAction.OnDigitChange(1, filtered))
                        if (filtered.isNotEmpty()) focusRequesters[2].requestFocus()
                    },
                    focusRequester = focusRequesters[1],
                    onBackspace = {
                        if (digits[1].isEmpty()) focusRequesters[0].requestFocus()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp)) // Wider gap between hour and minute

        // --- Minutes Pair ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.minute), fontSize = 16.sp, color = Color.Gray)
            Row {
                SingleDigitBoxWithFocus(
                    value = digits[2],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        onAction(ScheduleScreenAction.OnDigitChange(2, filtered))
                        if (filtered.isNotEmpty()) focusRequesters[3].requestFocus()
                    },
                    focusRequester = focusRequesters[2],
                    onBackspace = {
                        if (digits[2].isEmpty()) focusRequesters[1].requestFocus()
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                SingleDigitBoxWithFocus(
                    value = digits[3],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        if (filtered.isNotEmpty() && digits[2].isEmpty()) {
                            onAction(ScheduleScreenAction.OnDigitChange(2, "0"))
                        }
                        onAction(ScheduleScreenAction.OnDigitChange(3, filtered))
                        if (filtered.isNotEmpty()) focusRequesters[4].requestFocus()
                    },
                    focusRequester = focusRequesters[3],
                    onBackspace = {
                        if (digits[3].isEmpty()) focusRequesters[2].requestFocus()
                    }
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp)) // Wider gap between minute and second

        // --- Seconds Pair ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.second), fontSize = 16.sp, color = Color.Gray)
            Row {
                SingleDigitBoxWithFocus(
                    value = digits[4],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        onAction(ScheduleScreenAction.OnDigitChange(4, filtered))
                        if (filtered.isNotEmpty()) focusRequesters[5].requestFocus()
                    },
                    focusRequester = focusRequesters[4],
                    onBackspace = {
                        if (digits[4].isEmpty()) focusRequesters[3].requestFocus()
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                SingleDigitBoxWithFocus(
                    value = digits[5],
                    onValueChange = { input ->
                        val filtered = input.takeLast(1).filter { it.isDigit() }
                        if (filtered.isNotEmpty() && digits[4].isEmpty()) {
                            onAction(ScheduleScreenAction.OnDigitChange(4, "0"))
                        }
                        onAction(
                            ScheduleScreenAction.OnDigitChange(
                                5,
                                filtered
                            )
                        )
                    },
                    focusRequester = focusRequesters[5],
                    onBackspace = {
                        if (digits[5].isEmpty()) focusRequesters[4].requestFocus()
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Save Button
    Button(
        onClick = { onAction(ScheduleScreenAction.OnSave) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = stringResource(R.string.save), color = Color.White, fontSize = 18.sp)
    }
}