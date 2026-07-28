package org.smp.feature.flags.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.smp.feature.flags.theme.FlagMasterTheme

@Composable
fun VibrantHeader(
    questionNumber: Int,
    totalQuestions: Int,
    score: Int,
    remainingTime: String,
    showScorePopup: Boolean = false,
) {
    val colorScheme = MaterialTheme.colorScheme
    val animatedScoreScale = remember(score) { Animatable(1.2f) }
    LaunchedEffect(score) {
        animatedScoreScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (remainingTime.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorScheme.secondaryContainer)
                        .border(1.dp, colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = remainingTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Text(
                text = "$questionNumber / $totalQuestions",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )

            Box(
                modifier = Modifier.align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {
                HeaderPill(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = animatedScoreScale.value,
                            scaleY = animatedScoreScale.value
                        ),
                    background = colorScheme.tertiaryContainer,
                    borderColor = colorScheme.outlineVariant,
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val animatedScore by animateIntAsState(
                        targetValue = score,
                        label = "ScoreNumber"
                    )
                    Text(
                        text = "$animatedScore",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }

                ScorePopup(
                    visible = showScorePopup,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = (-24).dp, x = (-8).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(questionNumber.toFloat() / totalQuestions)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary)
            )
        }
    }
}

@Composable
private fun HeaderPill(
    background: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    horizontalPadding: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = horizontalPadding, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = { content() },
    )
}

@Composable
private fun ScorePopup(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally { it / 2 },
        exit = fadeOut(),
        modifier = modifier
    ) {
        Text(
            text = "+10",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Preview
@Composable
fun VibrantHeaderActivePreview() {
    FlagMasterTheme {
        VibrantHeader(
            questionNumber = 7,
            totalQuestions = 15,
            score = 5,
            remainingTime = "00:28",
        )
    }
}

@Preview
@Composable
fun VibrantHeaderResultPreview() {
    FlagMasterTheme {
        VibrantHeader(
            questionNumber = 15,
            totalQuestions = 15,
            score = 12,
            remainingTime = "",
            showScorePopup = true,
        )
    }
}
