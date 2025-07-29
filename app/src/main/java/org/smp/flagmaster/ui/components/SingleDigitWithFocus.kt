package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SingleDigitBoxWithFocus(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onBackspace: () -> Unit
) {
    var lastValue by remember { mutableStateOf(value) }

    TextField(
        value = value,
        onValueChange = { input ->
            val filtered = input.takeLast(1).filter { it.isDigit() }
            if (lastValue.isNotEmpty() && filtered.isEmpty()) {
                onBackspace()
            }
            lastValue = filtered
            onValueChange(filtered)
        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        modifier = Modifier
            .size(56.dp)
            .focusRequester(focusRequester)
            .onPreviewKeyEvent{ keyEvent ->
                if (
                    keyEvent.type == KeyEventType.KeyDown &&
                    keyEvent.key == Key.Backspace
                ) {
                    // When the box is empty and backspace is pressed, move focus to previous
                    if (value.isEmpty()) {
                        onBackspace()
                        true // consume the event
                    } else {
                        false // let default handling occur
                    }
                } else {
                    false // not backspace, do not consume
                }
            },
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {},
            onPrevious = {onBackspace()}
        )
    )
}

@Composable
@Preview
private fun SingleDigitBoxWithFocusPreview() {
    SingleDigitBoxWithFocus(value = "1", onValueChange = {}, focusRequester = FocusRequester(), onBackspace = {})
}
