package org.smp.flagmaster.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.smp.core.ui.BlueVibrantTheme
import org.smp.core.ui.ConfettiShower
import org.smp.core.ui.OrangeVibrantTheme
import org.smp.core.ui.VibrantThemeConfig
import org.smp.domain.model.Country
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun VibrantChallengeView(
    modifier: Modifier = Modifier,
    config: VibrantThemeConfig = BlueVibrantTheme,
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

    val isCorrect = selectedCountry?.code == correctAnswer
    val borderAlpha = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }
    val shakeOffset = remember { Animatable(0f) }
    var showScorePopup by remember { mutableStateOf(false) }

    LaunchedEffect(showResult) {
        if (showResult && selectedCountry != null) {
            if (isCorrect) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                // Screen Flash
                launch {
                    flashAlpha.animateTo(0.4f, tween(100))
                    flashAlpha.animateTo(0f, tween(400))
                }

                // +10 Popup
                showScorePopup = true

                // Auto-next for correct answer
                delay(800)
                onSeeResults()
            } else {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                // Screen Shake
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
            showScorePopup = false
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !showResult || selectedCountry == null -> Color.Transparent
            isCorrect -> Color(0xFF4CAF50)
            else -> Color(0xFFF44336)
        },
        label = "borderColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "StreakGlow")
    val glowColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFD700),
        targetValue = Color(0xFFFF6F00),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowColor"
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (showResult && isCorrect) {
            ConfettiShower()
        }

        // Screen Edge Glow for 15+ streak
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

        // Scrollable content — bottom padding reserves space beneath the pinned button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = if (showResult) 104.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VibrantHeader(
                questionNumber = questionNumber,
                totalQuestions = totalQuestions,
                score = score,
                remainingTime = if (showResult) "" else remainingTime,
                config = config,
                showScorePopup = showScorePopup
            )

            // Glassmorphic Content Card
            AnimatedContent(
                targetState = questionNumber,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300, easing = LinearEasing)) +
                            slideInHorizontally(
                                animationSpec = tween(
                                    300,
                                    easing = FastOutSlowInEasing
                                )
                            ) { it / 2 })
                        .togetherWith(
                            fadeOut(animationSpec = tween(300, easing = LinearEasing)) +
                                    slideOutHorizontally(
                                        animationSpec = tween(
                                            300,
                                            easing = FastOutSlowInEasing
                                        )
                                    ) { -it / 2 }
                        )
                },
                label = "QuestionTransition"
            ) { targetNum ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(config.cardBackground)
                        .border(
                            width = 3.dp,
                            color = animatedBorderColor.copy(alpha = borderAlpha.value),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.which_country_flag),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                lineHeight = 32.sp
                            )
                        )

                        this@Column.AnimatedVisibility(
                            visible = streak >= 2,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            label = "StreakVisibility_$targetNum"
                        ) {
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
                                        else Color(0xFFFF6F00).copy(alpha = 0.25f)
                                    )
                                    .border(
                                        2.dp,
                                        if (streak >= 5) glowColor else Color(0xFFFF6F00).copy(alpha = 0.5f),
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
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$streak streak",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(24.dp)
                                ),
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
                                    config = config,
                                    onClick = { onOptionSelected(country) },
                                    modifier = if (isWrongSelection) {
                                        Modifier.offset { IntOffset(shakeOffset.value.toInt(), 0) }
                                    } else {
                                        Modifier
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showResult && fact.isNotEmpty()) {
                FactPanel(fact = fact, config = config)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Button overlaid at the bottom, always visible above the scroll
        if (showResult) {
            VibrantCtaButton(
                text = stringResource(R.string.next_question),
                config = config,
                onClick = onSeeResults,
                countdownFraction = factCountdown.toFloat() / 10f,
                showCountdownLabel = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            )
        }
        // Flash Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = flashAlpha.value))
        )
    }
}

@Composable
fun FactPanel(fact: String, config: VibrantThemeConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(config.accentColor.copy(alpha = 0.15f))
            .border(1.dp, config.accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = config.accentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = fact,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        )
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
fun VibrantHeader(
    questionNumber: Int,
    totalQuestions: Int,
    score: Int,
    remainingTime: String,
    config: VibrantThemeConfig,
    showScorePopup: Boolean = false
) {
    // Using a key for score to trigger animation
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
            // Timer Circle (Left) — hidden when answer has been submitted
            if (remainingTime.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(config.timerCircleColor)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = remainingTime,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Progress Text (Center)
            Text(
                text = "$questionNumber / $totalQuestions",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Box(contentAlignment = Alignment.Center) {
                HeaderPill(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .graphicsLayer(
                            scaleX = animatedScoreScale.value,
                            scaleY = animatedScoreScale.value
                        ),
                    background = config.scorePillColor,
                    borderColor = Color.White.copy(alpha = 0.3f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val animatedScore by animateIntAsState(
                        targetValue = score,
                        label = "ScoreNumber"
                    )
                    Text(
                        text = "$animatedScore",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                this@Column.AnimatedVisibility(
                    visible = showScorePopup,
                    enter = fadeIn() + slideInHorizontally { it / 2 },
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = (-24).dp, x = (-8).dp)
                ) {
                    Text(
                        text = "+10",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(config.progressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(questionNumber.toFloat() / totalQuestions)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(config.progressIndicator)
            )
        }
    }
}

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
            .height(48.dp) // Reduced from 68.dp to fit 4 options on screen
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp)) // Slightly less rounding for compactness
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                enabled = !showResult,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(horizontal = 16.dp), // Reduced padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp, // Slightly smaller font
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

@Composable
fun VibrantCtaButton(
    text: String,
    config: VibrantThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    countdownFraction: Float = 1f,
    showCountdownLabel: Boolean = false,
) {
    val animatable = remember { Animatable(countdownFraction) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "PressScale"
    )

    LaunchedEffect(countdownFraction) {
        animatable.animateTo(
            targetValue = countdownFraction,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    val animatedFraction = animatable.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.horizontalGradient(config.buttonGradient))
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
    ) {
        // Draining overlay — starts full width, shrinks right-to-left as countdown ticks
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedFraction)
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.20f))
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            if (showCountdownLabel && countdownFraction > 0f) {
                Text(
                    text = "auto in ${(countdownFraction * 10).toInt()}s",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun BlueVibrantPreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            config = BlueVibrantTheme,
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
fun OrangeVibrantPreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            config = OrangeVibrantTheme,
            options = listOf(
                Country("United Arab Emirates", "ae"),
                Country("United Arab Emirates", "ae"),
                Country("Macedonia", "mk"),
                Country("Guernsey", "gg")
            ),
            selectedCountry = Country("United Arab Emirates", "ae")
        )
    }
}
