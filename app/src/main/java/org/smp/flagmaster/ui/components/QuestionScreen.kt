package org.smp.flagmaster.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.Country
import org.smp.domain.model.Question
import org.smp.flagmaster.R

// Bottom bar height + padding — content scrolls clear of the button
private val BottomBarHeight = 88.dp

// Full-screen gradient colours (light lavender → deeper purple)
private val GradientTop    = Color(0xFFEDE9FF)
private val GradientMid    = Color(0xFFCCC5FF)
private val GradientBottom = Color(0xFF9F95F0)

// Gradient for the CTA button
private val BtnGradientStart = Color(0xFF8B8EF8)
private val BtnGradientEnd   = Color(0xFF4355B9)

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
) {
    // Root Box fills the screen with the gradient
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientMid, GradientBottom)
                )
            )
    ) {

        // ── Scrollable content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = BottomBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Fixed-height streak slot
            StreakChip(streak = streak)

            Spacer(Modifier.height(8.dp))

            // Question prompt
            Text(
                text = stringResource(R.string.which_country_flag),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1050),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Flag card — white so the flag pops against the gradient
            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    (fadeIn(tween(300)) + scaleIn(initialScale = 0.88f, animationSpec = tween(300))) togetherWith
                        (fadeOut(tween(150)) + scaleOut(targetScale = 0.88f, animationSpec = tween(150)))
                },
                label = "flagCard"
            ) { currentQuestion ->
                Card(
                    modifier = Modifier.size(220.dp, 130.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
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
                    isCorrect = option.code == question.answerId,
                    showResult = showResult,
                    onClick = { if (!showResult) onAnswerSelected(option) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Fun fact card
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
                        containerColor = Color.White.copy(alpha = 0.75f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = question.fact,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1A1050)
                        )
                    }
                }
            }
        }

        // ── Gradient CTA button — absolutely positioned ──────────────────────
        AnimatedVisibility(
            visible = showResult,
            enter = fadeIn(tween(220)) + slideInVertically(tween(260)) { it / 2 },
            exit = fadeOut(tween(180)) + slideOutVertically(tween(220)) { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            // Transparent backing — gradient comes from the Box below
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BtnGradientStart, BtnGradientEnd)
                        )
                    )
                    .clickable { onNextQuestion() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (questionNumber < totalQuestions) {
                        stringResource(R.string.next_question)
                    } else {
                        stringResource(R.string.see_results)
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }
}

// Extracted to avoid ColumnScope.AnimatedVisibility receiver conflict inside Box.
@Composable
private fun StreakChip(streak: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(36.dp)
            .padding(bottom = 4.dp)
    ) {
        AnimatedVisibility(
            visible = streak >= 2,
            enter = fadeIn(tween(200)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
                initialScale = 0.4f,
            ),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.4f),
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(15.dp)
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
    }
}
