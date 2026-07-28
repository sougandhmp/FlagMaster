package org.smp.feature.flags.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.smp.domain.model.Country
import org.smp.domain.model.Question

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
    streak: Int = 0,
    factCountdown: Int = 10,
) {

    VibrantChallengeView(
        modifier = modifier,
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
