package org.smp.flagmaster.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.core.ui.BlueVibrantTheme
import org.smp.core.ui.VibrantThemeConfig
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun VibrantAnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    config: VibrantThemeConfig,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isWrongSelection = isSelected && !isCorrect
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "PressScale"
    )

    val background = when {
        showResult && isCorrect -> Color(0xFF4DD0E1).copy(alpha = 0.9f)
        showResult && isWrongSelection -> Color(0xFFE57373).copy(alpha = 0.9f)
        isSelected -> config.optionSelectedBackground
        else -> Color.White.copy(alpha = 0.1f)
    }

    val borderColor = when {
        showResult && (isCorrect || isWrongSelection) -> Color.White.copy(alpha = 0.9f)
        isSelected -> Color.White.copy(alpha = 0.8f)
        else -> Color.White.copy(alpha = 0.2f)
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
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        if (isSelected || (showResult && isCorrect)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
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
            config = BlueVibrantTheme,
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
            config = BlueVibrantTheme,
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
            config = BlueVibrantTheme,
            onClick = {},
        )
    }
}
