package org.smp.flagmaster.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.smp.domain.model.Country
import org.smp.domain.model.Question
import org.smp.flagmaster.ui.theme.BlueVibrantTheme
import org.smp.flagmaster.ui.theme.VibrantThemeConfig

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
) {
    VibrantChallengeView(
        modifier = modifier,
        config = config,
        questionNumber = questionNumber,
        totalQuestions = totalQuestions,
        score = score,
        remainingTime = remainingTime,
        flagCountryCode = question.countryCode,
        options = question.options,
        selectedCountry = question.options.find { it.name == selectedAnswer },
        onOptionSelected = { if (!showResult) onAnswerSelected(it) },
        onSeeResults = onNextQuestion,
        showResult = showResult,
        correctAnswer = question.answerId,
    )
}
