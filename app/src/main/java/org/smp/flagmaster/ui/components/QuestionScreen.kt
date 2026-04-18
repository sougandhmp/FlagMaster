package org.smp.flagmaster.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.Country
import org.smp.domain.model.Question
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.AnswerResult

private val TimerGreen = Color(0xFF4CAF50)
private val TimerYellow = Color(0xFFFFC107)
private val TimerRed = Color(0xFFF44336)

@Composable
fun QuestionScreen(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: String?,
    showResult: Boolean,
    onAnswerSelected: (Country) -> Unit,
    onNextQuestion: () -> Unit,
    modifier: Modifier = Modifier,
    streak: Int = 0,
    timerTotalSeconds: Int = 30,
    remainingTime: String = "",
) {
    val animatedProgress by animateFloatAsState(
        targetValue = questionNumber.toFloat() / totalQuestions.coerceAtLeast(1),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "questionProgress"
    )

    val timerFraction = remember(remainingTime) {
        val parts = remainingTime.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            val totalSeconds = minutes * 60 + seconds
            (totalSeconds.toFloat() / timerTotalSeconds.toFloat()).coerceIn(0f, 1f)
        } else 1f
    }

    val animatedTimerFraction by animateFloatAsState(
        targetValue = timerFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "timerFraction"
    )

    val timerColor = when {
        animatedTimerFraction > 0.66f -> TimerGreen
        animatedTimerFraction > 0.33f -> TimerYellow
        else -> TimerRed
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Question progress row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.question_progress, questionNumber, totalQuestions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timer bar — depletes as time runs out, changes color
        AnimatedVisibility(
            visible = remainingTime.isNotEmpty() && !showResult,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200)),
        ) {
            LinearProgressIndicator(
                progress = { animatedTimerFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = timerColor,
                trackColor = timerColor.copy(alpha = 0.2f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Streak chip — bounces in with a spring when streak >= 2
        AnimatedVisibility(
            visible = streak >= 2,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
                initialScale = 0.3f,
            ),
            exit = shrinkVertically(tween(150)) + fadeOut(tween(150)) + scaleOut(targetScale = 0.3f)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$streak streak!",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.which_country_flag),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Flag card — animates in/out when the question changes
        AnimatedContent(
            targetState = question,
            transitionSpec = {
                (fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = tween(300))) togetherWith
                    (fadeOut(tween(150)) + scaleOut(targetScale = 0.85f, animationSpec = tween(150)))
            },
            label = "flagCard"
        ) { currentQuestion ->
            Card(
                modifier = Modifier.size(200.dp, 120.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CountryFlag(countryCode = currentQuestion.countryCode)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer options
        question.options.forEach { option ->
            AnswerOption(
                text = option.name,
                isSelected = selectedAnswer == option.name,
                isCorrect = option.id == question.answerId,
                showResult = showResult,
                onClick = { if (!showResult) onAnswerSelected(option) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Fun fact card — slides in after the answer is revealed
        AnimatedVisibility(
            visible = showResult && question.fact.isNotEmpty(),
            enter = expandVertically(tween(300)) + fadeIn(tween(300)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200)),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = question.fact,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Next / Results button — appears during feedback
        AnimatedVisibility(
            visible = showResult,
            enter = expandVertically(tween(250)) + fadeIn(tween(250)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200)),
        ) {
            FilledTonalButton(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (questionNumber < totalQuestions) {
                        stringResource(R.string.next_question)
                    } else {
                        stringResource(R.string.see_results)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
