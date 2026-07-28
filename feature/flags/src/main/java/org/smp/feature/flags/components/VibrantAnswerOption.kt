package org.smp.feature.flags.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.smp.feature.flags.theme.FlagMasterTheme

@Composable
fun VibrantAnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val isWrongSelection = isSelected && !isCorrect
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "PressScale"
    )

    val background = when {
        showResult && isCorrect -> colorScheme.tertiaryContainer
        showResult && isWrongSelection -> colorScheme.errorContainer
        isSelected -> colorScheme.primaryContainer
        else -> colorScheme.surfaceContainerHigh
    }

    val contentColor = when {
        showResult && isCorrect -> colorScheme.onTertiaryContainer
        showResult && isWrongSelection -> colorScheme.onErrorContainer
        isSelected -> colorScheme.onPrimaryContainer
        else -> colorScheme.onSurface
    }

    val borderColor = when {
        showResult && (isCorrect || isWrongSelection) -> contentColor.copy(alpha = 0.4f)
        isSelected -> colorScheme.primary
        else -> colorScheme.outlineVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                enabled = !showResult,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )

        if (isSelected || (showResult && isCorrect)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f))
                    .border(1.dp, contentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun VibrantAnswerOptionDefaultPreview() {
    FlagMasterTheme {
        VibrantAnswerOption(
            text = "United Arab Emirates",
            isSelected = false,
            isCorrect = false,
            showResult = false,
            onClick = {},
        )
    }
}

@Preview
@Composable
fun VibrantAnswerOptionCorrectPreview() {
    FlagMasterTheme {
        VibrantAnswerOption(
            text = "United Arab Emirates",
            isSelected = true,
            isCorrect = true,
            showResult = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
fun VibrantAnswerOptionWrongPreview() {
    FlagMasterTheme {
        VibrantAnswerOption(
            text = "Macedonia",
            isSelected = true,
            isCorrect = false,
            showResult = true,
            onClick = {},
        )
    }
}
