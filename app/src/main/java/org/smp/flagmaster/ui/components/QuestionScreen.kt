package org.smp.flagmaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.smp.domain.model.Country
import org.smp.domain.model.Question
import org.smp.flagmaster.ui.theme.BlueVibrantTheme
import org.smp.flagmaster.ui.theme.VibrantThemeConfig
import org.smp.flagmaster.ui.theme.allVibrantThemes

private const val FACT_DISPLAY_SECONDS = 10

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
    score: Int = 0,
    remainingTime: String = "00",
    config: VibrantThemeConfig = BlueVibrantTheme,
    streak: Int = 0,
) {
    val themeConfig = remember(score) {
        allVibrantThemes.getOrElse(score % allVibrantThemes.size) { BlueVibrantTheme }
    }

    var factCountdown by remember { mutableIntStateOf(FACT_DISPLAY_SECONDS) }
    LaunchedEffect(showResult) {
        if (showResult) {
            factCountdown = FACT_DISPLAY_SECONDS
            while (factCountdown > 0) {
                delay(1_000L)
                factCountdown--
            }
        } else {
            factCountdown = FACT_DISPLAY_SECONDS
        }
    }

    VibrantChallengeView(
        modifier = modifier,
        config = themeConfig,
        questionNumber = questionNumber,
        totalQuestions = totalQuestions,
        score = score,
        remainingTime = remainingTime,
        streak = streak,
        flagCountryCode = question.countryCode,
        options = question.options,
        selectedCountry = question.options.find { it.code == selectedAnswer },
        onOptionSelected = { if (!showResult) onAnswerSelected(it) },
        onSeeResults = onNextQuestion,
        showResult = showResult,
        correctAnswer = question.answerId,
        fact = question.fact,
        factCountdown = factCountdown,
    )
}
