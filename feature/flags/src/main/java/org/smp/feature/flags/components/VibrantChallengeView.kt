package org.smp.feature.flags.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.smp.core.ui.ConfettiShower
import org.smp.domain.model.Country
import org.smp.feature.flags.R
import org.smp.feature.flags.theme.FlagMasterTheme
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun VibrantChallengeView(
    modifier: Modifier = Modifier,
    questionNumber: Int = 15,
    totalQuestions: Int = 15,
    score: Int = 2,
    remainingTime: String = "00:00",
    streak: Int = 0,
    flagCountryCode: String = "ae",
    options: List<Country>,
    selectedCountry: Country? = null,
    onOptionSelected: (Country) -> Unit = {},
    onSeeResults: () -> Unit = {},
    showResult: Boolean = false,
    correctAnswer: String? = null,
    fact: String = "",
    factCountdown: Int = 10,
) {
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme

    val isCorrect = selectedCountry?.code == correctAnswer
    val showScorePopup = showResult && isCorrect
    val borderAlpha = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(showResult) {
        if (showResult && selectedCountry != null) {
            if (isCorrect) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                launch {
                    flashAlpha.animateTo(0.4f, tween(100))
                    flashAlpha.animateTo(0f, tween(400))
                }
                delay(800.milliseconds)
                onSeeResults()
            } else {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                launch {
                    repeat(4) {
                        shakeOffset.animateTo(10f, tween(50))
                        shakeOffset.animateTo(-10f, tween(50))
                    }
                    shakeOffset.animateTo(0f, tween(50))
                }
            }
            borderAlpha.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            borderAlpha.snapTo(0f)
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !showResult || selectedCountry == null -> Color.Transparent
            isCorrect -> colorScheme.tertiary
            else -> colorScheme.error
        },
        label = "borderColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "StreakGlow")
    val glowColor by infiniteTransition.animateColor(
        initialValue = colorScheme.tertiary,
        targetValue = colorScheme.secondary,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowColor"
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (showResult && isCorrect) {
            ConfettiShower(
                colors = listOf(colorScheme.tertiary, colorScheme.primary, colorScheme.secondary)
            )
        }

        if (streak >= 15) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Transparent, glowColor.copy(alpha = 0.2f)),
                                center = center,
                                radius = size.minDimension
                            ),
                            blendMode = BlendMode.Screen
                        )
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = if (showResult) 104.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VibrantHeader(
                questionNumber = questionNumber,
                totalQuestions = totalQuestions,
                score = score,
                remainingTime = if (showResult) "" else remainingTime,
                showScorePopup = showScorePopup
            )

            QuestionCard(
                streak = streak,
                glowColor = glowColor,
                flagCountryCode = flagCountryCode,
                options = options,
                selectedCountry = selectedCountry,
                correctAnswer = correctAnswer,
                showResult = showResult,
                isCorrect = isCorrect,
                shakeOffsetX = shakeOffset.value,
                animatedBorderColor = animatedBorderColor,
                borderAlpha = borderAlpha.value,
                onOptionSelected = onOptionSelected,
            )

            if (showResult && fact.isNotEmpty()) {
                FactPanel(fact = fact)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showResult) {
            VibrantCtaButton(
                text = stringResource(R.string.next_question),
                onClick = onSeeResults,
                countdownFraction = factCountdown.toFloat() / 10f,
                showCountdownLabel = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.tertiary.copy(alpha = flashAlpha.value))
        )
    }
}

@Composable
private fun QuestionCard(
    streak: Int,
    glowColor: Color,
    flagCountryCode: String,
    options: List<Country>,
    selectedCountry: Country?,
    correctAnswer: String?,
    showResult: Boolean,
    isCorrect: Boolean,
    shakeOffsetX: Float,
    animatedBorderColor: Color,
    borderAlpha: Float,
    onOptionSelected: (Country) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(colorScheme.surfaceContainerHigh)
            .border(
                width = 3.dp,
                color = animatedBorderColor.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(32.dp)
            )
            .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.which_country_flag),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            )

            AnimatedVisibility(
                visible = streak >= 2,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                label = "StreakVisibility"
            ) {
                StreakBadge(streak = streak, glowColor = glowColor)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorScheme.surfaceContainerHighest)
                    .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                CountryFlag(countryCode = flagCountryCode)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { country ->
                    val isSelected = selectedCountry == country
                    val isWrongSelection = isSelected && !isCorrect && showResult
                    VibrantAnswerOption(
                        text = country.name,
                        isSelected = isSelected,
                        isCorrect = country.code == correctAnswer,
                        showResult = showResult,
                        onClick = { onOptionSelected(country) },
                        modifier = if (isWrongSelection) {
                            Modifier.offset { IntOffset(shakeOffsetX.toInt(), 0) }
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakBadge(streak: Int, glowColor: Color) {
    val colorScheme = MaterialTheme.colorScheme
    val bounceScale by animateFloatAsState(
        targetValue = if (streak >= 2) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "StreakBounce"
    )
    Row(
        modifier = Modifier
            .graphicsLayer(scaleX = bounceScale, scaleY = bounceScale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (streak >= 5) glowColor.copy(alpha = 0.3f)
                else colorScheme.secondary.copy(alpha = 0.25f)
            )
            .border(
                2.dp,
                if (streak >= 5) glowColor else colorScheme.secondary.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = when {
                streak >= 10 -> "🔥🚀"
                streak >= 5 -> "🔥✨"
                else -> "🔥"
            },
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "$streak streak",
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
fun VibrantChallengeViewPreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            options = listOf(
                Country("United Arab Emirates", "ae"),
                Country("Macedonia", "mk"),
                Country("Guernsey", "gg")
            ),
            selectedCountry = Country("United Arab Emirates", "ae"),
            showResult = true,
            correctAnswer = "mk",
            fact = "North Macedonia declared independence from Yugoslavia in 1991 and is one of the youngest countries in Europe.",
            factCountdown = 7
        )
    }
}

@Preview
@Composable
fun VibrantChallengeViewActivePreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            options = listOf(
                Country("United Arab Emirates", "ae"),
                Country("Macedonia", "mk"),
                Country("Guernsey", "gg")
            ),
            selectedCountry = Country("United Arab Emirates", "ae")
        )
    }
}
